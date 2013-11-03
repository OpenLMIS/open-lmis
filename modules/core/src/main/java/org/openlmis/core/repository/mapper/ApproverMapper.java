/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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
  List<User> getFacilityBasedAuthorizers( @Param(value = "RnrID") Long RnrID );

  @Select("select distinct u.* from requisitions r " +
      "join role_assignments ra on ra.supervisoryNodeId = r.supervisoryNodeId " +
      "join users u on u.id = ra.userid " +
      " and ra.programid = r.programid " +
      "and roleid in (select roleid from role_rights where rightname = 'APPROVE_REQUISITION') \n" +
      "where r.id = #{RnrID} and u.active = true")
  List<User> getNextSupervisors( @Param(value = "RnrID") Long RnrID );

}
