package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.domain.StockAdjustmentReasonProgram;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockAdjustmentReasonMapper {

  @Select("SELECT *" +
      " FROM losses_adjustments_types")
  List<StockAdjustmentReason> getAll();

  @Select("SELECT *" +
      " FROM losses_adjustments_types" +
      " WHERE isdefault = TRUE")
  List<StockAdjustmentReason> getAllDefault();

  @Select("SELECT lat.*" +
      " FROM losses_adjustments_types lat" +
      "   JOIN stock_adjustment_reasons_programs sarp ON sarp.reasonName = lat.name" +
      "   JOIN programs p ON p.code = sarp.programCode" +
      " WHERE p.id = #{programId}")
  List<StockAdjustmentReason> getAllByProgram(@Param("programId") Long programId);

  @Select("SELECT *" +
      " FROM losses_adjustments_types" +
      " WHERE name = #{name}")
  StockAdjustmentReason getByName(@Param("name") String name);

  @Insert({"INSERT INTO losses_adjustments_types (name" +
    ", description" +
    ", additive" +
    ", displayOrder" +
    ", isdefault" +
    ", category" +
    ") VALUES (#{name}" +
    ", #{description}" +
    ", #{additive}" +
    ", #{displayOrder}" +
    ", #{isDefault}" +
    ", #{category})"})
  void insert(StockAdjustmentReason reason);

  @Select("SELECT *" +
      " FROM stock_adjustment_reasons_programs" +
      " WHERE programCode = #{programCode} AND reasonName = #{reasonName}")
  StockAdjustmentReasonProgram getAdjustmentReasonProgram(@Param("programCode") String programCode,
                                                          @Param("reasonName") String reasonName);

  @Insert({"INSERT INTO stock_adjustment_reasons_programs (programCode, reasonName)",
      " VALUES (#{program.code}, #{reason.name})"})
  @Options(useGeneratedKeys = true)
  void insertAdjustmentReasonProgram(StockAdjustmentReasonProgram entry);

  @Update({"UPDATE stock_adjustment_reasons_programs" +
      " SET programCode = #{program.code}, reasonName = #{reason.name}",
      " WHERE id = #{id}"})
  void updateAdjustmentReasonProgram(StockAdjustmentReasonProgram entry);
}
