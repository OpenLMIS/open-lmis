package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.stereotype.Repository;

@Repository
public interface RnrMapper {

    @Select("insert into requisition(facility_code, program_code, status, modified_by, modified_date) " +
            "values (#{facilityCode}, #{programCode}, #{status}, #{modifiedBy}, #{modifiedDate}) returning id")
    @Options(useGeneratedKeys=true)
    public int insert(Rnr requisition);

    @Delete("delete from requisition")
    public void deleteAll();

}
