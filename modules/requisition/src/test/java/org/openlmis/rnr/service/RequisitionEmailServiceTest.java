package org.openlmis.rnr.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.User;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.domain.EmailAttachment;
import org.openlmis.email.service.EmailService;
import org.openlmis.files.excel.ExcelHandler;
import org.openlmis.files.excel.SingleListSheetExcelHandler;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.mapper.RnrEmailMapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RequisitionEmailServiceTest {


  @Mock
  private RnrEmailMapper rnrEmailMapper;

  @Mock
  private EmailService emailService;

  @Mock
  private SingleListSheetExcelHandler singleListSheetExcelHandler;

  private RequisitionEmailService requisitionEmailService;

  Rnr rnr;
  private List<Map<String, String>> dataList;
  private List<User> users;

  @Before
  public void setUp() throws Exception {
    requisitionEmailService = new RequisitionEmailService(rnrEmailMapper, emailService, singleListSheetExcelHandler);
    rnr = new Rnr();
    rnr.setId(1L);


    Map<String, String> item1 = new HashMap<>();
    Map<String, String> item2 = new HashMap<>();

    item1.put("facilityid", "156");
    item1.put("client_submitted_time", "12:21");
    item1.put("productCode", "31");
    item2.put("facilityid", "157");
    item2.put("clientSubmittedTime", "18:21");
    item2.put("productCode", "32");

    dataList = new ArrayList<>();


    initUsers();
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
  public void shouldReturnWhenRequisitionStatusNotAuthorized() throws Exception {
    Program program = new Program();
    program.setCode("ESS_MEDS");
    rnr.setProgram(program);
    rnr.setStatus(RnrStatus.SUBMITTED);
    requisitionEmailService.sendRequisitionEmailWithAttachment(rnr, users);
    verify(emailService, never()).sendMimeMessageToMultipleUser(any(String[].class), eq("rnr"), eq("message body"), org.mockito.Matchers.anyListOf(EmailAttachment.class));
  }

  @Test
  public void shouldGetEmailDataList() throws Exception {
    Program program = new Program();
    program.setCode("ESS_MEDS");
    rnr.setProgram(program);
    rnr.setStatus(RnrStatus.AUTHORIZED);

    when(rnrEmailMapper.getEmailAttachmentItems(rnr)).thenReturn(dataList);

    Workbook workBook = new XSSFWorkbook();
    workBook.createSheet();
    when(singleListSheetExcelHandler.readXssTemplateFile(anyString(), any(ExcelHandler.PathType.class))).thenReturn(workBook);

    String testFile= getClassPathTestFile("emailattachment.txt");
    when(singleListSheetExcelHandler.createXssFile(any(Workbook.class), anyString())).thenReturn(testFile);

    requisitionEmailService.sendRequisitionEmailWithAttachment(rnr, users);

    verify(emailService).sendMimeMessageToMultipleUser(any(String[].class), eq("rnr"), eq("message body"), org.mockito.Matchers.anyListOf(EmailAttachment.class));
  }

  private String getClassPathTestFile(String fileName) throws MalformedURLException {
    URL filepath = null;
    try {
      filepath = RequisitionEmailServiceTest.class.getClassLoader().getResource(fileName);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    return filepath == null ? "" : filepath.getPath();
  }
}