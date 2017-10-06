package org.openlmis.programs.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.openlmis.programs.domain.malaria.Treatment;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Insert("<script>" +
            "INSERT INTO treatments (productcode, amount, stock, implementationid) VALUES " +
            "<foreach item='treatment' collection='list' separator=','>" +
            "(#{treatment.product.code}, #{treatment.amount}, #{treatment.stock}, #{treatment.implementation.id})" +
            "</foreach>" +
            "</script>")
    @Options(useGeneratedKeys = true)
    int bulkInsert(@Param("list") List<Treatment> treatments);

}