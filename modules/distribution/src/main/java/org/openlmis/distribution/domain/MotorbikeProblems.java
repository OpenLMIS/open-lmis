package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.dto.MotorbikeProblemsDTO;
import org.openlmis.distribution.dto.Reading;

import static org.apache.commons.lang.BooleanUtils.isFalse;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class MotorbikeProblems extends BaseModel {
    Long facilityVisitId;
    Boolean lackOfFundingForFuel;
    Boolean repairsSchedulingProblem;
    Boolean lackOfFundingForRepairs;
    Boolean missingParts;
    Boolean other;
    String motorbikeProblemOther;

    public MotorbikeProblemsDTO transform() {
      MotorbikeProblemsDTO dto = new MotorbikeProblemsDTO();
      dto.setId(id);
      dto.setCreatedBy(createdBy);
      dto.setCreatedDate(createdDate);
      dto.setModifiedBy(modifiedBy);
      dto.setModifiedDate(modifiedDate);
      dto.setFacilityVisitId(facilityVisitId);
      dto.setLackOfFundingForFuel(new Reading(lackOfFundingForFuel));
      dto.setRepairsSchedulingProblem(new Reading(repairsSchedulingProblem));
      dto.setLackOfFundingForRepairs(new Reading(lackOfFundingForRepairs));
      dto.setMissingParts(new Reading(missingParts));
      dto.setOther(new Reading(other));
      dto.setMotorbikeProblemOther(new Reading(motorbikeProblemOther));
      if (isFalse(lackOfFundingForFuel) && isFalse((repairsSchedulingProblem)) && isFalse(lackOfFundingForRepairs) &&
        isFalse(missingParts) && isFalse(other)) {
          dto.setNotRecorded(true);
      }

      return dto;
    }
}
