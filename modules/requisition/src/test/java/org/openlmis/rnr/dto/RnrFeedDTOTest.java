/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.dto;

import org.junit.Test;
import org.openlmis.rnr.domain.Rnr;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;

public class RnrFeedDTOTest {
  @Test
  public void shouldPopulateFeedFromRequisition() throws Exception {
    Rnr rnr = make(a(defaultRnr));
    RnrFeedDTO feed = RnrFeedDTO.populate(rnr);

  //  assertThat(feed.getExternaLSystemName(), is())
  }
}
