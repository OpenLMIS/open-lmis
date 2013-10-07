package org.openlmis.distribution.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.GeneralObservation;
import org.openlmis.distribution.repository.mapper.GeneralObservationMapper;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class GeneralObservationRepositoryTest {

  @Mock
  GeneralObservationMapper generalObservationMapper;

  @InjectMocks
  GeneralObservationRepository generalObservationRepository;

  @Test
  public void shouldInsertGeneralObservation() {
    GeneralObservation generalObservation = new GeneralObservation();
    generalObservationRepository.insert(generalObservation);

    verify(generalObservationMapper).insert(generalObservation);
  }


}
