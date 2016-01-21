/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.upload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * AuditFields hold a userId and timestamp, which is used to set audit fields for an imported entity.
 */

@Data
@NoArgsConstructor
public class AuditFields {

  private Long user;
  private Date currentTimestamp;
  private List<String> headers;

  public AuditFields(Date currentTimestamp) {
    this.currentTimestamp = currentTimestamp;
  }

  public AuditFields(Long user, Date currentTimestamp) {
    this.user = user;
    this.currentTimestamp = currentTimestamp;
  }
}
