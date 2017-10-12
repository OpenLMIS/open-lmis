package org.openlmis.programs.domain.malaria.validators;

import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.programs.domain.malaria.validators.annotations.ValidateProduct;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProductExistsValidator implements ConstraintValidator<ValidateProduct, Product> {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void initialize(ValidateProduct constraintAnnotation) {

    }

    @Override
    public boolean isValid(Product product, ConstraintValidatorContext context) {
        Product existingProduct = productRepository.getByCode(product.getCode());
        return existingProduct != null;
    }
}
