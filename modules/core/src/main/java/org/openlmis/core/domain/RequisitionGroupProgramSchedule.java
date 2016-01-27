/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

/**
 * RequisitionGroupProgramSchedule represents the schedule to be mapped for a given program and requisition group
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequisitionGroupProgramSchedule extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "RG Code", nested = "code")
  private RequisitionGroup requisitionGroup;

  @ImportField(mandatory = true, name = "Program", nested = "code")
  private Program program;

  @ImportField(mandatory = true, name = "Schedule", nested = "code")
  private ProcessingSchedule processingSchedule;

  @ImportField(mandatory = true, name = "Direct Delivery")
  private boolean directDelivery;

  @ImportField(name = "Drop off Facility", nested = "code")
  private Facility dropOffFacility;
}
