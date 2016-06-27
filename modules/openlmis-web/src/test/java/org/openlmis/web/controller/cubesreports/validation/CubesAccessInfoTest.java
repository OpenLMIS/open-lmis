package org.openlmis.web.controller.cubesreports.validation;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
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
        CubesAccessInfo cubesAccessInfo = CubesAccessInfo.createInstance(CSRUR_II, "?cut=drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA");

        assertThat(cubesAccessInfo.getCurrentUserFacilityType(), is(CSRUR_II));
        assertThat(cubesAccessInfo.getFacility(), isEmptyOrNullString());
        assertThat(cubesAccessInfo.getDistrict(), is("MATOLA"));
        assertThat(cubesAccessInfo.getProvince(), is("MAPUTO_PROVINCIA"));
    }

    @Test
    public void shouldParseQueryStringWhenDistrictIsMissing() throws Exception {
        CubesAccessInfo cubesAccessInfo = CubesAccessInfo.createInstance(DDM, "?cut=drug:08S01Z|location:MAPUTO_PROVINCIA");

        assertThat(cubesAccessInfo.getCurrentUserFacilityType(), is(DDM));
        assertThat(cubesAccessInfo.getFacility(), isEmptyOrNullString());
        assertThat(cubesAccessInfo.getDistrict(), isEmptyOrNullString());
        assertThat(cubesAccessInfo.getProvince(), is("MAPUTO_PROVINCIA"));
    }

    @Test
    public void shouldParseQueryStringWhenAllLocationsAreMissing() throws Exception {
        CubesAccessInfo cubesAccessInfo = CubesAccessInfo.createInstance(DPM, "?cut=drug:08S01Z");

        assertThat(cubesAccessInfo.getCurrentUserFacilityType(), is(DPM));
        assertThat(cubesAccessInfo.getFacility(), isEmptyOrNullString());
        assertThat(cubesAccessInfo.getDistrict(), isEmptyOrNullString());
        assertThat(cubesAccessInfo.getProvince(), isEmptyOrNullString());
    }

    @Test
    public void nationalUserCanMissAllLocationInfo() throws Exception {
        CubesAccessInfo cubesAccessInfo = CubesAccessInfo.createInstance(DNM, "?cut=drug:08S01Z");

        boolean isMissing = cubesAccessInfo.isLocationInfoMissing();

        assertFalse(isMissing);
    }
}