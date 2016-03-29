package org.openlmis.restapi.service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.service.*;
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

  @Autowired
  ArchivedProductService archivedProductService;

  @Autowired
  StaticReferenceDataService staticReferenceDataService;

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
    Long facilityId = userService.getById(userId).getFacilityId();

    List<Product> latestProducts = getLatestProducts(afterUpdatedTime,facilityId);

    List<ProgramProduct> latestProgramProduct = programProductSevice.getLatestUpdatedProgramProduct(afterUpdatedTime);
    for (ProgramProduct programProduct : latestProgramProduct) {
      if (!isContainedInLatestProduct(latestProducts, programProduct)){
        latestProducts.add(programProduct.getProduct());
      }
    }

    List<String> allSupportedPrograms = getSupportedProgramsByFacility(facilityId);

    return prepareProductsBasedOnFacilitySupportedPrograms(latestProducts, allSupportedPrograms);
  }

  private boolean isContainedInLatestProduct(List<Product> latestProducts, ProgramProduct programProduct) {
    boolean isContainedInLatestProduct = false;
    for (Product latestProduct : latestProducts) {
      if (latestProduct.getCode().equals(programProduct.getProduct().getCode()))
        isContainedInLatestProduct = true;
    }
    return isContainedInLatestProduct;
  }

  private List<Product> getLatestProducts(Date afterUpdatedTime, Long facilityId) {

    if(afterUpdatedTime == null) {

      final List<String> archivedProductCodes = archivedProductService.getAllArchivedProducts(facilityId);

      return FluentIterable.from(productService.getAllProducts()).transform(new Function<Product, Product>() {
        @Override
        public Product apply(Product product) {
          product.setArchived(archivedProductCodes.contains(product.getCode()));
          return product;
        }
      }).toList();
    } else {
      return productService.getProductsAfterUpdatedDate(afterUpdatedTime);
    }
  }

  private List<String> getSupportedProgramsByFacility(Long facilityId) {
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

      List<String> programCodes = FluentIterable.from(programProductSevice.getActiveProgramCodesByProductCode(product.getCode())).filter(new Predicate<String>() {
        @Override
        public boolean apply(String programProductCode) {
          return programs.contains(programProductCode);
        }
      }).toList();

      if (!programCodes.isEmpty()) {
        productResponseList.add(new ProductResponse(product, programCodes));
      }
    }
    return productResponseList;
  }

}
