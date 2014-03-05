/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository;

import org.openlmis.rnr.domain.Column;
import org.openlmis.rnr.domain.RegimenColumn;
import org.openlmis.rnr.domain.RegimenTemplate;
import org.openlmis.rnr.repository.mapper.RegimenColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class for Regimen Column related database operations.
 */

@Repository
public class RegimenColumnRepository {

  @Autowired
  RegimenColumnMapper mapper;

  public List<RegimenColumn> getRegimenColumnsByProgramId(Long programId) {
    return mapper.getAllRegimenColumnsByProgramId(programId);
  }

  public List<RegimenColumn> getMasterRegimenColumnsByProgramId() {
    return mapper.getMasterRegimenColumns();
  }

  public void save(RegimenTemplate regimenTemplate, Long userId) {
    for (Column regimenColumn : regimenTemplate.getColumns()) {
      regimenColumn.setModifiedBy(userId);
      if (regimenColumn.getId() == null) {
        regimenColumn.setCreatedBy(userId);
        mapper.insert((RegimenColumn) regimenColumn, regimenTemplate.getProgramId());
      }
      mapper.update((RegimenColumn) regimenColumn);
    }
  }
}
