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


  public List<RegimenColumn> getRegimenColumnsByProgramId(Long programId) {
    List<RegimenColumn> regimenColumns = repository.getRegimenColumnsByProgramId(programId);
    if (regimenColumns.isEmpty()) {
      populateDefaultRegimenColumns(programId, regimenColumns);
    }
    return regimenColumns;
  }

  private void populateDefaultRegimenColumns(Long programId, List<RegimenColumn> regimenColumns) {
    repository.insert(new RegimenColumn(programId, "onTreatment", "Number of patients on treatment", "Numeric", true));
    repository.insert(new RegimenColumn(programId, "'initiatedTreatment'", "Number of patients to be initiated treatment", "Numeric", true));
    repository.insert(new RegimenColumn(programId, "'stoppedTreatment'", "Number of patients stopped treatment", "Numeric", true));
    repository.insert(new RegimenColumn(programId, "'remarks'", "Remarks", "Text", true));
    regimenColumns.addAll(repository.getRegimenColumnsByProgramId(programId));
  }

}
