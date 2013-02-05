package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyLineMapper {

  @Insert("INSERT INTO supply_lines " +
    "(description,supervisoryNodeId,programId,supplyingFacilityId,modifiedBy, modifiedDate)" +
    "VALUES (#{description},#{supervisoryNode.id},#{program.id},#{supplyingFacility.id},#{modifiedBy},#{modifiedDate})")
  @Options(useGeneratedKeys = true)
  Integer insert(SupplyLine supplyLine);

  @Select("SELECT * FROM supply_lines WHERE supervisoryNodeId = #{supervisoryNode.id} AND programId = #{program.id}")
  SupplyLine getSupplyLineBy(@Param(value = "supervisoryNode")SupervisoryNode supervisoryNode, @Param(value = "program")Program program);
}
