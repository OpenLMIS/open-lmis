package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.vaccine.domain.Vitamin;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VitaminMapper {

  @Select("select * from vaccine_vitamins")
  List<Vitamin> getAll();
}
