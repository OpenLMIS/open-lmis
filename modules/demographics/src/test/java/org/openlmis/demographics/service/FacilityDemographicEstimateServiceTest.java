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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.RequisitionGroupBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.domain.RightName;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.demographics.builders.AnnualFacilityEstimateBuilder;
import org.openlmis.demographics.builders.EstimateFormLineItemBuilder;
import org.openlmis.demographics.builders.EstimateCategoryBuilder;
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
public class FacilityDemographicEstimateServiceTest {

  @Mock
  DemographicEstimateCategoryService estimateCategoryService;

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
  private FacilityDemographicEstimateService service;

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

    service.finalize(form, 2L);

    verify(repository, never()).insert(facilityEstimateEntry);
    verify(repository, times(1)).update(any(AnnualFacilityEstimateEntry.class));
    verify(repository, times(1)).finalize(any(AnnualFacilityEstimateEntry.class));
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
    verify(repository, never()).finalize(facilityEstimateEntry);
    verify(repository, times(1)).undoFinalize(any(AnnualFacilityEstimateEntry.class));
  }

  @Test
  public void shouldGetEstimateForm() throws Exception {
    AnnualFacilityEstimateEntry facilityEstimateEntry = make(an(AnnualFacilityEstimateBuilder.defaultAnnualFacilityEstimateEntry));

    EstimateCategory category = make(a(EstimateCategoryBuilder.defaultEstimateCategory));
    when(estimateCategoryService.getAll()).thenReturn(asList(category));

    List<SupervisoryNode> nodes = asList( make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(SupervisoryNodeBuilder.id, 1L))));
    when(supervisoryNodeService.getAllSupervisoryNodesInHierarchyBy(1L,3L, RightName.MANAGE_DEMOGRAPHIC_ESTIMATES)).thenReturn(nodes);

    List<RequisitionGroup> groups = asList( make(a(RequisitionGroupBuilder.defaultRequisitionGroup, with(RequisitionGroupBuilder.code, "23"))));
    when(requisitionGroupService.getRequisitionGroupsBy(nodes)).thenReturn(groups);

    when(commaSeparator.commaSeparateIds(groups)).thenReturn("{1}");

    List<EstimateFormLineItem> lineItems = asList(make(a(EstimateFormLineItemBuilder.defaultDemographicEstimateLineItem, with(EstimateFormLineItemBuilder.facilityAnnualEstimates, asList(facilityEstimateEntry)))));
    when(repository.getFacilityList(3L, "{1}")).thenReturn(lineItems);

    EstimateForm form = service.getEstimateForm(1L, 3L, 2015);

    assertThat(form.getEstimateLineItems().size(), is(1));
    assertThat(form.getEstimateLineItems().get(0).getFacilityEstimates().size(), is(1));
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
}