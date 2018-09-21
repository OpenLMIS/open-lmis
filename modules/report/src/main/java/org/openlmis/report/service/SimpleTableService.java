package org.openlmis.report.service;

import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.report.mapper.RequisitionReportsMapper;
import org.openlmis.report.model.dto.MonthlyReortProgramType;
import org.openlmis.report.model.dto.RequisitionDTO;
import org.openlmis.report.model.params.NonSubmittedRequisitionReportsParam;
import org.openlmis.report.model.params.RequisitionReportsParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimpleTableService {
    @Autowired
    private RequisitionReportsMapper requisitionReportsMapper;

    @Autowired
    private FacilityMapper facilityMapper;
    protected static Logger logger = LoggerFactory.getLogger(SimpleTableService.class);

    public List<RequisitionDTO> getRequisitions(RequisitionReportsParam filterCriteria) {
        List<RequisitionDTO> requisitions = new ArrayList<>();
        requisitions.addAll(getSubmittedRequisitions(filterCriteria));
        requisitions.addAll(getUnSubmittedRequisitions(filterCriteria));

        return requisitions;
    }

    private List<RequisitionDTO> getSubmittedRequisitions(RequisitionReportsParam filterCriteria) {
        List<RequisitionDTO> requisitions = requisitionReportsMapper.getSubmittedRequisitionList(filterCriteria);
        return requisitions;
    }

    private List<RequisitionDTO> getUnSubmittedRequisitions(RequisitionReportsParam filterCriteria) {
        List<RequisitionDTO> requisitions = new ArrayList<>();
        List<Integer> facilityIds = getFacilityIds(filterCriteria);
        NonSubmittedRequisitionReportsParam nonSubmittedRequisitionReportsParam;
        for(Integer facilityId : facilityIds) {
            for(MonthlyReortProgramType programType : MonthlyReortProgramType.values()) {
                Integer programId = programType.getProgramId();

                if(filterCriteria.getProgramIds().contains(programId)) {
                    nonSubmittedRequisitionReportsParam = NonSubmittedRequisitionReportsParam.builder()
                            .startTime(filterCriteria.getStartTime())
                            .endTime(filterCriteria.getEndTime())
                            .facilityId(facilityId)
                            .programId(programId)
                            .build();
                    requisitions.addAll(requisitionReportsMapper.getUnSubmittedRequisitionList(nonSubmittedRequisitionReportsParam));
                }
            }
        }

        return requisitions;
    }

    private List<Integer> getFacilityIds(RequisitionReportsParam filterCriteria) {
        List<Integer> facilityIds = new ArrayList<>();

        if(null != filterCriteria.getFacilityId()) {
            facilityIds.add(filterCriteria.getFacilityId());
            return facilityIds;
        }

        if(null != filterCriteria.getDistrictId()) {
            facilityIds = facilityMapper.getFacilityIdByDistrictId(filterCriteria.getDistrictId());
            return facilityIds;
        }

        if(null != filterCriteria.getProvinceId()) {
            facilityIds = facilityMapper.getFacilityIdByProvinceId(filterCriteria.getProvinceId());
            return facilityIds;
        }

        facilityIds = facilityMapper.getAllFacilityIds();
        return facilityIds;
    }
}