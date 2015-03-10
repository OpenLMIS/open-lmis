/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

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
public class RefrigeratorProblem extends BaseModel {

  Long readingId;
  Boolean operatorError;
  Boolean burnerProblem;
  Boolean gasLeakage;
  Boolean egpFault;
  Boolean thermostatSetting;
  Boolean other;
  String otherProblemExplanation;

  public void validate() {
    if (!(isTrue(operatorError) || isTrue(burnerProblem) || isTrue(gasLeakage) || isTrue(egpFault) || isTrue(thermostatSetting) || isTrue(other))) {
      throw new DataException("error.invalid.reading.value");
    }
  }
}
