/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.upload.processor;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import org.openlmis.db.categories.UnitTests;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
@Category(UnitTests.class)
public class ParseIntegerFromDoubleTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private CsvContext context;
  private CellProcessor next;
  private ParseIntegerFromDouble parseIntegerFromDouble;

  @Before
  public void setUp() throws Exception {
    context = new CsvContext(1, 1, 1);
    next = mock(CellProcessor.class);
    parseIntegerFromDouble = new ParseIntegerFromDouble(next);
  }

  @Test
  public void shouldParseIntegerFromDouble() throws Exception {
    parseIntegerFromDouble.execute("99999999.99999999999999", context);

    verify(next).execute(argThat(parsedValueMatcher(99999999)), eq(context));
  }

  @Test
  public void shouldParseInteger() throws Exception {
    parseIntegerFromDouble.execute("999", context);

    verify(next).execute(argThat(parsedValueMatcher(999)), eq(context));
  }

  @Test
  public void shouldThrowExceptionIfValueIsGreaterThanMaxIntValue() throws Exception {
    expectedException.expect(SuperCsvCellProcessorException.class);
    expectedException.expectMessage("'2147483648' could not be parsed as an Integer");
    parseIntegerFromDouble.execute("2147483648", context);
  }

  @Test
  public void shouldThrowExceptionIfValueIsNotIntegerNorString() throws Exception {
    expectedException.expect(SuperCsvCellProcessorException.class);
    expectedException.expectMessage("the input value should be of type Integer or String but is of type java.lang.Double");

    parseIntegerFromDouble.execute(9999999999999999.99999999999999, context);
  }

  @Test
  public void shouldThrowExceptionIfValueNotValid() throws Exception {
    expectedException.expect(SuperCsvCellProcessorException.class);
    expectedException.expectMessage("'999,&999.99999999999999' could not be parsed as an Integer");

    parseIntegerFromDouble.execute("999,&999.99999999999999", context);
  }

  private Matcher<Object> parsedValueMatcher(final Integer expected) {
    return new ArgumentMatcher<Object>() {
      @Override
      public boolean matches(Object argument) {
        Integer result = (Integer) argument;
        return result.equals(expected);
      }
    };
  }
}