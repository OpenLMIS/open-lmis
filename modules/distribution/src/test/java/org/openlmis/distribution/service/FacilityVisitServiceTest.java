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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    String syncStatus = facilityVisitService.save(facilityVisit);

    verify(facilityVisitRepository).insert(facilityVisit);
    assertThat(syncStatus, is("Synced"));

  }

  @Test
  public void shouldReturnAlreadySyncedStatusIfFacilityVisitAlreadyExists(){
    FacilityVisit facilityVisit = new FacilityVisit();
    when(facilityVisitRepository.get(facilityVisit)).thenReturn(facilityVisit);
    String syncStatus = facilityVisitService.save(facilityVisit);

    assertThat(syncStatus, is("AlreadySynced"));

  }
  @Test
  public void shouldReturnFailedStatusIfInsertFails(){
    FacilityVisit facilityVisit = new FacilityVisit();

    doThrow(Exception.class).when(facilityVisitRepository).insert(facilityVisit);

    String syncStatus = facilityVisitService.save(facilityVisit);

    assertThat(syncStatus, is("Failed"));

  }

}
