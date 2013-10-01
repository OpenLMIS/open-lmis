/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.rnr.dto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;

import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.openlmis.rnr.builder.RequisitionBuilder.emergency;

@Category(UnitTests.class)
public class RnrDTOTest {
  @Test
  public void shouldPrepareRequisitionsForApproval() throws Exception {
    Rnr rnr = make(a(defaultRnr, with(emergency, true)));
    List<Rnr> rnrList = Arrays.asList(rnr);

    List<RnrDTO> rnrDTOs = RnrDTO.prepareForListApproval(rnrList);

    assertThat(rnrDTOs.size(), is(1));
    RnrDTO rnrDTO = rnrDTOs.get(0);
    assertThat(rnrDTO.getId(), is(rnr.getId()));
    assertThat(rnrDTO.getProgramId(), is(rnr.getProgram().getId()));
    assertThat(rnrDTO.getFacilityId(), is(rnr.getFacility().getId()));
    assertThat(rnrDTO.getProgramName(), is(rnr.getProgram().getName()));
    assertThat(rnrDTO.getFacilityCode(), is(rnr.getFacility().getCode()));
    assertThat(rnrDTO.getFacilityName(), is(rnr.getFacility().getName()));
    assertThat(rnrDTO.getSubmittedDate(), is(rnr.getSubmittedDate()));
    assertThat(rnrDTO.getModifiedDate(), is(rnr.getModifiedDate()));
    assertThat(rnrDTO.getPeriodStartDate(), is(rnr.getPeriod().getStartDate()));
    assertThat(rnrDTO.getPeriodEndDate(), is(rnr.getPeriod().getEndDate()));
    assertTrue(rnrDTO.isEmergency());
  }

  @Test
  public void shouldPrepareRequisitionsForView() throws Exception {
    Rnr rnr = make(a(defaultRnr, with(emergency, false)));
    List<Rnr> rnrList = Arrays.asList(rnr);

    List<RnrDTO> rnrDTOs = RnrDTO.prepareForView(rnrList);

    assertThat(rnrDTOs.size(), is(1));
    RnrDTO rnrDTO = rnrDTOs.get(0);
    assertThat(rnrDTO.getId(), is(rnr.getId()));
    assertThat(rnrDTO.getProgramId(), is(rnr.getProgram().getId()));
    assertThat(rnrDTO.getFacilityId(), is(rnr.getFacility().getId()));
    assertThat(rnrDTO.getProgramName(), is(rnr.getProgram().getName()));
    assertThat(rnrDTO.getFacilityCode(), is(rnr.getFacility().getCode()));
    assertThat(rnrDTO.getFacilityName(), is(rnr.getFacility().getName()));
    assertThat(rnrDTO.getSubmittedDate(), is(rnr.getSubmittedDate()));
    assertThat(rnrDTO.getModifiedDate(), is(rnr.getModifiedDate()));
    assertThat(rnrDTO.getPeriodStartDate(), is(rnr.getPeriod().getStartDate()));
    assertThat(rnrDTO.getPeriodEndDate(), is(rnr.getPeriod().getEndDate()));
    assertThat(rnrDTO.getStatus(), is(rnr.getStatus().name()));
    assertFalse(rnrDTO.isEmergency());
  }


}
