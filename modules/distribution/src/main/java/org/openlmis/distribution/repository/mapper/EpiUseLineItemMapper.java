/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the EpiUseLineItem entity to corresponding representation in database.
 */

@Repository
public interface EpiUseLineItemMapper {

  @Insert({"INSERT INTO epi_use_line_items (facilityVisitId, productGroupId, productGroupName, stockAtFirstOfMonth, received, ",
    "distributed, loss, stockAtEndOfMonth, expirationDate, createdBy, modifiedBy) VALUES (#{facilityVisitId}, #{productGroup.id}, #{productGroup.name}, #{stockAtFirstOfMonth},",
    " #{received}, #{distributed}, #{loss}, #{stockAtEndOfMonth}, #{expirationDate}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  public void insertLineItem(EpiUseLineItem epiUseLineItem);

  @Select({"SELECT * FROM epi_use_line_items WHERE id = #{id}"})
  @Results(value = {
    @Result(property = "productGroup.id", column = "productGroupId"),
    @Result(property = "productGroup.name", column = "productGroupName")
  })
  public EpiUseLineItem getLineItemById(EpiUseLineItem epiUseLineItem);

  @Update({"UPDATE epi_use_line_items SET received = #{received}, distributed = #{distributed}, loss = #{loss},",
    "stockAtFirstOfMonth = #{stockAtFirstOfMonth}, stockAtEndOfMonth = #{stockAtEndOfMonth}, expirationDate = #{expirationDate},",
    "modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT WHERE id = #{id}"})
  public void updateLineItem(EpiUseLineItem epiUseLineItem);

  @Select({"SELECT * FROM epi_use_line_items WHERE facilityVisitId = #{facilityVisitId} ORDER BY LOWER(productGroupName)"})
  @Results(value = {
    @Result(property = "productGroup.id", column = "productGroupId"),
    @Result(property = "productGroup.name", column = "productGroupName")
  })
  List<EpiUseLineItem> getBy(Long facilityVisitId);
}
