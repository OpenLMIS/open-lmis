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
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.demographics.builders.AnnualDistrictEstimateBuilder;
import org.openlmis.demographics.builders.EstimateFormLineItemBuilder;
import org.openlmis.demographics.domain.AnnualDistrictEstimateEntry;
import org.openlmis.demographics.dto.EstimateForm;
import org.openlmis.demographics.dto.EstimateFormLineItem;
import org.openlmis.demographics.repository.AnnualDistrictEstimateRepository;

import java.util.ArrayList;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class AnnualDistrictDemographicEstimateServiceTest {

  @Mock
  EstimateCategoryService estimateCategoryService;

  @Mock
  private AnnualDistrictEstimateRepository repository;

  @Mock
  private FacilityService facilityService;

  @Mock
  private CommaSeparator commaSeparator;

  @InjectMocks
  AnnualDistrictDemographicEstimateService service;

  private EstimateForm getDemographicEstimateFormForOneDistrict(AnnualDistrictEstimateEntry... facilityEstimateEntry) {
    EstimateForm form = new EstimateForm();
    form.setEstimateLineItems(new ArrayList<EstimateFormLineItem>());

    EstimateFormLineItem lineItem = make(a(EstimateFormLineItemBuilder.defaultDemographicEstimateLineItem));
    lineItem.setDistrictEstimates(new ArrayList<AnnualDistrictEstimateEntry>());
    for (AnnualDistrictEstimateEntry entry : facilityEstimateEntry) {
      lineItem.getDistrictEstimates().add(entry);
    }

    form.getEstimateLineItems().add(lineItem);
    return form;
  }

  @Test
  public void shouldSaveUpdates() throws Exception {

    AnnualDistrictEstimateEntry districtEstimateEntry = make(an(AnnualDistrictEstimateBuilder.defaultAnnualDistrictEstimateEntry,
      with(AnnualDistrictEstimateBuilder.id, 2L),
      with(AnnualDistrictEstimateBuilder.isFinal, false)
    ));
    EstimateForm form = getDemographicEstimateFormForOneDistrict(districtEstimateEntry);
    service.save(form, 2L);

    verify(repository, never()).insert(districtEstimateEntry);
    verify(repository, atLeastOnce()).update(any(AnnualDistrictEstimateEntry.class));
  }

  @Test
  public void shouldCreateNewRecords() throws Exception {
    AnnualDistrictEstimateEntry districtEstimateEntry = make(a(AnnualDistrictEstimateBuilder.defaultAnnualDistrictEstimateEntry));
    EstimateForm form = getDemographicEstimateFormForOneDistrict(districtEstimateEntry);

    service.save(form, 2L);

    verify(repository, atLeastOnce()).insert(districtEstimateEntry);
    verify(repository, never()).update(districtEstimateEntry);
  }

  @Test
  public void shouldNotUpdateIfRecordIsSetAsFinal() throws Exception {
    AnnualDistrictEstimateEntry districtEstimateEntry = make(a(AnnualDistrictEstimateBuilder.defaultAnnualDistrictEstimateEntry,
      with(AnnualDistrictEstimateBuilder.id, 3L),
      with(AnnualDistrictEstimateBuilder.isFinal, true)));

    when(repository.getEntryBy(districtEstimateEntry.getYear(), districtEstimateEntry.getDistrictId(), districtEstimateEntry.getProgramId(), districtEstimateEntry.getDemographicEstimateId()))
      .thenReturn(districtEstimateEntry);

    EstimateForm form = getDemographicEstimateFormForOneDistrict(districtEstimateEntry);

    districtEstimateEntry.setIsFinal(true);
    service.save(form, 2L);

    verify(repository, never()).insert(districtEstimateEntry);
    verify(repository, never()).update(districtEstimateEntry);
  }

  @Test
  public void shouldFinalize() throws Exception {
    AnnualDistrictEstimateEntry districtEstimateEntry = make(a(AnnualDistrictEstimateBuilder.defaultAnnualDistrictEstimateEntry,
      with(AnnualDistrictEstimateBuilder.id, 3L),
      with(AnnualDistrictEstimateBuilder.isFinal, false)));
    EstimateForm form = getDemographicEstimateFormForOneDistrict(districtEstimateEntry);

    service.finalizeEstimate(form, 2L);

    verify(repository, never()).insert(districtEstimateEntry);
    verify(repository, times(1)).update(any(AnnualDistrictEstimateEntry.class));
    verify(repository, times(1)).finalizeEstimate(any(AnnualDistrictEstimateEntry.class));
  }

  @Test
  public void shouldUndoFinalize() throws Exception {
    AnnualDistrictEstimateEntry districtEstimateEntry = make(a(AnnualDistrictEstimateBuilder.defaultAnnualDistrictEstimateEntry,
      with(AnnualDistrictEstimateBuilder.id, 2L),
      with(AnnualDistrictEstimateBuilder.isFinal, true)
    ));
    EstimateForm form = getDemographicEstimateFormForOneDistrict(districtEstimateEntry);

    service.undoFinalize(form, 2L);

    verify(repository, never()).insert(districtEstimateEntry);
    verify(repository, never()).update(districtEstimateEntry);
    verify(repository, never()).finalizeEstimate(districtEstimateEntry);
    verify(repository, times(1)).undoFinalize(any(AnnualDistrictEstimateEntry.class));
  }

}

