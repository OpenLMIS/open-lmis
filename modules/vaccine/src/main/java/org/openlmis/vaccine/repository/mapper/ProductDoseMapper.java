/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDoseMapper {

  @Select("select pd.* from vaccine_product_doses pd join vaccine_doses d on d.id = pd.doseId where productId = #{productId} and pd.programId = #{programId} order by d.displayOrder")
  List<VaccineProductDose> getDoseSettingByProduct(@Param("programId") Long programId, @Param("productId") Long productId);

  @Insert("insert into vaccine_product_doses (doseId, programId, productId, displayName, displayOrder, trackMale, trackFemale, denominatorEstimateCategoryId, createdBy, modifiedBy) " +
    " values " +
    " ( #{doseId}, #{programId} , #{productId}, #{displayName}, #{displayOrder}, #{trackMale}, #{trackFemale}, #{denominatorEstimateCategoryId}, #{createdBy}, #{modifiedBy} )")
  @Options(useGeneratedKeys = true)
  Integer insert(VaccineProductDose dose);

  @Update("update vaccine_product_doses " +
    " set " +
    " doseId = #{doseId}," +
    " programId = #{programId}, " +
    " productId = #{productId}, " +
    " displayName = #{displayName}, " +
    " displayOrder = #{displayOrder}, " +
    " denominatorEstimateCategoryId = #{denominatorEstimateCategoryId}," +
    " modifiedBy = #{modifiedBy}, " +
    " modifiedDate = CURRENT_TIMESTAMP" +
    " where id = #{id}")
  Integer update(VaccineProductDose dose);

  @Select("select d.* from vaccine_product_doses d  where programId = #{programId}")
  List<VaccineProductDose> getProgramProductDoses(@Param("programId") Long programId);
}
