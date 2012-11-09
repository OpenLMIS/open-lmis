package org.openlmis.core.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        Facility trz001 = new FacilityBuilder().withCode("TRZ001").withName("Ngorongoro Hospital").withType(1).withGZone(1).build();
        Facility trz002 = new FacilityBuilder().withCode("TRZ002").withName("Rural Clinic").withType(2).withGZone(2).build();

        facilityMapper.insert(trz001);
        facilityMapper.insert(trz002);

        List<Facility> facilities = facilityMapper.getAll();
        assertTrue(facilities.contains(trz001));
        assertTrue(facilities.contains(trz002));
    }

    @Test
    public void shouldInsertSupportedProgramsForFacility() {
        int programId = 1;
        Facility facility = new FacilityBuilder().withCode("TRZ001").withName("Ngorongoro Hospital").withType(1).withGZone(1).build();
        facilityMapper.insert(facility);

        int status = facilityMapper.map(facility.getCode(), programId, true);
        assertEquals(1, status);
    }

    @Test
    public void shouldFetchFacilityAndFacilityTypeData() {
        Facility facility1 = new FacilityBuilder()
                .withCode("TRZ001")
                .withName("Ngorongoro Hospital")
                .withType(2)
                .withGZone(1)
                .build();

        Facility facility2 = new FacilityBuilder().withDefaults().build();

        facilityMapper.insert(facility1);
        facilityMapper.insert(facility2);

        String facilityCode = "TRZ001";
        RequisitionHeader requisitionHeader = facilityMapper.getRequisitionHeaderData(facilityCode);

        assertEquals("TRZ001", requisitionHeader.getFacilityCode());
        assertEquals("Ngorongoro Hospital", requisitionHeader.getFacilityName());
        assertEquals("Clinic", requisitionHeader.getFacilityType());
        assertEquals(1.0, requisitionHeader.getEmergencyOrderPoint(), 0.0);
        assertEquals(2, requisitionHeader.getMaximumStockLevel());

    }
}
