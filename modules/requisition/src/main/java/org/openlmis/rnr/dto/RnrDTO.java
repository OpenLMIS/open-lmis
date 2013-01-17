package org.openlmis.rnr.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class RnrDTO {

  private Integer id;
  private String programName;
  private String facilityName;
  private String facilityCode;
  private Date submittedDate;
  private Date modifiedDate;
  private Date periodStartDate;
  private Date periodEndDate;
}
