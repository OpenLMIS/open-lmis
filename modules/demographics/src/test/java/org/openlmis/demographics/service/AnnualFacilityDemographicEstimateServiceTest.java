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

package org.openlmis.demographics.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.demographics.builders.AnnualFacilityEstimateBuilder;
import org.openlmis.demographics.builders.EstimateCategoryBuilder;
import org.openlmis.demographics.builders.EstimateFormLineItemBuilder;
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.openlmis.demographics.domain.EstimateCategory;
import org.openlmis.demographics.dto.EstimateForm;
import org.openlmis.demographics.dto.EstimateFormLineItem;
import org.openlmis.demographics.repository.AnnualFacilityEstimateRepository;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class AnnualFacilityDemographicEstimateServiceTest {

  @Mock
  EstimateCategoryService estimateCategoryService;

  @Mock
  private AnnualFacilityEstimateRepository repository;

  @Mock
  private FacilityService facilityService;

  @Mock
  private CommaSeparator commaSeparator;

  @Mock
  private SupervisoryNodeService supervisoryNodeService;

  @Mock
  private RequisitionGroupService requisitionGroupService;

  @InjectMocks
  private AnnualFacilityDemographicEstimateService service;

  private EstimateForm getDemographicEstimateFormForOneFacility(AnnualFacilityEstimateEntry... facilityEstimateEntry) {
    EstimateForm form  = new EstimateForm();
    form.setEstimateLineItems(new ArrayList<EstimateFormLineItem>());

    EstimateFormLineItem lineItem = make(a(EstimateFormLineItemBuilder.defaultDemographicEstimateLineItem));
    lineItem.setFacilityEstimates(new ArrayList<AnnualFacilityEstimateEntry>());
    for(AnnualFacilityEstimateEntry entry : facilityEstimateEntry){
      lineItem.getFacilityEstimates().add(entry);
    }

    form.getEstimateLineItems().add(lineItem);
    return form;
  }

  @Test
  public void shouldSaveUpdates() throws Exception {

    AnnualFacilityEstimateEntry facilityEstimateEntry = make(an(AnnualFacilityEstimateBuilder.defaultAnnualFacilityEstimateEntry,
      with(AnnualFacilityEstimateBuilder.id, 2L),
      with(AnnualFacilityEstimateBuilder.isFinal, false)
    ));
    EstimateForm form = getDemographicEstimateFormForOneFacility(facilityEstimateEntry);
    service.save(form, 2L);

    verify(repository, never()).insert(facilityEstimateEntry);
    verify(repository, atLeastOnce()).update(any(AnnualFacilityEstimateEntry.class));
  }

  @Test
  public void shouldCreateNewRecords() throws Exception {
    AnnualFacilityEstimateEntry facilityEstimateEntry = make(a(AnnualFacilityEstimateBuilder.defaultAnnualFacilityEstimateEntry));
    EstimateForm form = getDemographicEstimateFormForOneFacility(facilityEstimateEntry);

    service.save(form, 2L);

    verify(repository, atLeastOnce()).insert(facilityEstimateEntry);
    verify(repository, never()).update(facilityEstimateEntry);
  }

  @Test
  public void shouldNotUpdateIfRecordIsSetAsFinal() throws Exception {
    AnnualFacilityEstimateEntry facilityEstimateEntry = make(a(AnnualFacilityEstimateBuilder.defaultAnnualFacilityEstimateEntry,
      with(AnnualFacilityEstimateBuilder.id, 3L),
      with(AnnualFacilityEstimateBuilder.isFinal, true)));

    when(repository.getEntryBy(facilityEstimateEntry.getYear(), facilityEstimateEntry.getFacilityId(), facilityEstimateEntry.getProgramId(), facilityEstimateEntry.getDemographicEstimateId()))
      .thenReturn(facilityEstimateEntry);


    EstimateForm form = getDemographicEstimateFormForOneFacility(facilityEstimateEntry);

    facilityEstimateEntry.setIsFinal(true);
    service.save(form, 2L);

    verify(repository, never()).insert(facilityEstimateEntry);
    verify(repository, never()).update(facilityEstimateEntry);
  }

  @Test
  public void shouldFinalize() throws Exception {
    AnnualFacilityEstimateEntry facilityEstimateEntry = make(a(AnnualFacilityEstimateBuilder.defaultAnnualFacilityEstimateEntry,
      with(AnnualFacilityEstimateBuilder.id, 3L),
      with(AnnualFacilityEstimateBuilder.isFinal,false)));
    EstimateForm form = getDemographicEstimateFormForOneFacility(facilityEstimateEntry);

    service.finalizeEstimate(form, 2L);

    verify(repository, never()).insert(facilityEstimateEntry);
    verify(repository, times(1)).update(any(AnnualFacilityEstimateEntry.class));
    verify(repository, times(1)).finalizeEstimate(any(AnnualFacilityEstimateEntry.class));
  }

  @Test
  public void shouldUndoFinalize() throws Exception {
    AnnualFacilityEstimateEntry facilityEstimateEntry = make(a(AnnualFacilityEstimateBuilder.defaultAnnualFacilityEstimateEntry,
        with(AnnualFacilityEstimateBuilder.id, 2L),
        with(AnnualFacilityEstimateBuilder.isFinal, true)
      ));
    EstimateForm form = getDemographicEstimateFormForOneFacility(facilityEstimateEntry);

    service.undoFinalize(form, 2L);

    verify(repository, never()).insert(facilityEstimateEntry);
    verify(repository, never()).update(facilityEstimateEntry);
    verify(repository, never()).finalizeEstimate(facilityEstimateEntry);
    verify(repository, times(1)).undoFinalize(any(AnnualFacilityEstimateEntry.class));
  }


  @Test
  public void shouldGetEstimateValuesForFacilityWhenDemographicEstimateIsSaved() throws Exception {
    List<AnnualFacilityEstimateEntry> list = asList(make(an(AnnualFacilityEstimateBuilder.defaultAnnualFacilityEstimateEntry)));
    when(repository.getFacilityEstimateWithDetails(2015, 20L, 3L)).thenReturn(list);

    List<AnnualFacilityEstimateEntry> result = service.getEstimateValuesForFacility(20L, 3L, 2015);
    assertThat(result,is(list));
  }

  @Test
  public void shouldGetEstimateValuesForFacilityWhenDemographicEstimateIsNeverSubmitted() throws Exception {
    when(repository.getFacilityEstimateWithDetails(2015, 20L, 3L)).thenReturn(null);

    Facility facility = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.facilityId, 20L)));
    facility.setCatchmentPopulation(2000L);
    when(facilityService.getById(facility.getId())).thenReturn(facility);

    EstimateCategory category = make(a(EstimateCategoryBuilder.defaultEstimateCategory, with(EstimateCategoryBuilder.conversionFactor, 50d)));
    when(estimateCategoryService.getAll()).thenReturn(asList(category));

    List<AnnualFacilityEstimateEntry> result = service.getEstimateValuesForFacility(20L, 3L, 2015);
    assertThat(result.get(0).getValue(),is(1000L));
  }


  @Test
  public void shouldSave() throws Exception {
    EstimateForm form = new EstimateForm();
    EstimateFormLineItem estimateLineItem = new EstimateFormLineItem();
    estimateLineItem.setFacilityEstimates(new ArrayList<AnnualFacilityEstimateEntry>());
    estimateLineItem.getFacilityEstimates().add(new AnnualFacilityEstimateEntry(2000, 12L, 1L, false, 2L, null, 1.0d, 2L));
    form.setEstimateLineItems(asList(estimateLineItem));

    when(repository.insert(estimateLineItem.getFacilityEstimates().get(0))).thenReturn(1);
    service.save(form, 1L);
    verify(repository, atLeastOnce()).insert(Matchers.<AnnualFacilityEstimateEntry>any());
  }

  @Test
  public void shouldGetEstimateForm() throws Exception {
    EstimateCategory category1 = new EstimateCategory();
    category1.setDefaultConversionFactor(50.0);
    EstimateCategory category2 = new EstimateCategory();
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
    when(estimateCategoryService.getAll()).thenReturn(asList(category1, category2));
    when(commaSeparator.commaSeparateIds(groups)).thenReturn("{1}");

    when(repository.getFacilityList(2L, "{1}")).thenReturn(asList(new EstimateFormLineItem()));

    EstimateForm form = service.getEstimateForm(1L, 2L, 2005);

    verify(estimateCategoryService, atMost(1)).getAll();
    assertThat(form.getEstimateLineItems().size(), is(1));
    assertThat(form.getEstimateLineItems().get(0).getFacilityEstimates().size(), is(2));
    assertThat(form.getEstimateLineItems().get(0).getFacilityEstimates().get(0).getConversionFactor(), is(category1.getDefaultConversionFactor()));

  }

  @Test
  public void shouldGetEstimateValuesForFacilityWhenEstimatesWereSaved() throws Exception {
    List<AnnualFacilityEstimateEntry> list = asList(new AnnualFacilityEstimateEntry());
    when(repository.getFacilityEstimateWithDetails(2005, 2L, 2L)).thenReturn(list);

    List<AnnualFacilityEstimateEntry> response = service.getEstimateValuesForFacility(2L, 2L, 2005);
    assertThat(response, is(list));
    verify(facilityService, never()).getById(any(Long.class));
    verify(estimateCategoryService, never()).getAll();
  }

  @Test
  public void shouldGetEstimateValuesForFacilityWhenEstimatesAreNotFound() throws Exception{
    EstimateCategory category1 = new EstimateCategory();
    category1.setDefaultConversionFactor(50.0);
    EstimateCategory category2 = new EstimateCategory();
    category2.setDefaultConversionFactor(5.0);

    Facility facility = new Facility();
    facility.setCatchmentPopulation(20000L);
    when(facilityService.getById(2L)).thenReturn(facility);
    when(estimateCategoryService.getAll()).thenReturn(asList(category1, category2));
    when(repository.getFacilityEstimate(2005, 2L, 2L)).thenReturn(null);


    List<AnnualFacilityEstimateEntry> response = service.getEstimateValuesForFacility(2L, 2L, 2005);
    assertThat(response.size(), is(2));
    assertThat(response.get(0).getValue(), is(10000L));
    assertThat(response.get(1).getValue(), is(1000L));

    verify(estimateCategoryService, atMost(1)).getAll();
  }
}