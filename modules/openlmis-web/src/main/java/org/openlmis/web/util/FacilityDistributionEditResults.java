package org.openlmis.web.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.dto.DistributionDTO;

import java.util.ArrayList;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;
import static org.openlmis.distribution.util.UIMapping.getDataScreen;
import static org.openlmis.distribution.util.UIMapping.getField;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityDistributionEditResults {
  private List<FacilityDistributionEditDetail> details;
  private DistributionDTO distribution;
  private Long facilityId;
  private boolean conflict;

  public FacilityDistributionEditResults(Long facilityId) {
    this.details = new ArrayList<>();
    this.facilityId = facilityId;
  }

  public void allow(Object parent, String parentProperty, Object original,
                    String originalPropertyName, Object originalProperty, Object previousProperty,
                    Object replacementProperty, String addictional) {
    create(parent, parentProperty, original, originalPropertyName, originalProperty,
            previousProperty, replacementProperty, false, addictional);
  }

  public void deny(Object parent, String parentProperty, Object original,
                   String originalPropertyName, Object originalProperty, Object previousProperty,
                   Object replacementProperty, String addictional) {
    conflict = true;
    create(parent, parentProperty, original, originalPropertyName, originalProperty,
            previousProperty, replacementProperty, true, addictional);
  }

  private void create(Object parent, String parentProperty, Object original,
                      String originalPropertyName, Object originalProperty, Object previousProperty,
                      Object replacementProperty, boolean conflict, String addictional) {
    FacilityDistributionEditDetail detail = new FacilityDistributionEditDetail();

    detail.setParentDataScreenId(parent instanceof BaseModel ? ((BaseModel) parent).getId() : null);
    detail.setParentDataScreen(parent.getClass().getSimpleName());
    detail.setParentProperty(parentProperty);

    detail.setDataScreenId(original instanceof BaseModel ? ((BaseModel) original).getId() : null);
    detail.setDataScreen(original.getClass().getSimpleName());

    detail.setEditedItem(originalPropertyName);

    detail.setOriginalValue(originalProperty);
    detail.setPreviousValue(previousProperty);
    detail.setNewValue(replacementProperty);

    detail.setConflict(conflict);

    detail.setDataScreenUI(getDataScreen(detail.getDataScreen(), detail.getParentDataScreen()));
    detail.setEditedItemUI(getField(detail.getDataScreen(), detail.getEditedItem(), detail.getParentDataScreen(), detail.getParentProperty(), addictional));

    details.add(detail);
  }
}
