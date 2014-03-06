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
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductGroupMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

/**
 * This is Repository class for Product related database operations.
 */

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
