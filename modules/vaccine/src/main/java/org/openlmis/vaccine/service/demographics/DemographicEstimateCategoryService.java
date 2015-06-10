package org.openlmis.vaccine.service.demographics;

import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.openlmis.vaccine.repository.demographics.DemographicEstimateCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.openlmis.vaccine.utils.ListUtil.emptyIfNull;

@Service
public class DemographicEstimateCategoryService {

  @Autowired
  private DemographicEstimateCategoryRepository repository;

  public List<DemographicEstimateCategory> getAll(){
    return repository.getAll();
  }

  public DemographicEstimateCategory getById(Long id){
    return repository.getById(id);
  }

  public void save(List<DemographicEstimateCategory> categories){
    for(DemographicEstimateCategory category : emptyIfNull(categories)){
      if(category.getId() == null){
        repository.insert(category);
      }else{
        repository.update(category);
      }
    }
  }
}
