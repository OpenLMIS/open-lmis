package org.openlmis.core.repository.mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.domain.RequisitionHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.*;
import static org.openlmis.core.builder.FacilityBuilder.*;

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class FacilityMapperIT {

    public static final String PROGRAM_CODE = "TB";

    @Autowired
    ProgramSupportedMapper programSupportedMapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Before
    public void setUp() throws Exception {
        programSupportedMapper.deleteProgramMappings();
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
    public void shouldFetchFacilityAndFacilityTypeDataforRequisitionHeader() {
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

    @After
    public void tearDown() throws Exception {
        facilityMapper.deleteAll();

        Facility facility = make(a(FacilityBuilder.facility,
                with(code, "DDM001"),
                with(name, "Dodoma Hospital"),
                with(type, "lvl3_hospital")));
        facilityMapper.insert(facility);
        programSupportedMapper.addSupportedProgram(new ProgramSupported(facility.getCode(), PROGRAM_CODE, true, "test", now().toDate()));
        //TODO: remove this from tear down. its being used to leave some data for initiate rnr.!!!
    }
}
