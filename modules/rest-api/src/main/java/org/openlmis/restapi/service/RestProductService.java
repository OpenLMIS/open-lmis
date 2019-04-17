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

@Service
public class RestProductService {

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

  public List<ProductResponse> getTemp86KitChangeProducts(Long userId){
    Long facilityId = userService.getById(userId).getFacilityId();
    List<Product> kitChangesProducts = getKitChangeProducts();
    List<String> allSupportedPrograms = getSupportedProgramsByFacility(facilityId);

    return prepareProductsBasedOnFacilitySupportedPrograms(kitChangesProducts, allSupportedPrograms);
  }

  private List<Product> getKitChangeProducts() {
    List<Product> results = new ArrayList<>();

    List<String> wrongKitProductCodes = Arrays.asList(new String[]{"SCOD10","SCOD10-AL","SCOD12","SCOD12-AL"});
    for (String wrongKitProductCode : wrongKitProductCodes) {
      Product product = productService.getProductByCode(wrongKitProductCode);
      if(product!=null){
        product.setActive(false);
        product.setIsKit(false);
        results.add(product);
      }
    }

    List<String> rightKitProductCodes = Arrays.asList(new String[]{"26A01","26B01","26A02","26B02"});
    for (String rightKitProductCode : rightKitProductCodes) {
      Product product = productService.getProductByCode(rightKitProductCode);
      if(product!=null){
        product.setIsKit(true);
        results.add(product);
      }
    }

    return results;
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
