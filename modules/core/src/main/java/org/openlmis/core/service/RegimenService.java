package org.openlmis.core.service;

import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.repository.RegimenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegimenService {

  @Autowired
  RegimenRepository repository;

  public void save(List<Regimen> regimens) {
    for (Regimen regimen : regimens) {
      if (regimen.getId() != null) {
        repository.update(regimen);
      } else {
        repository.insert(regimen);
      }
    }
  }


  public List<Regimen> getByProgram(Long programId) {
    return repository.getByProgram(programId);
  }

  public List<RegimenCategory> getAllRegimenCategories() {
    return repository.getAllRegimenCategories();
  }

}
