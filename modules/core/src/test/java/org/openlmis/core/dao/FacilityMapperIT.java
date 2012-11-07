package org.openlmis.core.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class FacilityMapperIT {

    @Autowired
    FacilityMapper facilityMapper;

    @Before
    public void setUp() throws Exception {
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
}
