/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Data
@NoArgsConstructor
public class Report extends BaseModel {

  private String name;

  private byte[] data;

  private String parameters;

  public Report(MultipartFile file, Integer modifiedBy) throws IOException {
    this.name = file.getName();
    this.data = file.getBytes();
    this.modifiedBy = modifiedBy;
  }
}
