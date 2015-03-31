package org.openlmis.vaccine.service;

import org.openlmis.vaccine.domain.DiscardingReason;
import org.openlmis.vaccine.repository.DiscardingReasonsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscardingReasonsService {

  @Autowired
  private DiscardingReasonsRepository repository;

  public List<DiscardingReason> getAllReasons(){
    return repository.getAllReasons();
  }
}
