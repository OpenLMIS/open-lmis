/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.dto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProgramProductBuilder.defaultProgramProduct;

@Category(UnitTests.class)
public class ProgramProductDTOTest {

  @Test
  public void shouldCreateProgramProductDTOFromProgramProduct() {

    ProgramProduct programProduct = make(a(defaultProgramProduct));

    programProduct.setProductCategory(new ProductCategory("C1", "Category 1", 1));

    ProgramProductDTO programProductDTO = new ProgramProductDTO(programProduct);

    assertThat(programProductDTO.getProductCode(), is(programProduct.getProduct().getCode()));
    assertThat(programProductDTO.getProductName(), is(programProduct.getProduct().getPrimaryName()));
    assertThat(programProductDTO.getProgramCode(), is(programProduct.getProgram().getCode()));
    assertThat(programProductDTO.getProgramName(), is(programProduct.getProgram().getName()));
    assertThat(programProductDTO.getCategory(), is(programProduct.getProductCategory().getName()));
    assertThat(programProductDTO.getDescription(), is(programProduct.getProduct().getDescription()));
    assertThat(programProductDTO.getUnit(), is(programProduct.getProduct().getDosesPerDispensingUnit()));
  }
}
