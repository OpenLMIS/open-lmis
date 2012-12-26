package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ProgramProductRepository {

    private ProgramRepository programRepository;
    private ProductMapper productMapper;
    private ProgramProductMapper programProductMapper;

    @Autowired
    public ProgramProductRepository(ProgramRepository programRepository, ProductMapper productMapper, ProgramProductMapper programProductMapper) {
        this.programProductMapper = programProductMapper;
        this.programRepository = programRepository;
        this.productMapper = productMapper;
    }

    public void insert(ProgramProduct programProduct) {
        programProduct.getProgram().setId(programRepository.getIdForCode(programProduct.getProduct().getCode()));
        validateProductCode(programProduct.getProduct().getCode());

        try {
            programProductMapper.insert(programProduct);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new DataException("Duplicate entry for Product Code and Program Code combination found");
        }
    }

    private void validateProductCode(String code) {
        if (code == null || code.isEmpty() || productMapper.getIdByCode(code) == null) {
            throw new DataException("Invalid Product Code");
        }
    }
}
