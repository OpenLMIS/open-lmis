/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.mapper.reports;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.reports.DiseaseLineItem;
import org.openlmis.vaccine.domain.reports.VitaminSupplementationLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VitaminSupplementationLineItemMapper {

  @Insert("INSERT into vaccine_report_vitamin_supplementation_line_items " +
    " (reportId, vaccineVitaminId, vitaminAgeGroupId,vitaminName, displayOrder , createdBy, createdDate, modifiedBy, modifiedDate) " +
    " values" +
    " (#{reportId}, #{vaccineVitaminId}, #{vitaminAgeGroupId}, #{vitaminName}, #{displayOrder} , #{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  Integer insert(VitaminSupplementationLineItem lineItem);

  @Update("UPDATE vaccine_report_vitamin_supplementation_line_items " +
    " SET" +
    " maleValue = #{maleValue}, " +
    " femaleValue = #{femaleValue} " +
    " , modifiedBy = #{modifiedBy} " +
    " , modifiedDate = NOW()" +
    " WHERE id = #{id}")
  Integer update(VitaminSupplementationLineItem lineItem);

  @Select("select li.*, ag.name as ageGroup " +
          " from " +
          " vaccine_report_vitamin_supplementation_line_items li " +
    " join vaccine_vitamin_supplementation_age_groups ag " +
    " on ag.id = li.vitaminAgeGroupId " +
    " WHERE li.reportId = #{reportId} " +
    " order by id")
  List<VitaminSupplementationLineItem> getLineItems(@Param("reportId") Long reportId);
}
