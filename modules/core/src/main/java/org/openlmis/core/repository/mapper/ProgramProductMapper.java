package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramProductMapper {

    @Insert("INSERT INTO PROGRAM_PRODUCT(PROGRAM_CODE, PRODUCT_CODE,ACTIVE, MODIFIED_BY, MODIFIED_DATE)" +
            "VALUES (#{programCode}, #{productCode}, #{active}, #{modifiedBy}, #{modifiedDate})")
    int insert(ProgramProduct programProduct);

    @Delete ("DELETE FROM PROGRAM_PRODUCT")
    void deleteAll();
}