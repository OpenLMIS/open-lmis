package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.RegimenCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegimenCategoryMapper {

  @Select({"SELECT * FROM regimen_categories ORDER BY displayOrder, name"})
  List<RegimenCategory> getAll();

  @Select({"SELECT * FROM regimen_categories WHERE id = #{id}"})
  RegimenCategory getById(Long id);
}
