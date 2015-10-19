/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.demographics.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.demographics.builders.AnnualDistrictEstimateBuilder;
import org.openlmis.demographics.domain.AnnualDistrictEstimateEntry;
import org.openlmis.demographics.repository.mapper.AnnualDistrictEstimateMapper;

import static com.natpryce.makeiteasy.MakeItEasy.an;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.verify;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class AnnualDistrictEstimateRepositoryTest {

  @Mock
  AnnualDistrictEstimateMapper mapper;

  @InjectMocks
  AnnualDistrictEstimateRepository repository;

  @Test
  public void shouldGetDistrictEstimates() throws Exception {

    repository.getDistrictEstimates(2015, 2L, 3L);

    verify(mapper).getEstimatesForDistrict(2015,2L, 3L);
  }

  @Test
  public void shouldInsert() throws Exception {
    AnnualDistrictEstimateEntry estimate = make(an(AnnualDistrictEstimateBuilder.defaultAnnualDistrictEstimateEntry));

    repository.insert(estimate);

    verify(mapper).insert(estimate);
  }

  @Test
  public void shouldUpdate() throws Exception {
    AnnualDistrictEstimateEntry estimate = make(an(AnnualDistrictEstimateBuilder.defaultAnnualDistrictEstimateEntry));

    repository.update(estimate);

    verify(mapper).update(estimate);
  }

  @Test
  public void shouldGetDistrictLineItems() throws Exception {

    repository.getDistrictLineItems("12,3");

    verify(mapper).getDistrictLineItems("12,3");
  }

  @Test
  public void shouldGetFacilityEstimateAggregate() throws Exception {

    repository.getFacilityEstimateAggregate(2015,12L,2L);

    verify(mapper).getFacilityEstimateAggregate(2015,12L,2L);
  }

  @Test
  public void shouldFinalize() throws Exception {
    AnnualDistrictEstimateEntry estimate = make(an(AnnualDistrictEstimateBuilder.defaultAnnualDistrictEstimateEntry));

    repository.finalizeEstimate(estimate);

    verify(mapper).finalizeEstimate(estimate);
  }

  @Test
  public void shouldUndoFinalize() throws Exception {
    AnnualDistrictEstimateEntry estimate = make(an(AnnualDistrictEstimateBuilder.defaultAnnualDistrictEstimateEntry));

    repository.undoFinalize(estimate);

    verify(mapper).undoFinalize(estimate);
  }

  @Test
  public void shouldGetByFacilityProgramYearAndCategory() throws Exception {

    repository.getEntryBy(2005, 2L, 5L, 6L);

    verify(mapper).getEntryBy(2005, 2L, 5L, 6L);
  }

}