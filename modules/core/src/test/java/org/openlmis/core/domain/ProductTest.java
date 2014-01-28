/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class ProductTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowExceptionIfPackSizeIsZero() throws Exception {
    Product product = new Product();
    product.setPackSize(0);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.invalid.pack.size");

    product.validate();
  }

  @Test
  public void shouldThrowExceptionIfPackSizeIsNegative() throws Exception {
    Product product = new Product();
    product.setPackSize(-88);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.invalid.pack.size");

    product.validate();
  }

  @Test
  public void shouldReturnProductName() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    assertThat(product.getName(), is("Primary Name Tablet strength mg"));
  }
}
