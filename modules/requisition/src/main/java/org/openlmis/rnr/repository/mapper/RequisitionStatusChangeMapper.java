package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RequisitionStatusChangeMapper {

  @Insert({"INSERT INTO requisition_status_changes(rnrId, status, createdBy, modifiedBy) VALUES (#{rnrId}, #{status},",
    "#{createdBy.id}, #{createdBy.id})"})
  @Options(useGeneratedKeys = true)
  void insert(RequisitionStatusChange statusChange);

  @Select("SELECT * FROM requisition_status_changes WHERE id = #{id}")
  RequisitionStatusChange getById(Long id);

  @Select("SELECT createdDate FROM requisition_status_changes WHERE rnrId = #{rnrId} AND status = #{status}")
  Date getOperationDateFor(@Param("rnrId") Long rnrId, @Param("status") String status);

  @Select({"SELECT rsc.*, u.firstName, u.lastName, u.id as userId from requisition_status_changes rsc",
    "INNER JOIN users u ON rsc.createdBy = u.id WHERE rnrId = #{rnrId} ORDER BY createdDate"})
  @Results({
    @Result(column = "firstName", property = "createdBy.firstName"),
    @Result(column = "lastName", property = "createdBy.lastName"),
    @Result(column = "userId", property = "createdBy.id")
  })
  List<RequisitionStatusChange> getByRnrId(Long rnrId);
}
