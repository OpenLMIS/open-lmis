package org.openlmis.programs.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.programs.domain.malaria.Treatment;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatmentMapper {
    @Insert("INSERT INTO treatments (productcode" +
            ", amount" +
            ", stock" +
            ", implementationid" +
            ") VALUES ( #{product.code}" +
            ", #{amount}" +
            ", #{stock}" +
            ", #{implementation.id})")
    @Options(useGeneratedKeys = true)
    public int insert(Treatment treatment);
}