package org.openlmis.restapi.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.restapi.domain.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.FacilityBuilder.geographicZoneId;


@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class AppInfoMapperTest {

    @Autowired
    AppInfoMapper mapper;

    @Autowired
    FacilityMapper facilityMapper;

    @Test
    public void shouldCreateSuccess() throws Exception {

        Facility facility = initFacility();
        facilityMapper.insert(facility);

        AppInfo appInfo = new AppInfo(facility.getId(), "1.0");
        mapper.insert(appInfo);

        AppInfo queryAppInfo = mapper.queryVersionByFacilityCode(facility.getCode());

        assertThat(queryAppInfo.getAppVersion(), is(appInfo.getAppVersion()));
    }

    @Test
    public void shouldUpdateSuccess() throws Exception {

        Facility facility = initFacility();
        facilityMapper.insert(facility);
        AppInfo appInfo = new AppInfo(facility.getId(), "1.0");
        mapper.insert(appInfo);

        appInfo.setAppVersion("2.0");
        mapper.update(appInfo);

        AppInfo queryAppInfo = mapper.queryVersionByFacilityCode(facility.getCode());

        assertThat(queryAppInfo.getAppVersion(), is(appInfo.getAppVersion()));

    }

    private Facility initFacility() {
        Facility facility = make(a(defaultFacility,
                with(code, "TRZ001"),
                with(name, "Ngorongoro Hospital"),
                with(type, "warehouse"),
                with(geographicZoneId, 1L)));

        facility.setCode("testFacilityCode");
        return facility;
    }
}
