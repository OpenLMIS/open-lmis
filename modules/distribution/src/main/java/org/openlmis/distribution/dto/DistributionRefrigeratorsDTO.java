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
import org.apache.commons.collections.Transformer;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.domain.DistributionRefrigerators;
import org.openlmis.distribution.domain.RefrigeratorReading;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.collect;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class DistributionRefrigeratorsDTO extends BaseModel {

  Long facilityVisitId;
  List<RefrigeratorReadingDTO> readings;

  public DistributionRefrigerators transform() {
    List<RefrigeratorReading> refrigeratorReadings = new ArrayList<>();
    final Long createdBy = this.createdBy;
    final Long modifiedBy = this.modifiedBy;
    if (readings != null) {
      refrigeratorReadings = (List) collect(readings, new Transformer() {
        @Override
        public Object transform(Object o) {
          RefrigeratorReadingDTO readingDTO = (RefrigeratorReadingDTO) o;
          readingDTO.setCreatedBy(createdBy);
          readingDTO.setModifiedBy(modifiedBy);
          RefrigeratorReading reading = readingDTO.transform();
          return reading;
        }
      });
    }

    return new DistributionRefrigerators(refrigeratorReadings);
  }
}
