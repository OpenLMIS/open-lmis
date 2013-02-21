package org.openlmis.rnr.searchCriteria;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class RequisitionSearchCriteria {

  Integer userId;
  Integer facilityId;
  Integer programId;
  Integer periodId;
  Date dateRangeStart;
  Date dateRangeEnd;

  public RequisitionSearchCriteria(Integer facilityId, Integer programId, Date periodStartDate, Date periodEndDate) {
    this.facilityId = facilityId;
    this.programId = programId;
    this.dateRangeStart = periodStartDate;
    this.dateRangeEnd = periodEndDate;
  }

  public RequisitionSearchCriteria(Integer facilityId, Integer programId, Integer userId, Date dateRangeStart, Date dateRangeEnd) {
    this.facilityId = facilityId;
    this.programId = programId;
    this.userId = userId;
    this.dateRangeStart = dateRangeStart;
    this.dateRangeEnd = dateRangeEnd;
  }
}
