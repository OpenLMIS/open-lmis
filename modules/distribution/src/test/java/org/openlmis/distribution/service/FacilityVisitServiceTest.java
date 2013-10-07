package org.openlmis.distribution.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.FacilityVisitRepository;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)

@RunWith(MockitoJUnitRunner.class)
public class FacilityVisitServiceTest {

  @Mock
  FacilityVisitRepository facilityVisitRepository;

  @InjectMocks
  FacilityVisitService facilityVisitService;

  @Test
  public void shouldInsertFacilityVisit() {
    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisitService.save(facilityVisit);

    verify(facilityVisitRepository).insert(facilityVisit);
  }
}
