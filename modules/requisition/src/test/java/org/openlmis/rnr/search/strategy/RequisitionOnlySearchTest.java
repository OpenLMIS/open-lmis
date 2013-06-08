/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.search.strategy;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RequisitionOnlySearchTest {

  @Mock
  RequisitionRepository requisitionRepository;

  @Test
  public void testSearch() throws Exception {
    Long facilityId = 1L, programId = 1L, periodId = 4L;
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facilityId, programId, periodId);
    RequisitionOnlySearch requisitionOnlySearch = new RequisitionOnlySearch(criteria, requisitionRepository);
    Rnr requisition = new Rnr();
    when(requisitionRepository.getRequisitionWithoutLineItems(facilityId, programId, periodId)).thenReturn(requisition);

    List<Rnr> actualRequisitions = requisitionOnlySearch.search();

    assertThat(actualRequisitions, is(asList(requisition)));
    verify(requisitionRepository).getRequisitionWithoutLineItems(facilityId, programId, periodId);

  }
}
