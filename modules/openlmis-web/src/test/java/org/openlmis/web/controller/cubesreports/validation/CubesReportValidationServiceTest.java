package org.openlmis.web.controller.cubesreports.validation;

import org.apache.ibatis.session.RowBounds;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.moz.MozFacilityTypes;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.report.model.dto.GeographicZone;
import org.openlmis.report.service.lookup.ProfileBaseLookupService;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.moz.MozFacilityTypes.*;

@RunWith(PowerMockRunner.class)
public class CubesReportValidationServiceTest {

    @Mock
    private ProfileBaseLookupService profileBaseLookupService;
    @Mock
    private GeographicZoneRepository geographicZoneRepository;

    @InjectMocks
    private CubesReportValidationService cubesReportValidationService;

    @Before
    public void setUp() throws Exception {
        when(geographicZoneRepository.getByCode(anyString())).thenReturn(new org.openlmis.core.domain.GeographicZone());
    }

    @Test
    public void shouldNotValidateNationalUserAccess() throws Exception {
        when(profileBaseLookupService.getCurrentUserFacility()).thenReturn(createFacilityWithType(Central));

        boolean isValid = cubesReportValidationService.validate("/cube/vw_period_movements/aggregate", "whatever").isValid();

        assertTrue(isValid);
    }

    @Test
    public void shouldValidateProvinceAccessOfDPMUser() throws Exception {
        when(profileBaseLookupService.getCurrentUserFacility()).thenReturn(createFacilityWithType(DPM));

        when(profileBaseLookupService.getAllZones()).thenReturn(createGeoZones("MAPUTO_PROVINCIA"));
        boolean isValid = cubesReportValidationService.validate("/cube/vw_period_movements/aggregate", "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA").isValid();
        assertTrue(isValid);

        when(profileBaseLookupService.getAllZones()).thenReturn(createGeoZones("xxx"));
        isValid = cubesReportValidationService.validate("/cube/vw_period_movements/aggregate", "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA").isValid();
        assertFalse(isValid);
    }

    @Test
    public void shouldValidateDistrictAccessOfDDMUser() throws Exception {
        when(profileBaseLookupService.getCurrentUserFacility()).thenReturn(createFacilityWithType(DDM));

        when(profileBaseLookupService.getAllZones()).thenReturn(createGeoZones("MATOLA"));
        boolean isValid = cubesReportValidationService.validate("/cube/vw_period_movements/aggregate", "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA").isValid();
        assertTrue(isValid);

        when(profileBaseLookupService.getAllZones()).thenReturn(createGeoZones("xxx"));
        isValid = cubesReportValidationService.validate("/cube/vw_period_movements/aggregate", "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA").isValid();
        assertFalse(isValid);
    }

    @Test
    public void shouldValidateFacilityAccessOfFacilityUser() throws Exception {
        when(profileBaseLookupService.getCurrentUserFacility()).thenReturn(createFacilityWithType(CSRUR_I));

        when(profileBaseLookupService.getAllFacilities(any(RowBounds.class))).thenReturn(createFacilities("HF8"));
        boolean isValid = cubesReportValidationService.validate("/cube/vw_period_movements/aggregate", "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA").isValid();
        assertTrue(isValid);

        when(profileBaseLookupService.getAllFacilities(any(RowBounds.class))).thenReturn(createFacilities("xxx"));
        isValid = cubesReportValidationService.validate("/cube/vw_period_movements/aggregate", "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA").isValid();
        assertFalse(isValid);
    }

    @Test
    public void shouldExcludeCertainCubesAccessFromValidation() throws Exception {
        when(profileBaseLookupService.getCurrentUserFacility()).thenReturn(createFacilityWithType(CSRUR_I));

        assertTrue(cubesReportValidationService.validate("/cube/products/facts", "whatever").isValid());
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