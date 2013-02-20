package org.openlmis.rnr.searchCriteria;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class RequisitionSearchCriteria {

  Integer facilityId;
  Integer programId;
  Integer periodId;
  Date periodStartDate;
  Date periodEndDate;

  public RequisitionSearchCriteria(Integer facilityId, Integer programId, Date periodStartDate, Date periodEndDate) {
    this.facilityId = facilityId;
    this.programId = programId;
    this.periodStartDate = periodStartDate;
    this.periodEndDate = periodEndDate;
  }
}
