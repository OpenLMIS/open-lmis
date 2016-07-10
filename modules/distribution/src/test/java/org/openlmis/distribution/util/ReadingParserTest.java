package org.openlmis.distribution.util;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.AdultCoverageLineItem;
import org.openlmis.distribution.dto.Reading;

import java.util.Date;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ReadingParserTest {

  @Mock
  private Reading property;

  @Test
  public void shouldParseReadingPropertyCorrectly() throws Exception {
    ReadingParser.parse(property, null, null, String.class);
    verify(property).getEffectiveValue();

    ReadingParser.parse(property, null, null, Boolean.class);
    verify(property).parseBoolean();

    ReadingParser.parse(property, null, null, Float.class);
    verify(property).parseFloat();

    ReadingParser.parse(property, null, null, Date.class);
    verify(property).parseDate();

    ReadingParser.parse(property, null, null, Integer.class);
    verify(property).parseInt();

    ReadingParser.parse(property, AdultCoverageLineItem.class, "outreachTetanus1", Integer.class);
    verify(property).parsePositiveInt();
  }

}
