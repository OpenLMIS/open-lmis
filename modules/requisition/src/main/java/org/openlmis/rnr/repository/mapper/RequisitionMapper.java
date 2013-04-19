/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.rnr.domain.OrderBatch;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionMapper {

  @Insert("INSERT INTO requisitions(facilityId, programId, periodId, status, modifiedBy) " +
      "VALUES (#{facility.id}, #{program.id}, #{period.id}, #{status}, #{modifiedBy})")
  @Options(useGeneratedKeys = true)
  void insert(Rnr requisition);

  @Update({"UPDATE requisitions SET",
      "modifiedBy = #{modifiedBy},",
      "status = #{status},",
      "modifiedDate = DEFAULT,",
      "fullSupplyItemsSubmittedCost = #{fullSupplyItemsSubmittedCost},",
      "submittedDate = #{submittedDate},",
      "nonFullSupplyItemsSubmittedCost = #{nonFullSupplyItemsSubmittedCost},",
      "supervisoryNodeId = #{supervisoryNodeId},",
      "supplyingFacilityId = #{supplyingFacility.id},",
      "orderBatchId = #{orderBatch.id}",
      "WHERE id = #{id}"})
  void update(Rnr requisition);

  @Select("SELECT * FROM requisitions WHERE id = #{rnrId}")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "period.id", column = "periodId"),
      @Result(property = "supplyingFacility.id", column = "supplyingFacilityId"),
      @Result(property = "orderBatch", javaType = OrderBatch.class, column = "orderBatchId",
          one = @One(select = "org.openlmis.rnr.repository.mapper.RequisitionMapper.getOrderBatchById")),
      @Result(property = "fullSupplyLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.RnrLineItemMapper.getRnrLineItemsByRnrId")),
      @Result(property = "nonFullSupplyLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.RnrLineItemMapper.getNonFullSupplyRnrLineItemsByRnrId")),
  })
  Rnr getById(Integer rnrId);

  @Select({"SELECT id, programId, facilityId, periodId, submittedDate, modifiedDate",
      "FROM requisitions ",
      "WHERE programId =  #{programId}",
      "AND supervisoryNodeId =  #{supervisoryNode.id}"})
  @Results({@Result(property = "program.id", column = "programId"),
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "period.id", column = "periodId")})
  List<Rnr> getAuthorizedRequisitions(RoleAssignment roleAssignment);

  @Select("SELECT * FROM requisitions WHERE facilityId = #{facility.id} AND programId= #{program.id} AND periodId = #{period.id}")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "period.id", column = "periodId"),
      @Result(property = "fullSupplyLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.RnrLineItemMapper.getRnrLineItemsByRnrId")),
      @Result(property = "nonFullSupplyLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.rnr.repository.mapper.RnrLineItemMapper.getNonFullSupplyRnrLineItemsByRnrId")),
  })
  Rnr getRequisitionWithLineItems(@Param("facility") Facility facility,
                                  @Param("program") Program program,
                                  @Param("period") ProcessingPeriod period);


  @Select("SELECT * FROM requisitions " +
      "WHERE facilityId = #{facilityId} " +
      "AND programId = #{programId} " +
      "AND status NOT IN ('INITIATED', 'SUBMITTED') " +
      "ORDER BY submittedDate DESC " +
      "LIMIT 1")
  @Results(value = {
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "period.id", column = "periodId"),
  })
  Rnr getLastRequisitionToEnterThePostSubmitFlow(@Param(value = "facilityId") Integer facilityId,
                                                 @Param(value = "programId") Integer programId);

  @Select("SELECT id, programId, facilityId, periodId, supplyingFacilityId, submittedDate, modifiedDate FROM requisitions WHERE STATUS='APPROVED' ORDER BY submittedDate")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "period.id", column = "periodId"),
      @Result(property = "supplyingFacility.id", column = "supplyingFacilityId")
  })
  List<Rnr> getApprovedRequisitions();

  @Select({"SELECT * FROM requisitions ",
      "WHERE facilityId = #{facility.id} AND programId = #{program.id} AND periodId = ANY (#{periods}::INTEGER[]) AND status NOT IN ('INITIATED', 'SUBMITTED')"})
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "period.id", column = "periodId")
  })
  List<Rnr> get(@Param("facility") Facility facility, @Param("program") Program program, @Param("periods") String periodIds);

  @Insert("INSERT INTO order_batches(createdByUserId, createTimeStamp) VALUES (#{createdByUserId}, DEFAULT)")
  @Options(useGeneratedKeys = true)
  void createOrderBatch(OrderBatch orderBatch);

  @Select("SELECT * from order_batches WHERE id = #{id}")
  OrderBatch getOrderBatchById(Integer id);


  @Select({"SELECT R.*, O.id orderBatchId, O.createdByUserId orderBatchCreatedByUserId, O.createTimeStamp createTimeStamp FROM",
      "requisitions R INNER JOIN order_Batches O on R.orderbatchId = O.id",
      "WHERE R.status = #{status}",
      "ORDER BY O.createTimeStamp DESC"})
  @Results(value = {
      @Result(property = "facility.id", column = "facilityId"),
      @Result(property = "program.id", column = "programId"),
      @Result(property = "period.id", column = "periodId"),
      @Result(property = "supplyingFacility.id", column = "supplyingFacilityId"),
      @Result(property = "orderBatch.id", column = "orderBatchId"),
      @Result(property = "orderBatch.createTimeStamp", column = "createTimeStamp"),
      @Result(property = "orderBatch.createdByUserId", column = "orderBatchCreatedByUserId")
  })
  List<Rnr> getByStatus(RnrStatus status);
}
