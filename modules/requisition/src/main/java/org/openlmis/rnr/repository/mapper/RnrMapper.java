package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.stereotype.Repository;

@Repository
public interface RnrMapper {

  @Select("insert into requisition(facility_id, program_code, status, modified_by) " +
      "values (#{facilityId}, #{programCode}, #{status}, #{modifiedBy}) returning id")
  @Options(useGeneratedKeys = true)
  public Long insert(Rnr requisition);

  @Delete("delete from requisition")
  public void deleteAll();

  @Update("update requisition set modified_by = #{modifiedBy}, status = #{status}, modified_date= DEFAULT where id = #{id}")
  public void update(Rnr requisition);

  @Select("Select * from requisition where id = #{rnrId}")
  @Results(value = {
      @Result(property = "facilityId", column = "facility_id"),
      @Result(property = "programCode", column = "program_code"),
      @Result(property = "modifiedBy", column = "modified_by"),
      @Result(property = "modifiedDate", column = "modified_date")
  })
  public Rnr getRequisitionById(Long rnrId);

  @Select("Select * from requisition where facility_id = #{facilityId} and program_code= #{programCode}")
  @Results(value = {
      @Result(property = "facilityId", column = "facility_id"),
      @Result(property = "programCode", column = "program_code"),
      @Result(property = "modifiedBy", column = "modified_by"),
      @Result(property = "modifiedDate", column = "modified_date")
  })
  public Rnr getRequisitionByFacilityAndProgram(@Param("facilityId") Long facilityId,
                                                @Param("programCode") String programCode);
}
