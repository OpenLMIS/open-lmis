/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.demographics;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.builders.demographics.FacilityDemographicEstimateBuilder;
import org.openlmis.vaccine.domain.demographics.FacilityDemographicEstimate;
import org.openlmis.vaccine.repository.mapper.demographics.FacilityDemographicEstimateMapper;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityDemographicEstimateRepositoryTest {

  @Mock
  FacilityDemographicEstimateMapper mapper;

  @InjectMocks
  FacilityDemographicEstimateRepository repository;

  @Test
  public void shouldGetFacilityEstimate() throws Exception {
    when(mapper.getEstimatesForFacility(2005, 2L)).thenReturn(asList(new FacilityDemographicEstimate()));
    List<?> result = repository.getFacilityEstimate(2005, 2L);
    assertThat(result.size(), is(1));
    verify(mapper).getEstimatesForFacility(2005, 2L);
  }

  @Test
  public void shouldInsert() throws Exception {
    FacilityDemographicEstimate estimate = make(a(FacilityDemographicEstimateBuilder.defaultFacilityDemographicEstimate));
    when(mapper.insert(estimate)).thenReturn(1);
    repository.insert(estimate);
    verify(mapper).insert(estimate);
  }

  @Test
  public void shouldUpdate() throws Exception {
    FacilityDemographicEstimate estimate = make(a(FacilityDemographicEstimateBuilder.defaultFacilityDemographicEstimate));
    when(mapper.update(estimate)).thenReturn(1);
    repository.update(estimate);
    verify(mapper).update(estimate);
  }
}