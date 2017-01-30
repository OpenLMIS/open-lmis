package org.openlmis.web.util;

import com.google.common.base.Predicate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;
import static org.openlmis.distribution.util.UIMapping.getDataScreen;
import static org.openlmis.distribution.util.UIMapping.getField;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityDistributionEditResults {
  private static final Predicate<FacilityDistributionEditDetail> VISITED_PREDICATE = new VisitedPredicate();
  private static final Predicate<FacilityDistributionEditDetail> NOT_VISITED_PREDICATE = not(VISITED_PREDICATE);

  private List<FacilityDistributionEditDetail> details;
  private Long facilityId;
  private boolean conflict;

  public FacilityDistributionEditResults(Long facilityId) {
    this.details = new ArrayList<>();
    this.facilityId = facilityId;
  }

  public List<FacilityDistributionEditDetail> getDetails() {
    FacilityDistributionEditDetail visited = from(details).firstMatch(VISITED_PREDICATE).orNull();

    if (null != visited) {
      List<FacilityDistributionEditDetail> tmp = new ArrayList<>(details.size());
      tmp.add(visited);
      tmp.addAll(from(details).filter(NOT_VISITED_PREDICATE).toImmutableList());

      return tmp;
    }

    return details;
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

  private static final class VisitedPredicate implements Predicate<FacilityDistributionEditDetail> {

    @Override
    public boolean apply(@Nullable FacilityDistributionEditDetail input) {
      return null != input && input.getDataScreen().equals("FacilityVisit") && input.getEditedItem().equals("visited");
    }

  }
}
