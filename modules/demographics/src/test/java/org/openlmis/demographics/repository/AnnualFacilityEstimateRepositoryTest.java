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
import org.openlmis.demographics.builders.AnnualFacilityEstimateBuilder;
import org.openlmis.demographics.builders.EstimateFormLineItemBuilder;
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.openlmis.demographics.dto.EstimateFormLineItem;
import org.openlmis.demographics.repository.mapper.AnnualFacilityEstimateMapper;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.an;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class AnnualFacilityEstimateRepositoryTest {

  @Mock
  AnnualFacilityEstimateMapper mapper;

  @InjectMocks
  AnnualFacilityEstimateRepository repository;

  @Test
  public void shouldGetFacilityEstimate() throws Exception {
    when(mapper.getEstimatesForFacility(2005, 2L, 2L)).thenReturn(asList(new AnnualFacilityEstimateEntry()));

    List<?> result = repository.getFacilityEstimate(2005, 2L, 2L);

    assertThat(result.size(), is(1));
    verify(mapper).getEstimatesForFacility(2005, 2L, 2L);
  }

  @Test
  public void shouldInsert() throws Exception {
    AnnualFacilityEstimateEntry estimate = make(a(AnnualFacilityEstimateBuilder.defaultAnnualFacilityEstimateEntry));
    when(mapper.insert(estimate)).thenReturn(1);

    repository.insert(estimate);

    verify(mapper).insert(estimate);
  }

  @Test
  public void shouldUpdate() throws Exception {
    AnnualFacilityEstimateEntry estimate = make(a(AnnualFacilityEstimateBuilder.defaultAnnualFacilityEstimateEntry));
    when(mapper.update(estimate)).thenReturn(1);

    repository.update(estimate);

    verify(mapper).update(estimate);
  }

  @Test
  public void shouldGetFacilityList() throws Exception {
    List<EstimateFormLineItem> lineItems = asList(make(a(EstimateFormLineItemBuilder.defaultDemographicEstimateLineItem)));
    when(mapper.getFacilityList(2L, "123")).thenReturn(lineItems);

    List<EstimateFormLineItem> response = repository.getFacilityList(2L, "123");

    assertThat(response, is(lineItems));
    verify(mapper).getFacilityList(2L, "123");
  }

  @Test
  public void shouldFinalize() throws Exception {
    AnnualFacilityEstimateEntry estimate = make(an(AnnualFacilityEstimateBuilder.defaultAnnualFacilityEstimateEntry));

    repository.finalizeEstimate(estimate);

    verify(mapper).finalizeEstimate(estimate);
  }

  @Test
  public void shouldUndoFinalize() throws Exception {
    AnnualFacilityEstimateEntry estimate = make(an(AnnualFacilityEstimateBuilder.defaultAnnualFacilityEstimateEntry));

    repository.undoFinalize(estimate);

    verify(mapper).undoFinalize(estimate);
  }

  @Test
  public void shouldGetByFacilityProgramYearAndCategory() throws Exception {

    repository.getEntryBy(2005, 2L, 5L, 6L);

    verify(mapper).getEntryBy(2005, 2L, 5L, 6L);
  }


}