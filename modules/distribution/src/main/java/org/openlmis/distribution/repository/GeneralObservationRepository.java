package org.openlmis.distribution.repository;

import org.openlmis.distribution.domain.GeneralObservation;
import org.openlmis.distribution.repository.mapper.GeneralObservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GeneralObservationRepository {

  @Autowired
  GeneralObservationMapper mapper;

  public void insert(GeneralObservation generalObservation) {
    mapper.insert(generalObservation);
  }
}
