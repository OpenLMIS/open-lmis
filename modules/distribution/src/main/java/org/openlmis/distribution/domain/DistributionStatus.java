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

import lombok.Getter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.distribution.serializer.StatusDeSerializer;
import org.openlmis.distribution.serializer.StatusSerializer;

/**
 *  Enum for statuses of a distribution. These distribution statuses are used to get/set the current status of an
 *  initiated distribution.
 */

@JsonSerialize(using = StatusSerializer.class)
@JsonDeserialize(using = StatusDeSerializer.class)
public enum DistributionStatus {

  INITIATED("msg.status.initiated"),
  COMPLETED("msg.status.completed"),
  SYNCED("msg.status.synced");

  @Getter
  private final String statusKey;

  DistributionStatus(String statusKey) {
    this.statusKey = statusKey;
  }

}
