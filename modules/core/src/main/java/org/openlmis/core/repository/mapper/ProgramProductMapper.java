package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.openlmis.core.domain.ProgramProduct;

public interface ProgramProductMapper {

    @Insert("INSERT INTO PROGRAM_PRODUCT(PROGRAM_CODE, PRODUCT_CODE, MODIFIED_BY, MODIFIED_DATE)" +
            "VALUES (#{programCode}, #{productCode}, #{modifiedBy}, #{modifiedDate})")
    int insert(ProgramProduct programProduct);

}