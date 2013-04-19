/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.db.service;

import lombok.NoArgsConstructor;
import org.openlmis.db.repository.DbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@NoArgsConstructor
public class DbService {

  @Autowired
  private DbRepository dbRepository;

  public int getCount(String tableName) {
    return dbRepository.getCount(tableName);
  }

  public Date getCurrentTimestamp() {
    return dbRepository.getCurrentTimeStamp();
  }
}
