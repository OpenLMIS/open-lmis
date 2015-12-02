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
import org.openlmis.core.domain.ISA;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.repository.mapper.FacilityProgramProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductIsaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * FacilityProgramProductRepository is repository class for FacilityProgramProduct related database operations.
 */

@Repository
public class FacilityProgramProductRepository {

  @Autowired
  ProgramProductIsaMapper ppiMapper;

  @Autowired
  FacilityProgramProductMapper fppMapper;

  @Transactional
  public void insertISA(Long facilityId, ProgramProductISA isa)
  {
    fppMapper.insertISA(facilityId, isa);
  }

  public void updateISA(ProgramProductISA programProductISA) {
    ppiMapper.update(programProductISA);
  }

  public ProgramProductISA getIsaByProgramProductId(Long programProductId) {
    return ppiMapper.getIsaByProgramProductId(programProductId);
  }

  public ISA getOverriddenIsa(Long programProductId, Long facilityId)
  {
    return fppMapper.getOverriddenIsa(programProductId, facilityId);
  }


  public void deleteOverriddenIsa(Long programProductId, Long facilityId)
  {
    fppMapper.deleteOverriddenIsa(programProductId, facilityId);
  }

  @Transactional
  public void save(FacilityProgramProduct fpp) {
    fppMapper.removeFacilityProgramProduct(fpp.getId(), fpp.getFacilityId());
    fppMapper.insert(fpp);
    fppMapper.insertISA(fpp.getFacilityId(), fpp.getProgramProductIsa());
  }

  public FacilityProgramProduct getByCodes(String facilityCode, String programCode, String productCode) {
    return fppMapper.getByCodes(facilityCode, programCode, productCode);
  }
}
