package org.openlmis.rnr.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.Requisition;

public interface RnrMapper {

    @Select("insert into requisition(facility_code, program_code, status, modified_by, modified_date) " +
            "values (#{facilityCode}, #{programCode}, #{status}, #{modifiedBy}, #{modifiedDate}) returning id")
    @Options(useGeneratedKeys=true)
    public int insert(Requisition requisition);

    @Delete("delete from requisition")
    public void deleteAll();

}
