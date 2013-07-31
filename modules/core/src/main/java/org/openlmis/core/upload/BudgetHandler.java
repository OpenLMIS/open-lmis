package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Budget;
import org.openlmis.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
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
      budget.getPeriod().setId(processingPeriodService.getIdByName(budget.getPeriod().getName()));
  }

  private void validateBudget(Budget budget){
     facilityService.is
  }

  @Override
  protected String getDuplicateMessageKey() {
    return "error.duplicate.Budget";
  }
}
