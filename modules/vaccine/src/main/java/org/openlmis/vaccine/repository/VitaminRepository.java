package org.openlmis.vaccine.repository;

import org.openlmis.vaccine.domain.Vitamin;
import org.openlmis.vaccine.repository.mapper.VitaminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VitaminRepository {

  @Autowired
  VitaminMapper mapper;

  public List<Vitamin> getAll(){
    return mapper.getAll();
  }
}
