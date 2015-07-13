package org.openlmis.vaccine.repository.demographics;

import org.openlmis.vaccine.domain.demographics.DemographicEstimateCategory;
import org.openlmis.vaccine.repository.mapper.demographics.DemographicEstimateCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DemographicEstimateCategoryRepository {

  @Autowired
  private DemographicEstimateCategoryMapper mapper;

  public List<DemographicEstimateCategory> getAll(){
    return mapper.getAll();
  }

  public DemographicEstimateCategory getById(Long id){
    return mapper.getById(id);
  }

  public void insert(DemographicEstimateCategory category){
    mapper.insert(category);
  }

  public void update(DemographicEstimateCategory category){
    mapper.update(category);
  }
}
