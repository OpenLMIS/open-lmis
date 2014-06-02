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

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.repository.mapper.SupplyLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SupplyLineRepository is Repository class for SupplyLine related database operations.
 */

@Repository
@NoArgsConstructor
public class SupplyLineRepository {

  private SupplyLineMapper mapper;

  @Autowired
  public SupplyLineRepository(SupplyLineMapper mapper) {
    this.mapper = mapper;
  }

  public void insert(SupplyLine supplyLine) {
    mapper.insert(supplyLine);
  }

  public SupplyLine getSupplyLineBy(SupervisoryNode supervisoryNode, Program program) {
    return mapper.getSupplyLineBy(supervisoryNode, program);
  }

  public void update(SupplyLine supplyLine) {
    mapper.update(supplyLine);
  }

  public SupplyLine getById(Long id) {
    return mapper.getById(id);
  }

  public List<SupplyLine> search(String searchParam, String columnName, Pagination pagination) {
    if (columnName.equals("facility")) {
      return mapper.searchByFacilityName(searchParam, pagination);
    }
    if (columnName.equals("supervisoryNode")) {
      return mapper.searchBySupervisoryNodeName(searchParam, pagination);
    }
    return null;
  }


  public Integer getTotalSearchResultCount(String searchParam, String columnName) {
    if (columnName.equals("facility")) {
      return mapper.getTotalSearchResultsByFacilityName(searchParam);
    }
    if (columnName.equals("supervisoryNode")) {
      return mapper.getTotalSearchResultsBySupervisoryNodeName(searchParam);
    }
    return null;
  }
}
