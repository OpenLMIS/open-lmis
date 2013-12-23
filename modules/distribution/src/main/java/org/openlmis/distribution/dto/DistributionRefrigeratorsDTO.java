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
import org.apache.commons.collections.Transformer;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.domain.DistributionRefrigerators;
import org.openlmis.distribution.domain.RefrigeratorReading;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.collect;

@AllArgsConstructor
public class DistributionRefrigeratorsDTO extends BaseModel {

  Long facilityId;
  Long distributionId;
  List<RefrigeratorReadingDTO> readings;

  public DistributionRefrigerators transform() {
    List<RefrigeratorReading> refrigeratorReadings = (List) collect(readings, new Transformer() {
      @Override
      public Object transform(Object o) {
        RefrigeratorReading reading = ((RefrigeratorReadingDTO) o).transform(facilityId);
        return reading;
      }
    });

    return new DistributionRefrigerators(facilityId, distributionId, refrigeratorReadings);
  }
}
