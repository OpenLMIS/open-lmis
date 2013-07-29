package org.openlmis.core.event;

import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openlmis.core.dto.ProgramSupportedEventDTO;

import java.net.URISyntaxException;
import java.util.UUID;

public class ProgramSupportedEvent extends Event {

  public static final String CATEGORY = "programSupported";
  public static final String TITLE = "ProgramSupported";

  public ProgramSupportedEvent(ProgramSupportedEventDTO programSupportedEventDTO) throws URISyntaxException {
    this(UUID.randomUUID().toString(), TITLE, DateTime.now(), "", programSupportedEventDTO.getSerializedContents(), CATEGORY);

  }

  public ProgramSupportedEvent(String uuid, String title, DateTime timeStamp, String uriString,
                               String serializedContents, String category) throws URISyntaxException {
    super(uuid, title, timeStamp, uriString, serializedContents, category);
  }
}
