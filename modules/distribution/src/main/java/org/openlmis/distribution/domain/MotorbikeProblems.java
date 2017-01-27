package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;

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
}
