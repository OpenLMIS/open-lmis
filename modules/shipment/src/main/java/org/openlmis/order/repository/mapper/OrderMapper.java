/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.order.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the Order and OrderFileColumn entity to corresponding representation in database.
 */

@Repository
public interface OrderMapper {

  @Insert({"INSERT INTO orders(id, orderNumber, status, ftpcomment, supplyLineId, createdBy, modifiedBy) ",
      "VALUES (#{rnr.id}, #{orderNumber}, #{status}, #{ftpComment}, #{supplyLine.id}, #{createdBy}, #{createdBy})"})
  void insert(Order order);

  @Select({"SELECT DISTINCT O.* FROM orders O INNER JOIN supply_lines S ON O.supplyLineId = S.id ",
      "INNER JOIN fulfillment_role_assignments FRA ON S.supplyingFacilityId = FRA.facilityId ",
      "INNER JOIN role_rights RR ON FRA.roleId = RR.roleId",
      "WHERE FRA.userid = #{userId} AND RR.rightName = #{right} ORDER BY O.createdDate DESC LIMIT #{limit} OFFSET #{offset}"})
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "rnr.id", column = "id"),
      @Result(property = "shipmentFileInfo", javaType = ShipmentFileInfo.class, column = "shipmentId",
          one = @One(select = "org.openlmis.shipment.repository.mapper.ShipmentMapper.getShipmentFileInfo")),
      @Result(property = "supplyLine", javaType = SupplyLine.class, column = "supplyLineId",
          one = @One(select = "org.openlmis.core.repository.mapper.SupplyLineMapper.getById"))
  })
  List<Order> getOrders(@Param("limit") int limit, @Param("offset") int offset, @Param("userId") Long userId, @Param("right") String rightName);


  @Select({"SELECT DISTINCT O.*, f.name FROM orders O INNER JOIN supply_lines S ON O.supplyLineId = S.id ",
      "INNER JOIN fulfillment_role_assignments FRA ON S.supplyingFacilityId = FRA.facilityId",
      "INNER JOIN requisitions r on r.id = O.id ",
      "INNER JOIN role_rights RR ON FRA.roleId = RR.roleId ",
      " INNER JOIN facilities f on f.id = r.facilityid ",
      "WHERE FRA.userid = #{userId} AND RR.rightName = #{rightName} and S.supplyingFacilityId = #{supplyDepot} and r.programId = #{program} and r.periodId = #{period} " +
          "ORDER BY f.name ASC LIMIT #{limit} OFFSET #{offset}"})
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "rnr.id", column = "id"),
      @Result(property = "shipmentFileInfo", javaType = ShipmentFileInfo.class, column = "shipmentId",
          one = @One(select = "org.openlmis.shipment.repository.mapper.ShipmentMapper.getShipmentFileInfo")),
      @Result(property = "supplyLine", javaType = SupplyLine.class, column = "supplyLineId",
          one = @One(select = "org.openlmis.core.repository.mapper.SupplyLineMapper.getById"))
  })
  List<Order> getOrdersByDepot(@Param("limit") int limit, @Param("offset") int offset, @Param("userId") Long userId, @Param("rightName") String rightName, @Param("supplyDepot") Long supplyDepot, @Param("program") Long program, @Param("period") Long period);

  @Select("SELECT * FROM orders WHERE id = #{id}")
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "rnr.id", column = "id"),
      @Result(property = "supplyLine", javaType = SupplyLine.class, column = "supplyLineId",
          one = @One(select = "org.openlmis.core.repository.mapper.SupplyLineMapper.getById"))
  })
  Order getById(Long id);

  @Update({"UPDATE orders SET",
      "shipmentId = #{shipmentId},",
      "status = #{status},",
      "modifiedDate = CURRENT_TIMESTAMP",
      "WHERE orderNumber = #{orderNumber}"})
  void updateShipmentAndStatus(@Param("orderNumber") String orderNumber,
                               @Param("status") OrderStatus status,
                               @Param("shipmentId") Long shipmentId);

  @Select("SELECT * FROM order_file_columns ORDER BY position")
  List<OrderFileColumn> getOrderFileColumns();

  @Delete("DELETE from order_file_columns")
  void deleteOrderFileColumns();

  @Insert("INSERT INTO order_file_columns (dataFieldLabel, includeInOrderFile, format, columnLabel, position, openLmisField, nested, keyPath, createdBy, modifiedBy)" +
      " VALUES (#{dataFieldLabel}, #{includeInOrderFile}, #{format}, #{columnLabel}, #{position}, #{openLmisField}, #{nested}, #{keyPath}, #{modifiedBy}, #{modifiedBy})")
  void insertOrderFileColumn(OrderFileColumn orderFileColumn);


  @Update("UPDATE orders SET status = #{status}, ftpComment = #{ftpComment}, modifiedDate = CURRENT_TIMESTAMP WHERE id = #{id}")
  void updateOrderStatus(Order order);

  @Select("SELECT status FROM orders WHERE orderNumber = #{orderNumber}")
  OrderStatus getStatus(String orderNumber);

  @Select("SELECT ceil(count(*)::float/#{pageSize}) FROM orders")
  Integer getNumberOfPages(int pageSize);

  @Select("SELECT ceil(count(*)::float/#{pageSize}) FROM orders o join supply_lines s on s.id = o.supplylineid join requisitions r on r.id = o.id where r.programId = #{program} and r.periodid = #{period} and s.supplyingfacilityid = #{depot}")
  Integer getNumberOfPagesByDepot(@Param("pageSize") int pageSize, @Param("depot") long depot, @Param("program") long program, @Param("period") long period);


  @Select({"SELECT O.* FROM orders O INNER JOIN requisitions r on r.id = O.id INNER JOIN supply_lines S ON O.supplyLineId = S.id",
      "WHERE r.programId = #{program} and supplyingFacilityId = ANY(#{facilityIds}::INTEGER[]) AND O.status = ANY(#{statuses}::VARCHAR[]) ",
      "ORDER BY O.createdDate"})
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "rnr.id", column = "id"),
      @Result(property = "shipmentFileInfo", javaType = ShipmentFileInfo.class, column = "shipmentId",
          one = @One(select = "org.openlmis.shipment.repository.mapper.ShipmentMapper.getShipmentFileInfo")),
      @Result(property = "supplyLine", javaType = SupplyLine.class, column = "supplyLineId",
          one = @One(select = "org.openlmis.core.repository.mapper.SupplyLineMapper.getById"))
  })
  List<Order> getByWarehouseIdsAndStatuses(@Param("facilityIds") String facilityIds, @Param("statuses") String statuses, @Param("program") Long program);

  @Select({"SELECT O.* FROM orders O INNER JOIN requisitions r on r.id = O.id INNER JOIN supply_lines S ON O.supplyLineId = S.id",
      "WHERE r.facilityId = #{facility} and r.programId = #{program} and supplyingFacilityId = ANY(#{facilityIds}::INTEGER[]) AND O.status = ANY(#{statuses}::VARCHAR[]) ",
      "ORDER BY O.createdDate"})
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "rnr.id", column = "id"),
      @Result(property = "shipmentFileInfo", javaType = ShipmentFileInfo.class, column = "shipmentId",
          one = @One(select = "org.openlmis.shipment.repository.mapper.ShipmentMapper.getShipmentFileInfo")),
      @Result(property = "supplyLine", javaType = SupplyLine.class, column = "supplyLineId",
          one = @One(select = "org.openlmis.core.repository.mapper.SupplyLineMapper.getById"))
  })
  List<Order> getByWarehouseIdsAndStatusesByFacility(@Param("facilityIds") String facilityIds, @Param("statuses") String statuses, @Param("program") Long program, @Param("facility") Long facilityId);


  @Select("SELECT * FROM orders WHERE orderNumber = #{orderNumber}")
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "rnr.id", column = "id"),
      @Result(property = "supplyLine", javaType = SupplyLine.class, column = "supplyLineId",
          one = @One(select = "org.openlmis.core.repository.mapper.SupplyLineMapper.getById"))
  })
  Order getByOrderNumber(String orderNumber);
}