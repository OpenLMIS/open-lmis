/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.repository.mapper.RegimenCategoryMapper;
import org.openlmis.core.repository.mapper.RegimenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RegimenRepository is Repository class for Regimen related database operations.
 */

@Repository
public class RegimenRepository {

  @Autowired
  RegimenMapper mapper;

  @Autowired
  RegimenCategoryMapper regimenCategoryMapper;

  public List<Regimen> getByProgram(Long programId) {
    return mapper.getByProgram(programId);
  }

  public void save(List<Regimen> regimens, Long userId) {
    for (Regimen regimen : regimens) {
      regimen.setModifiedBy(userId);
      if (regimen.getId() == null) {
        regimen.setCreatedBy(userId);
        mapper.insert(regimen);
      }
      mapper.update(regimen);
    }
  }

}
