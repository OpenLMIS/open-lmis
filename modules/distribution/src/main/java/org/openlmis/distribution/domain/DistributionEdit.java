/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.distribution.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.User;
import org.openlmis.core.serializer.DateTimeSerializer;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@EqualsAndHashCode(callSuper = false)
@Data
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistributionEdit implements Comparable<DistributionEdit> {

    User user;
    Distribution distribution;

    @JsonSerialize(using = DateTimeSerializer.class)
    Date startedAt;

    @Override
    public int compareTo(DistributionEdit arg) {
        if (startedAt == arg.startedAt) {
            return 0;
        }

        if (startedAt == null) {
            return 1;
        }

        if (arg.startedAt == null) {
            return -1;
        }

        return Long.compare(arg.getStartedAt().getTime(), startedAt.getTime());
    }
}
