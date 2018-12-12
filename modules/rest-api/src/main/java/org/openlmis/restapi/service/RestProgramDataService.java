package org.openlmis.restapi.service;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.moz.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramDataColumnRepository;
import org.openlmis.core.repository.ProgramDataRepository;
import org.openlmis.core.repository.SyncUpHashRepository;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramDataColumnMapper;
import org.openlmis.core.repository.mapper.SupplementalProgramMapper;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.restapi.domain.*;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.ServiceLineItem;
import org.openlmis.rnr.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestProgramDataService {

  @Autowired
  private SupplementalProgramMapper supplementalProgramMapper;

  @Autowired
  private ProgramDataRepository programDataRepository;

  @Autowired
  private FacilityMapper facilityMapper;

  @Autowired
  private ProgramDataColumnMapper programDataColumnMapper;

  @Autowired
  private SyncUpHashRepository syncUpHashRepository;

  @Autowired
  private RestRequisitionService restRequisitionService;

  @Autowired
  private ServiceRepository serviceRepository;

  @Autowired
  private ProgramDataColumnRepository programDataColumnRepository;

  @Transactional
  public Rnr createProgramDataForm(ProgramDataFormDTO requestBodyData, long userId) {
    if (syncUpHashRepository.hashExists(requestBodyData.getSyncUpHash())) {
      return null;
    }

    Facility facility = facilityMapper.getById(requestBodyData.getFacilityId());
    if (facility == null) {
      throw new DataException("error.facility.unknown");
    }

    ProgramDataForm programDataForm = convertRequestBodyDataToProgramDataForm(requestBodyData, userId, facility);

    programDataRepository.createProgramDataForm(programDataForm);
    Rnr requisition = restRequisitionService.submitReport(createReport(programDataForm), userId);
    syncUpHashRepository.save(requestBodyData.getSyncUpHash());
    return requisition;
  }

  public ProgramDataForm convertRequestBodyDataToProgramDataForm(ProgramDataFormDTO requestBodyData, long userId, Facility facility) {
    SupplementalProgram supplementalProgram = supplementalProgramMapper.getSupplementalProgramByCode(requestBodyData.getProgramCode());
    ProgramDataForm programDataForm = new ProgramDataForm(facility, supplementalProgram, requestBodyData.getPeriodBegin(),
        requestBodyData.getPeriodEnd(), requestBodyData.getSubmittedTime(), requestBodyData.getObservation());
    programDataForm.setCreatedBy(userId);
    programDataForm.setModifiedBy(userId);

    programDataForm.setProgramDataItems(new ArrayList<ProgramDataItem>());
    for (ProgramDataFormItemDTO programDataFormItemDTO : requestBodyData.getProgramDataFormItems()) {
      ProgramDataColumn programDataColumn = programDataColumnMapper.getColumnByCode(programDataFormItemDTO.getColumnCode());
      if (programDataColumn == null) {
        throw new DataException("error.wrong.program.column");
      }
      ProgramDataItem programDataItem = new ProgramDataItem(programDataForm, programDataFormItemDTO.getName(),
          programDataColumn, programDataFormItemDTO.getValue());
      programDataForm.getProgramDataItems().add(programDataItem);
    }
    programDataForm.setProgramDataFormBasicItems(new ArrayList<ProgramDataFormBasicItem>());
    if (null != requestBodyData.getProgramDataFormBasicItems()) {
      for (ProgramDataFormBasicItemDTO programDataFormBasicItemDTO : requestBodyData.getProgramDataFormBasicItems()) {
        ProgramDataFormBasicItem programDataFormBasicItem = new ProgramDataFormBasicItem(programDataFormBasicItemDTO.getProductCode(),
                programDataFormBasicItemDTO.getBeginningBalance(),
                programDataFormBasicItemDTO.getQuantityReceived(),
                programDataFormBasicItemDTO.getQuantityDispensed(),
                programDataFormBasicItemDTO.getTotalLossesAndAdjustments(),
                programDataFormBasicItemDTO.getStockInHand(),
                programDataFormBasicItemDTO.getExpirationDate(),
                programDataForm);
        programDataForm.getProgramDataFormBasicItems().add(programDataFormBasicItem);
      }
    }
    programDataForm.setObservation(requestBodyData.getObservation());
    programDataForm.setProgramDataFormSignatures(requestBodyData.getProgramDataFormSignatures());
    return programDataForm;
  }

  public List<ProgramDataFormDTO> getProgramDataFormsByFacility(Long facilityId) {
    return FluentIterable.from(programDataRepository.getProgramDataFormsByFacilityId(facilityId)).transform(new Function<ProgramDataForm, ProgramDataFormDTO>() {
      @Override
      public ProgramDataFormDTO apply(ProgramDataForm input) {
        return ProgramDataFormDTO.prepareForRest(input);
      }
    }).toList();
  }

  public Report createReport(ProgramDataForm programDataForm) {
    Report report = new Report();
    report.setAgentCode(programDataForm.getFacility().getCode());
    if (null != programDataForm.getSupplementalProgram()) {
      report.setProgramCode(programDataForm.getSupplementalProgram().getCode());
    }
    report.setEmergency(false);
    report.setClientSubmittedTime(DateUtil.formatDate(programDataForm.getSubmittedTime()));
    report.setActualPeriodEndDate(DateUtil.formatDate(programDataForm.getEndDate()));
    report.setActualPeriodStartDate(DateUtil.formatDate(programDataForm.getStartDate()));
    report.setRnrSignatures(programDataForm.getProgramDataFormSignatures());
    report.setProgramDataFormId(programDataForm.getId());
    report.setProducts(rnrLineItems(programDataForm));
    report.setRegimens(regimens(programDataForm));
    report.setClientSubmittedNotes(programDataForm.getObservation());
    report.setProgramCode("TEST_KIT");
    report.setServiceLineItems(converterServiceLineItems(programDataForm));

    return report;
  }

  private List<ServiceLineItem> converterServiceLineItems(ProgramDataForm programDataForm) {
    List<ServiceLineItem> serviceLineItems = new ArrayList<>();
    List<ProgramDataItem> programDataItems = programDataForm.getProgramDataItems();
    List<org.openlmis.rnr.domain.Service> services = serviceRepository.getAll();
    List<ProgramDataColumn> programDataColumns = programDataColumnRepository.getAll();
    if(CollectionUtils.isNotEmpty(programDataItems)) {
      for(ProgramDataItem programDataItem : programDataItems) {
        ServiceLineItem serviceLineItem = new ServiceLineItem();
        serviceLineItem.setServiceId(getServiceId(services, programDataItem));
        serviceLineItem.setProgramDataColumnId(getProgramDateColumnId(programDataColumns, programDataItem));
        serviceLineItem.setValue(programDataItem.getValue());
        serviceLineItems.add(serviceLineItem);
      }

    }
    return serviceLineItems;
  }

  private Long getProgramDateColumnId(List<ProgramDataColumn> programDataColumns, ProgramDataItem programDataItem) {
    for(ProgramDataColumn programDataColumn : programDataColumns) {
      if(programDataColumn.getCode().equals(programDataItem.getProgramDataColumn().getCode())) {
        return programDataColumn.getId();
      }
    }
    throw new DataException("Program data not existed!");
  }

  private Long getServiceId(List<org.openlmis.rnr.domain.Service> services, ProgramDataItem programDataItem) {
    for(org.openlmis.rnr.domain.Service service : services) {
      if(service.getName().equals(programDataItem.getName())) {
        return service.getId();
      }
    }
    throw new DataException("Service not existed!");
  }

  private List<RnrLineItem> rnrLineItems(ProgramDataForm programDataForm) {
    List<RnrLineItem> rnrLineItems = new ArrayList<RnrLineItem>();
    if (null != programDataForm.getProgramDataFormBasicItems()) {
      rnrLineItems = FluentIterable.from(programDataForm.getProgramDataFormBasicItems())
              .transform(new Function<ProgramDataFormBasicItem, RnrLineItem>() {
                @Override
                public RnrLineItem apply(ProgramDataFormBasicItem programDataFormBasicItem) {
                  RnrLineItem rnrLineItem = new RnrLineItem();
                  rnrLineItem.setProductCode(programDataFormBasicItem.getProductCode());
                  rnrLineItem.setBeginningBalance(programDataFormBasicItem.getBeginningBalance());
                  rnrLineItem.setQuantityReceived(programDataFormBasicItem.getQuantityReceived());
                  rnrLineItem.setQuantityDispensed(programDataFormBasicItem.getQuantityDispensed());
                  rnrLineItem.setTotalLossesAndAdjustments(programDataFormBasicItem.getTotalLossesAndAdjustments());
                  rnrLineItem.setStockInHand(programDataFormBasicItem.getStockInHand());
                  rnrLineItem.setExpirationDate(programDataFormBasicItem.getExpirationDate());
                  return rnrLineItem;
                }
              }).toList();
    }
    return rnrLineItems;
  }

  private List<RegimenLineItemForRest> regimens(ProgramDataForm programDataForm) {

    Map<String, RegimenLineItemForRest> columnCodesMap = columnCodesMap();

    for (ProgramDataItem programDataItem : programDataForm.getProgramDataItems()) {
      String columnCode = programDataItem.getProgramDataColumn().getCode();
      if (!StringUtils.equalsIgnoreCase(columnCode, "APES")
              && columnCodesMap.containsKey(columnCode)) {
        RegimenLineItemForRest regimenLineItemForRest = columnCodesMap.get(columnCode);
        regimenLineItemForRest.setPatientsOnTreatment(regimenLineItemForRest.getPatientsOnTreatment()
                + programDataItem.getValue().intValue());
      }
    }
    return new ArrayList<>(columnCodesMap.values());
  }

  private Map<String, RegimenLineItemForRest> columnCodesMap() {
    Map<String, RegimenLineItemForRest> columnCodesMap = new HashMap<String, RegimenLineItemForRest>();
    columnCodesMap.put("CONSUME_HIVDETERMINE", regimenLineItemForRest("HIV Determine Consumo"));
    columnCodesMap.put("UNJUSTIFIED_HIVDETERMINE", regimenLineItemForRest("HIV Determine Injustificado"));
    columnCodesMap.put("POSITIVE_HIVDETERMINE", regimenLineItemForRest("HIV Determine Positivos +"));
    columnCodesMap.put("CONSUME_HIVUNIGOLD", regimenLineItemForRest("HIV Unigold Consumo"));
    columnCodesMap.put("UNJUSTIFIED_HIVUNIGOLD", regimenLineItemForRest("HIV Unigold Injustificado"));
    columnCodesMap.put("POSITIVE_HIVUNIGOLD", regimenLineItemForRest("HIV Unigold Positivos +"));
    columnCodesMap.put("POSITIVE_MALARIA", regimenLineItemForRest("Malaria Teste Positivos +"));
    columnCodesMap.put("CONSUME_MALARIA", regimenLineItemForRest("Malaria Teste Rápido Consumo"));
    columnCodesMap.put("UNJUSTIFIED_MALARIA", regimenLineItemForRest("Malaria Teste Rápido Injustificado"));
    columnCodesMap.put("POSITIVE_SYPHILLIS", regimenLineItemForRest("Sífilis Teste Positivos +"));
    columnCodesMap.put("CONSUME_SYPHILLIS", regimenLineItemForRest("Sífilis Teste Rápido Consumo"));
    columnCodesMap.put("UNJUSTIFIED_SYPHILLIS", regimenLineItemForRest("Sífilis Teste Rápido Injustificado"));

    return columnCodesMap;
  }

  private RegimenLineItemForRest regimenLineItemForRest(String regimenName) {
    RegimenLineItemForRest regimenLineItemForRest = new RegimenLineItemForRest();
    regimenLineItemForRest.setName(regimenName);
    regimenLineItemForRest.setPatientsOnTreatment(0);
    regimenLineItemForRest.setCategoryName("Adults");
    return regimenLineItemForRest;
  }


}
