/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.repository.RequisitionGroupProgramScheduleRepository;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.RequisitionGroupProgramScheduleBuilder.*;

public class RequisitionGroupProgramScheduleServiceTest {

  @Mock
  RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;

  private RequisitionGroupProgramScheduleService service;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    service = new RequisitionGroupProgramScheduleService(requisitionGroupProgramScheduleRepository);
  }

  @Test
  public void shouldSaveRequisitionGroupProgramSchedule() throws Exception {

    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();

    service.save(requisitionGroupProgramSchedule);

    verify(requisitionGroupProgramScheduleRepository).insert(requisitionGroupProgramSchedule);
  }

  @Test
  public void shouldGetScheduleForRequisitionGroupCodeAndProgramCodeCombination() throws Exception {

    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = make(a(defaultRequisitionGroupProgramSchedule));
    RequisitionGroupProgramSchedule fetchedRequisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();

    when(requisitionGroupProgramScheduleRepository.getScheduleForRequisitionGroupCodeAndProgramCode(
      REQUISITION_GROUP_CODE, PROGRAM_CODE)).thenReturn(fetchedRequisitionGroupProgramSchedule);

    assertThat(service.getScheduleForRequisitionGroupCodeAndProgramCode(requisitionGroupProgramSchedule), is(fetchedRequisitionGroupProgramSchedule));
  }
}
