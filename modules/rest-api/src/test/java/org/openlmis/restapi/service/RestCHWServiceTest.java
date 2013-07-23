package org.openlmis.restapi.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.builder.CHWBuilder;
import org.openlmis.restapi.domain.CHW;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.restapi.builder.CHWBuilder.defaultCHW;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@Category(UnitTests.class)

@PrepareForTest(RestCHWService.class)
public class RestCHWServiceTest {

  @Mock
  FacilityService facilityService;

  @InjectMocks
  RestCHWService restCHWService;
  @Test
  public void shouldCreateFacilityForCHW() throws Exception {
    CHW chw = make(a(defaultCHW));

    Facility baseFacility = getBaseFacility(chw);

    Facility facility = mock(Facility.class);
    when(facilityService.getByCode(facility)).thenReturn(baseFacility);
    whenNew(Facility.class).withNoArguments().thenReturn(facility);

    restCHWService.create(chw);

    verify(facility).setCode(chw.getAgentCode());
    verify(facility).setParentFacilityId(baseFacility.getId());
    verify(facility).setName(chw.getAgentName());
    verify(facility).setFacilityType(baseFacility.getFacilityType());
    verify(facility).setMainPhone(chw.getPhoneNumber());
    verify(facility).setGeographicZone(baseFacility.getGeographicZone());
    verify(facility).setActive(chw.getActive());
    verify(facility).setVirtualFacility(true);
    verify(facility).setSdp(true);
    verify(facility).setDataReportable(true);
    verify(facilityService).save(facility);

  }

  private Facility getBaseFacility(CHW chw) {
    Facility baseFacility = new Facility(1l);
    baseFacility.setCode(chw.getBaseFacilityCode());
    baseFacility.setFacilityType(new FacilityType());
    baseFacility.setGeographicZone(new GeographicZone());
    return baseFacility;
  }
}
