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

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.RegimenCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hassan
 * Date: 11/21/13
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */

@Repository
public interface RegimenCategoryReportMapper {

    @Select("SELECT id,code,name FROM regimen_categories ORDER BY displayOrder, name")
    List<RegimenCategory> getAll();

    @Select("SELECT * FROM regimen_categories WHERE id = #{id}")
            List<RegimenCategory>getById(Long id);
}
