package org.openlmis.distribution.service;

import org.openlmis.distribution.domain.GeneralObservation;
import org.openlmis.distribution.repository.GeneralObservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GeneralObservationService {

  @Autowired
  GeneralObservationRepository repository;

  public void save(GeneralObservation generalObservation) {
    repository.insert(generalObservation);
  }
}
