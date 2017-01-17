package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.User;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistributionsEditHistory {
  private Distribution distribution;

  private String district;
  private Facility facility;

  private String dataScreen;
  private String editedItem;

  private String originalValue;
  private String newValue;

  private Date editedDatetime;
  private User editedBy;

}
