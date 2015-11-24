/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.repository.RequisitionGroupProgramScheduleRepository;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.RequisitionGroupProgramScheduleBuilder.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RequisitionGroupProgramScheduleServiceTest {

  @Mock
  RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;

  @InjectMocks
  private RequisitionGroupProgramScheduleService service;

  @Test
  public void shouldInsertRequisitionGroupProgramScheduleWhenIdIsNull() throws Exception {
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();

    service.save(requisitionGroupProgramSchedule);

    verify(requisitionGroupProgramScheduleRepository).insert(requisitionGroupProgramSchedule);
    verify(requisitionGroupProgramScheduleRepository, never()).update(requisitionGroupProgramSchedule);
  }

  @Test
  public void shouldUpdateRequisitionGroupProgramScheduleWhenIdIsNotNull() throws Exception {
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();
    requisitionGroupProgramSchedule.setId(1L);

    service.save(requisitionGroupProgramSchedule);

    verify(requisitionGroupProgramScheduleRepository).update(requisitionGroupProgramSchedule);
    verify(requisitionGroupProgramScheduleRepository, never()).insert(requisitionGroupProgramSchedule);
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
