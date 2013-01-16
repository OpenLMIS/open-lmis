package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;

import static com.natpryce.makeiteasy.Property.newProperty;

public class FacilityApprovedProductBuilder {

  public static final Property<FacilityApprovedProduct, String> facilityTypeCode = newProperty();
  public static final Property<FacilityApprovedProduct, String> programCode = newProperty();
  public static final Property<FacilityApprovedProduct, String> productCode = newProperty();
  public static final Property<FacilityApprovedProduct, Integer> maxMonthsOfStock = newProperty();

  public static final String defaultFacilityTypeCode = "code";
  public static final String defaultProgramCode = "YELL_FVR";
  public static final String defaultProductCode = "P999";
  public static final Integer defaultMaxMonthsOfStock = 3;

  public static final Instantiator<FacilityApprovedProduct> defaultFacilityApprovedProduct = new Instantiator<FacilityApprovedProduct>() {

    @Override
    public FacilityApprovedProduct instantiate(PropertyLookup<FacilityApprovedProduct> lookup) {

      Program program = new Program();
      program.setCode(lookup.valueOf(programCode, defaultProgramCode));

      Product product = new Product();
      product.setCode(lookup.valueOf(productCode, defaultProductCode));

      ProgramProduct programProduct = new ProgramProduct(program, product, 1, true);

      return new FacilityApprovedProduct(
        lookup.valueOf(facilityTypeCode, defaultFacilityTypeCode),
        programProduct,
        lookup.valueOf(maxMonthsOfStock, defaultMaxMonthsOfStock));

    }
  };
}