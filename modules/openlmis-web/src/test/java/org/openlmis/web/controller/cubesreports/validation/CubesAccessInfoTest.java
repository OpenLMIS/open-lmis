package org.openlmis.web.controller.cubesreports.validation;

import org.junit.Test;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.moz.MozFacilityTypes;
import org.openlmis.report.model.dto.Facility;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.domain.moz.MozFacilityTypes.*;

public class CubesAccessInfoTest {

    private String noLocationQueryString = "?cut=drug:08S01Z";
    private String noFacilityQueryString = "?cut=drug:08S01Z|location:MAPUTO_PROVINCIA,MATOLA";
    private String onlyProvinceQueryString = "?cut=drug:08S01Z|location:MAPUTO_PROVINCIA";

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
    public void shouldNotFillAnyLocationInfoForNationalUser() throws Exception {
        CubesAccessInfo cubesAccessInfo = noLocationWithType(DNM);

        String filled = cubesAccessInfo.fillMissingLocation(asList(createZone("a", "Province")), createFacilities("c"));

        assertThat(filled, is(noLocationQueryString));
    }

    @Test
    public void shouldFillProvinceInfoForDPMUser() throws Exception {
        CubesAccessInfo noLocation = noLocationWithType(DPM);
        String filled = noLocation.fillMissingLocation(asList(createZone("a", "province")), createFacilities("c"));
        assertThat(filled, is(noLocationQueryString + "|location:a"));

        CubesAccessInfo noFacility = noFacilityWithType(DPM);
        filled = noFacility.fillMissingLocation(asList(createZone("a", "province")), createFacilities("c"));
        assertThat(filled, is(noFacilityQueryString));

        CubesAccessInfo onlyProvinceWithType = onlyProvinceWithType(DPM);
        filled = onlyProvinceWithType.fillMissingLocation(asList(createZone("a", "province")), createFacilities("c"));
        assertThat(filled, is(onlyProvinceQueryString));
    }

    @Test
    public void shouldFillDistrictInfoForDDMUser() throws Exception {
        CubesAccessInfo noLocation = noLocationWithType(DDM);
        String filled = noLocation.fillMissingLocation(asList(createZone("a", "province"), createZone("b", "district")), createFacilities("c"));
        assertThat(filled, is(noLocationQueryString + "|location:a,b"));

        CubesAccessInfo noFacility = noFacilityWithType(DDM);
        filled = noFacility.fillMissingLocation(asList(createZone("a", "province"), createZone("b", "district")), createFacilities("c"));
        assertThat(filled, is(noFacilityQueryString));

        CubesAccessInfo onlyProvinceWithType = onlyProvinceWithType(DDM);
        filled = onlyProvinceWithType.fillMissingLocation(asList(createZone("a", "province"), createZone("b", "district")), createFacilities("c"));
        assertThat(filled, is(onlyProvinceQueryString + ",b"));
    }

    @Test
    public void shouldFillFacilityInfoForFacilityUser() throws Exception {
        CubesAccessInfo noLocation = noLocationWithType(CSRUR_I);
        String filled = noLocation.fillMissingLocation(asList(createZone("a", "province"), createZone("b", "district")), createFacilities("c"));
        assertThat(filled, is(noLocationQueryString + "|facility:c"));

        CubesAccessInfo noFacility = noFacilityWithType(CSRUR_I);
        filled = noFacility.fillMissingLocation(asList(createZone("a", "province"), createZone("b", "district")), createFacilities("c"));
        assertThat(filled, is(noFacilityQueryString + "|facility:c"));

        CubesAccessInfo onlyProvinceWithType = onlyProvinceWithType(CSRUR_I);
        filled = onlyProvinceWithType.fillMissingLocation(asList(createZone("a", "province"), createZone("b", "district")), createFacilities("c"));
        assertThat(filled, is(onlyProvinceQueryString + "|facility:c"));
    }

    private List<Facility> createFacilities(String code) {
        Facility facility = new Facility();
        facility.setCode(code);
        return asList(facility);
    }

    private GeographicZone createZone(String zoneCode, String levelCode) {
        GeographicLevel level = new GeographicLevel();
        level.setCode(levelCode);

        GeographicZone geographicZone = new GeographicZone();
        geographicZone.setCode(zoneCode);
        geographicZone.setLevel(level);

        return geographicZone;
    }

    private CubesAccessInfo noFacilityWithType(MozFacilityTypes facilityType) {
        return CubesAccessInfo.createInstance(facilityType, noFacilityQueryString);
    }

    private CubesAccessInfo onlyProvinceWithType(MozFacilityTypes facilityType) {
        return CubesAccessInfo.createInstance(facilityType, onlyProvinceQueryString);
    }

    private CubesAccessInfo noLocationWithType(MozFacilityTypes facilityType) {
        return CubesAccessInfo.createInstance(facilityType, noLocationQueryString);
    }
}