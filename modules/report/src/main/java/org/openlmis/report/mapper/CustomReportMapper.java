/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.report.model.CustomReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustomReportMapper {

  @Select("select id, reportKey, name, description, category from custom_reports")
  List<Map> getListOfReports();

  @SelectProvider(type = PureSqlProvider.class, method = "sql")
  List<Map> getReportData(@Param("sql") String sql, @Param("param") Map param);

  @Select("select * from custom_reports where reportKey = #{key}")
  Map getCustomReportByKey(@Param("key") String key);

  @Insert("insert into custom_reports " +
      "   (name, reportKey, description, help, filters, query, category, columnOptions, createdBy, createdDate, modifiedBy, modifiedDate ) " +
      " values " +
      " (#{name}, #{reportKey}, #{description}, #{help}, #{filters}, #{query}, #{category}, #{columnOptions}, #{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate})")
  void insert(CustomReport report);

  @Update("update custom_reports " +
      " set name = #{name}, reportKey = #{reportKey}, description = #{description}, help = #{help}, filters = #{filters}, query = #{query}, category = #{query} , columnOptions = #{columnOptions}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate}" +
      " where id = #{id}")
  void update(CustomReport report);

}
