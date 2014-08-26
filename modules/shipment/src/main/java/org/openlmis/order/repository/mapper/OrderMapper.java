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
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
  List<Order> getOrders(@Param("limit") int limit, @Param("offset") int offset, @Param("userId") Long userId, @Param("right") Right right);


  @Select({"SELECT DISTINCT O.* FROM orders O INNER JOIN supply_lines S ON O.supplyLineId = S.id ",
      "INNER JOIN fulfillment_role_assignments FRA ON S.supplyingFacilityId = FRA.facilityId",
       "INNER JOIN requisitions r on r.id = O.id ",
      "INNER JOIN role_rights RR ON FRA.roleId = RR.roleId",
      "WHERE FRA.userid = #{userId} AND RR.rightName = #{right} and S.supplyingFacilityId = #{supplyDepot} and r.programId = #{program} ORDER BY O.createdDate DESC LIMIT #{limit} OFFSET #{offset}"})
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "rnr.id", column = "id"),
      @Result(property = "shipmentFileInfo", javaType = ShipmentFileInfo.class, column = "shipmentId",
          one = @One(select = "org.openlmis.shipment.repository.mapper.ShipmentMapper.getShipmentFileInfo")),
      @Result(property = "supplyLine", javaType = SupplyLine.class, column = "supplyLineId",
          one = @One(select = "org.openlmis.core.repository.mapper.SupplyLineMapper.getById"))
  })
  List<Order> getOrdersByDepot(@Param("limit") int limit, @Param("offset") int offset, @Param("userId") Long userId, @Param("right") Right right, @Param("supplyDepot") Long supplyDepot, @Param("program") Long program);


  @SelectProvider(type = ViewOrderSearch.class, method = "getOrderByCriteria")
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "rnr.id", column = "id"),
      @Result(property = "shipmentFileInfo", javaType = ShipmentFileInfo.class, column = "shipmentId",
          one = @One(select = "org.openlmis.shipment.repository.mapper.ShipmentMapper.getShipmentFileInfo")),
      @Result(property = "supplyLine", javaType = SupplyLine.class, column = "supplyLineId",
          one = @One(select = "org.openlmis.core.repository.mapper.SupplyLineMapper.getById"))
  })
  List<Order> getSearchOrders(@Param("userId") Long userId, @Param("page") int page, @Param("query") String query, @Param("searchType") String searchType );

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
    "modifiedDate = DEFAULT",
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


  @Update("UPDATE orders SET status = #{status}, ftpComment = #{ftpComment}, modifiedDate = DEFAULT WHERE id = #{id}")
  void updateOrderStatus(Order order);

  @Select("SELECT status FROM orders WHERE orderNumber = #{orderNumber}")
  OrderStatus getStatus(String orderNumber);

  @Select("SELECT ceil(count(*)::float/#{pageSize}) FROM orders")
  Integer getNumberOfPages(int pageSize);

  @Select("SELECT ceil(count(*)::float/#{pageSize}) FROM orders o join supply_lines s on s.id = o.supplylineid join requisitions r on r.id = o.id where r.programId = #{program} and s.supplyingfacilityid = #{depot}")
  Integer getNumberOfPagesByDepot(@Param("pageSize")int pageSize, @Param("depot") long depot, @Param("program") long program);


  @Select({"SELECT O.* FROM orders O INNER JOIN supply_lines S ON O.supplyLineId = S.id",
    "WHERE supplyingFacilityId = ANY(#{facilityIds}::INTEGER[]) AND status = ANY(#{statuses}::VARCHAR[])",
    "ORDER BY O.createdDate"})
  @Results({
    @Result(property = "id", column = "id"),
    @Result(property = "rnr.id", column = "id"),
    @Result(property = "shipmentFileInfo", javaType = ShipmentFileInfo.class, column = "shipmentId",
      one = @One(select = "org.openlmis.shipment.repository.mapper.ShipmentMapper.getShipmentFileInfo")),
    @Result(property = "supplyLine", javaType = SupplyLine.class, column = "supplyLineId",
      one = @One(select = "org.openlmis.core.repository.mapper.SupplyLineMapper.getById"))
  })
  List<Order> getByWarehouseIdsAndStatuses(@Param("facilityIds") String facilityIds, @Param("statuses") String statuses);

  @Select("SELECT * FROM orders WHERE orderNumber = #{orderNumber}")
  @Results({
    @Result(property = "id", column = "id"),
    @Result(property = "rnr.id", column = "id"),
    @Result(property = "supplyLine", javaType = SupplyLine.class, column = "supplyLineId",
      one = @One(select = "org.openlmis.core.repository.mapper.SupplyLineMapper.getById"))
  })
  Order getByOrderNumber(String orderNumber);

  public class ViewOrderSearch {

    @SuppressWarnings("UnusedDeclaration")
    public static String getOrderByCriteria(Map<String, Object> params){
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT DISTINCT O.* FROM orders O INNER JOIN supply_lines S ON O.supplyLineId = S.id " +
      "INNER JOIN requisitions R ON R.id = O.id " +
      "INNER JOIN facilities F ON R.facilityId = F.id " +
      "INNER JOIN programs P ON P.id = R.programId " +
      "INNER JOIN fulfillment_role_assignments FRA ON S.supplyingFacilityId = FRA.facilityId " +
      "INNER JOIN role_rights RR ON FRA.roleId = RR.roleId" );
      appendQueryClausesBySearchType(sql, params) ;

      Integer pageNumber = (Integer) params.get("page");
      Integer pageSize = 100;


      return sql.append("ORDER BY createdDate" ).append(" LIMIT ").append(pageSize)
          .append(" OFFSET ").append((pageNumber - 1) * pageSize).toString();

    }

    private static void appendQueryClausesBySearchType(StringBuilder sql, Map<String, Object> params) {
      String searchType = (String) params.get("searchType");
      String searchVal = ((String) params.get("query")).toLowerCase();
      Long userId = (Long) params.get("userId");

      if (searchVal.isEmpty()) {
        sql.append("WHERE ");
      } else if (searchType.isEmpty() || searchType.equalsIgnoreCase(RequisitionService.SEARCH_ALL)) {
        sql.append("WHERE (LOWER(P.name) LIKE '%" + searchVal + "%' OR LOWER(F.name) LIKE '%" +
            searchVal + "%' OR LOWER(F.code) LIKE '%" + searchVal + "%' OR LOWER(SF.name) LIKE '%" + searchVal + "%') AND ");
      } else if (searchType.equalsIgnoreCase(RequisitionService.SEARCH_FACILITY_CODE)) {
        sql.append("WHERE LOWER(F.code) LIKE '%" + searchVal + "%' AND ");
      } else if (searchType.equalsIgnoreCase(RequisitionService.SEARCH_FACILITY_NAME)) {
        sql.append("WHERE LOWER(F.name) LIKE '%" + searchVal + "%' AND ");
      } else if (searchType.equalsIgnoreCase(RequisitionService.SEARCH_PROGRAM_NAME)) {
        sql.append("WHERE LOWER(P.name) LIKE '%" + searchVal + "%' AND ");
      } else if (searchType.equalsIgnoreCase(RequisitionService.SEARCH_SUPPLYING_DEPOT_NAME)) {
        sql.append("WHERE LOWER(SF.name) LIKE '%" + searchVal + "%' AND ");
      }
      sql.append("FRA.userId = " + userId + " AND RR.rightName = 'VIEW_ORDER' AND ");

    }
  }
}
