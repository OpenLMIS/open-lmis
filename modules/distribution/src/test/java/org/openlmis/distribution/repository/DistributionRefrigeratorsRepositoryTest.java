/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.DistributionRefrigerators;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.repository.mapper.DistributionRefrigeratorsMapper;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionRefrigeratorsRepositoryTest {

  @InjectMocks
  private DistributionRefrigeratorsRepository repository;

  @Mock
  private DistributionRefrigeratorsMapper mapper;

  @Test
  public void shouldSave() throws Exception {

    DistributionRefrigerators distributionRefrigerators = new DistributionRefrigerators();
    RefrigeratorReading refrigeratorReading = mock(RefrigeratorReading.class);
    distributionRefrigerators.setReadings(asList(refrigeratorReading));

    repository.save(distributionRefrigerators);

    verify(mapper).insert(distributionRefrigerators);
    verify(refrigeratorReading).setDistributionRefrigeratorsId(distributionRefrigerators.getId());
    verify(mapper).insertReading(refrigeratorReading);
  }
}
