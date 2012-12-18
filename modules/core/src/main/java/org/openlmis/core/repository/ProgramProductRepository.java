package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ProgramProductRepository {

    private ProgramMapper programMapper;
    private ProductMapper productMapper;
    private ProgramProductMapper programProductMapper;

    @Autowired
    public ProgramProductRepository(ProgramMapper programMapper, ProductMapper productMapper, ProgramProductMapper programProductMapper) {
        this.programProductMapper = programProductMapper;
        this.programMapper = programMapper;
        this.productMapper = productMapper;
    }

    public void insert(ProgramProduct programProduct) {
        validateProgramCode(programProduct.getProgram().getCode());
        validateProductCode(programProduct.getProduct().getCode());
        try {
            int id = programProductMapper.insert(programProduct);
            programProduct.setId(id);
        }catch (DuplicateKeyException duplicateKeyException) {
            throw new RuntimeException("Duplicate entry for Product Code and Program Code combination found");
        }
    }

    private void validateProductCode(String code) {
        if (code == null || code.isEmpty() || productMapper.getIdByCode(code) == null) {
            throw new RuntimeException("Invalid Product Code");
        }
    }

    private void validateProgramCode(String code) {
        if (code == null || code.isEmpty() || programMapper.getIdByCode(code) == null) {
            throw new RuntimeException("Invalid Program Code");
        }
    }
}
