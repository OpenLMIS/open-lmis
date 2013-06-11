/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
public class ProductGroup extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Product Group Code")
  private String code;

  @ImportField(mandatory = true, name = "Product Group Name")
  private String name;

  private Long createdBy;

  private Date createdDate;

}
