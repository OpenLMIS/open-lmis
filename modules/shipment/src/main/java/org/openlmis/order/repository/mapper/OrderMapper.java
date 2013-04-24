/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.order.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper {

  @Insert("INSERT INTO orders(rnrId, fulfilled, createdBy) VALUES (#{rnr.id}, #{fulfilled}, #{createdBy})")
  @Options(useGeneratedKeys = true)
  void insert(Order order);

  @Select("SELECT * FROM orders ORDER BY createdDate DESC")
      @Results({
          @Result(property = "rnr.id", column = "rnrId")
      })
  List<Order> getAll();

  @Update("UPDATE orders SET shipmentId=#{shipmentId},fulfilled=#{fulfilled} WHERE rnrid=ANY(#{rnrIds}::INTEGER[]) AND fulfilled IS NOT true")
  void updateFulfilledFlagAndShipmentId(@Param(value = "rnrIds") String rnrIds, @Param(value = "fulfilled") Boolean fulfilled, @Param(value = "shipmentId") Integer shipmentId);

  @Select("SELECT * FROM orders WHERE id = #{id}")
  @Results({
    @Result(property = "rnr.id", column = "rnrId")
  })
  Order getById(Integer id);
}
