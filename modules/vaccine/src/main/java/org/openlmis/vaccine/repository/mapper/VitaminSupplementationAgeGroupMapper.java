package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.vaccine.domain.VitaminSupplementationAgeGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VitaminSupplementationAgeGroupMapper {

  @Select("select * from vaccine_vitamin_supplementation_age_groups")
  List<VitaminSupplementationAgeGroup> getAll();
}
