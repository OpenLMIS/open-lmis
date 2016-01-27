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
import org.openlmis.report.model.dto.Regimen;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hassan
 * Date: 11/21/13
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */

@Repository
public interface RegimenReportMapper{

     @Select("SELECT * FROM regimens R INNER JOIN regimen_categories RC ON R.categoryId = RC.id\n" +
             "         ORDER BY RC.displayOrder,R.displayOrder")


    List<Regimen> getByProgram();

    @Select("SELECT * FROM regimens ORDER BY displayOrder, name")
    List<Regimen> getAll();


   @Select("SELECT * FROM regimens R INNER JOIN regimen_categories RC ON R.categoryId = RC.id\n" +
           "  where categoryid = #{categoryid} ORDER BY RC.displayOrder,R.displayOrder")
    List<Regimen>getRegimenByCategory(Long id);


}
