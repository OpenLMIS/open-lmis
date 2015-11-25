package org.openlmis.rnr.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.email.domain.EmailAttachment;
import org.openlmis.email.domain.EmailMessage;
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
	public static final String EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_PREFIX = "EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_";


	@Autowired
	private RnrMapperForSIMAM rnrMapperForSIMAM;

	@Autowired
	private EmailService emailService;

	@Autowired
	ConfigurationSettingService settingService;

	@Autowired
	private SingleListSheetExcelHandler singleListSheetExcelHandler;

	public static final Map<String, String> SIMAM_PROGRAMS_MAP = MapUtils.putAll(new HashMap(),
										new String[][]{
											{"MMIA", "TARV"},
											{"ESS_MEDS", "Medicamentos Essenciais"}
										});

	public void sendRequisitionEmailWithAttachment(Rnr requisition, List<User> users) {
		if (!requisition.getStatus().equals(RnrStatus.AUTHORIZED) || users.size() <= 0) {
			return;
		}

		insertEmailMessages(requisition, users);
	}

	private void insertEmailMessages(Rnr requisition, List<User> users) {
		List<EmailAttachment> emailAttachments = prepareEmailAttachmentsForSIMAM(requisition);
		emailService.insertEmailAttachmentList(emailAttachments);

		final String subject = "SIMAM Import Files for Requisition #" + requisition.getId();
		final String messageBody = createEmailBodyContent(requisition.getProgram().getCode());

		for (User user : users) {
			String emailAddress = user.getEmail();
			if (emailAddress != null) {
				EmailMessage emailMessage = new EmailMessage();
				emailMessage.setTo(emailAddress);
				emailMessage.setText(messageBody);
				emailMessage.setSubject(subject);
				emailMessage.setEmailAttachments(emailAttachments);
				emailMessage.setHtml(true);
				emailService.queueEmailMessage(emailMessage);
			}
		}
	}

	private String createEmailBodyContent(String programCode) {
		String emailContent = settingService.getConfigurationStringValue(EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_PREFIX + programCode);
		return emailContent == null ? "" : emailContent;
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

	public  EmailAttachment generateRequisitionExcelForSIMAM(Rnr requisition) {
		List<Map<String, String>> requisitionItemsData = rnrMapperForSIMAM.getRnrItemsForSIMAMImport(requisition);
		convertOpenLMISProgramCodeToSIMAMCode(requisitionItemsData);
		Workbook workbook = singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_RNR_XLSX, ExcelHandler.PathType.FILE);
		singleListSheetExcelHandler.createDataRows(workbook.getSheetAt(0), requisitionItemsData);

		String requiFileName = fileNameForRequiItems(requisition);
		String filePath = singleListSheetExcelHandler.createXssFile(workbook, requiFileName);
		return generateEmailAttachment(requiFileName, filePath);
	}

	private EmailAttachment generateRegimenExcelForSIMAM(Rnr requisition) {
		List<Map<String, String>> regimenItemsData = rnrMapperForSIMAM.getRegimenItemsForSIMAMImport(requisition);

		Workbook workbook;
		if (regimenItemsData.isEmpty()) {
			workbook = singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_REGIMEN_XLSX_EMPTY, ExcelHandler.PathType.FILE);
		} else {

			CollectionUtils.collect(regimenItemsData, new Transformer() {
				@Override
				public Map<String, String> transform(Object input) {
					((Map<String, String>) input).put("movDescID", "0");
					return (Map<String, String>) input;
				}
			});

			convertOpenLMISProgramCodeToSIMAMCode(regimenItemsData);

			workbook = singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_REGIMEN_XLSX, ExcelHandler.PathType.FILE);
			singleListSheetExcelHandler.createDataRows(workbook.getSheetAt(0), regimenItemsData);
		}

		String regimenFileName = fileNameForRegimens(requisition);
		String filePath = singleListSheetExcelHandler.createXssFile(workbook, regimenFileName);
		return generateEmailAttachment(regimenFileName, filePath);
	}

	private String fileNameForRequiItems(Rnr requisition) {
		return REQUI_FILE_NAME_PREFIX + requisition.getId() + "_" + requisition.getFacility().getName() + "_" + requisition.getPeriod().getName() + "_" +
                       requisition.getProgram().getName() + ".xlsx";
	}

	private String fileNameForRegimens(Rnr requisition) {
		return REGIMEN_FILE_NAME_PREFIX + requisition.getId() + "_" + requisition.getFacility().getName() + "_" + requisition.getPeriod().getName() + "_" +
                       requisition.getProgram().getName() + ".xlsx";
	}

	private List<EmailAttachment> prepareEmailAttachmentsForSIMAM(Rnr requisition) {

		List<EmailAttachment> emailAttachments = new ArrayList<>();
		EmailAttachment attachmentForRequisition = generateRequisitionExcelForSIMAM(requisition);
		emailAttachments.add(attachmentForRequisition);

		EmailAttachment attachmentForRegimen = generateRegimenExcelForSIMAM(requisition);
		emailAttachments.add(attachmentForRegimen);

		return emailAttachments;
	}

	private EmailAttachment generateEmailAttachment(String fileName, String filePath) {
		EmailAttachment attachmentForRegimen = new EmailAttachment();
		attachmentForRegimen.setAttachmentName(fileName);
		attachmentForRegimen.setAttachmentPath(filePath);
		attachmentForRegimen.setAttachmentFileType(FILE_APPLICATION_VND_MS_EXCEL);
		return attachmentForRegimen;
	}
}
