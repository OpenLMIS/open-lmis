/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.core.event;

import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openlmis.core.domain.Program;

import java.net.URISyntaxException;
import java.util.UUID;

public class ProgramChangeEvent extends Event {

  public ProgramChangeEvent(Program program) throws URISyntaxException {
    super(UUID.randomUUID().toString(), "Program", DateTime.now(), "", "ProgramFeedDTO.getSerializedContents(program)", "program");
  }
}
