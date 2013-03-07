package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class GeographicZoneMapperIT {

  @Autowired
  private GeographicZoneMapper mapper;

  @Test
  public void shouldSaveGeographicZone() throws Exception {
    GeographicZone geographicZone = new GeographicZone(null, "code", "name", new GeographicLevel(2,null, null), null, null);

    mapper.insert(geographicZone);

    GeographicZone returnedZone = mapper.getGeographicZoneByCode("code");

    assertThat(returnedZone, is(geographicZone));
  }

  @Test
  public void shouldGetGeographicLevelByCode() throws Exception {
    String code = "state";
    GeographicLevel geographicLevel = mapper.getGeographicLevelByCode(code);
    assertThat(geographicLevel.getName(), is("State"));
    assertThat(geographicLevel.getId(), is(1));
  }

  @Test
  public void shouldGetNullIfZoneNotPresent() throws Exception {
    GeographicZone nullZone = mapper.getGeographicZoneByCode("some random code");

    assertThat(nullZone, is(nullValue()));
  }
}
