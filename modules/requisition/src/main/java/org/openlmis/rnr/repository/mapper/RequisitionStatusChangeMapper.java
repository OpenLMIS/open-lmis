package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface RequisitionStatusChangeMapper {

  @Insert({"INSERT INTO requisition_status_changes(rnrId, status, createdBy, modifiedBy) VALUES (#{rnrId}, #{status},",
    "#{createdBy}, #{createdBy})"})
  @Options(useGeneratedKeys = true)
  void insert(RequisitionStatusChange statusChange);

  @Select("SELECT * FROM requisition_status_changes WHERE id = #{id}")
  RequisitionStatusChange getById(Long id);

  @Select("SELECT createdDate FROM requisition_status_changes WHERE rnrId = #{rnrId} AND status = #{status}")
  Date getOperationDateFor(@Param("rnrId") Long rnrId, @Param("status") String status);
}
