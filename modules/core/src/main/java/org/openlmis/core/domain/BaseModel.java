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

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

@Data
@JsonSerialize()
@EqualsAndHashCode(callSuper = false)
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

  @JsonProperty("modifiedDate")
  public Date getModifiedDate() {
    return modifiedDate;
  }

  @JsonIgnore
  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  @JsonProperty("modifiedBy")
  public Long getModifiedBy() {
    return modifiedBy;
  }

  @JsonIgnore
  public void setModifiedBy(Long modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  @JsonProperty("createdDate")
  public Date getCreatedDate() {
    return createdDate;
  }

  @JsonIgnore
  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  @JsonProperty("createdBy")
  public Long getCreatedBy() {
    return createdBy;
  }

  @JsonIgnore
  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }
}
