package org.openlmis.web.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityDistributionEditDetail {

  private Long parentDataScreenId;
  private String parentDataScreen;
  private String parentProperty;

  private Long dataScreenId;
  private String dataScreen;

  private String editedItem;

  private Object originalValue;
  private Object previousValue;
  private Object newValue;

  private boolean conflict;
}
