package org.openlmis.distribution.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.distribution.domain.GeneralObservation;
import org.openlmis.distribution.repository.GeneralObservationRepository;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GeneralObservationServiceTest {

  @Mock
  GeneralObservationRepository generalObservationRepository;

  @InjectMocks
  GeneralObservationService generalObservationService;

  @Test
  public void shouldInsertGeneralObservation() {
    GeneralObservation generalObservation = new GeneralObservation();
    generalObservationService.save(generalObservation);

    verify(generalObservationRepository).insert(generalObservation);
  }
}
