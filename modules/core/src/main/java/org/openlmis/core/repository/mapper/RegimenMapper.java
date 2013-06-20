package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.Regimen;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegimenMapper {


  @Insert({"INSERT INTO regimens (code, name, active, programId, categoryId, displayOrder, createdBy) ",
    "VALUES (#{code}, #{name}, #{active}, #{programId}, #{category.id}, #{displayOrder}, #{createdBy})"})
  @Options(useGeneratedKeys = true)
  public void insert(Regimen regimen);


  @Select({"SELECT * FROM regimens r INNER JOIN regimen_categories rc ON r.categoryid = rc.id ",
    "WHERE r.programId=#{programId} ORDER BY rc.displayOrder,r.displayOrder"})
  List<Regimen> getByProgram(Long programId);

  @Update("UPDATE regimens set code=#{code}, name=#{name}, active=#{active}, displayOrder = #{displayOrder}")
  void update(Regimen regimen);
}
