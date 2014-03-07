/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.event;

import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openlmis.core.domain.Program;
import org.openlmis.core.dto.ProgramFeedDTO;

import java.net.URISyntaxException;
import java.util.UUID;

/**
 * This class is responsible for generating a feed on the event of change in supported programs of a facility.
 */

public class ProgramChangeEvent extends Event {

  public static final String FEED_CATEGORY = "program-catalog-changes";

  public ProgramChangeEvent(Program program) throws URISyntaxException {
    super(UUID.randomUUID().toString(), "Program Catalog Changes", DateTime.now(), "", new ProgramFeedDTO(program).getSerializedContents(), FEED_CATEGORY);
  }
}
