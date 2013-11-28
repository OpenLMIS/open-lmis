/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.core.upload;

import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Budget;
import org.openlmis.core.service.*;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@EqualsAndHashCode(callSuper=false)
public class BudgetHandler extends AbstractModelPersistenceHandler {

  @Autowired
  private BudgetService service;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProgramService programService;

  @Autowired
  private ProcessingPeriodService processingPeriodService;

  @Override
  protected BaseModel getExisting(BaseModel record) {
    Budget budget = ((Budget) record);
    return service.getByCodes(budget.getProgram().getCode(), budget.getPeriod().getName(), budget.getFacility().getCode() );
  }

  @Override
  protected void save(BaseModel record) {
    fillReferenceData((Budget) record);
    validateBudget((Budget) record);
    service.save((Budget) record);
  }

  private void fillReferenceData(Budget budget){
      // set the id's of all reference data
      // this is to make sure that the save routine will be able to save this budget information
      budget.setFacility( facilityService.getByCode(budget.getFacility() ) );
      budget.setProgram( programService.getByCode(budget.getProgram().getCode()));
      //budget.getPeriod().setId(processingPeriodService.getIdByName(budget.getPeriod().getName()));
  }

  private void validateBudget(Budget budget){

  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.Budget";
  }

  @Override
  public void postProcess(AuditFields auditFields) {
  }

}
