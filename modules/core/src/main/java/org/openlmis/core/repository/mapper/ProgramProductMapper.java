package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramProductMapper {

    @Insert("INSERT INTO PROGRAM_PRODUCT(PROGRAM_CODE, PRODUCT_ID,ACTIVE, MODIFIED_BY, MODIFIED_DATE)" +
            "VALUES (#{programCode},(select id from product where LOWER(code)=  LOWER(#{productCode})), #{active}, #{modifiedBy}, #{modifiedDate})")
    int insert(ProgramProduct programProduct);

    @Delete ("DELETE FROM PROGRAM_PRODUCT")
    void deleteAll();
}