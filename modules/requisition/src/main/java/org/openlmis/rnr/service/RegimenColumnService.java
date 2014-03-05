/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.rnr.domain.Column;
import org.openlmis.rnr.domain.RegimenColumn;
import org.openlmis.rnr.domain.RegimenTemplate;
import org.openlmis.rnr.repository.RegimenColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling RegimenColumn entity.
 */

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
    List<? extends Column> regimenColumns = repository.getRegimenColumnsByProgramId(programId);
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
