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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
  public void shouldInsertFacilityVisitIfDoesNotExistAlready() {
    FacilityVisit facilityVisit = new FacilityVisit();
    when(facilityVisitRepository.get(facilityVisit)).thenReturn(null);

    boolean syncStatus = facilityVisitService.save(facilityVisit);

    verify(facilityVisitRepository).insert(facilityVisit);
    assertFalse(syncStatus);
  }

  @Test
  public void shouldReturnAlreadySyncedStatusIfFacilityVisitAlreadySynced() {
    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisit.setSynced(true);
    when(facilityVisitRepository.get(facilityVisit)).thenReturn(facilityVisit);

    boolean syncStatus = facilityVisitService.save(facilityVisit);

    assertFalse(syncStatus);
  }

  @Test
  public void shouldReturnTrueStatusIfFacilityVisitIsToBeSynced() throws Exception {
    FacilityVisit facilityVisit = new FacilityVisit();
    when(facilityVisitRepository.get(facilityVisit)).thenReturn(facilityVisit);

    boolean syncStatus = facilityVisitService.save(facilityVisit);

    assertThat(facilityVisit.getSynced(), is(true));
    verify(facilityVisitRepository).update(facilityVisit);
    assertTrue(syncStatus);
  }
}
