/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProcessingScheduleRepository is Repository class for ProcessingSchedule related database operations.
 */

@Repository
@NoArgsConstructor
public class ProcessingScheduleRepository {
  private ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  public ProcessingScheduleRepository(ProcessingScheduleMapper processingScheduleMapper) {
    this.processingScheduleMapper = processingScheduleMapper;
  }

  public List<ProcessingSchedule> getAll() {
    return processingScheduleMapper.getAll();
  }

  public void create(ProcessingSchedule processingSchedule) {
    processingSchedule.validate();
    try {
      processingScheduleMapper.insert(processingSchedule);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("error.schedule.code.exist");
    }
  }

  public ProcessingSchedule get(Long id) {
    return processingScheduleMapper.get(id);
  }

  public void update(ProcessingSchedule processingSchedule) {
    processingSchedule.validate();
    try {
      processingScheduleMapper.update(processingSchedule);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("error.schedule.code.exist");
    }
  }

  public ProcessingSchedule getByCode(String code) {
    return processingScheduleMapper.getByCode(code);
  }
}
