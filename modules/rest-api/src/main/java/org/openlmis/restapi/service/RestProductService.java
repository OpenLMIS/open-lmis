package org.openlmis.restapi.service;

import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
  private ProductRepository productRepository;

  public Product buildAndSave(Product product) {
    productRepository.insert(buildKit(product));
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
}
