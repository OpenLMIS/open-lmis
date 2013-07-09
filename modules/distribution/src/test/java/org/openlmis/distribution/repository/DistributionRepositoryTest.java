/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.repository.mapper.DistributionMapper;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionRepositoryTest {

  @Mock
  DistributionMapper mapper;

  @InjectMocks
  DistributionRepository repository;

  @Test
  public void itShouldUseMapperToCreateDistribution() throws Exception {
    Distribution distribution = new Distribution();
    doNothing().when(mapper).insert(distribution);
    repository.create(distribution);

    verify(mapper).insert(distribution);
  }
}
