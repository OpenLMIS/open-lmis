package org.openlmis.vaccine.repository.mapper.OrderRequisitions;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.stockmanagement.domain.LotOnHand;
import org.openlmis.stockmanagement.repository.mapper.StockCardMapper;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition;
import org.openlmis.vaccine.dto.OrderRequisitionDTO;
import org.openlmis.vaccine.dto.OrderRequisitionStockCardDTO;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface VaccineOrderRequisitionMapper {

    @Insert("INSERT INTO vaccine_order_requisitions (periodId,programId,status,supervisoryNodeId,facilityId,orderDate," +
            " createdBy, createdDate,modifiedBy,modifiedDate,emergency )    " +
            "VALUES (#{periodId},#{programId},#{status},#{supervisoryNodeId},#{facilityId},#{orderDate}," +
            "#{createdBy}, NOW(),#{modifiedBy},NOW(),#{emergency} )")
    @Options(useGeneratedKeys = true)
    Integer insert(VaccineOrderRequisition orderRequisition);

    @Update("Update vaccine_order_requisitions SET " +
            "periodId = #{periodId}, " +
            "programId = #{programId}, " +
            "status = #{status}, " +
            "supervisoryNodeId = #{supervisoryNodeId}, " +
            "facilityId = #{facilityId}, " +
            "orderDate = #{orderDate}," +
            "modifiedBy = #{createdBy}, " +
            "modifiedDate = #{modifiedDate}," +
            "emergency = #{emergency} " +
            "WHERE id = #{id} ")
    void update(VaccineOrderRequisition orderRequisition);

    @Update("Update vaccine_order_requisitions SET   "
            +" status = 'ISSUED'  " +
            "WHERE id = #{orderId}  ")
    Long updateORStatus(@Param("orderId") Long orderId);

    @Select(" SELECT * FROM vaccine_order_requisitions WHERE periodId = #{periodId}  and  programId = #{programId} AND facilityId = #{facilityId} and emergency=false")
    VaccineOrderRequisition getByFacilityProgram(@Param("periodId") Long periodId, @Param("programId") Long programId, @Param("facilityId") Long facilityId);

    @Select("select * from vaccine_order_requisitions " +
            "   where " +
            "   facilityId = #{facilityId} and programId = #{programId} order by id desc limit 1")
    VaccineOrderRequisition getLastReport(@Param("facilityId") Long facilityId, @Param("programId") Long programId);



    @Select(" select * from vaccine_order_requisitions where id = #{id} ")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "periodId", column = "periodId"),
            @Result(property = "facilityId", column = "facilityId"),
            @Result(property = "programId", column = "programId"),
            @Result(property = "lineItems", javaType = List.class, column = "id",
                    many = @Many(select = "org.openlmis.vaccine.repository.mapper.OrderRequisitions.VaccineOrderRequisitionLineItemsMapper.getLineItems")),
            @Result(property = "statusChanges", javaType = List.class, column = "id",
                    many = @Many(select = "org.openlmis.vaccine.repository.mapper.OrderRequisitions.VaccineStatusRequisitionChangeMapper.getChangeLogByReportId")),
            @Result(property = "facility", javaType = Facility.class, column = "facilityId",
                    many = @Many(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "program", javaType = Program.class, column = "programId",
                    many = @Many(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById")),
            @Result(property = "period", javaType = ProcessingPeriod.class, column = "periodId",
                    many = @Many(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById"))
    })
    VaccineOrderRequisition getAllOrderDetails(@Param("id") Long id);



    @Select("select max(s.scheduleId) id from requisition_group_program_schedules s " +
            " join requisition_group_members m " +
            "     on m.requisitionGroupId = s.requisitionGroupId " +
            " where " +
            "   s.programId = #{programId} " +
            "   and m.facilityId = #{facilityId} ")
    Long getScheduleFor(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

    @Select("SELECT DISTINCT f.id AS facilityId,r.periodId,f.name facilityName,R.orderDate,R.ID,R.STATUS,ra.programid AS programId,pp.name periodName  " +
            "   FROM facilities f  " +
            "     JOIN requisition_group_members m ON m.facilityid = f.id  " +
            "     JOIN requisition_groups rg ON rg.id = m.requisitiongroupid  " +
            "     JOIN supervisory_nodes sn ON sn.id = rg.supervisorynodeid  " +
            "     JOIN role_assignments ra ON ra.supervisorynodeid = sn.id OR ra.supervisorynodeid = sn.parentid " +
            "     JOIN vaccine_order_requisitions r on f.id = r.facilityId and sn.id = r.supervisorynodeid " +
            "     JOIN processing_periods pp on r.periodId = pp.id " +
            "     WHERE ra.userId = #{userId} AND R.STATUS  IN('SUBMITTED') and r.programId = #{programId} and sn.FACILITYiD = #{facilityId}")
      List<OrderRequisitionDTO> getPendingRequest(@Param("userId") Long userId, @Param("facilityId") Long facilityId, @Param("programId") Long programId);


    @Select("select r.id, p.name as periodName, r.facilityId, r.status, r.programId " +
            " from vaccine_order_requisitions r " +
            "   join processing_periods p on p.id = r.periodId " +
            " where r.facilityId = #{facilityId} and r.programId = #{programId}" +
            " order by p.startDate desc ")
    List<OrderRequisitionDTO> getReportedPeriodsForFacility(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

    @Select("Select id from vaccine_order_requisitions where facilityid = #{facilityId} and periodid = #{periodId}")
    Long getReportIdForFacilityAndPeriod(@Param("facilityId") Long facilityId, @Param("periodId") Long periodId);

    @Select("select * from vaccine_order_requisitions r " +
            "JOIN vaccine_order_requisition_line_items li on r.id = li.orderId  " +
            " WHERE programId = #{programId} AND periodId = #{periodId} and facilityId = #{facilityId} and R.STATUS  IN('SUBMITTED') ")
    List<OrderRequisitionDTO> getAllBy(@Param("programId") Long programId, @Param("periodId") Long periodId, @Param("facilityId") Long facilityId);

    @Select("select r.id,p.name programName, f.name facilityName,r.status,pp.startdate periodStartDate,pp.enddate periodEndDate,emergency,orderDate::timestamp  from vaccine_order_requisitions r   " +
            "JOIN programs p on r.programId =p.id  " +
            "JOIN processing_periods pp on r.periodId = pp.id  " +
            "JOIN facilities f on r.facilityId= f.id    "+
            " WHERE programId = #{programId} AND r.createdDate >= #{dateRangeStart}::date and r.createdDate <= #{dateRangeEnd}::date  " +
            " and facilityId = #{facilityId} and R.STATUS  IN('SUBMITTED')")
    List<OrderRequisitionDTO> getSearchedDataBy(@Param("facilityId") Long facilityId,
                                                @Param("dateRangeStart") String dateRangeStart,
                                                @Param("dateRangeEnd") String dateRangeEnd
                                                ,@Param("programId") Long programId);

    @Select("SELECT *" +
            " FROM vw_stock_cards" +
            " WHERE facilityid = #{facilityId}" +
            " AND programid = #{programId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "product", column = "productId", javaType = Product.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
            @Result(property = "lotsOnHand", column = "id", javaType = List.class,
                    many = @Many(select = "getLotsOnHand"))

    })
    List<OrderRequisitionStockCardDTO> getAllByFacilityAndProgram(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

    @Select("SELECT loh.*" +
            " FROM lots_on_hand loh" +
            " WHERE loh.stockcardid = #{stockCardId}")
    @Results({
            @Result(
                    property = "lot", column = "lotId", javaType = Lot.class,
                    one = @One(select = "org.openlmis.stockmanagement.repository.mapper.LotMapper.getById"))
    })
    List<LotOnHand> getLotsOnHand(@Param("stockCardId") Long stockCardId);

}

