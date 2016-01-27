/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.email.service.EmailService;
import org.openlmis.report.ReportManager;
import org.openlmis.report.model.dto.MessageCollection;
import org.openlmis.report.model.dto.MessageDto;
import org.openlmis.sms.domain.SMS;
import org.openlmis.sms.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/messages")
public class ReportingRateNotificationController extends BaseController {

    public static final String SMS = "sms";
    public static final String EMAIL = "email";
    public static final String DIRECTION_OUT = "O";

    public static final String XLS = "XLS";
    public static final String HTML = "HTML";

    public static final String REPORT_PDF = "report.pdf";
    public static final String REPORT_HTML = "report.html";
    public static final String REPORT_XLS = "report.xls";

    public static final String APPLICATION_HTML = "application/html";
    public static final String APPLICATION_VND_MS_EXCEL = "application/vnd.ms-excel";
    public static final String APPLICATION_PDF = "application/pdf";
    @Autowired
    public ReportManager reportManager;
    @Autowired
    private SMSService smsService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JavaMailSender mailSender;


    @RequestMapping(value = "/send", method = POST, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> send(
            @RequestBody MessageCollection messages
    ) {

        for (MessageDto dto : messages.getMessages()) {

            switch (dto.getType()) {
                case SMS:
                    queueSms(dto);
                    break;
                case EMAIL:
                    queueSimpleMail(dto);
                    break;
                default:
            }
        }

        return OpenLmisResponse.success("Success");
    }

    private void queueSimpleMail(MessageDto dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getContact());
        message.setSubject("Reporting rate notice");
        message.setText(dto.getMessage());
        emailService.send(message);
    }

    private void queueSms(MessageDto dto) {
        SMS sms = new SMS();
        sms.setMessage(dto.getMessage());
        sms.setPhoneNumber(dto.getContact());
        sms.setDateSaved(new Date());
        sms.setDirection(DIRECTION_OUT);
        smsService.sendAsync(sms);
    }

    @RequestMapping(value = "/send/report", method = POST, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> sendWithReportAttachment(
            @RequestBody MessageCollection messageParams,
            HttpServletRequest request
    ) {

        Integer userId = loggedInUserId(request).intValue();

        /** extract message inputs from the payload **/
        String reportKey = messageParams.getReportKey();
        List<MessageDto> messages = messageParams.getMessages();
        String subject = messageParams.getSubject();
        String outputOption = messageParams.getOutputOption();
        Map<String, String[]> reportFilterParams = messageParams.getReportParams();

        /** Export report and process email attachment **/
        ByteArrayOutputStream byteArrayOutputStream = reportManager.exportReportBytesStream(userId, reportKey, reportFilterParams, outputOption);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        DataSource attachmentDataSource;
        String fileName;
        switch (outputOption.toUpperCase()) {
            case XLS:
                attachmentDataSource = new ByteArrayDataSource(bytes, APPLICATION_VND_MS_EXCEL);
                fileName = REPORT_XLS;
                break;
            case HTML:
                attachmentDataSource = new ByteArrayDataSource(bytes, APPLICATION_HTML);
                fileName = REPORT_HTML;
                break;
            default:
                attachmentDataSource = new ByteArrayDataSource(bytes, APPLICATION_PDF);
                fileName = REPORT_PDF;
                break;
        }

        for (MessageDto dto : messages) {
            emailService.sendMimeMessage(dto.getContact(), subject, dto.getMessage(), fileName, attachmentDataSource);
        }

        return OpenLmisResponse.success("Success");
    }
}
