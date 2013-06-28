package org.openlmis.core.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RegimenColumn;
import org.openlmis.core.repository.RegimenColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class RegimenColumnService {

  @Autowired
  RegimenColumnRepository repository;

  public void save (List<RegimenColumn> regimenColumns) {
    for(RegimenColumn regimenColumn : regimenColumns) {
      save(regimenColumn);
    }
  }

  public void save (RegimenColumn regimenColumn) {
    if (regimenColumn.getId() == null) {
      repository.insert(regimenColumn);
      return;
    }
    repository.update(regimenColumn);
  }

  public List<RegimenColumn> getAllRegimenColumnsByProgramId(Long programId) {
    return repository.getAllRegimenColumnsByProgramId(programId);
  }

}
