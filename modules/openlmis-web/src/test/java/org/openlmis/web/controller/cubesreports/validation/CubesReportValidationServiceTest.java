package org.openlmis.web.controller.cubesreports.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.moz.MozFacilityTypes;
import org.openlmis.report.model.dto.GeographicZone;
import org.openlmis.report.service.lookup.ProfileBaseLookupService;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.moz.MozFacilityTypes.*;

@RunWith(PowerMockRunner.class)
public class CubesReportValidationServiceTest {

    @Mock
    private ProfileBaseLookupService profileBaseLookupService;

    @InjectMocks
    private CubesReportValidationService cubesReportValidationService;

    @Test
    public void shouldNotValidateNationalUserAccess() throws Exception {
        when(profileBaseLookupService.getCurrentUserFacility()).thenReturn(createFacilityWithType(DNM));

        boolean isValid = cubesReportValidationService.isQueryValid("/cube/vw_period_movements/aggregate", "whatever");

        assertTrue(isValid);
    }

    @Test
    public void shouldValidateProvinceAccessOfDPMUser() throws Exception {
        when(profileBaseLookupService.getCurrentUserFacility()).thenReturn(createFacilityWithType(DPM));

        when(profileBaseLookupService.getAllZones()).thenReturn(createGeoZones("MAPUTO_PROVINCIA"));
        boolean isValid = cubesReportValidationService.isQueryValid("/cube/vw_period_movements/aggregate", "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA");
        assertTrue(isValid);

        when(profileBaseLookupService.getAllZones()).thenReturn(createGeoZones("xxx"));
        isValid = cubesReportValidationService.isQueryValid("/cube/vw_period_movements/aggregate", "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA");
        assertFalse(isValid);
    }

    @Test
    public void shouldValidateDistrictAccessOfDDMUser() throws Exception {
        when(profileBaseLookupService.getCurrentUserFacility()).thenReturn(createFacilityWithType(DDM));

        when(profileBaseLookupService.getAllZones()).thenReturn(createGeoZones("MATOLA"));
        boolean isValid = cubesReportValidationService.isQueryValid("/cube/vw_period_movements/aggregate", "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA");
        assertTrue(isValid);

        when(profileBaseLookupService.getAllZones()).thenReturn(createGeoZones("xxx"));
        isValid = cubesReportValidationService.isQueryValid("/cube/vw_period_movements/aggregate", "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA");
        assertFalse(isValid);
    }

    @Test
    public void shouldValidateFacilityAcessOfFacilityUser() throws Exception {

    }

    private Facility createFacilityWithType(MozFacilityTypes facilityTypes) {
        FacilityType facilityType = new FacilityType();
        facilityType.setCode(facilityTypes.toString());

        Facility facility = new Facility();
        facility.setFacilityType(facilityType);

        return facility;
    }

    private ArrayList<GeographicZone> createGeoZones(String code) {
        GeographicZone geographicZone = new GeographicZone();
        geographicZone.setCode(code);

        ArrayList<GeographicZone> zones = new ArrayList<>();
        zones.add(geographicZone);
        return zones;
    }
}