/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openlmis.core.domain.ProgramSupported;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
public class ProgramSupportedEventDTO extends BaseFeedDTO {

  private String facilityCode;
  private List<ProgramSupportedDTO> programsSupported;

  public static final String CATEGORY = "programs-supported";
  public static final String TITLE = "Programs Supported";


  public ProgramSupportedEventDTO(String facilityCode, List<ProgramSupported> programSupportedList) {
    this.facilityCode = facilityCode;

    this.programsSupported = new ArrayList<>();
    for (ProgramSupported ps : programSupportedList) {
      ProgramSupportedDTO psDTO = new ProgramSupportedDTO(ps.getProgram().getCode(), ps.getProgram().getName(),
        ps.getActive(), ps.getStartDate());

      programsSupported.add(psDTO);
    }
  }

  @AllArgsConstructor
  @Data
  @JsonSerialize(include = NON_EMPTY)
  class ProgramSupportedDTO {
    private String code;
    private String name;
    private Boolean active;
    private Date startDate;
  }

  public Event createEvent() throws URISyntaxException {
    return new Event(UUID.randomUUID().toString(), TITLE, DateTime.now(), "", getSerializedContents(), CATEGORY);
  }

}
