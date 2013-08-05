package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@Transactional
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class RefrigeratorMapperIT {

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  RefrigeratorMapper mapper;

  @Test
  public void shouldReturnListOfRefrigeratosForAFacility() throws Exception {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    Refrigerator refrigerator = new Refrigerator("SAM","AUO","SAM1",facility.getId());
    refrigerator.setCreatedBy(1L);
    refrigerator.setModifiedBy(1L);

    mapper.insert(refrigerator);

    List<Refrigerator> refrigerators = mapper.getRefrigerators(facility.getId());

    assertThat(refrigerators.size(),is(1));
    assertThat(refrigerators.get(0),is(refrigerator));
  }

}
