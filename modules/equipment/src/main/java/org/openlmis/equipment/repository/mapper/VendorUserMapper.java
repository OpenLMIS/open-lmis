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

package org.openlmis.equipment.repository.mapper;/*  * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.  *  * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.  *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.  *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.User;
import org.openlmis.equipment.domain.VendorUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorUserMapper {

  @Select("SELECT u.id, u.firstName, u.lastName, u.email, u.username, u.active " +
          "from equipment_service_vendor_users vu " +
          "JOIN users u on vu.userId = u.id " +
          "WHERE vu.vendorId=#{vendorId} order by u.firstName, u.lastName")
  List<User> getAllUsersForVendor(Long vendorId);

  @Insert("INSERT INTO equipment_service_vendor_users (userId, vendorId, createdBy, createdDate, modifiedBy, modifiedDate) " +
          "VALUES (#{user.id}, #{vendor.id}, #{createdBy}, #{createdDate}, #{modifiedBy}, #{modifiedDate})")
  @Options(useGeneratedKeys = true)
  void insert(VendorUser vendorUser);

  @Update("UPDATE equipment_service_vendor_users " +
          "SET userId = #{user.id}, vendorId = #{vendorId}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} " +
          "WHERE id = #{id}")
  void update(VendorUser vendorUser);

  @Delete("DELETE FROM equipment_service_vendor_users " +
          "WHERE vendorId = #{vendorId} AND userId = #{userId}")
  void remove(@Param(value="vendorId") Long vendorId, @Param(value="userId") Long userId);

  @Select("SELECT u.id, u.firstName, u.lastName, u.email, u.username, u.active " +
          "from users u " +
          "WHERE u.id not in (SELECT userId from equipment_service_vendor_users) AND u.active = TRUE " +
      "  order by u.firstName, u.lastName")
  List<User> getAllUsersAvailableForVendor();
}
