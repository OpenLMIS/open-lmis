package org.openlmis.core.repository.mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.FacilityBuilder.*;

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class FacilityMapperIT {

    @Autowired
    FacilityMapper facilityMapper;

    @Before
    public void setUp() throws Exception {
        facilityMapper.deleteProgramMappings();
        facilityMapper.deleteAll();
    }

    @Test
    public void shouldFetchAllFacilitiesAvailable() throws Exception {
        Facility trz001 = make(a(defaultFacility,
                with(code, "TRZ001"),
                with(name, "Ngorongoro Hospital"),
                with(type, 1),
                with(geographicZone, 1)));
        Facility trz002 = make(a(defaultFacility,
                with(code, "TRZ002"),
                with(name, "Rural Clinic"),
                with(type, 2),
                with(geographicZone, 2)));

        facilityMapper.insert(trz001);
        facilityMapper.insert(trz002);

        List<Facility> facilities = facilityMapper.getAll();
        assertTrue(facilities.contains(trz001));
        assertTrue(facilities.contains(trz002));
    }

    @Test
    public void shouldInsertSupportedProgramsForFacility() {
        int programId = 1;
        Facility facility = make(a(defaultFacility));
        facilityMapper.insert(facility);
        int count = facilityMapper.map(facility.getCode(), programId, true);
        facilityMapper.map(facility.getCode(), 5, false);
        assertEquals(1, count);
    }

    @Test
    public void shouldFetchFacilityAndFacilityTypeData() {
        Facility facility1 = make(a(defaultFacility,
                with(code, "TRZ001"),
                with(name, "Ngorongoro Hospital"),
                with(type, 2)));

        Facility facility2 = make(a(defaultFacility));

        facilityMapper.insert(facility1);
        facilityMapper.insert(facility2);

        String facilityCode = "TRZ001";
        RequisitionHeader requisitionHeader = facilityMapper.getRequisitionHeaderData(facilityCode);

        assertEquals("TRZ001", requisitionHeader.getFacilityCode());
        assertEquals("Ngorongoro Hospital", requisitionHeader.getFacilityName());
        assertEquals("Lvl3 Hospital", requisitionHeader.getFacilityType());
        assertEquals(.5, requisitionHeader.getEmergencyOrderPoint(), 0.0);
        assertEquals(3, requisitionHeader.getMaximumStockLevel());

        assertEquals("Dodoma", requisitionHeader.getZone().getValue());
        assertEquals("Arusha", requisitionHeader.getParentZone().getValue());

        assertEquals("city", requisitionHeader.getZone().getLabel());
        assertEquals("state", requisitionHeader.getParentZone().getLabel());
    }

    @Test
    public void shouldInsertFacility() throws Exception {
        assertThat(facilityMapper.insert(make(a(defaultFacility))), is(1));
    }

    @After
    public void tearDown() throws Exception {
        Facility facility = make(a(defaultFacility,
                with(code, "DDM001"),
                with(name, "Dodoma Hospital"),
                with(type, 2)));
        facilityMapper.insert(facility);
        facilityMapper.map(facility.getCode(),5,true);
    }
}
