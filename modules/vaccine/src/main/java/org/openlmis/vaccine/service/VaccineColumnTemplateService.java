/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.vaccine.service;

import org.openlmis.vaccine.domain.reports.LogisticsColumn;
import org.openlmis.vaccine.dto.ProgramColumnTemplateDTO;
import org.openlmis.vaccine.repository.VaccineColumnTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VaccineColumnTemplateService {

  @Autowired
  private VaccineColumnTemplateRepository repository;

  public ProgramColumnTemplateDTO getTemplate(Long programId) {
    List<LogisticsColumn> columns = repository.getTemplateForProgram(programId);
    if(columns == null || columns.isEmpty()){
      columns = repository.getMasterColumns();
      for(LogisticsColumn column: columns){
        column.setProgramId(programId);
        column.setId(null);
        column.setVisible(true);
      }
    }
    ProgramColumnTemplateDTO dto = new ProgramColumnTemplateDTO();
    dto.setProgramId(programId);
    dto.setColumns(columns);
    return dto;
  }

  public void saveChanges(List<LogisticsColumn> columns){
    for(LogisticsColumn column : columns){
      if(column.getId() == null){
        repository.insertProgramColumn(column);
      }else{
        repository.updateProgramColumn(column);
      }
    }
  }
}
