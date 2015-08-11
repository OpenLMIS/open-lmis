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

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.report.model.CustomReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustomReportMapper {

  @Select("select id, reportKey, name, description, category, columnoptions, filters from custom_reports order by category, name")
  List<Map> getListOfReports();

  @Select("select * from custom_reports order by category, name")
  List<CustomReport> getListWithFullAttributes();

  @SelectProvider(type = PureSqlProvider.class, method = "sql")
  List<Map> getReportData(Map param);

  @Select("select * from custom_reports where reportKey = #{key}")
  Map getCustomReportByKey(@Param("key") String key);

  @Insert("insert into custom_reports " +
      "   (name, reportkey, description, help, filters, query, category, columnoptions ) " +
      " values " +
      " (#{name}, #{reportkey}, #{description}, #{help}, #{filters}, #{query}, #{category}, #{columnoptions})")
  void insert(CustomReport report);

  @Update("update custom_reports " +
      " set name = #{name}, reportKey = #{reportkey}, description = #{description}, help = #{help}, filters = #{filters}, query = #{query}, category = #{category} , columnOptions = #{columnoptions}" +
      " where id = #{id}")
  void update(CustomReport report);

}
