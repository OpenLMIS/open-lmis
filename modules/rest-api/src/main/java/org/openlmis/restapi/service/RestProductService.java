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
import org.openlmis.restapi.domain.ProgramProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.openlmis.restapi.config.FilterProductConfig.*;

@Service
public class RestProductService {

  @Autowired
  ArchivedProductService archivedProductService;
  @Autowired
  StaticReferenceDataService staticReferenceDataService;
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
    productService.save(buildProduct(product), true);
    return product;
  }

  private Product buildProduct(Product product) {
    product.setDefaultValuesForMandatoryFieldsIfNotExist();

    if (product.getKitProductList() != null) {
      for (KitProduct kitProduct : product.getKitProductList()) {
        kitProduct.setKitCode(product.getCode());
      }
    }
    return product;
  }

  public List<ProductResponse> getLatestProductsAfterUpdatedTime(Date afterUpdatedTime, String versionCode, Long userId) {
    Long facilityId = userService.getById(userId).getFacilityId();

    List<Product> latestProducts = getLatestProducts(afterUpdatedTime, versionCode, facilityId);

    List<ProgramProduct> latestProgramProduct = programProductSevice.getLatestUpdatedProgramProduct(afterUpdatedTime);
    for (ProgramProduct programProduct : latestProgramProduct) {
      if (!isContainedInLatestProduct(latestProducts, programProduct)) {
        latestProducts.add(programProduct.getProduct());
      }
    }

    List<String> allSupportedPrograms = getSupportedProgramsByFacility(facilityId);

    return prepareProductsBasedOnFacilitySupportedPrograms(latestProducts, allSupportedPrograms);
  }

  public List<ProductResponse> getTemp86KitChangeProducts(Long userId) {
    Long facilityId = userService.getById(userId).getFacilityId();
    List<Product> kitChangesProducts = getKitChangeProducts();
    List<String> allSupportedPrograms = getSupportedProgramsByFacility(facilityId);

    return prepareProductsBasedOnFacilitySupportedPrograms(kitChangesProducts, allSupportedPrograms);
  }

  private List<Product> getKitChangeProducts(String[] productList, boolean isRightProduct) {
    List<Product> changeProducts = new ArrayList<>();

    List<String> kitProductsCodes = Arrays.asList(productList);
    for (String kitProductCode : kitProductsCodes) {
      Product product = productService.getProductByCode(kitProductCode);
      if (product == null) continue;
      if (!isRightProduct) product.setActive(false);

      product.setIsKit(isRightProduct);
      changeProducts.add(product);
    }
    return changeProducts;
  }

  private List<Product> getKitChangeProducts() {
    List<Product> lists = new ArrayList<>();
    List<Product> wrongKitProducts = getKitChangeProducts(WRONG_KIT_PRODUCTS, false);
    List<Product> rightKitProducts = getKitChangeProducts(RIGHT_KIT_PRODUCTS, true);
    lists.addAll(wrongKitProducts);
    lists.addAll(rightKitProducts);
    return lists;
  }

  private boolean isContainedInLatestProduct(List<Product> latestProducts, ProgramProduct programProduct) {
    boolean isContainedInLatestProduct = false;
    for (Product latestProduct : latestProducts) {
      if (latestProduct.getCode().equals(programProduct.getProduct().getCode()))
        isContainedInLatestProduct = true;
    }
    return isContainedInLatestProduct;
  }

  private List<Product> getLatestProducts(Date afterUpdatedTime, String versionCode, Long facilityId) {

    if (afterUpdatedTime == null) {

      final List<String> archivedProductCodes = archivedProductService.getAllArchivedProducts(facilityId);

      return FluentIterable.from(productService.getAllProducts()).transform(new Function<Product, Product>() {
        @Override
        public Product apply(Product product) {
          product.setArchived(archivedProductCodes.contains(product.getCode()));
          return product;
        }
      }).toList();
    } else {
      List<Product> products = productService.getProductsAfterUpdatedDate(afterUpdatedTime);
      if (isVersionCodeOverThanFilterThresholdVersion(versionCode)) {
        return filterProductFromGetProductsAfterUpdatedDate(products);
      }
      return products;
    }
  }

  private List<Product> filterProductFromGetProductsAfterUpdatedDate(List<Product> products) {
    return new ArrayList<Product>(FluentIterable.from(products).transform(new Function<Product, Product>() {
      @Override
      public Product apply(Product product) {
        if (WRONG_KIT_PRODUCTS_SET.contains(product.getCode())) {
          product.setArchived(false);
          product.setIsKit(false);
        }
        if (RIGHT_KIT_PRODUCTS_SET.contains(product.getCode())) {
          product.setIsKit(true);
        }
        return product;
      }
    }).toList());
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

    for (final Product product : latestProducts) {
      List<ProgramProduct> productPrograms = programProductSevice.getByProductCode(product.getCode());
      List<ProgramProductResponse> programsResponse = FluentIterable.from(productPrograms).filter(new Predicate<ProgramProduct>() {
        @Override
        public boolean apply(ProgramProduct programProduct) {
          return programs.contains(programProduct.getProgram().getCode());
        }
      }).transform(new Function<ProgramProduct, ProgramProductResponse>() {
        @Override
        public ProgramProductResponse apply(ProgramProduct programProduct) {
          return new ProgramProductResponse(programProduct.getProgram().getCode(), product.getCode(),
                  programProduct.getActive(), programProduct.getProductCategory().getName());
        }
      }).toList();

      List<String> programCodes = FluentIterable.from(programProductSevice.getActiveProgramCodesByProductCode(product.getCode())).filter(new Predicate<String>() {
        @Override
        public boolean apply(String programProductCode) {
          return programs.contains(programProductCode);
        }
      }).toList();

      if (!programsResponse.isEmpty()) {
        productResponseList.add(new ProductResponse(product, programCodes, programsResponse));
      }
    }
    return productResponseList;
  }

}
