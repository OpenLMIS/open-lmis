package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.DosageUnit;
import org.springframework.stereotype.Repository;

@Repository
public interface DosageUnitMapper {

    // Used by mapper
    @Select("SELECT * FROM dosage_units WHERE id = #{id}")
    DosageUnit getById(Integer id);

}
