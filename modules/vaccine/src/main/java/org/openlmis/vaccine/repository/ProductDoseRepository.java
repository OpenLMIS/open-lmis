/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.vaccine.repository;

import org.openlmis.vaccine.domain.VaccineDose;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.repository.mapper.ProductDoseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductDoseRepository {

  @Autowired
  private ProductDoseMapper mapper;

  public void insert(VaccineProductDose dose){
    mapper.insert(dose);
  }

  public void update(VaccineProductDose dose){
    mapper.update(dose);
  }

  public List<VaccineProductDose> getDosesForProduct(Long programId, Long productId){
    return mapper.getDoseSettingByProduct(programId, productId);
  }

  public List<VaccineDose> getAllDoses(){
    return mapper.getAllDoses();
  }

  public List<VaccineProductDose> getProgramProductDoses(Long programId) {
    return mapper.getProgramProductDoses(programId);
  }

  public void deleteAllByProgram(Long programId){
    mapper.deleteByProgram(programId);
  }
}
