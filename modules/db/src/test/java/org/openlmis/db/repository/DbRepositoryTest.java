/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.db.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.db.repository.mapper.DbMapper;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DbRepositoryTest {

  @Mock
  DbMapper dbMapper;

  @InjectMocks
  DbRepository dbRepository;

  @Test
  public void shouldGetCurrentDbTimeStamp() throws Exception {
    Date expectedTimeStamp = new Date();
    when(dbMapper.getCurrentTimeStamp()).thenReturn(expectedTimeStamp);

    Date currentTimeStamp = dbRepository.getCurrentTimeStamp();

    assertThat(currentTimeStamp, is(expectedTimeStamp));
    verify(dbMapper).getCurrentTimeStamp();
  }

  @Test
  public void shouldGetCountByTableName() throws Exception {
    String table = "facilities";
    when(dbMapper.getCount(table)).thenReturn(15);

    int facilityCount = dbRepository.getCount(table);

    assertThat(facilityCount, is(15));
    verify(dbMapper).getCount(table);
  }
}
