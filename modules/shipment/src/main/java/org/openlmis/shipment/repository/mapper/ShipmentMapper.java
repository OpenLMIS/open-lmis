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

import java.util.Date;


@Repository
public interface ShipmentMapper {

  @Insert({"INSERT INTO shipped_line_items (rnrId, productCode, quantityShipped, modifiedDate) " +
     "VALUES" +
     "(#{rnrId}, #{productCode}, #{quantityShipped}, #{modifiedDate})"})
   @Options(useGeneratedKeys = true)
   public void insertShippedLineItem(ShippedLineItem shippedLineItem);

  @Insert({"INSERT INTO shipment_file_info (fileName, processingError) VALUES (#{fileName},#{processingError})"})
  @Options(useGeneratedKeys = true)
  void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo);

  @Select("Select * from shipment_file_info where id = #{id}")
  ShipmentFileInfo getShipmentFileInfo(Long id);

  @Select("SELECT * FROM shipped_line_items WHERE rnrId=#{rnrId} AND productCode=#{productCode}")
  ShippedLineItem getShippedLineItem(ShippedLineItem shippedLineItem);

  @Update("UPDATE shipped_line_items SET rnrId=#{rnrId}, productCode=#{productCode},quantityShipped=#{quantityShipped},modifiedDate=#{modifiedDate} WHERE id=#{id}")
  void updateShippedLineItem(ShippedLineItem shippedLineItem);

  @Select("SELECT modifiedDate FROM shipped_line_items WHERE rnrId = #{rnrId} LIMIT 1")
  Date getProcessedTimeStamp(ShippedLineItem shippedLineItem);
}
