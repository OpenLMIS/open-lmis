/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.openlmis.distribution.domain.DistributionStatus.INITIATED;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionRepositoryTest {

  @Mock
  DistributionMapper mapper;

  @InjectMocks
  DistributionRepository repository;

  @Test
  public void shouldCreateDistributionInInitiatedState() throws Exception {
    Distribution distribution = new Distribution();
    doNothing().when(mapper).insert(distribution);

    Distribution initiatedDistribution = repository.create(distribution);

    assertThat(initiatedDistribution.getStatus(), is(INITIATED));
    verify(mapper).insert(distribution);
  }

  @Test
  public void shouldGetDistributionIfExists() throws Exception {
    Distribution distribution = new Distribution();
    repository.get(distribution);

    verify(mapper).get(distribution);
  }
}
