/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
  protected Long modifiedBy;

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


}
