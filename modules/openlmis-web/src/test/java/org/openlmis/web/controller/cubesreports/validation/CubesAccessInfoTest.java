package org.openlmis.web.controller.cubesreports.validation;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.domain.moz.MozFacilityTypes.CSRUR_I;

public class CubesAccessInfoTest {
    @Test
    public void shouldParseProvinceDistrictFacilityFromQueryString() throws Exception {
        CubesAccessInfo cubesAccessInfo = CubesAccessInfo.createInstance(CSRUR_I, "?cut=facility:HF8|drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA");

        assertThat(cubesAccessInfo.getCurrentUserFacilityType(), is(CSRUR_I));
        assertThat(cubesAccessInfo.getFacility(), is("HF8"));
        assertThat(cubesAccessInfo.getDistrict(), is("MATOLA"));
        assertThat(cubesAccessInfo.getProvince(), is("MAPUTO_PROVINCIA"));
    }
}