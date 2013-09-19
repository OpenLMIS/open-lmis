package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.springframework.core.env.Environment;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class StaticReferenceDataServiceTest {

  @Mock
  Environment environment;

  @InjectMocks
  StaticReferenceDataService service;

  @Test
  public void shouldGetEnvironmentProperty() throws Exception {
    String propertyName = "any.property.name";
    String expectedValue = "property.value";

    Mockito.when(environment.getProperty(propertyName)).thenReturn(expectedValue);

    String value = service.getPropertyValue(propertyName);

    assertThat(value, is(expectedValue));
  }
}
