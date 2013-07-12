package org.openlmis.rnr.repository.mapper;


import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegimenLineItemMapper {

  @Insert({"INSERT INTO regimen_line_items(code, name, regimenDisplayOrder, regimenCategory, regimenCategoryDisplayOrder, rnrId) values " +
    "(#{code}, #{name}, #{regimenDisplayOrder}, #{category.name}, #{category.displayOrder}, #{rnrId})"})
  @Options(useGeneratedKeys = true)
  public void insert(RegimenLineItem regimenLineItem);

  @Select("SELECT * FROM regimen_line_items WHERE rnrId = #{rnrId} ORDER BY regimenDisplayOrder")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "code", column = "code"),
    @Result(property = "name", column = "name"),
    @Result(property = "patientsOnTreatment", column = "patientsOnTreatment"),
    @Result(property = "patientsToInitiateTreatment", column = "patientsToInitiateTreatment"),
    @Result(property = "remarks", column = "remarks"),
    @Result(property = "patientsStoppedTreatment", column = "patientsStoppedTreatment"),
    @Result(property = "regimenDisplayOrder", column = "regimenDisplayOrder"),
    @Result(property = "category.name", column = "regimenCategory"),
    @Result(property = "category.displayOrder", column = "regimenCategoryDisplayOrder"),
  })
  public List<RegimenLineItem> getRegimenLineItemsByRnrId(Long rnrId);

  @Update("UPDATE regimen_line_items set patientsOnTreatment = #{patientsOnTreatment},patientsToInitiateTreatment = #{patientsToInitiateTreatment}," +
    "patientsStoppedTreatment = #{patientsStoppedTreatment},remarks = #{remarks},modifiedBy = #{modifiedBy}, modifiedDate = COALESCE(#{modifiedDate},NOW()) where id=#{id}")
  void update(RegimenLineItem regimenLineItem);
}
