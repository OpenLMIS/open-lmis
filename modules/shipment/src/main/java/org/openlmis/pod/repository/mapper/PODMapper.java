/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pod.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * It maps the OrderPOD and OrderPODLineItem entity to corresponding representation in database.
 */

@Repository
public interface PODMapper {

  @Insert(
    {"INSERT INTO pod_line_items (podId, productCode, quantityReceived, quantityShipped, quantityReturned, productName, dispensingUnit, packsToShip, fullSupply,",
      "productCategory, productCategoryDisplayOrder, productDisplayOrder, createdBy, modifiedBy, replacedProductCode, createdDate, modifiedDate) VALUES ",
      "(#{podId}, #{productCode}, #{quantityReceived}, #{quantityShipped}, #{quantityReturned}, #{productName}, #{dispensingUnit}, #{packsToShip}, #{fullSupply},",
      "#{productCategory}, #{productCategoryDisplayOrder}, #{productDisplayOrder}, #{createdBy}, #{modifiedBy}, #{replacedProductCode}, DEFAULT, DEFAULT)"})
  @Options(useGeneratedKeys = true)
  void insertPODLineItem(OrderPODLineItem orderPodLineItem);

  @Select(
    {"SELECT * FROM pod_line_items WHERE podId = #{podId} ORDER BY productCategoryDisplayOrder,",
      "LOWER(productCategory), productDisplayOrder NULLS LAST, LOWER(productCode)"})
  List<OrderPODLineItem> getPODLineItemsByPODId(Long podId);

  @Insert(
    {"INSERT INTO pod (orderId, orderNumber, facilityId, programId, periodId, receivedDate, deliveredBy, receivedBy, createdBy, modifiedBy) VALUES ",
      "(#{orderId}, #{orderNumber}, #{facilityId}, #{programId}, #{periodId}, #{receivedDate}, #{deliveredBy}, #{receivedBy}, #{createdBy}, #{modifiedBy} )"})
  @Options(useGeneratedKeys = true)
  void insertPOD(OrderPOD orderPod);

  @Select({"SELECT * FROM pod WHERE id = #{id}"})
  @Results({
    @Result(column = "id", property = "id"),
    @Result(property = "podLineItems", javaType = List.class, column = "id", many = @Many(select = "org.openlmis.pod.repository.mapper.PODMapper.getPODLineItemsByPODId"))
  })
  OrderPOD getPODById(Long id);

  @Select({"SELECT PLI.* FROM pod_line_items PLI INNER JOIN pod P ON PLI.podId = P.id ",
    "WHERE P.facilityId = #{requisition.facility.id} ",
    "AND P.programId = #{requisition.program.id} ",
    "AND P.createdDate >= #{startDate} ",
    "AND PLI.productCode = #{productCode}",
    "ORDER BY p.createdDate DESC LIMIT #{n}"})
  List<OrderPODLineItem> getNPodLineItems(@Param("productCode") String productCode,
                                          @Param("requisition") Rnr requisition,
                                          @Param("n") Integer n,
                                          @Param("startDate") Date startDate);

  @Select({"SELECT * FROM pod WHERE orderId = #{orderId}"})
  @Results(value = {@Result(column = "id", property = "id"), @Result(property = "podLineItems", javaType = List.class,
    column = "id",
    many = @Many(select = "org.openlmis.pod.repository.mapper.PODMapper.getPODLineItemsByPODId")),})
  OrderPOD getPODByOrderId(Long orderId);

  @Update({"UPDATE pod SET modifiedBy = #{modifiedBy}, receivedDate = #{receivedDate}, receivedBy = #{receivedBy},",
    " deliveredBy = #{deliveredBy}, modifiedDate = DEFAULT WHERE id = #{id}"})
  void update(OrderPOD orderPOD);

  @Update({"UPDATE pod_line_items SET quantityReceived = #{quantityReceived}, quantityReturned = #{quantityReturned}, ",
    "notes = #{notes}, modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT WHERE id = #{id}"})
  void updateLineItem(OrderPODLineItem lineItem);
}
