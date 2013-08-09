/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductGroupMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@NoArgsConstructor
public class ProductRepository {

  ProductMapper mapper;

  ProductGroupMapper productGroupMapper;

  @Autowired
  public ProductRepository(ProductMapper mapper, ProductGroupMapper productGroupMapper) {
    this.mapper = mapper;
    this.productGroupMapper = productGroupMapper;
  }

  public void insert(Product product) {
    try {
      validateAndSetDosageUnit(product);
      validateAndSetProductForm(product);
      validateAndSetProductGroup(product);
      mapper.insert(product);
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

  public void update(Product product) {
    mapper.update(product);
  }

  private void validateAndSetProductGroup(Product product) {
    ProductGroup group = product.getProductGroup();
    if (group == null) return;

    String productGroupCode = group.getCode();
    if (productGroupCode == null || productGroupCode.isEmpty()) return;

    ProductGroup productGroup = productGroupMapper.getByCode(productGroupCode);
    if (productGroup == null) throw new DataException("error.reference.data.invalid.product.group");

    group.setId(productGroup.getId());
  }

  public Long getIdByCode(String code) {
    Long productCode = mapper.getIdByCode(code);

    if (productCode == null)
      throw new DataException("product.code.invalid");

    return productCode;
  }

  private void validateAndSetProductForm(Product product) {
    ProductForm form = product.getForm();
    if (form == null) return;

    String productFormCode = form.getCode();
    if (productFormCode == null || productFormCode.isEmpty()) return;

    Long productFormId = mapper.getProductFormIdForCode(productFormCode);
    if (productFormId == null) throw new DataException("error.reference.data.invalid.product.form");

    form.setId(productFormId);
  }

  private void validateAndSetDosageUnit(Product product) {
    DosageUnit dosageUnit = product.getDosageUnit();
    if (dosageUnit == null) return;

    String dosageUnitCode = dosageUnit.getCode();
    if (dosageUnitCode == null || dosageUnitCode.isEmpty()) return;

    Long dosageUnitId = mapper.getDosageUnitIdForCode(dosageUnitCode);
    if (dosageUnitId == null)
      throw new DataException("error.reference.data.invalid.dosage.unit");

    dosageUnit.setId(dosageUnitId);
  }

  public Product getByCode(String code) {
    return mapper.getByCode(code);
  }

  public void update(Product product) {
    mapper.update(product);
  }

  public Long getDosageUnitIdForCode(String code) {
    return mapper.getDosageUnitIdForCode(code);
  }

  public Long getProductFormIdForCode(String code) {
    return mapper.getProductFormIdForCode(code);
  }

  public boolean isActive(String code) {
    return mapper.isActive(code);
  }
}
