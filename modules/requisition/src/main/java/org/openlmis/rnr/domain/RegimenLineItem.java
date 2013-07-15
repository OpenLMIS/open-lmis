package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.domain.RegimenColumn;
import org.openlmis.core.domain.RegimenTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_NULL)
public class RegimenLineItem extends BaseModel {

  public static final String ON_TREATMENT = "patientsOnTreatment";
  public static final String INITIATED_TREATMENT = "patientsToInitiateTreatment";
  public static final String STOPPED_TREATMENT = "patientsStoppedTreatment";
  public static final String TYPE_NUMERIC = "regimen.reporting.dataType.numeric";
  public static final String REMARKS = "remarks";

  private Long rnrId;
  private String code;
  private String name;
  private Integer patientsOnTreatment;
  private Integer patientsToInitiateTreatment;
  private Integer patientsStoppedTreatment;
  private String remarks;
  private RegimenCategory category;
  private Integer regimenDisplayOrder;

  private static Logger logger = LoggerFactory.getLogger(RegimenLineItem.class);

  public RegimenLineItem(Long rnrId, RegimenCategory category, Long createdBy, Long modifiedBy) {
    this.rnrId = rnrId;
    this.category = category;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
  }

  public void setRegimenFieldsAccordingToTemplate(RegimenTemplate regimenTemplate) {
    if (regimenTemplate.isRegimenColumnVisible(ON_TREATMENT))
      patientsOnTreatment = 0;

    if (regimenTemplate.isRegimenColumnVisible(INITIATED_TREATMENT))
      patientsToInitiateTreatment = 0;

    if (regimenTemplate.isRegimenColumnVisible(STOPPED_TREATMENT))
      patientsStoppedTreatment = 0;

    if (regimenTemplate.isRegimenColumnVisible(REMARKS))
      remarks = "";
  }

  public void copyCreatorEditableFieldsForRegimen(RegimenLineItem regimenLineItem, RegimenTemplate regimenTemplate) {
    for (RegimenColumn regimenColumn : regimenTemplate.getRegimenColumns()) {
      String fieldName = regimenColumn.getName();
      if (regimenColumn.getVisible())
        copyColumnData(fieldName, regimenLineItem);
    }

  }

  private void copyColumnData(String fieldName, RegimenLineItem regimenLineItem) {
    try {
      Field field = this.getClass().getDeclaredField(fieldName);
      field.set(this, field.get(regimenLineItem));
    } catch (Exception e) {
      logger.error("Error in reading RnrLineItem's field", e);
    }
  }

}
