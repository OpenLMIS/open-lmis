/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Right;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RightsMapper maps the rights entity to corresponding representation in database.
 */
@Repository
public interface RightMapper {

  @Insert({"INSERT INTO rights(name, rightType, createdDate) VALUES ",
    "(#{name}, #{type}, CURRENT_TIMESTAMP)"})
  void insertRight(Right right);

  @Select({"SELECT COUNT(*) FROM rights r",
    "INNER JOIN role_rights rt ON rt.rightName = r.name",
    "INNER JOIN role_assignments ra ON ra.roleId = rt.roleId WHERE ra.userId = #{userId}",
    "AND r.rightType='REPORTING'"})
  Integer totalReportingRightsFor(Long userId);

  @Select({"SELECT * FROM rights ORDER BY displayOrder"})
  @Results(value = {
    @Result(property = "type", column = "rightType")})
  List<Right> getAll();
}
