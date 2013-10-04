/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;


import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegimenLineItemMapper {

  @Insert({"INSERT INTO regimen_line_items(code, name, regimenDisplayOrder, regimenCategory, regimenCategoryDisplayOrder, rnrId, modifiedBy, createdBy) values " +
    "(#{code}, #{name}, #{regimenDisplayOrder}, #{category.name}, #{category.displayOrder}, #{rnrId}, #{modifiedBy}, #{createdBy})"})
  @Options(useGeneratedKeys = true)
  public void insert(RegimenLineItem regimenLineItem);

  @Select("SELECT * FROM regimen_line_items WHERE rnrId = #{rnrId} ORDER BY regimenCategoryDisplayOrder, regimenDisplayOrder")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "code", column = "code"),
    @Result(property = "name", column = "name"),
    @Result(property = "patientsOnTreatment", column = "patientsOnTreatment"),
    @Result(property = "patientsToInitiateTreatment", column = "patientsToInitiateTreatment"),
    @Result(property = "remarks", column = "remarks"),
    @Result(property = "patientsStoppedTreatment", column = "patientsStoppedTreatment"),
    @Result(property = "regimenDisplayOrder", column = "regimenDisplayOrder"),
    @Result(property = "category.name", column = "regimenCategory"),
    @Result(property = "category.displayOrder", column = "regimenCategoryDisplayOrder"),
  })
  public List<RegimenLineItem> getRegimenLineItemsByRnrId(Long rnrId);

  @Update("UPDATE regimen_line_items set patientsOnTreatment = #{patientsOnTreatment},patientsToInitiateTreatment = #{patientsToInitiateTreatment}," +
    "patientsStoppedTreatment = #{patientsStoppedTreatment},remarks = #{remarks},modifiedBy = #{modifiedBy}, modifiedDate =CURRENT_TIMESTAMP where id=#{id}")
  void update(RegimenLineItem regimenLineItem);
}
