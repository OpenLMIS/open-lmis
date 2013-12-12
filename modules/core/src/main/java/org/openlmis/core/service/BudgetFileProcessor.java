package org.openlmis.core.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.transformer.budget.BudgetLineItemTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

@Service
public class BudgetFileProcessor {

  private static Logger logger = Logger.getLogger(BudgetFileProcessor.class);

  @Autowired
  BudgetFileTemplateService budgetFileTemplateService;

  @Autowired
  BudgetLineItemTransformer budgetLineItemTransformer;

  @Autowired
  FacilityService facilityService;

  @Autowired
  ProgramService programService;
  private ProcessingScheduleService processingScheduleService;


  public void process(Message message) throws IOException {

    File file = (File) message.getPayload();

    EDIFileTemplate budgetFileTemplate = budgetFileTemplateService.get();

    ICsvListReader listReader = new CsvListReader(new FileReader(file), STANDARD_PREFERENCE);

    if (budgetFileTemplate.getConfiguration().isHeaderInFile()) listReader.getHeader(true);

    List<String> csvRow;
    while ((csvRow = listReader.read()) != null) {
      Collection<EDIFileColumn> includedColumns = budgetFileTemplate.filterIncludedColumns();

      BudgetLineItemDTO budgetLineItemDTO = BudgetLineItemDTO.populate(csvRow, includedColumns);
      try {
        budgetLineItemDTO.checkMandatoryFields();
        Facility facility = validateFacility(budgetLineItemDTO.getFacilityCode());
        Program program = validateProgram(budgetLineItemDTO.getProgramCode());


        EDIFileColumn periodDateColumn = (EDIFileColumn) CollectionUtils.find(includedColumns, new Predicate() {
          @Override
          public boolean evaluate(Object o) {
            EDIFileColumn periodDateColumn = (EDIFileColumn) o;
            return periodDateColumn.getName().equals("periodStartDate") && periodDateColumn.getInclude();
          }
        });
        String datePattern = periodDateColumn == null ? null : periodDateColumn.getDatePattern();
        BudgetLineItem budgetLineItem = budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern);

        ProcessingPeriod processingPeriod = validatePeriod(facility, program, budgetLineItem.getPeriodDate());
        budgetLineItem.setPeriodId(processingPeriod.getId());


      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        continue;
      }
    }


  }

  private ProcessingPeriod validatePeriod(Facility facility, Program program, Date date) {
    ProcessingPeriod periodForDate = processingScheduleService.getPeriodForDate(facility, program, date);
    if (periodForDate == null) {
      throw new DataException("period.invalid");
    }
    return periodForDate;
  }

  private Program validateProgram(String programCode) {
    Program program = programService.getByCode(programCode);
    if (program == null) {
      throw new DataException("program.code.invalid");
    }
    return program;
  }

  private Facility validateFacility(String facilityCode) {
    Facility facility = new Facility();
    facility.setCode(facilityCode);
    if (facilityService.getByCode(facility) == null) {
      throw new DataException("error.facility.code.invalid");
    }
    return facility;
  }
}
