package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.util.DateUtil;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionDTO {
	private Long id;
	private Long programId;
	private String programName;
	private String programCode;

	private Long facilityId;
	private String facilityName;
	private String facilityCode;

	private String submittedUser;
	private String submittedDateString;
	private String clientSubmittedTimeString;

	private Date submittedDate;
	private Date clientSubmittedTime;

	private String requisitionStatus;

	public String getClientSubmittedTimeString() {
		return DateUtil.formatDate(clientSubmittedTime);
	}

	public String getSubmittedDateString() {
		return DateUtil.formatDate(submittedDate);
	}

}
