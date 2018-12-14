package org.openlmis.rnr.service;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.StaticReferenceDataService;
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
import org.springframework.beans.factory.annotation.Value;
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
	public static final String TEMPLATE_IMPORT_RNR_XLSX_EMPTY = "template_Simam_import_Requi_EMPTY.xlsx";
	public static final String TEMPLATE_IMPORT_REGIMEN_XLSX = "template_Simam_import_Regimen.xlsx";
	public static final String TEMPLATE_IMPORT_REGIMEN_XLSX_EMPTY = "template_Simam_import_Regimen_EMPTY.xlsx";

	public static final String FILE_APPLICATION_VND_MS_EXCEL = "application/excel";
	public static final String FILE_APPLICATION_PDF = "application/pdf";

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

	@Autowired
	private PDFGenerator pdfGenerator;

	@Autowired
	private StaticReferenceDataService staticReferenceDataService;

    @Value("${email.attachment.cache.path}")
    protected String pdfDirectory;

	public static final Map<String, String> SIMAM_PROGRAMS_MAP = MapUtils.putAll(new HashMap(),
										new String[][]{
                        {"MMIA", "TARV"},
                        {"ESS_MEDS", "Via Clássica"},
												{"VIA", "Via Clássica"},
												{"TB", "Tuberculose"},
                        {"MALARIA", "Malaria"},
                        {"TARV", "TARV"},
                        {"PTV", "PTV"},
                        {"TEST_KIT", "Testes Rápidos Diag."},
                        {"PME", "PME"},
												{"NUTRITION", "Via Clássica"}
										});

	public void queueRequisitionEmailWithAttachment(Rnr requisition, List<User> users) {
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
		for (Map<String, String> itemMap : itemsMap) {
			String programCodeForProduct;
			List<String> programCodesForProduct = rnrMapperForSIMAM.getProductProgramCode(itemMap.get("product_code"),
					Integer.parseInt(itemMap.get("form_program_id")));
			//If the programs associated with the product contain form codes (VIA/MMIA) and subprogram code,
			//use subprogram code; if a product is with multiple active subprograms under a form, then the first will be selected
			if (programCodesForProduct.size() > 1) {
				programCodeForProduct = FluentIterable.from(programCodesForProduct).filter(new Predicate<String>() {
					@Override
					public boolean apply(String input) {
						return !"VIA".equals(input) && !"MMIA".equals(input);
					}
				}).toList().get(0);
			} else {
				programCodeForProduct = programCodesForProduct.get(0);
			}
			if (SIMAM_PROGRAMS_MAP.get(programCodeForProduct) != null) {
				itemMap.put("program_code", SIMAM_PROGRAMS_MAP.get(programCodeForProduct));
			} else {
				itemMap.put("program_code", programCodeForProduct);
			}
			//SIMAM needs to prepend "a" to the product code
			itemMap.put("product_code", "a" + itemMap.get("product_code"));
		}
	}

	public EmailAttachment generateRequisitionAttachmentForSIMAM(Rnr requisition) {
		String filePath = generateRequisitionExcelForSIMAM(requisition);
		return generateEmailAttachment(fileNameForRequiItems(requisition), filePath, FILE_APPLICATION_VND_MS_EXCEL);
	}

	public String generateRequisitionExcelForSIMAM(Rnr requisition) {
		List<Map<String, String>> requisitionItemsData = rnrMapperForSIMAM.getRnrItemsForSIMAMImport(requisition);

		if(requisition.getProgram().getCode().equals("PTV")) {
			for(Map<String, String> requisitionItem : requisitionItemsData) {
				requisitionItem.put("quantity_dispensed", requisitionItem.get("total_service_quantity"));
			}
		}
		CollectionUtils.collect(requisitionItemsData, new Transformer() {
			@Override
			public Map<String, String> transform(Object input) {
				((Map<String, String>) input).put("emprest", "0");
				return (Map<String, String>) input;
			}
		});
		Workbook workbook;
		if(requisitionItemsData.isEmpty()) {
			workbook = singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_RNR_XLSX_EMPTY, ExcelHandler.PathType.FILE);
		} else {
			convertOpenLMISProgramCodeToSIMAMCode(requisitionItemsData);
			workbook = singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_RNR_XLSX, ExcelHandler.PathType.FILE);
			singleListSheetExcelHandler.createDataRows(workbook.getSheetAt(0), requisitionItemsData);
		}

		return singleListSheetExcelHandler.createXssFile(workbook, fileNameForRequiItems(requisition));
	}

	private EmailAttachment generateRegimenAttachmentForSIMAM(Rnr requisition) {
		String filePath = generateRegimenExcelForSIMAM(requisition);
		return generateEmailAttachment(fileNameForRegimens(requisition), filePath, FILE_APPLICATION_VND_MS_EXCEL);
	}

	public String generateRegimenExcelForSIMAM(Rnr requisition) {
		List<Map<String, String>> regimenItemsData = rnrMapperForSIMAM.getRegimenItemsForSIMAMImport(requisition);

		//Requested by SIMAM support to specifically convert "ABC+3TC+LPV/r" to a different name to be able to import into SIMAM
		for (Map<String, String> regimenItem : regimenItemsData) {
			if ("ABC+3TC+LPV/r".equals(regimenItem.get("regimen_name")) && "Paediatrics".equals(regimenItem.get("category"))) {
				regimenItem.put("regimen_name", "ABC+3TC+LPV/r (2FC+LPV/r)");
			}
		}

		Workbook workbook;
		if (regimenItemsData.isEmpty()) {
			workbook = singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_REGIMEN_XLSX_EMPTY, ExcelHandler.PathType.FILE);
		} else {

			CollectionUtils.collect(regimenItemsData, new Transformer() {
				@Override
				public Map<String, String> transform(Object input) {
					//OpenLMIS 2.0 does not assign program codes to regimens, use form code and map to SIMAM code
					String programCode = ((Map<String, String>) input).get("program_code");
					((Map<String, String>) input).put("program_code", SIMAM_PROGRAMS_MAP.get(programCode));
					((Map<String, String>) input).put("movDescID", "0");
					return (Map<String, String>) input;
				}
			});

			workbook = singleListSheetExcelHandler.readXssTemplateFile(TEMPLATE_IMPORT_REGIMEN_XLSX, ExcelHandler.PathType.FILE);
			singleListSheetExcelHandler.createDataRows(workbook.getSheetAt(0), regimenItemsData);
		}

		return singleListSheetExcelHandler.createXssFile(workbook, fileNameForRegimens(requisition));
	}

	public String fileNameForRequiItems(Rnr requisition) {
		return REQUI_FILE_NAME_PREFIX + requisition.getId() + "_" + requisition.getFacility().getName() + "_" + requisition.getPeriod().getName() + "_" +
                       requisition.getProgram().getName() + ".xlsx";
	}

	public String fileNameForRegimens(Rnr requisition) {
		return REGIMEN_FILE_NAME_PREFIX + requisition.getId() + "_" + requisition.getFacility().getName() + "_" + requisition.getPeriod().getName() + "_" +
                       requisition.getProgram().getName() + ".xlsx";
	}



	private List<EmailAttachment> prepareEmailAttachmentsForSIMAM(Rnr requisition) {

		List<EmailAttachment> emailAttachments = new ArrayList<>();
		EmailAttachment attachmentForRequisition = generateRequisitionAttachmentForSIMAM(requisition);
		emailAttachments.add(attachmentForRequisition);

		EmailAttachment attachmentForRegimen = generateRegimenAttachmentForSIMAM(requisition);
		emailAttachments.add(attachmentForRegimen);

		if (staticReferenceDataService.getBoolean("email.attachment.form.pdf")) {
			EmailAttachment formPdfAttachment = generatePdfForSIMAM(requisition);
			emailAttachments.add(formPdfAttachment);
		}

		return emailAttachments;
	}

	private EmailAttachment generatePdfForSIMAM(Rnr requisition) {
		String filePathForPdf = pdfGenerator.generatePdf(requisition.getId(), requisition.getProgram().getId(), pdfDirectory);
		return generateEmailAttachment(pdfGenerator.getNameForPdf(), filePathForPdf, FILE_APPLICATION_PDF);
	}

	private EmailAttachment generateEmailAttachment(String fileName, String filePath, String fileType) {
		EmailAttachment attachmentForRegimen = new EmailAttachment();
		attachmentForRegimen.setAttachmentName(fileName);
		attachmentForRegimen.setAttachmentPath(filePath);
		attachmentForRegimen.setAttachmentFileType(fileType);
		return attachmentForRegimen;
	}
}
