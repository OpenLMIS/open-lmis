/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EpiInventoryLineItemTest {

  @Test
  public void shouldCreateEpiInventoryLineItemFromFacilityProgramProduct() throws Exception {
    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct(new ProgramProduct(), 4L, null);
    facilityProgramProduct.setDisplayOrder(1);
    Product product = new Product();
    product.setCode("P10");
    product.setPrimaryName("Primary Name");
    facilityProgramProduct.setProduct(product);
    FacilityProgramProduct spyFPP = spy(facilityProgramProduct);
    doReturn(567).when(spyFPP).calculateIsa(420L, 5);

    EpiInventoryLineItem lineItem = new EpiInventoryLineItem(6L, spyFPP, 420L, 5);

    assertThat(lineItem.getIdealQuantity(), is(567));
    assertThat(lineItem.getProductName(), is("Primary Name"));
    assertThat(lineItem.getProductCode(), is("P10"));
    assertThat(lineItem.getProductDisplayOrder(), is(1));
  }
}
