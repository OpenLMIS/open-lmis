package org.openlmis.distribution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.domain.MotorbikeProblems;

import static org.apache.commons.lang.BooleanUtils.isFalse;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class MotorbikeProblemsDTO extends BaseModel {
    Long facilityVisitId;
    Reading lackOfFundingForFuel;
    Reading repairsSchedulingProblem;
    Reading lackOfFundingForRepairs;
    Reading missingParts;
    Reading other;
    Reading motorbikeProblemOther;
    Boolean notRecorded;

    public MotorbikeProblems transform() {
      MotorbikeProblems motorbikeProblems = new MotorbikeProblems();
      motorbikeProblems.setId(id);
      motorbikeProblems.setCreatedBy(createdBy);
      motorbikeProblems.setCreatedDate(createdDate);
      motorbikeProblems.setModifiedBy(modifiedBy);
      motorbikeProblems.setModifiedDate(modifiedDate);
      motorbikeProblems.setFacilityVisitId(facilityVisitId);

      if (isFalse(notRecorded)) {
        Boolean lackOfFundingForFuel = Reading.safeRead(this.lackOfFundingForFuel).parseBoolean();
        Boolean repairsSchedulingProblem = Reading.safeRead(this.repairsSchedulingProblem).parseBoolean();
        Boolean lackOfFundingForRepairs = Reading.safeRead((this.lackOfFundingForRepairs)).parseBoolean();
        Boolean missingParts = Reading.safeRead(this.missingParts).parseBoolean();
        Boolean other = Reading.safeRead(this.other).parseBoolean();
        String motorbikeProblemOther = Reading.safeRead(this.motorbikeProblemOther).getEffectiveValue();

        motorbikeProblems.setLackOfFundingForFuel(lackOfFundingForFuel);
        motorbikeProblems.setRepairsSchedulingProblem(repairsSchedulingProblem);
        motorbikeProblems.setLackOfFundingForRepairs(lackOfFundingForRepairs);
        motorbikeProblems.setMissingParts(missingParts);
        motorbikeProblems.setOther(other);
        motorbikeProblems.setMotorbikeProblemOther(motorbikeProblemOther);
      }

      return motorbikeProblems;
    }
}

