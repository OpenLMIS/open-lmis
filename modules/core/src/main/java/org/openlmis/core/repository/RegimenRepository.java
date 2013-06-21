package org.openlmis.core.repository;

import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.repository.mapper.RegimenCategoryMapper;
import org.openlmis.core.repository.mapper.RegimenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RegimenRepository {

  @Autowired
  RegimenMapper mapper;

  @Autowired
  RegimenCategoryMapper regimenCategoryMapper;

  public void insert(Regimen regimen) {
    mapper.insert(regimen);
  }

  public void update(Regimen regimen) {
    mapper.update(regimen);
  }

  public List<Regimen> getByProgram(Long programId) {
    return mapper.getByProgram(programId);
  }

  public List<RegimenCategory> getAllRegimenCategories() {
    return regimenCategoryMapper.getAll();
  }
}
