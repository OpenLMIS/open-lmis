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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.transformer.budget.BudgetLineItemTransformer;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.supercsv.io.CsvListReader;

import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({BudgetFileProcessor.class, BudgetLineItemDTO.class})
@Category(UnitTests.class)
public class BudgetFileProcessorTest {

  @Mock
  BudgetFileService budgetFileService;

  @Mock
  BudgetFileTemplateService budgetFileTemplateService;

  @Mock
  BudgetLineItemTransformer transformer;

  @Mock
  FacilityService facilityService;

  @Mock
  private ProgramService programService;

  @Mock
  private ProcessingScheduleService processingScheduleService;

  @Mock
  private BudgetLineItemService budgetLineItemService;

  @Mock
  BudgetFilePostProcessHandler budgetFilePostProcessHandler;

  @Mock
  MessageService messageService;

  @InjectMocks
  BudgetFileProcessor budgetFileProcessor;

  @Mock
  ApplicationContext applicationContext;

  private Message message;
  private EDIFileTemplate ediFileTemplate;
  private EDIConfiguration configuration;
  private String dateFormat;

  private EDIFileColumn periodDateColumn;
  private EDIFileColumn defaultEDIColumn;
  private List<String> csvRow;
  private File budgetFile;
  private CsvListReader listReader;

  //TODO: refactor these unit tests to include mocks instead of real objects

  @Before
  public void setUp() throws Exception {
    budgetFile = mock(File.class);
    message = mock(Message.class);
    when(message.getPayload()).thenReturn(budgetFile);
    listReader = mock(CsvListReader.class);
    FileReader fileReader = mock(FileReader.class);
    whenNew(FileReader.class).withArguments(budgetFile).thenReturn(fileReader);
    whenNew(CsvListReader.class).withArguments(fileReader, STANDARD_PREFERENCE).thenReturn(listReader);
    csvRow = asList("F10", "HIV", "2013-12-10", "345.45", "My good notes");
    when(listReader.read()).thenReturn(csvRow).thenReturn(null);

    ediFileTemplate = new EDIFileTemplate();
    dateFormat = "mm/dd/yy";
    periodDateColumn = new EDIFileColumn("periodStartDate", "label.date", true, true, 2, dateFormat);
    defaultEDIColumn = new EDIFileColumn("facilityCode", "label.facility.code", true, true, 1, "");
    ediFileTemplate.setColumns(asList(defaultEDIColumn, periodDateColumn));
    configuration = new EDIConfiguration(false);
    ediFileTemplate.setConfiguration(configuration);
    when(budgetFileTemplateService.get()).thenReturn(ediFileTemplate);
    when(applicationContext.getBean(BudgetFileProcessor.class)).thenReturn(budgetFileProcessor);
  }

  @Test
  public void shouldIgnoreFirstLineAsHeadersIfIncludedInTemplate() throws Exception {
    configuration.setHeaderInFile(true);
    when(listReader.getRowNumber()).thenReturn(1);

    budgetFileProcessor.process(message);

    verify(listReader).getHeader(true);
  }

  @Test
  public void shouldNotReadHeadersIfNotIncluded() throws Exception {
    configuration.setHeaderInFile(false);
    when(listReader.getRowNumber()).thenReturn(1);

    budgetFileProcessor.process(message);

    verify(listReader, never()).getHeader(true);
  }

  @Test
  public void shouldSaveBudgetFileLineItem() throws Exception {
    configuration.setHeaderInFile(true);
    when(listReader.getRowNumber()).thenReturn(2, 3);
    String budgetFileName = "BudgetFileName";
    when(budgetFile.getName()).thenReturn(budgetFileName);
    BudgetFileInfo budgetFileInfo = new BudgetFileInfo();
    budgetFileInfo.setId(1L);
    whenNew(BudgetFileInfo.class).withArguments(budgetFileName, false).thenReturn(budgetFileInfo);
    mockStatic(BudgetLineItemDTO.class);
    BudgetLineItemDTO lineItemDTO = mock(BudgetLineItemDTO.class);
    when(lineItemDTO.getFacilityCode()).thenReturn("F10");
    when(lineItemDTO.getProgramCode()).thenReturn("HIV");
    when(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns())).thenReturn(lineItemDTO);
    Facility facility = new Facility();
    facility.setCode("F10");
    when(facilityService.getByCode(facility)).thenReturn(facility);
    Program program = new Program();
    when(programService.getByCode("HIV")).thenReturn(program);
    BudgetLineItem budgetLineItem = mock(BudgetLineItem.class);
    ProcessingPeriod processingPeriod = mock(ProcessingPeriod.class);
    when(processingScheduleService.getPeriodForDate(facility, program, budgetLineItem.getPeriodDate())).thenReturn(processingPeriod);
    when(transformer.transform(lineItemDTO, dateFormat, 1)).thenReturn(budgetLineItem);

    budgetFileProcessor.process(message);

    verify(transformer).transform(lineItemDTO, dateFormat, 1);
    verify(budgetLineItem).setBudgetFileId(budgetFileInfo.getId());
    verify(budgetLineItemService).save(budgetLineItem);
    verify(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns()));
  }

  @Test
  public void shouldCreateBudgetLineItemWithDateAsNullIfNotIncludedInFile() throws Exception {
    configuration.setHeaderInFile(true);
    when(listReader.getRowNumber()).thenReturn(2, 3);
    BudgetLineItemDTO lineItemDTO = mock(BudgetLineItemDTO.class);
    mockStatic(BudgetLineItemDTO.class);
    List<String> csvRow = asList("F10", "HIV", "2013-12-10", "345.45", "My good notes");
    when(BudgetLineItemDTO.populate(csvRow, asList(defaultEDIColumn))).thenReturn(lineItemDTO);
    when(lineItemDTO.getFacilityCode()).thenReturn("F10");
    when(lineItemDTO.getProgramCode()).thenReturn("HIV");
    Facility facility = new Facility();
    facility.setCode("F10");
    when(facilityService.getByCode(facility)).thenReturn(facility);
    Program program = new Program();
    when(programService.getByCode("HIV")).thenReturn(program);
    BudgetLineItem budgetLineItem = new BudgetLineItem();
    Date date = new Date();
    budgetLineItem.setPeriodDate(date);
    when(transformer.transform(lineItemDTO, null, 1)).thenReturn(budgetLineItem);
    ProcessingPeriod processingPeriod = new ProcessingPeriod();
    when(processingScheduleService.getPeriodForDate(facility, program, date)).thenReturn(processingPeriod);
    periodDateColumn.setInclude(false);

    budgetFileProcessor.process(message);

    verify(transformer).transform(lineItemDTO, null, 1);
    verify(BudgetLineItemDTO.populate(csvRow, asList(defaultEDIColumn)));
  }

  @Test
  public void shouldNotProcessARecordIfMandatoryFieldIsMissing() throws Exception {
    configuration.setHeaderInFile(true);
    when(listReader.getRowNumber()).thenReturn(1).thenReturn(2);
    BudgetLineItemDTO lineItemDTO = mock(BudgetLineItemDTO.class);
    mockStatic(BudgetLineItemDTO.class);
    when(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns())).thenReturn(lineItemDTO);
    doThrow(new DataException("Missing mandatory Fields")).when(lineItemDTO).checkMandatoryFields();

    budgetFileProcessor.process(message);

    verify(lineItemDTO).checkMandatoryFields();
    verify(transformer, never()).transform(lineItemDTO, null, 1);
    verify(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns()));
  }

  @Test
  public void shouldNotProcessTheRecordIfFacilityCodeIsInvalid() throws Exception {
    configuration.setHeaderInFile(true);
    when(listReader.getRowNumber()).thenReturn(1).thenReturn(2);
    BudgetLineItemDTO lineItemDTO = mock(BudgetLineItemDTO.class);
    mockStatic(BudgetLineItemDTO.class);
    when(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns())).thenReturn(lineItemDTO);
    when(lineItemDTO.getFacilityCode()).thenReturn("F899");
    Facility facility = new Facility();
    facility.setCode("F899");
    when(facilityService.getByCode(facility)).thenReturn(null);

    budgetFileProcessor.process(message);

    verify(lineItemDTO).checkMandatoryFields();
    verify(transformer, never()).transform(lineItemDTO, null, 1);
    verify(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns()));
  }

  @Test
  public void shouldNotProcessTheRecordIfProgramCodeIsInvalid() throws Exception {
    configuration.setHeaderInFile(true);
    when(listReader.getRowNumber()).thenReturn(1).thenReturn(2);
    BudgetLineItemDTO lineItemDTO = mock(BudgetLineItemDTO.class);
    mockStatic(BudgetLineItemDTO.class);
    when(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns())).thenReturn(lineItemDTO);
    when(lineItemDTO.getProgramCode()).thenReturn("P12345");
    when(lineItemDTO.getFacilityCode()).thenReturn("F11");
    Facility facility = new Facility();
    facility.setCode("F11");
    when(facilityService.getByCode(facility)).thenReturn(facility);
    when(programService.getByCode("P12345")).thenReturn(null);

    budgetFileProcessor.process(message);

    verify(lineItemDTO).checkMandatoryFields();
    verify(transformer, never()).transform(lineItemDTO, null, 1);
    verify(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns()));
  }

  @Test
  public void shouldNotProcessRecordIfPeriodDateDoesNotBelongToAnyPeriod() throws Exception {
    when(listReader.read()).thenReturn(csvRow).thenReturn(null);
    when(listReader.getRowNumber()).thenReturn(1).thenReturn(2);
    configuration.setHeaderInFile(true);
    BudgetLineItemDTO lineItemDTO = mock(BudgetLineItemDTO.class);
    mockStatic(BudgetLineItemDTO.class);
    when(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns())).thenReturn(lineItemDTO);
    Facility facility = new Facility();
    facility.setCode("F10");
    when(lineItemDTO.getFacilityCode()).thenReturn("F10'");
    when(facilityService.getByCode(facility)).thenReturn(facility);
    Program program = new Program();
    when(programService.getByCode("HIV")).thenReturn(program);
    BudgetLineItem budgetLineItem = new BudgetLineItem();
    budgetLineItem.setPeriodDate(new Date());
    when(transformer.transform(lineItemDTO, dateFormat, 1)).thenReturn(budgetLineItem);
    when(processingScheduleService.getPeriodForDate(facility, program, budgetLineItem.getPeriodDate())).thenReturn(null);

    budgetFileProcessor.process(message);

    verify(lineItemDTO).checkMandatoryFields();
    assertThat(budgetLineItem.getPeriodId(), is(nullValue()));
  }

  @Test
  public void shouldSaveBudgetFileInfo() throws Exception {
    String budgetFileName = "BudgetFileName";
    when(listReader.getRowNumber()).thenReturn(1);
    when(budgetFile.getName()).thenReturn(budgetFileName);
    BudgetFileInfo budgetFileInfo = new BudgetFileInfo();
    whenNew(BudgetFileInfo.class).withArguments(budgetFileName, false).thenReturn(budgetFileInfo);
    when(listReader.read()).thenReturn(null);

    budgetFileProcessor.process(message);

    verify(budgetFileService).save(budgetFileInfo);
  }

  @Test
  public void shouldDisplayErrorIfFileIsBlank() throws Exception {
    configuration.setHeaderInFile(false);
    when(listReader.read()).thenReturn(null);
    when(listReader.getRowNumber()).thenReturn(0);

    budgetFileProcessor.process(message);

    verify(messageService).message("error.facility.code.invalid");
  }

  @Test
  public void shouldDisplayErrorIfFileIsBlankWithOnlyHeader() throws Exception {
    configuration.setHeaderInFile(true);
    when(listReader.read()).thenReturn(null);
    when(listReader.getRowNumber()).thenReturn(1);

    budgetFileProcessor.process(message);

    verify(messageService).message("error.facility.code.invalid");
  }

}
