package org.openlmis.distribution.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.FacilityVisitRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityVisitServiceTest {

  @Mock
  FacilityVisitRepository facilityVisitRepository;

  @InjectMocks
  FacilityVisitService facilityVisitService;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldSaveFacilityVisitWithVisitDetails() {
    FacilityVisit facilityVisit = spy(new FacilityVisit());

    doNothing().when(facilityVisit).setApplicableVisitInfo();
    when(facilityVisitRepository.save(facilityVisit)).thenReturn(facilityVisit);

    FacilityVisit savedVisit = facilityVisitService.save(facilityVisit);

    assertThat(savedVisit, is(facilityVisit));
    verify(facilityVisitRepository).save(facilityVisit);
    verify(facilityVisit).setApplicableVisitInfo();
  }

  @Test
  public void shouldSyncFacilityVisitIfNotAlreadySynced() throws Exception {
    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisit.setId(4L);
    FacilityVisit existingFacilityVisit = new FacilityVisit();
    existingFacilityVisit.setSynced(false);

    when(facilityVisitRepository.getById(4L)).thenReturn(existingFacilityVisit);

    FacilityVisit syncedVisit = facilityVisitService.setSynced(facilityVisit);

    verify(facilityVisitRepository).update(facilityVisit);
    assertTrue(syncedVisit.getSynced());
    assertTrue(facilityVisit.getSynced());
  }

  @Test
  public void shouldGiveErrorIfFacilityAlreadySynced() throws Exception {
    FacilityVisit alreadySyncedVisit = new FacilityVisit();
    alreadySyncedVisit.setId(6L);
    alreadySyncedVisit.setSynced(true);

    FacilityVisit facilityVisit = new FacilityVisit();
    facilityVisit.setId(6L);

    when(facilityVisitRepository.getById(6L)).thenReturn(alreadySyncedVisit);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.facility.already.synced");

    facilityVisitService.setSynced(facilityVisit);
  }
}
