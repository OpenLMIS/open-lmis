package org.openlmis.distribution.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.mapper.FacilityVisitMapper;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityVisitRepositoryTest {

  @Mock
  FacilityVisitMapper facilityVisitMapper;

  @InjectMocks
  FacilityVisitRepository facilityVisitRepository;

  @Test
  public void shouldInsertFacilityVisit() {
    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisitRepository.insert(facilityVisit);

    verify(facilityVisitMapper).insert(facilityVisit);
  }


}
