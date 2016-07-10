/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.domain.RefrigeratorProblem;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  RefrigeratorProblem represents an entity which keeps track of the problems recorded for particular
 *  refrigerator in a facility visit.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class RefrigeratorProblemDTO extends BaseModel {

  Long readingId;
  Reading operatorError;
  Reading burnerProblem;
  Reading gasLeakage;
  Reading egpFault;
  Reading thermostatSetting;
  Reading other;
  Reading otherProblemExplanation;

  public RefrigeratorProblem transform() {
    Boolean operatorError = Reading.safeRead(this.operatorError).parseBoolean();
    Boolean burnerProblem = Reading.safeRead(this.burnerProblem).parseBoolean();
    Boolean gasLeakage = Reading.safeRead(this.gasLeakage).parseBoolean();
    Boolean egpFault = Reading.safeRead(this.egpFault).parseBoolean();
    Boolean thermostatSetting = Reading.safeRead(this.thermostatSetting).parseBoolean();
    Boolean other = Reading.safeRead(this.other).parseBoolean();
    String effectiveValue = Reading.safeRead(otherProblemExplanation).getEffectiveValue();

    RefrigeratorProblem problem = new RefrigeratorProblem();
    problem.setId(id);
    problem.setCreatedBy(createdBy);
    problem.setCreatedDate(createdDate);
    problem.setModifiedBy(modifiedBy);
    problem.setModifiedDate(modifiedDate);
    problem.setReadingId(readingId);
    problem.setOperatorError(operatorError);
    problem.setBurnerProblem(burnerProblem);
    problem.setGasLeakage(gasLeakage);
    problem.setEgpFault(egpFault);
    problem.setThermostatSetting(thermostatSetting);
    problem.setOther(other);
    problem.setOtherProblemExplanation(effectiveValue);

    return problem;
  }

}
