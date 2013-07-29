/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public abstract class LineItem extends BaseModel {

  protected Long rnrId;

  abstract public boolean compareCategory(LineItem lineItem);

  @JsonIgnore
  abstract public String getCategoryName();

  @JsonIgnore
  abstract public String getValue(String columnName) throws NoSuchFieldException, IllegalAccessException;

  @JsonIgnore
  public abstract boolean isRnrLineItem();

}
