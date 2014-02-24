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
import org.openlmis.distribution.domain.*;
import org.openlmis.distribution.repository.mapper.VaccinationCoverageMapper;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class VaccinationCoverageRepositoryTest {

  @Mock
  private VaccinationCoverageMapper mapper;

  @InjectMocks
  private VaccinationCoverageRepository repository;

  @Test
  public void shouldSaveChildCoverage() throws Exception {
    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem();
    OpenedVialLineItem openedVialLineItem = new OpenedVialLineItem();
    VaccinationChildCoverage vaccinationChildCoverage = new VaccinationChildCoverage();
    vaccinationChildCoverage.setChildCoverageLineItems(asList(childCoverageLineItem));
    vaccinationChildCoverage.setOpenedVialLineItems(asList(openedVialLineItem));

    repository.saveChildCoverage(vaccinationChildCoverage);

    verify(mapper).insertChildCoverageLineItem(childCoverageLineItem);
    verify(mapper).insertChildCoverageOpenedVialLineItem(openedVialLineItem);
  }

  @Test
  public void shouldUpdateChildCoverageIfIdExists() throws Exception {
    ChildCoverageLineItem childCoverageLineItem = new ChildCoverageLineItem();
    childCoverageLineItem.setId(12345L);
    OpenedVialLineItem openedVialLineItem = new OpenedVialLineItem();
    openedVialLineItem.setId(2345L);
    VaccinationChildCoverage vaccinationChildCoverage = new VaccinationChildCoverage();
    vaccinationChildCoverage.setChildCoverageLineItems(asList(childCoverageLineItem));
    vaccinationChildCoverage.setOpenedVialLineItems(asList(openedVialLineItem));

    repository.saveChildCoverage(vaccinationChildCoverage);

    verify(mapper).updateChildCoverageLineItem(childCoverageLineItem);
    verify(mapper).updateChildCoverageOpenedVialLineItem(openedVialLineItem);
  }

  @Test
  public void shouldInsertAdultCoverageLineItemsAndOpenedVialLineItems(){
    VaccinationAdultCoverage adultCoverage = new VaccinationAdultCoverage(asList(new AdultCoverageLineItem(), new AdultCoverageLineItem()));
    OpenedVialLineItem openedVialLineItem = new OpenedVialLineItem();
    adultCoverage.setOpenedVialLineItems(asList(openedVialLineItem));

    repository.saveAdultCoverage(adultCoverage);

    verify(mapper, times(2)).insertAdultCoverageLineItem(any(AdultCoverageLineItem.class));
    verify(mapper).insertAdultCoverageOpenedVialLineItem(openedVialLineItem);
  }

  @Test
  public void shouldUpdateAdultCoverageLineItemsAndOpenedVialLineItemsIfAlreadyExists() throws Exception {
    AdultCoverageLineItem adultCoverageLineItem = new AdultCoverageLineItem();
    adultCoverageLineItem.setId(1L);
    VaccinationAdultCoverage adultCoverage = new VaccinationAdultCoverage(asList(adultCoverageLineItem));
    OpenedVialLineItem openedVialLineItem = new OpenedVialLineItem();
    openedVialLineItem.setId(2L);
    adultCoverage.setOpenedVialLineItems(asList(openedVialLineItem));

    repository.saveAdultCoverage(adultCoverage);

    verify(mapper).updateAdultCoverageLineItem(adultCoverageLineItem);
    verify(mapper).updateAdultCoverageOpenedVialLineItem(openedVialLineItem);

  }
}
