/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.dto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRequisition;
import static org.openlmis.rnr.builder.RequisitionBuilder.emergency;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RnrDTO.class)
public class RnrDTOTest {

  @Test
  public void shouldPrepareRequisitionsForApproval() throws Exception {
    Rnr rnr = make(a(defaultRequisition, with(emergency, true)));
    rnr.setModifiedDate(new Date());
    List<Rnr> rnrList = Arrays.asList(rnr);

    List<RnrDTO> rnrDTOs = RnrDTO.prepareForListApproval(rnrList);

    assertThat(rnrDTOs.size(), is(1));
    RnrDTO rnrDTO = rnrDTOs.get(0);
    assertRnrDTO(rnrDTO, rnr);
  }

  @Test
  public void shouldPrepareRequisitionsForView() throws Exception {
    Rnr rnr = make(a(defaultRequisition, with(emergency, false)));
    rnr.setModifiedDate(new Date());
    List<Rnr> rnrList = Arrays.asList(rnr);

    List<RnrDTO> rnrDTOs = RnrDTO.prepareForView(rnrList);

    assertThat(rnrDTOs.size(), is(1));
    RnrDTO rnrDTO = rnrDTOs.get(0);
    assertRnrDTO(rnrDTO, rnr);
    assertThat(rnrDTO.getRequisitionStatus(), is(rnr.getStatus().name()));
  }

  private void assertRnrDTO(RnrDTO rnrDTO, Rnr rnr) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    assertThat(rnrDTO.getId(), is(rnr.getId()));
    assertThat(rnrDTO.getProgramId(), is(rnr.getProgram().getId()));
    assertThat(rnrDTO.getFacilityId(), is(rnr.getFacility().getId()));
    assertThat(rnrDTO.getProgramName(), is(rnr.getProgram().getName()));
    assertThat(rnrDTO.getFacilityCode(), is(rnr.getFacility().getCode()));
    assertThat(rnrDTO.getFacilityName(), is(rnr.getFacility().getName()));
    assertThat(rnrDTO.getStringSubmittedDate(), is(simpleDateFormat.format(rnr.getSubmittedDate())));
    assertThat(rnrDTO.getStringModifiedDate(), is(simpleDateFormat.format(rnr.getModifiedDate())));
    assertThat(rnrDTO.getStringPeriodStartDate(), is(simpleDateFormat.format(rnr.getPeriod().getStartDate())));
    assertThat(rnrDTO.getStringPeriodEndDate(), is(simpleDateFormat.format(rnr.getPeriod().getEndDate())));
    assertThat(rnrDTO.isEmergency(), is(rnr.isEmergency()));
  }


}
