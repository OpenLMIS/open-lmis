package org.openlmis.equipment.repository;

import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.repository.mapper.DonorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DonorRepository {

  @Autowired
  private DonorMapper mapper;

  public List<Donor> getAll(){
    return mapper.getAll();
  }

  public void insert(Donor donor){
    mapper.insert(donor);
  }

  public void update(Donor donor){
    mapper.update(donor);
  }
}
