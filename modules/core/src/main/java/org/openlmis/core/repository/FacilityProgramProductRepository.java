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

import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.repository.mapper.FacilityProgramProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductIsaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FacilityProgramProductRepository {

  @Autowired
  ProgramProductIsaMapper programProductIsaMapper;

  @Autowired
  FacilityProgramProductMapper mapper;

  public void insertISA(ProgramProductISA programProductISA) {
    programProductIsaMapper.insert(programProductISA);
  }

  public void updateISA(ProgramProductISA programProductISA) {
    programProductIsaMapper.update(programProductISA);
  }

  public ProgramProductISA getIsaByProgramProductId(Long programProductId) {
    return programProductIsaMapper.getIsaByProgramProductId(programProductId);
  }

  public Integer getOverriddenIsa(Long programProductId, Long facilityId) {
    return mapper.getOverriddenIsa(programProductId, facilityId);
  }

  public void save(FacilityProgramProduct product) {
    mapper.removeFacilityProgramProductMapping(product.getId(), product.getFacilityId());
    mapper.insert(product);
  }

  public List<FacilityProgramProduct> getByFacilityAndProgram(Long facilityId, Long programId) {
    return mapper.getByFacilityAndProgram(facilityId, programId);
  }
}
