/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.utils.DateUtil;

import java.util.Date;

/**
 * Class BaseModel is the root of the domain objects hierarchy. It defines identity and audit fields for any entity.
 * Most domain objects have BaseModel as a superclass which defines its primary attributes.
 */

@Getter
@Setter
@NoArgsConstructor
@JsonSerialize()
public abstract class BaseModel {

  protected Long id;

  @JsonIgnore
  protected Long createdBy;

  @JsonIgnore
  protected Long modifiedBy;

  @JsonIgnore
  protected Date createdDate;

  @JsonIgnore
  protected Date modifiedDate;

  public BaseModel(Long id) {
    this.id = id;
  }

  protected static String getFormattedDate(Date date)
  {
    return DateUtil.getFormattedDate(date, "yyyy-dd-MM");
  }

  /**
   * Returns weather this object has a domain identity.  That is, is it persistent.
   *
   * @return true if it has an id.  False otherwise.
   */
  public boolean hasId() {
    return (null == id) ? false : true;
  }
}
