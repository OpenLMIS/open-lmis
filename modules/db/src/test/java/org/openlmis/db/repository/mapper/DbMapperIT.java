/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.db.repository.mapper;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-db.xml")
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

