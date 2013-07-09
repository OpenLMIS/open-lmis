package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenTemplate;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_NULL)
public class RegimenLineItem extends BaseModel {

  public static final String ON_TREATMENT = "onTreatment";
  public static final String INITIATED_TREATMENT = "initiatedTreatment";
  public static final String STOPPED_TREATMENT = "stoppedTreatment";
  public static final String TYPE_NUMERIC = "regimen.reporting.dataType.numeric";
  public static final String REMARKS = "remarks";

  Regimen regimen;
  Long rnrId;
  Integer patientsOnTreatment;
  Integer patientsToInitiateTreatment;
  Integer patientsStoppedTreatment;
  String remarks;

  public RegimenLineItem(Long rnrId, Regimen regimen) {
    this.rnrId = rnrId;
    this.regimen = regimen;
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
}
