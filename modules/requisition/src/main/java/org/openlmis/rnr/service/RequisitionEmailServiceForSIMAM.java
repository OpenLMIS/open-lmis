package org.openlmis.rnr.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.openlmis.core.domain.User;
import org.openlmis.email.domain.EmailAttachment;
import org.openlmis.email.service.EmailService;
import org.openlmis.files.excel.ExcelHandler;
import org.openlmis.files.excel.SingleListSheetExcelHandler;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.mapper.RnrMapperForSIMAM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionEmailServiceForSIMAM {
  private static final Logger logger = LoggerFactory.getLogger(RequisitionEmailServiceForSIMAM.class);

  public static final String TEMPLATE_IMPORT_RNR_XLSX = "template_Simam_import_Requi.xlsx";
  public static final String TEMPLATE_IMPORT_REGIMEN_XLSX = "template_Simam_import_Regimen.xlsx";
  public static final String TEMPLATE_IMPORT_REGIMEN_XLSX_EMPTY = "template_Simam_import_Regimen_EMPTY.xlsx";

  public static final String FILE_APPLICATION_VND_MS_EXCEL = "application/excel";

  public static final String REGIMEN_FILE_NAME_PREFIX = "Regimen_Requi";
  public static final String REQUI_FILE_NAME_PREFIX = "Requi";


  @Autowired
  private RnrMapperForSIMAM rnrMapperForSIMAM;

  @Autowired
  private EmailService emailService;

  @Autowired
  private SingleListSheetExcelHandler singleListSheetExcelHandler;

  public static final Map<String, String> SIMAM_PROGRAMS_MAP = MapUtils.putAll(new HashMap(), new String[][]{
      {"MMIA", "TARV"},
      {"ESS_MEDS", "Medicamentos Essenciais"}
  });

  public void sendRequisitionEmailWithAttachment(Rnr requisition, List<User> users) {
    if (!requisition.getStatus().equals(RnrStatus.AUTHORIZED) || users.size() <= 0 ) {
      return;
    }

    final List<String> to = new ArrayList<>();
    for (User user : users) {
      to.add(user.getEmail());
    }

    final String subject = "SIMAM Import Files for Requisition #" + requisition.getId();
    final String messageBody = createEmailBodyContent(requisition);

    String fileNameSuffix = requisition.getId() + "_" + requisition.getFacility().getName() + "_" + requisition.getPeriod().getName() + "_" + requisition.getProgram().getName() + ".xlsx";

    List<EmailAttachment> emailAttachments = prepareEmailAttachmentsForSIMAM(requisition, fileNameSuffix);
    emailService.sendMimeMessageToMultipleUser(to.toArray(new String[0]), subject, messageBody, emailAttachments);
  }

  private String createEmailBodyContent(Rnr requisition) {
    StringBuilder content = new StringBuilder();
    content.append("Facility: " + requisition.getFacility().getName() + "\r\n");
    content.append("Program: " + requisition.getProgram().getName() + "\r\n");
    content.append("Period:" + requisition.getPeriod().getName() + "\r\n");

    return content.toString();
  }

  private void convertOpenLMISProgramCodeToSIMAMCode(final List<Map<String, String>> itemsMap) {

    CollectionUtils.collect(itemsMap, new Transformer() {
      @Override
      public Map<String, String> transform(Object input) {
        String programCode = ((Map<String, String>) input).get("program_code");
        ((Map<String, String>) input).put("program_code", SIMAM_PROGRAMS_MAP.get(programCode));
        return (Map<String, String>) input;
      }
    });
  }

  private String generateRequisitionExcelForSIMAM(Rnr requisition, String fileName) {
    List<Map<String, String>> requisitionItemsData = rnrMapperForSIMAM.getRnrItemsForSIMAMImport(requisition);
    convertOpenLMISProgramCodeToSIMAMCode(requisitionItemsData);

    Workbook workbook = singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_RNR_XLSX,
        ExcelHandler.PathType.FILE);
    singleListSheetExcelHandler.createDataRows(workbook.getSheetAt(0), requisitionItemsData);
    return singleListSheetExcelHandler.createXssFile(workbook, fileName);
  }

  private String generateRegimenExcelForSIMAM(Rnr requisition, String fileName) {
    List<Map<String, String>> regimenItemsData = rnrMapperForSIMAM.getRegimenItemsForSIMAMImport(requisition);

    Workbook workbook;
    if (regimenItemsData.isEmpty()) {
       workbook = singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_REGIMEN_XLSX_EMPTY,
          ExcelHandler.PathType.FILE);
    } else {

      convertOpenLMISProgramCodeToSIMAMCode(regimenItemsData);

      workbook = singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_REGIMEN_XLSX,
          ExcelHandler.PathType.FILE);
      singleListSheetExcelHandler.createDataRows(workbook.getSheetAt(0), regimenItemsData);
    }
    return singleListSheetExcelHandler.createXssFile(workbook, fileName);

  }

  private String fileNameForRequiItems(Rnr requisition) {
    return "Regimen_Requi" + requisition.getId() + "_" + requisition.getFacility().getName() + "_" + requisition.getPeriod().getName() + "_" + requisition.getProgram().getName();
  }

  private String fileNameForRegimens(Rnr requisition) {
    return "Requi" + requisition.getId() + "_" + requisition.getFacility().getName() + "_" + requisition.getPeriod().getName() + "_" + requisition.getProgram().getName();
  }

  private List<EmailAttachment> prepareEmailAttachmentsForSIMAM(Rnr requisition, String fileNameSuffix) {
    List<EmailAttachment> emailAttachments = new ArrayList<>();

    String requiFileName = REQUI_FILE_NAME_PREFIX + fileNameSuffix;
    String regimenFileName = REGIMEN_FILE_NAME_PREFIX + fileNameSuffix;

    String requisitionItemsFilePath = generateRequisitionExcelForSIMAM(requisition, requiFileName);
    String regimenItemsFilePath = generateRegimenExcelForSIMAM(requisition, regimenFileName);

    DataSource requisitionItemsFile = emailService.getFileDataSource(requisitionItemsFilePath, FILE_APPLICATION_VND_MS_EXCEL);
    DataSource regimenItemsFile = emailService.getFileDataSource(regimenItemsFilePath, FILE_APPLICATION_VND_MS_EXCEL);

    emailAttachments.add(new EmailAttachment(requiFileName, requisitionItemsFile));
    emailAttachments.add(new EmailAttachment(regimenFileName, regimenItemsFile));

    return emailAttachments;
  }
}
