package org.openlmis.rnr.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RegimenColumn;
import org.openlmis.core.domain.RegimenTemplate;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.rnr.repository.RegimenColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class RegimenColumnService {

  @Autowired
  RegimenColumnRepository repository;

  @Autowired
  MessageService messageService;

  @Autowired
  ProgramService programService;

  public void save(RegimenTemplate regimenTemplate, Long userId) {
    repository.save(regimenTemplate, userId);
    programService.setRegimenTemplateConfigured(regimenTemplate.getProgramId());
  }

  public List<RegimenColumn> getRegimenColumnsByProgramId(Long programId) {
    return repository.getRegimenColumnsByProgramId(programId);
  }

  public RegimenTemplate getRegimenTemplateOrMasterTemplate(Long programId) {
    List<RegimenColumn> regimenColumns = repository.getRegimenColumnsByProgramId(programId);
    if (regimenColumns == null || regimenColumns.size() == 0) {
      regimenColumns = repository.getMasterRegimenColumnsByProgramId();
    }
    return new RegimenTemplate(programId, regimenColumns);
  }

  public RegimenTemplate getRegimenTemplateByProgramId(Long programId) {
    return new RegimenTemplate(programId, repository.getRegimenColumnsByProgramId(programId));
  }

  public List<RegimenColumn> getRegimenColumnsForPrintByProgramId(Long programId) {
    List<RegimenColumn> regimenColumns = repository.getRegimenColumnsByProgramId(programId);
    for (RegimenColumn regimenColumn : regimenColumns) {
      if (regimenColumn.getLabel().equals("header.code") || regimenColumn.getLabel().equals("header.name")) {
        regimenColumn.setLabel(messageService.message(regimenColumn.getLabel()));
      }
    }
    return regimenColumns;
  }
}
