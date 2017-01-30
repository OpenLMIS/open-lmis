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
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;
import org.openlmis.distribution.dto.Reading;
import org.openlmis.distribution.dto.RefrigeratorProblemDTO;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
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
public class RefrigeratorProblem extends BaseModel {

  Long readingId;
  Boolean operatorError;
  Boolean burnerProblem;
  Boolean gasLeakage;
  Boolean egpFault;
  Boolean thermostatSetting;
  Boolean other;
  String otherProblemExplanation;

  public RefrigeratorProblem(Long readingId) {
    this.readingId = readingId;
  }

  public void validate() {
    if (!(isTrue(operatorError) || isTrue(burnerProblem) || isTrue(gasLeakage) || isTrue(egpFault) || isTrue(thermostatSetting) || isTrue(other))) {
      throw new DataException("error.invalid.reading.value");
    }
  }

  public RefrigeratorProblemDTO transform() {
    RefrigeratorProblemDTO dto = new RefrigeratorProblemDTO();
    dto.setId(id);
    dto.setCreatedBy(createdBy);
    dto.setCreatedDate(createdDate);
    dto.setModifiedBy(modifiedBy);
    dto.setModifiedDate(modifiedDate);
    dto.setReadingId(readingId);
    dto.setOperatorError(new Reading(operatorError));
    dto.setBurnerProblem(new Reading(burnerProblem));
    dto.setGasLeakage(new Reading(gasLeakage));
    dto.setEgpFault(new Reading(egpFault));
    dto.setThermostatSetting(new Reading(thermostatSetting));
    dto.setOther(new Reading(other));
    dto.setOtherProblemExplanation(new Reading(otherProblemExplanation));

    return dto;
  }
}
