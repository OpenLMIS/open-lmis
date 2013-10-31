package org.openlmis.distribution.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.FacilityVisitRepository;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)

@RunWith(MockitoJUnitRunner.class)
public class FacilityVisitServiceTest {

  @Mock
  FacilityVisitRepository facilityVisitRepository;

  @InjectMocks
  FacilityVisitService facilityVisitService;

  @Rule
  public ExpectedException expectedException = none();

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
  public void shouldReturnFailedStatusIfInsertFails() throws Exception{
    FacilityVisit facilityVisit = new FacilityVisit();

    doThrow(Exception.class).when(facilityVisitRepository).insert(facilityVisit);

    expectedException.expect(Exception.class);
    facilityVisitService.save(facilityVisit);
  }

}
