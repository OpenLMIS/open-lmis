/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.springframework.stereotype.Repository;


@Repository
public interface ShipmentMapper {

  @Insert({"INSERT INTO shipped_line_items (orderId, productCode, quantityShipped) " +
     "VALUES" +
     "(#{orderId}, #{productCode}, #{quantityShipped})"})
   @Options(useGeneratedKeys = true)
   public void insertShippedLineItem(ShippedLineItem shippedLineItem);

  @Insert({"INSERT INTO shipment_file_info (fileName,success) VALUES (#{name},#{success})"})
  @Options(useGeneratedKeys = true)
  void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo);

  @Select("SELECT * from shipped_line_items where orderId=#{orderId}")
  ShippedLineItem getShippedLineItemByOrderId(Integer orderId);

  @Update("UPDATE shipped_line_items set orderId=#{orderId}, productCode=#{productCode},quantityShipped=#{quantityShipped} WHERE id=#{id}")
  void updateShippedLineItem(ShippedLineItem shippedLineItem);
}
