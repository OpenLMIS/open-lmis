package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.*;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.transformer.budget.BudgetLineItemTransformer;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.Message;
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

  @InjectMocks
  BudgetFileProcessor budgetFileProcessor;
  private Message message;
  private EDIFileTemplate ediFileTemplate;
  private EDIConfiguration configuration;
  private CsvListReader reader;
  private String datePattern;
  private EDIFileColumn periodDateColumn;

  private EDIFileColumn defaultEDIColumn;

  @Before
  public void setUp() throws Exception {
    File budgetFile = new ClassPathResource("valid_budget_file.csv").getFile();

    message = mock(Message.class);
    when(message.getPayload()).thenReturn(budgetFile);
    ediFileTemplate = new EDIFileTemplate();
    datePattern = "mm/dd/yy";
    periodDateColumn = new EDIFileColumn("periodStartDate", "label.date", true, true, 2, datePattern);
    defaultEDIColumn = new EDIFileColumn("facilityCode", "label.facility.code", true, true, 1, "");
    ediFileTemplate.setColumns(asList(defaultEDIColumn, periodDateColumn));
    configuration = new EDIConfiguration(false);
    ediFileTemplate.setConfiguration(configuration);
    when(budgetFileTemplateService.get()).thenReturn(ediFileTemplate);

    reader = spy(new CsvListReader(new FileReader(budgetFile), STANDARD_PREFERENCE));
    whenNew(CsvListReader.class).withAnyArguments().thenReturn(reader);
  }

  @Test
  public void shouldIgnoreFirstLineAsHeadersIfIncludedInTemplate() throws Exception {
    configuration.setHeaderInFile(true);

    budgetFileProcessor.process(message);

    verify(reader).getHeader(true);
  }

  @Test
  public void shouldNotReadHeadersIfNotIncluded() throws Exception {
    budgetFileProcessor.process(message);

    verify(reader, never()).getHeader(true);
  }

  @Test
  public void shouldCreateBudgetFileLineItem() throws Exception {
    configuration.setHeaderInFile(true);
    mockStatic(BudgetLineItemDTO.class);
    BudgetLineItemDTO lineItemDTO = mock(BudgetLineItemDTO.class);
    when(lineItemDTO.getFacilityCode()).thenReturn("F10");
    when(lineItemDTO.getProgramCode()).thenReturn("HIV");
    List<String> csvRow = asList("F10", "HIV", "2013-12-10", "345.45", "My good notes");
    when(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns())).thenReturn(lineItemDTO);
    Facility facility = new Facility();
    facility.setCode("F10");
    when(facilityService.getByCode(facility)).thenReturn(facility);
    Program program = new Program();
    when(programService.getByCode("HIV")).thenReturn(program);

    budgetFileProcessor.process(message);

    verify(transformer).transform(lineItemDTO, datePattern);
    verify(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns()));
  }

  @Test
  public void shouldCreateBudgetLineItemWithDateAsNullIfNotIncludedInFile() throws Exception {
    configuration.setHeaderInFile(true);
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

    periodDateColumn.setInclude(false);

    budgetFileProcessor.process(message);

    verify(transformer).transform(lineItemDTO, null);
    verify(BudgetLineItemDTO.populate(csvRow, asList(defaultEDIColumn)));

  }


  @Test
  public void shouldNotProcessARecordIfMandatoryFieldIsMissing() throws Exception {

    File budgetFile = mock(File.class);
    when(message.getPayload()).thenReturn(budgetFile);

    CsvListReader listReader = mock(CsvListReader.class);
    FileReader fileReader = mock(FileReader.class);
    whenNew(FileReader.class).withArguments(budgetFile).thenReturn(fileReader);
    whenNew(CsvListReader.class).withArguments(fileReader, STANDARD_PREFERENCE).thenReturn(listReader);
    List<String> csvRow = asList("", "HIV", "2013-12-10", "345.45", "My good notes");
    when(listReader.read()).thenReturn(csvRow).thenReturn(null);
    configuration.setHeaderInFile(true);
    BudgetLineItemDTO lineItemDTO = mock(BudgetLineItemDTO.class);
    mockStatic(BudgetLineItemDTO.class);
    when(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns())).thenReturn(lineItemDTO);
    doThrow(new DataException("Missing mandatory Fields")).when(lineItemDTO).checkMandatoryFields();

    budgetFileProcessor.process(message);

    verify(lineItemDTO).checkMandatoryFields();
    verify(transformer, never()).transform(lineItemDTO, null);
    verify(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns()));
  }

  @Test
  public void shouldNotProcessTheRecordIfFacilityCodeIsInvalid() throws Exception {
    File budgetFile = mock(File.class);
    when(message.getPayload()).thenReturn(budgetFile);

    CsvListReader listReader = mock(CsvListReader.class);
    FileReader fileReader = mock(FileReader.class);
    whenNew(FileReader.class).withArguments(budgetFile).thenReturn(fileReader);
    whenNew(CsvListReader.class).withArguments(fileReader, STANDARD_PREFERENCE).thenReturn(listReader);
    List<String> csvRow = asList("F10", "HIV", "2013-12-10", "345.45", "My good notes");
    when(listReader.read()).thenReturn(csvRow).thenReturn(null);
    configuration.setHeaderInFile(true);
    BudgetLineItemDTO lineItemDTO = mock(BudgetLineItemDTO.class);
    mockStatic(BudgetLineItemDTO.class);
    when(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns())).thenReturn(lineItemDTO);
    Facility facility = new Facility();
    facility.setCode("F10");
    when(facilityService.getByCode(facility)).thenThrow(new DataException("Invalid facility code"));

    budgetFileProcessor.process(message);

    verify(lineItemDTO).checkMandatoryFields();
    verify(transformer, never()).transform(lineItemDTO, null);
    verify(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns()));
  }

  @Test
  public void shouldNotProcessTheRecordIfProgramCodeIsInvalid() throws Exception {
    File budgetFile = mock(File.class);
    when(message.getPayload()).thenReturn(budgetFile);

    CsvListReader listReader = mock(CsvListReader.class);
    FileReader fileReader = mock(FileReader.class);
    whenNew(FileReader.class).withArguments(budgetFile).thenReturn(fileReader);
    whenNew(CsvListReader.class).withArguments(fileReader, STANDARD_PREFERENCE).thenReturn(listReader);
    List<String> csvRow = asList("F10", "HIV", "2013-12-10", "345.45", "My good notes");
    when(listReader.read()).thenReturn(csvRow).thenReturn(null);
    configuration.setHeaderInFile(true);
    BudgetLineItemDTO lineItemDTO = mock(BudgetLineItemDTO.class);
    mockStatic(BudgetLineItemDTO.class);
    when(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns())).thenReturn(lineItemDTO);
    Facility facility = new Facility();
    facility.setCode("F10");
    when(facilityService.getByCode(facility)).thenReturn(facility);
    when(programService.getByCode("HIV")).thenThrow(new DataException("Invalid program code"));

    budgetFileProcessor.process(message);

    verify(lineItemDTO).checkMandatoryFields();
    verify(transformer, never()).transform(lineItemDTO, null);
    verify(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns()));
  }

  @Test
  public void shouldNotProcessRecordIfPeriodDateDoesNotBelongToAnyPeriod() throws Exception {
    File budgetFile = mock(File.class);
    when(message.getPayload()).thenReturn(budgetFile);

    CsvListReader listReader = mock(CsvListReader.class);
    FileReader fileReader = mock(FileReader.class);
    whenNew(FileReader.class).withArguments(budgetFile).thenReturn(fileReader);
    whenNew(CsvListReader.class).withArguments(fileReader, STANDARD_PREFERENCE).thenReturn(listReader);
    List<String> csvRow = asList("F10", "HIV", "2013-12-10", "345.45", "My good notes");
    when(listReader.read()).thenReturn(csvRow).thenReturn(null);
    configuration.setHeaderInFile(true);
    BudgetLineItemDTO lineItemDTO = mock(BudgetLineItemDTO.class);
    mockStatic(BudgetLineItemDTO.class);
    when(BudgetLineItemDTO.populate(csvRow, ediFileTemplate.getColumns())).thenReturn(lineItemDTO);
    Facility facility = new Facility();
    facility.setCode("F10");
    Program program = new Program();
    when(facilityService.getByCode(facility)).thenReturn(facility);
    when(programService.getByCode("HIV")).thenReturn(program);
    BudgetLineItem budgetLineItem = new BudgetLineItem();
    budgetLineItem.setPeriodDate(new Date());
    when(transformer.transform(lineItemDTO, datePattern)).thenReturn(budgetLineItem);
    when(processingScheduleService.getPeriodForDate(facility, program, budgetLineItem.getPeriodDate())).thenReturn(null);

    budgetFileProcessor.process(message);

    verify(lineItemDTO).checkMandatoryFields();
    assertThat(budgetLineItem.getPeriodId(), is(nullValue()));

  }
}
