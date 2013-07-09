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

  public List<Regimen> getByProgram(Long programId) {
    return mapper.getByProgram(programId);
  }

  public List<RegimenCategory> getAllRegimenCategories() {
    return regimenCategoryMapper.getAll();
  }

  public void save(List<Regimen> regimens, Long userId) {
    for (Regimen regimen : regimens) {
      regimen.setModifiedBy(userId);
      if (regimen.getId() == null) {
        regimen.setCreatedBy(userId);
        mapper.insert(regimen);
      }
      mapper.update(regimen);
    }
  }

}
