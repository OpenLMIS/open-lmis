package org.openlmis.web.controller.cubesreports.validation;

import org.junit.Test;
import org.openlmis.core.domain.moz.MozFacilityTypes;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.*;
import static org.openlmis.core.domain.moz.MozFacilityTypes.*;

public class CubesAccessInfoTest {
    @Test
    public void shouldParseProvinceDistrictFacilityFromQueryString() throws Exception {
        CubesAccessInfo cubesAccessInfo = CubesAccessInfo.createInstance(CSRUR_I, "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA");

        assertThat(cubesAccessInfo.getCurrentUserFacilityType(), is(CSRUR_I));
        assertThat(cubesAccessInfo.getFacility(), is("HF8"));
        assertThat(cubesAccessInfo.getDistrict(), is("MATOLA"));
        assertThat(cubesAccessInfo.getProvince(), is("MAPUTO_PROVINCIA"));
    }

    @Test
    public void shouldParseQueryStringWhenFacilityIsMissing() throws Exception {
        CubesAccessInfo cubesAccessInfo = noFacilityWithType(CSRUR_II);

        assertThat(cubesAccessInfo.getCurrentUserFacilityType(), is(CSRUR_II));
        assertThat(cubesAccessInfo.getFacility(), isEmptyOrNullString());
        assertThat(cubesAccessInfo.getDistrict(), is("MATOLA"));
        assertThat(cubesAccessInfo.getProvince(), is("MAPUTO_PROVINCIA"));
    }

    @Test
    public void shouldParseQueryStringWhenDistrictIsMissing() throws Exception {
        CubesAccessInfo cubesAccessInfo = onlyProvinceWithType(DDM);

        assertThat(cubesAccessInfo.getCurrentUserFacilityType(), is(DDM));
        assertThat(cubesAccessInfo.getFacility(), isEmptyOrNullString());
        assertThat(cubesAccessInfo.getDistrict(), isEmptyOrNullString());
        assertThat(cubesAccessInfo.getProvince(), is("MAPUTO_PROVINCIA"));
    }

    @Test
    public void shouldParseQueryStringWhenAllLocationsAreMissing() throws Exception {
        CubesAccessInfo cubesAccessInfo = noLocationWithType(DPM);

        assertThat(cubesAccessInfo.getCurrentUserFacilityType(), is(DPM));
        assertThat(cubesAccessInfo.getFacility(), isEmptyOrNullString());
        assertThat(cubesAccessInfo.getDistrict(), isEmptyOrNullString());
        assertThat(cubesAccessInfo.getProvince(), isEmptyOrNullString());
    }

    @Test
    public void nationalUserCanMissAllLocationInfo() throws Exception {
        CubesAccessInfo cubesAccessInfo = noLocationWithType(DNM);

        boolean isMissing = cubesAccessInfo.isLocationInfoMissing();

        assertFalse(isMissing);
    }

    @Test
    public void DMPUserCanMissDistrictAndFacilityInfo() throws Exception {
        CubesAccessInfo noLocation = noLocationWithType(DPM);
        boolean isMissing = noLocation.isLocationInfoMissing();
        assertTrue(isMissing);

        CubesAccessInfo noFacility = noFacilityWithType(DPM);
        isMissing = noFacility.isLocationInfoMissing();
        assertFalse(isMissing);

        CubesAccessInfo onlyProvinceWithType = onlyProvinceWithType(DPM);
        isMissing = onlyProvinceWithType.isLocationInfoMissing();
        assertFalse(isMissing);
    }

    @Test
    public void DDMUserCanMissFacilityInfo() throws Exception {
        CubesAccessInfo noLocation = noLocationWithType(DDM);
        boolean isMissing = noLocation.isLocationInfoMissing();
        assertTrue(isMissing);

        CubesAccessInfo noFacility = noFacilityWithType(DDM);
        isMissing = noFacility.isLocationInfoMissing();
        assertFalse(isMissing);

        CubesAccessInfo onlyProvinceWithType = onlyProvinceWithType(DDM);
        isMissing = onlyProvinceWithType.isLocationInfoMissing();
        assertTrue(isMissing);
    }

    @Test
    public void FacilityUserCanNotMissAnything() throws Exception {
        CubesAccessInfo noLocation = noLocationWithType(CSRUR_I);
        boolean isMissing = noLocation.isLocationInfoMissing();
        assertTrue(isMissing);

        CubesAccessInfo noFacility = noFacilityWithType(CSRUR_I);
        isMissing = noFacility.isLocationInfoMissing();
        assertTrue(isMissing);

        CubesAccessInfo onlyProvinceWithType = onlyProvinceWithType(CSRUR_I);
        isMissing = onlyProvinceWithType.isLocationInfoMissing();
        assertTrue(isMissing);
    }

    private CubesAccessInfo noFacilityWithType(MozFacilityTypes facilityType) {
        return CubesAccessInfo.createInstance(facilityType, "?cut=drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA");
    }

    private CubesAccessInfo onlyProvinceWithType(MozFacilityTypes facilityType) {
        return CubesAccessInfo.createInstance(facilityType, "?cut=drug:08S01Z|location:MAPUTO_PROVINCIA");
    }

    private CubesAccessInfo noLocationWithType(MozFacilityTypes facilityType) {
        return CubesAccessInfo.createInstance(facilityType, "?cut=drug:08S01Z");
    }
}