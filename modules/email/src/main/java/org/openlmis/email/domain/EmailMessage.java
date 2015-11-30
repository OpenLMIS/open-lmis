/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.
 *  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.email.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenlmisEmailMessage is an entity that contains additional id attribute apart from basic attributes provided by
 * SimpleMailMessage.
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class EmailMessage extends SimpleMailMessage {
	private Long id;
	private String receiver;
	private boolean isHtml;
	private boolean sent;

	private List<EmailAttachment> emailAttachments = new ArrayList<>();

	public EmailMessage() {
		super();
	}

	public String getReceiver() {
		return super.getTo() != null ? super.getTo()[0] : null;
	}

	public void addEmailAttachment(EmailAttachment attachment) {
		this.emailAttachments.add(attachment);
	}
}
