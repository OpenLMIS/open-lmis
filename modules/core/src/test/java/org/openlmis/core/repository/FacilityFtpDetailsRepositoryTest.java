package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.repository.mapper.FacilityFtpDetailsMapper;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityFtpDetailsRepositoryTest {

  @Mock
  FacilityFtpDetailsMapper mapper;

  @InjectMocks
  FacilityFtpDetailsRepository repository;

  @Test
  public void shouldUpdateFacilityFtpDetails() throws Exception {
    FacilityFtpDetails facilityFtpDetails = mock(FacilityFtpDetails.class);
    repository.update(facilityFtpDetails);
    verify(mapper).update(facilityFtpDetails);
  }

  @Test
  public void shouldInsertFacilityFtpDetails() throws Exception {
    FacilityFtpDetails facilityFtpDetails = mock(FacilityFtpDetails.class);
    repository.insert(facilityFtpDetails);
    verify(mapper).insert(facilityFtpDetails);
  }

  @Test
  public void shouldGetFacilityFtpDetailsByFacilityCode() throws Exception {
    FacilityFtpDetails facilityFtpDetails = mock(FacilityFtpDetails.class);
    String facilityCode = "F10";
    when(mapper.getByFacilityCode(facilityCode)).thenReturn(facilityFtpDetails);
    FacilityFtpDetails result = repository.getByFacilityCode(facilityCode);
    assertThat(result, is(facilityFtpDetails));
    verify(mapper).getByFacilityCode(facilityCode);
  }



}
