package org.openlmis.web.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;

import java.util.ArrayList;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityDistributionEditResults {
  private List<FacilityDistributionEditDetail> details;
  private Long facilityId;
  private boolean conflict;

  public FacilityDistributionEditResults(Long facilityId) {
    this.details = new ArrayList<>();
    this.facilityId = facilityId;
  }

  public void allow(Object parent, String parentProperty, Object original, String originalPropertyName, Object originalProperty, Object previousProperty, Object replacementProperty) {
    create(parent, parentProperty, original, originalPropertyName, originalProperty, previousProperty, replacementProperty, false);
  }

  public void deny(Object parent, String parentProperty, Object original, String originalPropertyName, Object originalProperty, Object previousProperty, Object replacementProperty) {
    conflict = true;
    create(parent, parentProperty, original, originalPropertyName, originalProperty, previousProperty, replacementProperty, true);
  }

  private void create(Object parent, String parentProperty, Object original, String originalPropertyName, Object originalProperty, Object previousProperty, Object replacementProperty, boolean conflict) {
    FacilityDistributionEditDetail detail = new FacilityDistributionEditDetail();

    detail.setParentDataScreenId(parent instanceof BaseModel ? ((BaseModel) original).getId() : null);
    detail.setParentDataScreen(parent.getClass().getSimpleName());
    detail.setParentProperty(parentProperty);

    detail.setDataScreenId(original instanceof BaseModel ? ((BaseModel) original).getId() : null);
    detail.setDataScreen(original.getClass().getSimpleName());

    detail.setEditedItem(originalPropertyName);

    detail.setOriginalValue(originalProperty);
    detail.setPreviousValue(previousProperty);
    detail.setNewValue(replacementProperty);

    detail.setConflict(conflict);

    details.add(detail);
  }
}
