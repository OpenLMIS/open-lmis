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
import org.openlmis.vaccine.domain.reports.AdverseEffectLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineReportAdverseEffectMapper {


  @Insert("INSERT INTO vaccine_report_adverse_effect_line_items " +
    "(reportId, productId, date, manufacturerId, batch, expiry, cases, investigation, notes, createdBy, createdDate, modifiedBy, modifiedDate) " +
    " values " +
    "( #{reportId}, #{productId}, #{date}, #{manufacturerId}, #{batch}, #{expiry}, #{cases}, #{investigation}, #{notes}, #{createdBy}, NOW(), #{modifiedBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  void insert(AdverseEffectLineItem lineItem);

  @Update("UPDATE vaccine_report_adverse_effect_line_items " +
    " SET" +
      " reportId = #{reportId}" +
      " , productId = #{productId}" +
      " , date = #{date}" +
      " , manufacturerId = #{manufacturerId} " +
      " , batch = #{batch}" +
      " , expiry = #{expiry}" +
      " , cases = #{cases}" +
      " , investigation = #{investigation}" +
      " , notes = #{notes}" +
      " , modifiedBy = #{modifiedBy}" +
      " , modifiedDate = NOW() " +
    " WHERE id = #{id}")
  void update(AdverseEffectLineItem lineItem);

  @Select("SELECT e.*, p.primaryName as productName from vaccine_report_adverse_effect_line_items e join products p on p.id = e.productId where reportId = #{reportId} order by e.id")
  List<AdverseEffectLineItem> getLineItems(@Param("reportId") Long reportId);
}
