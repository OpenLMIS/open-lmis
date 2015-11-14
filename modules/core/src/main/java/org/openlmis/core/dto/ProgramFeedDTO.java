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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.openlmis.core.domain.Program;

/**
 * ProgramFeedDTO consolidates program information like programCode and programName, to be used while displaying program
 * information to user,  for eg. in feed.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ProgramFeedDTO extends BaseFeedDTO {

  private String programCode;

  private String programName;

  public ProgramFeedDTO(Program program) {
    this.programCode = program.getCode();
    this.programName = program.getName();
  }
}
