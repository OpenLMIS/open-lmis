/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.transformer.budget.BudgetLineItemTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.openlmis.core.dto.BudgetLineItemDTO.populate;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

/**
 * Exposes the services for processing Budget File.
 */

@Component
@MessageEndpoint
@NoArgsConstructor
public class BudgetFileProcessor {

  private static Logger logger = Logger.getLogger(BudgetFileProcessor.class);

  @Autowired
  private BudgetFileTemplateService budgetFileTemplateService;

  @Autowired
  private BudgetLineItemTransformer budgetLineItemTransformer;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProgramService programService;

  @Autowired
  private BudgetFileService budgetFileService;

  @Autowired
  private ProcessingScheduleService processingScheduleService;

  @Autowired
  private BudgetLineItemService budgetLineItemService;

  @Autowired
  private BudgetFilePostProcessHandler budgetFilePostProcessHandler;

  private MessageService messageService = MessageService.getRequestInstance();

  @Autowired
  private ApplicationContext applicationContext;

  private BudgetFileProcessor getSpringProxy() {
    return applicationContext.getBean(this.getClass());
  }

  public void process(Message message) throws Exception {
    File budgetFile = (File) message.getPayload();

    logger.debug("processing Budget File " + budgetFile.getName());

    BudgetFileInfo budgetFileInfo = saveBudgetFile(budgetFile, false);

    Boolean processingError = getSpringProxy().processBudgetFile(budgetFile, budgetFileInfo);

    budgetFileInfo.setProcessingError(processingError);
    budgetFilePostProcessHandler.process(budgetFileInfo, budgetFile);
  }

  @Transactional
  public Boolean processBudgetFile(File budgetFile, BudgetFileInfo budgetFileInfo) throws Exception {

    Boolean processingError = false;
    EDIFileTemplate budgetFileTemplate = budgetFileTemplateService.get();

    ICsvListReader listReader = new CsvListReader(new FileReader(budgetFile), STANDARD_PREFERENCE);

    if (budgetFileTemplate.getConfiguration().isHeaderInFile()) {
      listReader.getHeader(true);
    }

    List<String> csvRow;
    Integer rowNumber;
    Collection<EDIFileColumn> includedColumns = budgetFileTemplate.filterIncludedColumns();

    while ((csvRow = listReader.read()) != null) {

      BudgetLineItemDTO budgetLineItemDTO = populate(csvRow, includedColumns);
      try {

        budgetLineItemDTO.checkMandatoryFields();
        rowNumber = listReader.getRowNumber() - budgetFileTemplate.getRowOffset();

        Facility facility = getValidatedFacility(budgetLineItemDTO.getFacilityCode(), rowNumber);
        Program program = getValidatedProgram(budgetLineItemDTO.getProgramCode(), rowNumber);

        String dateFormat = budgetFileTemplate.getDateFormatForColumn("periodStartDate");
        BudgetLineItem budgetLineItem = budgetLineItemTransformer.transform(budgetLineItemDTO, dateFormat, rowNumber);

        ProcessingPeriod processingPeriod = getValidatedPeriod(facility, program, budgetLineItem.getPeriodDate(), rowNumber);

        budgetLineItem.setFacilityId(facility.getId());
        budgetLineItem.setProgramId(program.getId());
        budgetLineItem.setPeriodId(processingPeriod.getId());
        budgetLineItem.setBudgetFileId(budgetFileInfo.getId());

        budgetLineItemService.save(budgetLineItem);

      } catch (DataException e) {
        processingError = true;
        String errorMessage = messageService.message(e.getOpenLmisMessage());
        logger.error(errorMessage, e);
      }
    }

    if (listReader.getRowNumber() == budgetFileTemplate.getRowOffset()) {
      logger.error(messageService.message("error.facility.code.invalid"));
      processingError = true;
    }
    return processingError;
  }

  private BudgetFileInfo saveBudgetFile(File budgetFile, Boolean processingError) {

    BudgetFileInfo budgetFileInfo = new BudgetFileInfo(budgetFile.getName(), processingError);
    budgetFileService.save(budgetFileInfo);

    return budgetFileInfo;
  }

  private ProcessingPeriod getValidatedPeriod(Facility facility, Program program, Date date, Integer rowNumber) {
    ProcessingPeriod periodForDate = processingScheduleService.getPeriodForDate(facility, program, date);
    if (periodForDate == null) {
      throw new DataException("budget.start.date.invalid", date, facility.getCode(), program.getCode(), rowNumber);
    }
    return periodForDate;
  }

  private Program getValidatedProgram(String programCode, Integer rowNumber) {
    Program program = programService.getByCode(programCode);
    if (program == null) {
      throw new DataException("budget.program.code.invalid", programCode, rowNumber);
    }
    return program;
  }

  private Facility getValidatedFacility(String facilityCode, Integer rowNumber) {
    Facility facility = new Facility();
    facility.setCode(facilityCode);
    if ((facility = facilityService.getByCode(facility)) == null) {
      throw new DataException("budget.facility.code.invalid", facilityCode, rowNumber);
    }
    return facility;
  }
}
