package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ProgramProductBuilder {

  public static final Property<ProgramProduct, Integer> programId = newProperty();
  public static final Property<ProgramProduct, Integer> productId = newProperty();
  public static final Property<ProgramProduct, Integer> dosagePerMonth = newProperty();

  public static final Instantiator<ProgramProduct> defaultProgramProduct = new Instantiator<ProgramProduct>() {

    @Override
    public ProgramProduct instantiate(PropertyLookup<ProgramProduct> lookup) {
      Product product = new Product();
      product.setId(lookup.valueOf(productId, 1));
      Program program = new Program();
      program.setId(lookup.valueOf(programId, 1));
      return new ProgramProduct(program, product, lookup.valueOf(dosagePerMonth, 1), true);
    }
  };
}
