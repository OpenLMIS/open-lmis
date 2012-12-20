package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ProductRepository {

  ProductMapper mapper;

  @Autowired
  public ProductRepository(ProductMapper mapper) {
    this.mapper = mapper;
  }

  public void insert(Product product) {
    try {
      validateAndSetDosageUnit(product);
      validateAndSetProductForm(product);
      mapper.insert(product);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("Duplicate Product Code found");
    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      String errorMessage = dataIntegrityViolationException.getMessage().toLowerCase();
      if (errorMessage.contains("foreign key") || errorMessage.contains("violates not-null constraint")) {
        throw new DataException("Missing/Invalid Reference data");
      } else {
        throw new DataException("Incorrect data length");
      }
    }
  }

  private void validateAndSetProductForm(Product product) {
    ProductForm form = product.getForm();
    if(form == null) return;

    String productFormCode = form.getCode();
    if (productFormCode == null || productFormCode.isEmpty()) return;

    Integer productFormId = mapper.getProductFormIdForCode(productFormCode);
    if (productFormId == null) throw new DataException("Invalid reference data 'Product Form'");

    form.setId(productFormId);
  }

  private void validateAndSetDosageUnit(Product product) {
    DosageUnit dosageUnit = product.getDosageUnit();
    if (dosageUnit == null) return;

    String dosageUnitCode = dosageUnit.getCode();
    if (dosageUnitCode == null || dosageUnitCode.isEmpty()) return;

    Integer dosageUnitId = mapper.getDosageUnitIdForCode(dosageUnitCode);
    if (dosageUnitId == null)
      throw new DataException("Invalid reference data 'Dosage Unit'");

    dosageUnit.setId(dosageUnitId);
  }

}
