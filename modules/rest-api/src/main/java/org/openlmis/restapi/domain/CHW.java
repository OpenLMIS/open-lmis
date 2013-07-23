package org.openlmis.restapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CHW {

  private String agentCode;
  private String agentName;
  private String baseFacilityCode;
  private String phoneNumber;
  private Boolean active;

}
