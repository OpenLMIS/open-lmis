package org.openlmis.equipment.repository.mapper;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

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
