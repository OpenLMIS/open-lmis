package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.stereotype.Repository;

@Repository
public interface RnrMapper {

    @Select("insert into requisition(facility_code, program_code, status, modified_by) " +
            "values (#{facilityCode}, #{programCode}, #{status}, #{modifiedBy}) returning id")
    @Options(useGeneratedKeys=true)
    public int insert(Rnr requisition);

    @Delete("delete from requisition")
    public void deleteAll();

    @Update("update requisition set modified_by = #{modifiedBy}, status = #{status}, modified_date= DEFAULT where id = #{id}")
    public void update(Rnr requisition);

    @Select("Select * from requisition where id = #{rnrId}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "facilityCode", column = "facility_code"),
            @Result(property = "programCode", column = "program_code"),
            @Result(property = "status", column = "status"),
            @Result(property = "modifiedBy", column = "modified_by"),
            @Result(property = "modifiedDate", column = "modified_date")
    })
    public Rnr getRequisitionById(int rnrId);

}
