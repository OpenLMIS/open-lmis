/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.db.repository;


import lombok.NoArgsConstructor;
import org.openlmis.db.repository.mapper.DbMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
@NoArgsConstructor
public class DbRepository {

  @Autowired
  DbMapper dbMapper;

  public Date getCurrentTimeStamp() {
    return dbMapper.getCurrentTimeStamp();
  }

  public int getCount(String table) {
    return dbMapper.getCount(table);
  }
}
