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
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface ShipmentMapper {

  @Insert({"INSERT INTO shipment_line_items ",
    "(orderId, productCode, quantityShipped, cost, packedDate, shippedDate)",
    "VALUES",
    "(#{orderId}, #{productCode}, #{quantityShipped}, #{cost}, #{packedDate}, #{shippedDate})"})
  @Options(useGeneratedKeys = true)
  public void insertShippedLineItem(ShipmentLineItem shipmentLineItem);

  @Insert({"INSERT INTO shipment_file_info (fileName, processingError) VALUES (#{fileName},#{processingError})"})
  @Options(useGeneratedKeys = true)
  void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo);

  @Select("SELECT * FROM shipment_file_info WHERE id = #{id}")
  ShipmentFileInfo getShipmentFileInfo(Long id);

  @Select({"SELECT * FROM shipment_line_items",
    "WHERE orderId = #{orderId} AND productCode=#{productCode}"})
  ShipmentLineItem getShippedLineItem(ShipmentLineItem shipmentLineItem);

  @Update({"UPDATE shipment_line_items ",
    "SET orderId = #{orderId},",
    "productCode= #{productCode},",
    "quantityShipped= #{quantityShipped},",
    "modifiedDate = DEFAULT",
    "WHERE id= #{id}"})
  void updateShippedLineItem(ShipmentLineItem shipmentLineItem);

  @Select("SELECT modifiedDate FROM shipment_line_items WHERE orderId = #{orderId} LIMIT 1")
  Date getProcessedTimeStamp(ShipmentLineItem shipmentLineItem);
}
