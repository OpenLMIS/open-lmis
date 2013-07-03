package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Regimen;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_NULL)
public class RegimenLineItem extends BaseModel {
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
}
