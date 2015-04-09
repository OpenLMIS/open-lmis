package org.openlmis.vaccine.repository;

import org.openlmis.vaccine.domain.VitaminSupplementationAgeGroup;
import org.openlmis.vaccine.repository.mapper.VitaminSupplementationAgeGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VitaminSupplementationAgeGroupRepository {
  @Autowired
  VitaminSupplementationAgeGroupMapper mapper;

  public List<VitaminSupplementationAgeGroup> getAll(){
    return mapper.getAll();
  }
}
