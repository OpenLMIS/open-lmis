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

  public static final String ON_TREATMENT = "onTreatment";
  public static final String INITIATED_TREATMENT = "initiatedTreatment";
  public static final String STOPPED_TREATMENT = "stoppedTreatment";
  public static final String REMARKS = "remarks";

  public static final String NUMBER_OF_PATIENTS_ON_TREATMENT = "regimen.reporting.patients.on.treatment";
  public static final String NUMBER_OF_PATIENTS_TO_BE_INITIATED_TREATMENT = "regimen.reporting.patients.initiated.treatment";
  public static final String NUMBER_OF_PATIENTS_STOPPED_TREATMENT = "regimen.reporting.patients.stopped.treatment";
  public static final String REMARKS_LABEL = "regimen.reporting.patients.remarks";
  public static final String TYPE_NUMERIC = "regimen.reporting.dataType.numeric";
  public static final String TYPE_TEXT = "regimen.reporting.dataType.text";

  @Autowired
  RegimenColumnRepository repository;

  @Autowired
  MessageService messageService;

  public void save(List<RegimenColumn> regimenColumns, Long userId) {
    for(RegimenColumn regimenColumn : regimenColumns) {
      save(regimenColumn, userId);
    }
  }

  public void save(RegimenColumn regimenColumn, Long userId) {
    if (regimenColumn.getId() == null) {
      regimenColumn.setCreatedBy(userId);
      repository.insert(regimenColumn);
      return;
    }
    regimenColumn.setModifiedBy(userId);
    repository.update(regimenColumn);
  }


  public List<RegimenColumn> getRegimenColumnsByProgramId(Long programId, Long userId) {
    List<RegimenColumn> regimenColumns = repository.getRegimenColumnsByProgramId(programId);
    if (regimenColumns.isEmpty()) {
      populateDefaultRegimenColumns(programId, regimenColumns, userId);
    }
    return regimenColumns;
  }

  private void populateDefaultRegimenColumns(Long programId, List<RegimenColumn> regimenColumns, Long userId) {
    repository.insert(new RegimenColumn(programId, ON_TREATMENT, messageService.message(NUMBER_OF_PATIENTS_ON_TREATMENT), messageService.message(TYPE_NUMERIC), true, userId));
    repository.insert(new RegimenColumn(programId, INITIATED_TREATMENT, messageService.message(NUMBER_OF_PATIENTS_TO_BE_INITIATED_TREATMENT), messageService.message(TYPE_NUMERIC), true, userId));
    repository.insert(new RegimenColumn(programId, STOPPED_TREATMENT, messageService.message(NUMBER_OF_PATIENTS_STOPPED_TREATMENT), messageService.message(TYPE_NUMERIC), true, userId));
    repository.insert(new RegimenColumn(programId, REMARKS, messageService.message(REMARKS_LABEL), messageService.message(TYPE_TEXT), true, userId));
    regimenColumns.addAll(repository.getRegimenColumnsByProgramId(programId));
  }

}
