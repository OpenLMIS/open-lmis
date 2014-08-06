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
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.DosageUnitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class DosageUnitRepository {

  private DosageUnitMapper duMapper;

  @Autowired
  public DosageUnitRepository(DosageUnitMapper dosageUnitMapper) {
    this.duMapper = dosageUnitMapper;
  }

  public DosageUnit getByCode(String code) {
    return duMapper.getByCode(code);
  }

  public DosageUnit getExisting(DosageUnit du) {
    return duMapper.getByCode(du.getCode());
  }

  public void insert(DosageUnit du) {
    du.isValid();
    if(getByCode(du.getCode()) != null) throw new DataException("error.duplicate.dosage.unit.code");

    try {
      duMapper.insert(du);
    } catch(DataIntegrityViolationException dive) {
      throw new DataException("error.incorrect.length", dive);
    }
  }

  public void update(DosageUnit du) {
    duMapper.update(du);
  }
}
