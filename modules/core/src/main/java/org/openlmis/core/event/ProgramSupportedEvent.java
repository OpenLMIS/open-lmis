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
import org.openlmis.core.dto.ProgramSupportedEventDTO;

import java.net.URISyntaxException;
import java.util.UUID;

public class ProgramSupportedEvent extends Event {

  public static final String CATEGORY = "program-supported";
  public static final String TITLE = "ProgramSupported";

  public ProgramSupportedEvent(ProgramSupportedEventDTO programSupportedEventDTO) throws URISyntaxException {
    this(UUID.randomUUID().toString(), TITLE, DateTime.now(), "", programSupportedEventDTO.getSerializedContents(), CATEGORY);

  }

  public ProgramSupportedEvent(String uuid, String title, DateTime timeStamp, String uriString,
                               String serializedContents, String category) throws URISyntaxException {
    super(uuid, title, timeStamp, uriString, serializedContents, category);
  }
}
