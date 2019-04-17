/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.DosageUnitMapper;
import org.openlmis.core.repository.mapper.ProductGroupMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * ProductRepository is Repository class for Product related database operations.
 */

@Component
@NoArgsConstructor
public class ProductRepository {

  @Autowired
  ProductMapper mapper;

  @Autowired
  ProductGroupMapper productGroupMapper;

  @Autowired
  DosageUnitMapper dosageUnitMapper;

  public void insert(Product product) {
    try {
      mapper.insert(product);
      if (!product.getKitProductList().isEmpty()) {
        for (KitProduct kitProduct : product.getKitProductList()) {
          mapper.insertKitProduct(kitProduct);
        }
      }
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("error.duplicate.product.code");
    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      String errorMessage = dataIntegrityViolationException.getMessage().toLowerCase();
      if (errorMessage.contains("foreign key") || errorMessage.contains("violates not-null constraint")) {
        throw new DataException("error.reference.data.missing");
      } else {
        throw new DataException("error.incorrect.length");
      }
    }
  }

  public void update(Product product, boolean updateKitProductList) {
    try {
      mapper.update(product);

      if (updateKitProductList) {
        updateKitProductList(product);
      }

    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("error.duplicate.product.code");
    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      String errorMessage = dataIntegrityViolationException.getMessage().toLowerCase();
      if (errorMessage.contains("foreign key") || errorMessage.contains("violates not-null constraint")) {
        throw new DataException("error.reference.data.missing");
      } else {
        throw new DataException("error.incorrect.length");
      }
    }
  }

  private void updateKitProductList(Product product) {
    List<KitProduct> oldKitProductList = mapper.getKitProductsByProductCode(product.getCode());
    if (CollectionUtils.isNotEmpty(oldKitProductList)) {
      mapper.clearKitProductsByProductCode(product.getCode());
    }


    List<KitProduct> newKitProductList = product.getKitProductList();
    if (CollectionUtils.isNotEmpty(newKitProductList)) {
      for (KitProduct kitProduct : product.getKitProductList()) {
        mapper.insertKitProduct(kitProduct);
      }
    }

    updateLastModifieddate(oldKitProductList,newKitProductList);
  }

  public void updateLastModifieddate(List<KitProduct> oldKitProductList,List<KitProduct> newKitProductList){
    List<KitProduct> subKitList  = ListUtils.union(ListUtils.subtract(oldKitProductList,newKitProductList),ListUtils.subtract(newKitProductList,oldKitProductList));
    Set<String> kitCodes  = new HashSet<>();
    for(KitProduct kitProduct:subKitList){
      kitCodes.add(kitProduct.getKitCode());
    }

    for(String kitCode : kitCodes){
      mapper.updateModifieddateByCode(kitCode);
    }
  }

  public Long getIdByCode(String code) {
    Long productId = mapper.getIdByCode(code);
    if (productId == null)
      throw new DataException("product.code.invalid");
    return productId;
  }

  public Product getByCode(String code) {
    return mapper.getByCode(code);
  }

  public DosageUnit getDosageUnitByCode(String code) {
    return mapper.getDosageUnitByCode(code);
  }

  public boolean isActive(String code) {
    return mapper.isActive(code);
  }

  public Integer getTotalSearchResultCount(String searchParam) {
    return mapper.getTotalSearchResultCount(searchParam);
  }

  public List<DosageUnit> getAllDosageUnits() {
    return dosageUnitMapper.getAll();
  }

  public Product getById(Long id) {
    return mapper.getById(id);
  }

  public List<Product> getAllProducts() {
    return mapper.list();
  }

  public List<Product> getProductsAfterUpdatedTime(Date date) {
    return mapper.listProductsAfterUpdatedTime(date);
  }

  public Product getProductByCode(String productCode){
    return mapper.getProductByCode(productCode);
  }


  public List<Product> getProductCodeForUpdateStatus() {
    return mapper.getAllProductWithCode();
  }

  public void updateProductStatus(boolean active, long id) {
    mapper.updateProductActiveStatus(active, id);
  }

  public List<KitProduct> getKitProductsByProductCode(String productCode){
    return mapper.getKitProductsByProductCode(productCode);
  }
}
