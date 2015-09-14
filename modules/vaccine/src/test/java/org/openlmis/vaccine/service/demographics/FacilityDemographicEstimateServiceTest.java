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

package org.openlmis.vaccine.service.demographics;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.openlmis.vaccine.domain.demographics.FacilityDemographicEstimate;
import org.openlmis.vaccine.dto.DemographicEstimateForm;
import org.openlmis.vaccine.dto.DemographicEstimateLineItem;
import org.openlmis.vaccine.repository.demographics.FacilityDemographicEstimateRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityDemographicEstimateServiceTest {

  @Mock
  FacilityDemographicEstimateRepository repository;

  @Mock
  FacilityService facilityService;

  @Mock
  DemographicEstimateCategoryService categoryService;

  @Mock
  SupervisoryNodeService supervisoryNodeService;

  @Mock
  CommaSeparator commaSeparator;


  @Mock
  RequisitionGroupService requisitionGroupService;

  @InjectMocks
  FacilityDemographicEstimateService service;
  @Test
  public void shouldSave() throws Exception {
    DemographicEstimateForm form = new DemographicEstimateForm();
    DemographicEstimateLineItem estimateLineItem = new DemographicEstimateLineItem();
    estimateLineItem.setFacilityEstimates(new ArrayList<FacilityDemographicEstimate>());
    estimateLineItem.getFacilityEstimates().add(new FacilityDemographicEstimate(2000, 12L, 1L, false, 2L, null, 1.0d, 2L));
    form.setEstimateLineItems(asList(estimateLineItem));

    when(repository.insert(estimateLineItem.getFacilityEstimates().get(0))).thenReturn(1);
    service.save(form, 1L);
    verify(repository, atLeastOnce()).insert(Matchers.<FacilityDemographicEstimate>any());
  }

  @Test
  public void shouldGetEstimateFor() throws Exception {
    DemographicEstimateCategory category1 = new DemographicEstimateCategory();
    category1.setDefaultConversionFactor(50.0);
    DemographicEstimateCategory category2 = new DemographicEstimateCategory();
    category2.setDefaultConversionFactor(5.0);

    Facility facility = new Facility();
    facility.setId(39L);
    facility.setCatchmentPopulation(20000L);
    facility.setGeographicZone(new GeographicZone());
    facility.getGeographicZone().setName("Geo Name");

    List<SupervisoryNode> nodes = asList(new SupervisoryNode());
    when(supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(1L, 2L, RightName.MANAGE_DEMOGRAPHIC_ESTIMATES)).thenReturn(nodes);
    List<RequisitionGroup> groups = asList(new RequisitionGroup());
    groups.get(0).setId(1L);

    when(requisitionGroupService.getRequisitionGroupsBy(nodes)).thenReturn(groups);
    when(facilityService.getUserSupervisedFacilities(1L, 2L, RightName.MANAGE_DEMOGRAPHIC_ESTIMATES)).thenReturn(asList(facility));
    when(categoryService.getAll()).thenReturn(asList(category1, category2));
    when(commaSeparator.commaSeparateIds(groups)).thenReturn("{1}");

    when(repository.getFacilityList(2L, "{1}")).thenReturn(asList(new DemographicEstimateLineItem()));

    DemographicEstimateForm form = service.getEstimateForm(1L, 2L, 2005);

    verify(categoryService, atMost(1)).getAll();
    assertThat(form.getEstimateLineItems().size(), is(1));
    assertThat(form.getEstimateLineItems().get(0).getFacilityEstimates().size(), is(2));
    assertThat(form.getEstimateLineItems().get(0).getFacilityEstimates().get(0).getConversionFactor(), is(category1.getDefaultConversionFactor()));

  }

  @Test
  public void shouldGetEstimateValuesForFacilityWhenEstimatesWereSaved() throws Exception {
    List<FacilityDemographicEstimate> list = asList(new FacilityDemographicEstimate());
    when(repository.getFacilityEstimateWithDetails(2005, 2L, 2L)).thenReturn(list);

    List<FacilityDemographicEstimate> response = service.getEstimateValuesForFacility(2L, 2L, 2005);
    assertThat(response, is(list));
    verify(facilityService, never()).getById(any(Long.class));
    verify(categoryService, never()).getAll();
  }

  @Test
  public void shouldGetEstimateValuesForFacilityWhenEstimatesAreNotFound() throws Exception{
    DemographicEstimateCategory category1 = new DemographicEstimateCategory();
    category1.setDefaultConversionFactor(50.0);
    DemographicEstimateCategory category2 = new DemographicEstimateCategory();
    category2.setDefaultConversionFactor(5.0);

    Facility facility = new Facility();
    facility.setCatchmentPopulation(20000L);
    when(facilityService.getById(2L)).thenReturn(facility);
    when(categoryService.getAll()).thenReturn(asList(category1, category2));
    when(repository.getFacilityEstimate(2005, 2L, 2L)).thenReturn(null);


    List<FacilityDemographicEstimate> response = service.getEstimateValuesForFacility(2L, 2L, 2005);
    assertThat(response.size(), is(2));
    assertThat(response.get(0).getValue(), is(10000L));
    assertThat(response.get(1).getValue(), is(1000L));

    verify(categoryService, atMost(1)).getAll();
  }

}