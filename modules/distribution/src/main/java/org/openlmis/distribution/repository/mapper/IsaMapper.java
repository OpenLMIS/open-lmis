package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.springframework.stereotype.Repository;

@Repository
public interface IsaMapper {

  @Insert({"INSERT INTO program_product_isa",
    "(programProductId, whoRatio, dosesPerYear, wastageRate, bufferPercentage, minimumValue, adjustmentValue, calculatedIsa)",
    "VALUES (#{programProductId}, #{whoRatio}, #{dosesPerYear}, #{wastageRate}, #{bufferPercentage},",
    "#{minimumValue}, #{adjustmentValue}, #{calculatedIsa} )"})
  @Options(useGeneratedKeys = true)
  void insert(ProgramProductISA programProductISA);

  @Update({"UPDATE program_product_isa SET whoRatio = #{whoRatio} , dosesPerYear = #{dosesPerYear}, ",
    "wastageRate = #{wastageRate}, bufferPercentage = #{bufferPercentage}, minimumValue = #{minimumValue}, ",
    "adjustmentValue = #{adjustmentValue}, calculatedIsa = #{calculatedIsa} where id = #{id}"})
  void update(ProgramProductISA programProductISA);

  @Select("SELECT * FROM program_product_isa WHERE programProductId = #{programProductId}")
  ProgramProductISA getIsa(Long programProductId);
}
