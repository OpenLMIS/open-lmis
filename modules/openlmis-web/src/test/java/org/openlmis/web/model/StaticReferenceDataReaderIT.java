package org.openlmis.web.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.web.configurationReader.StaticReferenceDataReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
public class StaticReferenceDataReaderIT {

	@Autowired
	StaticReferenceDataReader staticReferenceDataReader;

	@Test
	public void shouldReturnCurrencyConfigured() throws Exception {
		assertThat(staticReferenceDataReader.getCurrency(), is("$"));
	}
}
