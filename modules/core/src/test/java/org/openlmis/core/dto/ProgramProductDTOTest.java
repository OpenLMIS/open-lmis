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

    programProduct.getProduct().setCategory(new ProductCategory("P1", "Product Name", 1));

    ProgramProductDTO programProductDTO = new ProgramProductDTO(programProduct);

    assertThat(programProductDTO.getProductCode(), is(programProduct.getProduct().getCode()));
    assertThat(programProductDTO.getProductName(), is(programProduct.getProduct().getPrimaryName()));
    assertThat(programProductDTO.getProgramCode(), is(programProduct.getProgram().getCode()));
    assertThat(programProductDTO.getProgramName(), is(programProduct.getProgram().getName()));
    assertThat(programProductDTO.getCategory(), is(programProduct.getProduct().getCategory().getName()));
    assertThat(programProductDTO.getDescription(), is(programProduct.getProduct().getDescription()));
    assertThat(programProductDTO.getUnit(), is(programProduct.getProduct().getDosesPerDispensingUnit()));
  }
}
