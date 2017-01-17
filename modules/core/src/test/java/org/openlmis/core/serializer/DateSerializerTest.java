package org.openlmis.core.serializer;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;

import java.util.Calendar;
import java.util.Date;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DateSerializerTest {

  @Mock
  JsonGenerator generator;

  @Mock
  SerializerProvider provider;

  DateSerializer serializer = new DateSerializer();

  @Test
  public void shouldFormatDate() throws Exception {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2016, Calendar.JUNE, 23);

    Date date = calendar.getTime();

    serializer.serialize(date, generator, provider);

    verify(generator).writeString("06/23/2016");
  }

}
