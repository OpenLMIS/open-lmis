package org.openlmis.rnr.service;

import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.service.EmailService;
import org.openlmis.files.excel.ExcelHandler;
import org.openlmis.files.excel.SingleListSheetExcelHandler;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.mapper.RnrMapperForSIMAM;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.service.RequisitionEmailServiceForSIMAM.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RequisitionEmailServiceForSIMAMTest {


  @Mock
  private RnrMapperForSIMAM rnrMapperForSIMAM;

  @Mock
  private EmailService emailService;

  @Mock
  private PDFGenerator PDFGenerator;

  @Mock
  ConfigurationSettingService settingService;

  @Mock
  private SingleListSheetExcelHandler singleListSheetExcelHandler;

  @Mock
  private StaticReferenceDataService staticReferenceDataService;

  private RequisitionEmailServiceForSIMAM requisitionEmailServiceForSIMAM = null;

  Rnr rnr;
  private List<Map<String, String>> dataList;
  private List<User> users;
  private List<Map<String, String>> regimenDataList;

  @Before
  public void setUp() throws Exception {
    rnr = make(a(RequisitionBuilder.defaultRequisition));
    rnr.setId(1L);

    initUsers();
    when(settingService.getConfigurationStringValue(anyString())).thenReturn("email content");
    requisitionEmailServiceForSIMAM =
            new RequisitionEmailServiceForSIMAM(rnrMapperForSIMAM, emailService, settingService,singleListSheetExcelHandler, PDFGenerator, staticReferenceDataService, "pdfPath");
  }

  private void initRnrItems(String programCode) {
    rnr.setProgram(make(a(ProgramBuilder.defaultProgram, with(ProgramBuilder.programCode, programCode))));

    Map<String, String> item1 = MapUtils.putAll(new HashMap(), new String[][]{
        {"facilityid", "156"},
        {"client_submitted_time", "12:21"},
        {"product_code", "P1"},
        {"form_program_id", "12"}
    });

    Map<String, String> item2 = MapUtils.putAll(new HashMap(), new String[][]{
        {"facilityid", "157"},
        {"client_submitted_time", "18:21"},
        {"product_code", "P2"},
        {"form_program_id", "12"}
    });

    dataList = asList(item1, item2);
  }

  private void initRegimenItems(String programCode) {
    Map<String, String> item1 = MapUtils.putAll(new HashMap(), new String[][]{
        {"program_code", programCode},
        {"regimen_name", "abc"},
        {"total", "100"},
        {"date", "10-10-2011"}
    });

    Map<String, String> item2 = MapUtils.putAll(new HashMap(), new String[][]{
        {"program_code", programCode},
        {"regimen_name", "def"},
        {"total", "300"},
        {"date", "10-10-2011"}
    });

    regimenDataList = asList(item1, item2);
  }

  private void initUsers() {
    User user1 = new User();
    user1.setEmail("em1");
    user1.setFirstName("F");
    user1.setLastName("L");
    User user2 = new User();
    user2.setEmail("em2");
    user2.setFirstName("F");
    user2.setLastName("L");
    users = new ArrayList<>();
    users.add(user1);
    users.add(user2);
  }

  @Test
  public void shouldNotEmailWhenRequisitionStatusNotAuthorized() throws Exception {
    rnr.setStatus(RnrStatus.SUBMITTED);
    requisitionEmailServiceForSIMAM.queueRequisitionEmailWithAttachment(rnr, users);
    verify(emailService, never()).insertEmailAttachmentList(anyList());
  }

  @Test
  public void shouldNotEmailWhenUsersAreEmpty() throws Exception {
    rnr.setStatus(RnrStatus.AUTHORIZED);
    requisitionEmailServiceForSIMAM.queueRequisitionEmailWithAttachment(rnr, new ArrayList<User>());
    verify(emailService, never()).insertEmailAttachmentList(anyList());
  }

  @Test
  public void shouldGetEmailDataList() throws Exception {
    rnr.setStatus(RnrStatus.AUTHORIZED);

    initRnrItems("MMIA");

    when(rnrMapperForSIMAM.getRnrItemsForSIMAMImport(rnr)).thenReturn(dataList);
    when(rnrMapperForSIMAM.getProductProgramCode("P1", 12)).thenReturn(asList("MMIA"));
    when(rnrMapperForSIMAM.getProductProgramCode("P2", 12)).thenReturn(asList("MMIA"));

    Workbook workBook = new XSSFWorkbook();
    workBook.createSheet();
    when(singleListSheetExcelHandler.readXssTemplateFile(anyString(), any(ExcelHandler.PathType.class))).thenReturn(workBook);
    when(singleListSheetExcelHandler.createXssFile(any(Workbook.class), anyString())).thenReturn("anything");

    requisitionEmailServiceForSIMAM.queueRequisitionEmailWithAttachment(rnr, users);

    verify(emailService).insertEmailAttachmentList(any(List.class));
    verify(emailService, times(2)).queueEmailMessage(any(EmailMessage.class));
  }

  @Test
  public void shouldCreateExcelWithVIARnrItems() throws MalformedURLException {
    rnr.setStatus(RnrStatus.AUTHORIZED);

    initRnrItems("VIA");
    when(rnrMapperForSIMAM.getRnrItemsForSIMAMImport(rnr)).thenReturn(dataList);
    when(rnrMapperForSIMAM.getProductProgramCode("P1", 12)).thenReturn(asList("VIA", "ESS_MEDS"));
    when(rnrMapperForSIMAM.getProductProgramCode("P2", 12)).thenReturn(asList("VIA", "MALARIA"));

    Workbook workBook = new XSSFWorkbook();
    workBook.createSheet();
    when(singleListSheetExcelHandler.readXssTemplateFile(anyString(), any(ExcelHandler.PathType.class))).thenReturn(workBook);

    requisitionEmailServiceForSIMAM.queueRequisitionEmailWithAttachment(rnr, users);

    assertEquals(SIMAM_PROGRAMS_MAP.get("ESS_MEDS"), dataList.get(0).get("program_code"));
    assertEquals(SIMAM_PROGRAMS_MAP.get("MALARIA"), dataList.get(1).get("program_code"));
    assertEquals("aP1", dataList.get(0).get("product_code"));
  }

  @Test
  public void shouldCreateExcelWithMMIARnrItems() throws MalformedURLException {
    rnr.setStatus(RnrStatus.AUTHORIZED);

    initRnrItems("MMIA");
    when(rnrMapperForSIMAM.getRnrItemsForSIMAMImport(rnr)).thenReturn(dataList);
    when(rnrMapperForSIMAM.getProductProgramCode("P1", 12)).thenReturn(asList("MMIA"));
    when(rnrMapperForSIMAM.getProductProgramCode("P2", 12)).thenReturn(asList("MMIA"));

    Workbook workBook = new XSSFWorkbook();
    workBook.createSheet();
    when(singleListSheetExcelHandler.readXssTemplateFile(anyString(), any(ExcelHandler.PathType.class))).thenReturn(workBook);

    requisitionEmailServiceForSIMAM.queueRequisitionEmailWithAttachment(rnr, users);

    assertEquals(SIMAM_PROGRAMS_MAP.get("MMIA"), dataList.get(0).get("program_code"));
  }

  @Test
  public void shouldReturnEmptyRegimenExcelWhenRegimenHasNoItem() throws MalformedURLException {
    rnr.setStatus(RnrStatus.AUTHORIZED);

    initRnrItems("MMIA");
    when(rnrMapperForSIMAM.getRnrItemsForSIMAMImport(rnr)).thenReturn(dataList);
    when(rnrMapperForSIMAM.getRegimenItemsForSIMAMImport(rnr)).thenReturn(new ArrayList<Map<String, String>>());
    when(rnrMapperForSIMAM.getProductProgramCode("P1", 12)).thenReturn(asList("MMIA"));
    when(rnrMapperForSIMAM.getProductProgramCode("P2", 12)).thenReturn(asList("MMIA"));

    Workbook workBook = new XSSFWorkbook();
    workBook.createSheet();
    when(singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_RNR_XLSX,
        ExcelHandler.PathType.FILE)).thenReturn(workBook);
    requisitionEmailServiceForSIMAM.queueRequisitionEmailWithAttachment(rnr, users);

    verify(singleListSheetExcelHandler).readXssTemplateFile(TEMPLATE_IMPORT_REGIMEN_XLSX_EMPTY, ExcelHandler.PathType.FILE);
  }

  @Test
  public void shouldReturnRegimenExcelWhenRegimenHasItems() throws MalformedURLException {
    rnr.setStatus(RnrStatus.AUTHORIZED);

    initRnrItems("MMIA");
    initRegimenItems("MMIA");
    when(rnrMapperForSIMAM.getRnrItemsForSIMAMImport(rnr)).thenReturn(dataList);
    when(rnrMapperForSIMAM.getRegimenItemsForSIMAMImport(rnr)).thenReturn(regimenDataList);
    when(rnrMapperForSIMAM.getProductProgramCode("P1", 12)).thenReturn(asList("MMIA"));
    when(rnrMapperForSIMAM.getProductProgramCode("P2", 12)).thenReturn(asList("MMIA"));

    Workbook workBook = new XSSFWorkbook();
    workBook.createSheet();
    when(singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_RNR_XLSX, ExcelHandler.PathType.FILE)).thenReturn(workBook);
    when(singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_REGIMEN_XLSX, ExcelHandler.PathType.FILE)).thenReturn(workBook);

    when(singleListSheetExcelHandler.createXssFile(workBook, "Regimen_Requi" + getFileName() + ".xlsx")).thenReturn("expected file path");

    requisitionEmailServiceForSIMAM.queueRequisitionEmailWithAttachment(rnr, users);

    verify(singleListSheetExcelHandler).readXssTemplateFile(TEMPLATE_IMPORT_REGIMEN_XLSX, ExcelHandler.PathType.FILE);
  }

  @Test
  public void shouldConvertParticularRegimenNameForSIMAM() throws MalformedURLException {
    rnr.setStatus(RnrStatus.AUTHORIZED);
    initRnrItems("MMIA");
    initRegimenItems("MMIA");

    Map<String, String> item3 = MapUtils.putAll(new HashMap(), new String[][]{
        {"program_code", "MMIA"},
        {"regimen_name", "ABC+3TC+LPV/r"},
        {"total", "300"},
        {"date", "10-10-2012"},
        {"category", "Paediatrics"}
    });
    List<Map<String, String>> newRegimenDataList = new ArrayList<>();
    newRegimenDataList.addAll(regimenDataList);
    newRegimenDataList.add(item3);

    when(rnrMapperForSIMAM.getRnrItemsForSIMAMImport(rnr)).thenReturn(dataList);
    when(rnrMapperForSIMAM.getRegimenItemsForSIMAMImport(rnr)).thenReturn(newRegimenDataList);
    when(rnrMapperForSIMAM.getProductProgramCode("P1", 12)).thenReturn(asList("MMIA"));
    when(rnrMapperForSIMAM.getProductProgramCode("P2", 12)).thenReturn(asList("MMIA"));

    Workbook workBook = new XSSFWorkbook();
    workBook.createSheet();
    when(singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_RNR_XLSX, ExcelHandler.PathType.FILE)).thenReturn(workBook);
    when(singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_REGIMEN_XLSX, ExcelHandler.PathType.FILE)).thenReturn(workBook);

    when(singleListSheetExcelHandler.createXssFile(workBook, "Regimen_Requi" + getFileName() + ".xlsx")).thenReturn("expected file path");

    requisitionEmailServiceForSIMAM.queueRequisitionEmailWithAttachment(rnr, users);

    ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<Sheet> captor2 = ArgumentCaptor.forClass(Sheet.class);

    verify(singleListSheetExcelHandler, times(2)).createDataRows(captor2.capture(), captor.capture());
    List<List> captorAllValues = captor.getAllValues();
    List<Map<String, String>> argument = captorAllValues.get(1);
    assertThat(argument.get(2).get("regimen_name"), is("ABC+3TC+LPV/r (2FC+LPV/r)"));
  }

  private String getFileName() {
    return rnr.getId() + "_" + rnr.getFacility().getName() + "_" + rnr.getPeriod().getName() + "_" + rnr.getProgram().getName();
  }
}