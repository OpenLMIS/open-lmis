/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.service.SupervisoryNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SupervisoryNodeHandler is used for uploads of supervisory nodes. It uploads each supervisory node record by record.
 */
@Component
@NoArgsConstructor
public class SupervisoryNodeHandler extends AbstractModelPersistenceHandler {

  private SupervisoryNodeService supervisoryNodeService;

  @Autowired
  public SupervisoryNodeHandler(SupervisoryNodeService supervisoryNodeService) {
    this.supervisoryNodeService = supervisoryNodeService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return supervisoryNodeService.getByCode((SupervisoryNode) record);
  }

  @Override
  protected void save(BaseModel record) {
    supervisoryNodeService.save((SupervisoryNode) record);
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.supervisory.node";
  }

}
