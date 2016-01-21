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

import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupplyLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SupplyLineRepository is Repository class for SupplyLine related database operations.
 */

@Repository
public class SupplyLineRepository {

  @Autowired
  private SupplyLineMapper mapper;

  public void insert(SupplyLine supplyLine) {
    try {
      mapper.insert(supplyLine);
    } catch (DuplicateKeyException ex) {
      throw new DataException("error.supplying.facility.already.assigned");
    }
  }

  public SupplyLine getSupplyLineBy(SupervisoryNode supervisoryNode, Program program) {
    return mapper.getSupplyLineBy(supervisoryNode, program);
  }

  public void update(SupplyLine supplyLine) {
    try {
      mapper.update(supplyLine);
    } catch (DuplicateKeyException ex) {
      throw new DataException("error.supplying.facility.already.assigned");
    }
  }

  public SupplyLine getById(Long id) {
    return mapper.getById(id);
  }

  public List<SupplyLine> search(String searchParam, String column, Pagination pagination) {
    return mapper.search(searchParam, column, pagination);
  }

  public Integer getTotalSearchResultCount(String searchParam, String column) {
    return mapper.getSearchedSupplyLinesCount(searchParam, column);
  }

  public List<Facility> getSupplyingFacilities(Long userId){
    return mapper.getSupplyingFacilities(userId);
  }

  public SupplyLine getSupplyLineByFacilityProgram(Long facilityId, Long programId) {
    return mapper.getByFacilityByProgram(facilityId, programId);
  }
}
