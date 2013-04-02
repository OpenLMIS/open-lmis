package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionStatusChangeMapper {

  @Insert({"INSERT INTO requisition_status_changes(rnrId, status, statusChangedBy) VALUES (#{rnrId}, #{status},",
    "#{statusChangedBy})"})
  @Options(useGeneratedKeys = true)
  void insert(RequisitionStatusChange statusChange);

  @Select("SELECT * FROM requisition_status_changes WHERE id = #{id}")
  RequisitionStatusChange getById(Integer id);
}
