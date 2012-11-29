package org.openlmis.core.repository.mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.openlmis.core.builder.FacilityBuilder.*;

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class FacilityMapperIT {

    @Autowired
    FacilityMapper facilityMapper;

    @Before
    @After
    public void setUp() throws Exception {
        facilityMapper.deleteAll();
    }

    @Test
    public void shouldFetchAllFacilitiesAvailable() throws Exception {
        Facility trz001 = make(a(facility,
                with(code, "TRZ001"),
                with(name, "Ngorongoro Hospital"),
                with(type, "warehouse"),
                with(geographicZone, 1)));
        Facility trz002 = make(a(facility,
                with(code, "TRZ002"),
                with(name, "Rural Clinic"),
                with(type, "lvl3_hospital"),
                with(geographicZone, 2)));

        facilityMapper.insert(trz001);
        facilityMapper.insert(trz002);

        List<Facility> facilities = facilityMapper.getAll();
        assertTrue(facilities.contains(trz001));
        assertTrue(facilities.contains(trz002));
    }

    @Test
    public void shouldFetchFacilityAndFacilityTypeDataForRequisitionHeader() {
        Facility facility1 = make(a(facility,
                with(code, "TRZ001"),
                with(name, "Ngorongoro Hospital"),
                with(type, "lvl3_hospital")));

        Facility facility2 = make(a(facility));

        facilityMapper.insert(facility1);
        facilityMapper.insert(facility2);

        String facilityCode = "TRZ001";
        RequisitionHeader requisitionHeader = facilityMapper.getRequisitionHeaderData(facilityCode);

        assertEquals("TRZ001", requisitionHeader.getFacilityCode());
        assertEquals("Ngorongoro Hospital", requisitionHeader.getFacilityName());
        assertEquals("Lvl3 Hospital", requisitionHeader.getFacilityType());
        assertEquals(.5, requisitionHeader.getEmergencyOrderPoint(), 0.0);
        assertEquals(3, requisitionHeader.getMaximumStockLevel());
        assertEquals("MoH", requisitionHeader.getFacilityOperatedBy());

        assertEquals("Dodoma", requisitionHeader.getZone().getValue());
        assertEquals("Arusha", requisitionHeader.getParentZone().getValue());

        assertEquals("city", requisitionHeader.getZone().getLabel());
        assertEquals("state", requisitionHeader.getParentZone().getLabel());
    }

    @Test
    public void shouldInsertFacility() throws Exception {
        assertThat(facilityMapper.insert(make(a(facility))), is(1));
    }

    @Test
    public void shouldGetAllFacilityTypes() throws Exception {
        List<FacilityType> facilityTypes = facilityMapper.getAllTypes();
        assertThat(facilityTypes.size(), is(11));
        FacilityType facilityType = facilityTypes.get(0);
        assertThat(facilityType.getCode(), is("warehouse"));
        assertThat(facilityType.getName(), is("Warehouse"));
        assertThat(facilityType.getDescription(), is("Central Supply Depot"));
        assertThat(facilityType.getLevelId(), is(nullValue()));
        assertThat(facilityType.getNominalMaxMonth(), is(3));
        assertThat(facilityType.getNominalEop(), is(0.5));
        assertThat(facilityType.getDisplayOrder(), is(11));
        assertThat(facilityType.isActive(), is(true));
    }

    @Test
    public void shouldGetAllOperators() throws Exception {
        List<FacilityOperator> allOperators = facilityMapper.getAllOperators();
        assertThat(allOperators.size(), is(4));
        FacilityOperator facilityOperator = allOperators.get(0);
        assertThat(facilityOperator.getCode(), is("MoH"));
        assertThat(facilityOperator.getText(), is("MoH"));
        assertThat(facilityOperator.getDisplayOrder(), is(1));
    }

    @Test
    public void shouldGetAllGeographicZones() throws Exception {
        List<GeographicZone> allGeographicZones = facilityMapper.getAllGeographicZones();
        assertThat(allGeographicZones.size(), is(3));
        GeographicZone geographicZone = allGeographicZones.get(0);

        assertThat(geographicZone.getId(), is(1L));
        assertThat(geographicZone.getValue(), is("Arusha"));
        assertThat(geographicZone.getLabel(), is("state"));
    }
}
