/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.reporting.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class TemplateParameterTest {

  private TemplateParameter parameter;

  @Before
  public void setUp() throws Exception {
    parameter = new TemplateParameter();
  }

  @Test
  public void shouldGetParsedValueForIntegerDataType() throws Exception {
    parameter.setDataType("java.lang.Integer");
    assertThat(parameter.getParsedValueOf("2334"), is((Object) 2334));
  }

  @Test
  public void shouldGetParsedValueForShortDataType() throws Exception {
    parameter.setDataType("java.lang.Short");
    Short shortValue = (short) 123;
    assertThat(parameter.getParsedValueOf("123"), is((Object) shortValue));
  }

  @Test
  public void shouldGetParsedValueForLongDataType() throws Exception {
    parameter.setDataType("java.lang.Long");
    assertThat(parameter.getParsedValueOf("2334782"), is((Object) 2334782L));
  }

  @Test
  public void shouldGetParsedValueForBooleanDataType() throws Exception {
    parameter.setDataType("java.lang.Boolean");
    assertThat(parameter.getParsedValueOf("true"), is((Object) true));
  }

  @Test
  public void shouldGetParsedValueForDateDataType() throws Exception {
    parameter.setDataType("java.util.Date");
    String stringDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    assertThat(parameter.getParsedValueOf(stringDate), is((Object) new SimpleDateFormat("dd/MM/yyyy").parse(stringDate)));
  }

  @Test
  public void shouldGetParsedValueForFloatDataType() throws Exception {
    parameter.setDataType("java.lang.Float");
    assertThat(parameter.getParsedValueOf("23.2"), is((Object) 23.2f));
  }

  @Test
  public void shouldGetParsedValueForDoubleDataType() throws Exception {
    parameter.setDataType("java.lang.Double");
    assertThat(parameter.getParsedValueOf("23.3456"), is((Object) 23.3456));
  }

  @Test
  public void shouldGetParsedValueForBigDecimalDataType() throws Exception {
    parameter.setDataType("java.math.BigDecimal");
    BigDecimal bigDecimal = new BigDecimal("23.234566");
    assertThat(parameter.getParsedValueOf("23.234566"), is((Object) bigDecimal));
  }

  @Test
  public void shouldGetParsedValueForStringDataType() throws Exception {
    parameter.setDataType("java.lang.String");
    assertThat(parameter.getParsedValueOf("text"), is((Object) "text"));
  }
}
