package org.openlmis.core.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityFtpDetailsRepository;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityFtpDetailsServiceTest {

  @Mock
  FacilityFtpDetailsRepository repository;

  @Mock
  FacilityService facilityService;

  @InjectMocks
  FacilityFtpDetailsService service;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldUpdateIfExisting() throws Exception {
    FacilityFtpDetails facilityFtpDetails = new FacilityFtpDetails();
    facilityFtpDetails.setId(1L);
    facilityFtpDetails.setFacilityCode("F10");

    Facility facility = new Facility();
    facility.setCode("F10");
    when(facilityService.getByCode(facility)).thenReturn(facility);

    service.save(facilityFtpDetails);

    verify(repository).update(facilityFtpDetails);
  }

  @Test
  public void shouldInsertIfNotExisting() throws Exception {
    FacilityFtpDetails facilityFtpDetails = new FacilityFtpDetails();
    facilityFtpDetails.setFacilityCode("F10");

    Facility facility = new Facility();
    facility.setCode("F10");
    when(facilityService.getByCode(facility)).thenReturn(facility);

    service.save(facilityFtpDetails);

    verify(repository).insert(facilityFtpDetails);
  }

  @Test
  public void shouldThrowExceptionIfFacilityDoesNotExist() throws Exception {

    FacilityFtpDetails facilityFtpDetails = new FacilityFtpDetails();
    facilityFtpDetails.setFacilityCode("F10");

    Facility facility = new Facility();
    facility.setCode("F10");
    when(facilityService.getByCode(facility)).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.facility.code.invalid");

    service.save(facilityFtpDetails);

    verify(service, never()).insert(facilityFtpDetails);
    verify(service, never()).update(facilityFtpDetails);
  }

  @Test
  public void shouldUpdateFacilityFtpDetails() throws Exception {
    FacilityFtpDetails facilityFtpDetails = mock(FacilityFtpDetails.class);
    service.update(facilityFtpDetails);
    verify(repository).update(facilityFtpDetails);
  }

  @Test
  public void shouldInsertFacilityFtpDetails() throws Exception {
    FacilityFtpDetails facilityFtpDetails = mock(FacilityFtpDetails.class);
    service.insert(facilityFtpDetails);
    verify(repository).insert(facilityFtpDetails);
  }

  @Test
  public void shouldGetFacilityFtpDetailsByFacilityCode() throws Exception {
    FacilityFtpDetails facilityFtpDetails = mock(FacilityFtpDetails.class);
    String facilityCode = "F10";
    when(repository.getByFacilityCode(facilityCode)).thenReturn(facilityFtpDetails);
    FacilityFtpDetails result = service.getByFacilityCode(facilityCode);
    assertThat(result, is(facilityFtpDetails));
    verify(repository).getByFacilityCode(facilityCode);
  }


}
