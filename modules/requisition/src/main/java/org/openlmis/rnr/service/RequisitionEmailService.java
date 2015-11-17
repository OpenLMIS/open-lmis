package org.openlmis.rnr.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.openlmis.core.domain.User;
import org.openlmis.email.domain.EmailAttachment;
import org.openlmis.email.service.EmailService;
import org.openlmis.files.excel.ExcelHandler;
import org.openlmis.files.excel.SingleListSheetExcelHandler;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.mapper.RnrEmailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionEmailService {
  private static final Logger logger = LoggerFactory.getLogger(RequisitionEmailService.class);

  public static final String TEMPLATE_IMPORT_VIA_RNR_XLSX = "templete_Simam_import_Via_Classica_Requi.xlsx";
  public static final String TEMPLATE_IMPORT_VIA_REGIMEN_XLSX = "templete_Simam_import_Via_Regimen.xlsx";

  public static final String ATTACHMENT_VIA_RNR_ITEMS_NAME = "Simam_import_Via_Classica_Requi.xlsx";
  public static final String ATTACHMENT_VIA_RNR_REGIMEN_NAME = "Simam_import_Via_Regimen.xlsx";

  public static final String ATTACHMENT_MMIA_RNR_ITEMS_NAME = "created_rnr.xlsx";

  public static final String FILE_APPLICATION_VND_MS_EXCEL = "application/excel";

  @Autowired
  private RnrEmailMapper rnrEmailMapper;

  @Autowired
  private EmailService emailService;

  @Autowired
  private SingleListSheetExcelHandler singleListSheetExcelHandler;

  public void sendRequisitionEmailWithAttachment(Rnr requisition, List<User> users) {
    if (requisition.getStatus() != RnrStatus.AUTHORIZED || users.size() <= 0 ) {
      return;
    }

    final List<String> to = new ArrayList<>();
    for (User user : users) {
      to.add(user.getEmail());
    }

    if (isProgram(requisition, "MMIA")) {
      //TODO

    } else if (isProgram(requisition, "ESS_MEDS")) {
      final String subject = "rnr";
      final String messageBody = "message body";
      List<EmailAttachment> emailAttachments = generateViaAttachment(requisition);
      emailService.sendMimeMessageToMultipleUser(to.toArray(new String[0]), subject, messageBody, emailAttachments);
    }
  }

  private boolean isProgram(Rnr requisition, String type) {
    return type.equals(requisition.getProgram().getCode());
  }

  private List<EmailAttachment> generateViaAttachment(Rnr requisition) {
    List<EmailAttachment> emailAttachments = new ArrayList<>();

    List<Map<String, String>> emailAttachmentData = rnrEmailMapper.getEmailAttachmentItems(requisition);
    Workbook workbook = singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_VIA_RNR_XLSX,
        ExcelHandler.PathType.FILE);
    singleListSheetExcelHandler.createDataRows(workbook.getSheetAt(0), emailAttachmentData);
    String outFilePath = singleListSheetExcelHandler.createXssFile(workbook, ATTACHMENT_VIA_RNR_ITEMS_NAME);
    DataSource fileDataSource = emailService.getFileDataSource(outFilePath, FILE_APPLICATION_VND_MS_EXCEL);

    emailAttachments.add(new EmailAttachment(ATTACHMENT_VIA_RNR_ITEMS_NAME, fileDataSource));

    return emailAttachments;
  }

}
