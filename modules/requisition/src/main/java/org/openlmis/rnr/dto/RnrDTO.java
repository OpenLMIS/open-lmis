package org.openlmis.rnr.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_NULL)
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
