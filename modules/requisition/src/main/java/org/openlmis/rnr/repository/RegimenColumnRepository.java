package org.openlmis.rnr.repository;

import org.openlmis.rnr.domain.Column;
import org.openlmis.rnr.domain.RegimenColumn;
import org.openlmis.rnr.domain.RegimenTemplate;
import org.openlmis.rnr.repository.mapper.RegimenColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RegimenColumnRepository {

  @Autowired
  RegimenColumnMapper mapper;

  public List<RegimenColumn> getRegimenColumnsByProgramId(Long programId) {
    return mapper.getAllRegimenColumnsByProgramId(programId);
  }

  public List<RegimenColumn> getMasterRegimenColumnsByProgramId() {
    return mapper.getMasterRegimenColumns();
  }

  public void save(RegimenTemplate regimenTemplate, Long userId) {
    for (Column regimenColumn : regimenTemplate.getColumns()) {
      regimenColumn.setModifiedBy(userId);
      if (regimenColumn.getId() == null) {
        regimenColumn.setCreatedBy(userId);
        mapper.insert((RegimenColumn) regimenColumn, regimenTemplate.getProgramId());
      }
      mapper.update((RegimenColumn) regimenColumn);
    }
  }
}
