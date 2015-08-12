package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.dto.StockAdjustmentReason;
import org.openlmis.core.dto.StockAdjustmentReasonProgram;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockAdjustmentReasonMapper {

  @Select("SELECT *" +
      " FROM losses_adjustments_types")
  List<StockAdjustmentReason> getAll();

  @Select("SELECT *" +
      " FROM losses_adjustments_types" +
      " WHERE additive = #{additive}")
  List<StockAdjustmentReason> getAllByAdditive(@Param("additive") Boolean additive);

  @Select("SELECT *" +
      " FROM losses_adjustments_types" +
      " WHERE name = #{name}")
  StockAdjustmentReason getByName(@Param("name") String name);

  @Insert({"INSERT INTO losses_adjustments_types (name, description, additive, displayOrder)",
      " VALUES (#{name}, #{description}, #{additive}, #{displayOrder})"})
  void insert(StockAdjustmentReason reason);

  @Update({"UPDATE losses_adjustments_types" +
      " SET description = #{description}, additive = #{additive}, displayOrder = #{displayOrder}",
      " WHERE name = #{name}"})
  void update(StockAdjustmentReason reason);

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
