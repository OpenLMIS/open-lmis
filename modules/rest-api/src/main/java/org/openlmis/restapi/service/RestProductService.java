package org.openlmis.restapi.service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.ProductService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramSupportedService;
import org.openlmis.core.service.UserService;
import org.openlmis.restapi.domain.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RestProductService {
  private final static String DEFAULT_DISPENDING_UNIT = "1";
  private final static Integer DEFAULT_PACK_SIZE = 1;
  private final static Integer DEFAULT_DOSES_DISPENSING_UNIT = 1;
  private final static Boolean DEFAULT_FULL_SUPPLY = true;
  private final static Boolean DEFAULT_TRACER = false;
  private final static Boolean DEFAULT_ROUND_TO_ZERO = false;
  private final static Integer DEFAULT_PACK_ROUNDING_THRESHOLD = 0;

  @Autowired
  private ProductService productService;

  @Autowired
  private ProgramProductService programProductSevice;

  @Autowired
  private UserService userService;

  @Autowired
  private ProgramSupportedService programSupportedService;

  @Transactional
  public Product buildAndSave(Product product) {
    productService.save(buildKit(product));
    return product;
  }

  private Product buildKit(Product product) {
    setProductDefaultValuesForKit(product);

    for(KitProduct kitProduct : product.getKitProductList()) {
      kitProduct.setKitCode(product.getCode());
    }
    return product;
  }

  public void setProductDefaultValuesForKit(Product product) {
    if (product.getDispensingUnit() == null) {
      product.setDispensingUnit(DEFAULT_DISPENDING_UNIT);
    }
    if (product.getPackSize() == null) {
      product.setPackSize(DEFAULT_PACK_SIZE);
    }
    if (product.getDosesPerDispensingUnit() == null) {
      product.setDosesPerDispensingUnit(DEFAULT_DOSES_DISPENSING_UNIT);
    }
    if (product.getFullSupply() == null) {
      product.setFullSupply(DEFAULT_FULL_SUPPLY);
    }
    if (product.getTracer() == null) {
      product.setTracer(DEFAULT_TRACER);
    }
    if (product.getRoundToZero() == null) {
      product.setRoundToZero(DEFAULT_ROUND_TO_ZERO);
    }
    if (product.getPackRoundingThreshold() == null) {
      product.setPackRoundingThreshold(DEFAULT_PACK_ROUNDING_THRESHOLD);
    }
    product.setIsKit(!product.getKitProductList().isEmpty());
    product.setActive(true);
  }

  public List<ProductResponse> getLatestProductsAfterUpdatedTime(Date afterUpdatedTime, Long userId) {

    List<Product> latestProducts = getLatestProducts(afterUpdatedTime);

    List<String> allSupportedPrograms = getSupportedProgramsByFacility(userId);

    return prepareProductsBasedOnFacilitySupportedPrograms(latestProducts, allSupportedPrograms);
  }

  private List<Product> getLatestProducts(Date afterUpdatedTime) {

    if(afterUpdatedTime == null) {
      return productService.getAllProducts();
    } else {
      return productService.getProductsAfterUpdatedDate(afterUpdatedTime);
    }
  }

  private List<String> getSupportedProgramsByFacility(Long userId) {
    User user = userService.getById(userId);
    Long facilityId = user.getFacilityId();

    List<ProgramSupported> programSupportedList = programSupportedService.getAllByFacilityId(facilityId);
    return FluentIterable.from(programSupportedList).transform(new Function<ProgramSupported, String>() {
      @Override
      public String apply(ProgramSupported programSupported) {
        return programSupported.getProgram().getCode();
      }
    }).toList();
  }

  private List<ProductResponse> prepareProductsBasedOnFacilitySupportedPrograms(List<Product> latestProducts, final List<String> programs) {
    List<ProductResponse> productResponseList = new ArrayList<>();

    for (Product product : latestProducts) {

      List<String> programCodes = FluentIterable.from(programProductSevice.getByProductCode(product.getCode())).filter(new Predicate<ProgramProduct>() {
        @Override
        public boolean apply(ProgramProduct programProduct) {
          return programs.contains(programProduct.getProgram().getCode());
        }
      }).transform(new Function<ProgramProduct, String>() {
        @Override
        public String apply(ProgramProduct programProduct) {
          return programProduct.getProgram().getCode();
        }
      }).toList();

      if (!programCodes.isEmpty()) {
        productResponseList.add(new ProductResponse(product, programCodes));
      }
    }
    return productResponseList;
  }

}
