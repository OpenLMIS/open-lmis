/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.db.repository.mapper;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-db.xml")
public class DbMapperIT {

  @Autowired
  DbMapper dbMapper;

  @Autowired
  ComboPooledDataSource dataSource;

  @Test
  public void shouldGetCurrentDbTimeStamp() throws Exception {
    Date currentTimeStamp = dbMapper.getCurrentTimeStamp();

    assertThat(currentTimeStamp, is(any(Date.class)));
    assertThat(currentTimeStamp, is(notNullValue()));
  }

  @Test
  public void shouldGetCountOfRecordsByTableName() throws Exception {
    int userCount = dbMapper.getCount("geographic_levels");

    assertThat(userCount, is(4));
  }
}

