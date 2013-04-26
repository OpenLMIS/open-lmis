/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.order.domain.Order;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper {

  @Insert("INSERT INTO orders(rnrId, status, createdBy) VALUES (#{rnr.id}, #{status}, #{createdBy})")
  @Options(useGeneratedKeys = true)
  void insert(Order order);

  @Select("SELECT * FROM orders ORDER BY createdDate DESC")
      @Results({
          @Result(property = "rnr.id", column = "rnrId"),
          @Result(property = "shipmentFileInfo", javaType = ShipmentFileInfo.class, column = "shipmentId",
                  one = @One(select = "org.openlmis.shipment.repository.mapper.ShipmentMapper.getShipmentFileInfo")),
      })
  List<Order> getAll();

  @Select("SELECT * FROM orders WHERE id = #{id}")
  @Results({
    @Result(property = "rnr.id", column = "rnrId")
  })
  Order getById(Integer id);

  @Update("UPDATE orders SET shipmentId=#{shipmentFileInfo.id},status=#{status} WHERE rnrid=#{rnr.id} AND STATUS='RELEASED'")
  void updateShipmentInfo(Order order);
}
