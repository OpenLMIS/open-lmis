/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.openlmis.order.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.List;

@Repository
public interface OrderMapper {

  @Insert("INSERT INTO orders(rnrId, fulfilled, createdBy) VALUES (#{rnr.id}, #{fulfilled}, #{createdBy})")
  @Options(useGeneratedKeys = true)
  void insert(Order order);

  @Select("Select * from orders ORDER BY createdDate DESC")
  List<Order> getAll();

  @Update("Update orders set shipmentId=#{shipmentId},fulfilled=#{fulfilled} where id=#{id}")
  void updateFullfilledFlagAndShipmentId(Order order);

}
