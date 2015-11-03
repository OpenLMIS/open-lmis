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

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import org.openlmis.core.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApproverMapper {

  @Select("select distinct u.* from requisitions r " +
      "join facilities f on r.facilityid = f.id " +
      "join users u on u.facilityid = f.id " +
      "join role_assignments ra " +
          "on ra.userid = u.id and ra.programid = r.programid " +
          "and roleid in (select roleid from role_rights where rightname = 'AUTHORIZE_REQUISITION') \n" +
      "where r.id = #{RnrID} and u.active = true")
  List<User> getFacilityBasedAuthorizers( @Param(value = "RnrID") Long rnrID );

  @Select("select distinct u.* from requisitions r " +
      "join ( select * from role_assignments " +
      "           where " +
      "               roleId in (select roleId from role_rights where rightName = 'APPROVE_REQUISITION') " +
      "     ) ra " +
      "   on  ra.supervisoryNodeId = r.supervisoryNodeId " +
      "         and ra.programId = r.programId " +
      " join users u " +
      "   on u.id = ra.userId " +
      "where r.id = #{RnrID} and u.active = true")
  List<User> getNextSupervisors( @Param(value = "RnrID") Long rnrID );

}
