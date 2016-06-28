package org.openlmis.web.controller.cubesreports.validation;

import org.apache.ibatis.session.RowBounds;
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
import static org.mockito.Matchers.any;
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
    public void shouldValidateFacilityAccessOfFacilityUser() throws Exception {
        when(profileBaseLookupService.getCurrentUserFacility()).thenReturn(createFacilityWithType(CSRUR_I));

        when(profileBaseLookupService.getAllFacilities(any(RowBounds.class))).thenReturn(createFacilities("HF8"));
        boolean isValid = cubesReportValidationService.isQueryValid("/cube/vw_period_movements/aggregate", "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA");
        assertTrue(isValid);

        when(profileBaseLookupService.getAllFacilities(any(RowBounds.class))).thenReturn(createFacilities("xxx"));
        isValid = cubesReportValidationService.isQueryValid("/cube/vw_period_movements/aggregate", "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA");
        assertFalse(isValid);
    }

    @Test
    public void shouldExcludeCertainCubesAccessFromValidation() throws Exception {
        when(profileBaseLookupService.getCurrentUserFacility()).thenReturn(createFacilityWithType(CSRUR_I));

        assertTrue(cubesReportValidationService.isQueryValid("/cube/products/facts", "whatever"));
        assertTrue(cubesReportValidationService.isQueryValid("/cube/vw_carry_start_dates/facts", "whatever"));
    }

    private ArrayList<org.openlmis.report.model.dto.Facility> createFacilities(String aaa) {
        ArrayList<org.openlmis.report.model.dto.Facility> facilities = new ArrayList<>();
        org.openlmis.report.model.dto.Facility facility = new org.openlmis.report.model.dto.Facility();
        facility.setCode(aaa);
        facilities.add(facility);
        return facilities;
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