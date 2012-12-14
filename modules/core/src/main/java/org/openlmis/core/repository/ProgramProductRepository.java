package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class ProgramProductRepository {

    private ProgramMapper programMapper;
    private ProductMapper productMapper;
    private ProgramProductMapper programProductMapper;

    @Autowired
    public ProgramProductRepository(ProgramProductMapper mapper, ProgramMapper programMapper, ProductMapper productMapper) {
        this.programProductMapper = mapper;
        this.programMapper = programMapper;
        this.productMapper = productMapper;
    }

    public List<ProgramProduct> getByFacilityAndProgram(Integer facilityId, String programCode) {
        return programProductMapper.getFullSupplyProductsByFacilityAndProgram(facilityId, programCode);
    }

    public void insert(ProgramProduct programProduct) {
        validateProgramCode(programProduct.getProgram().getCode());
        validateProductCode(programProduct.getProduct().getCode());
        int id = programProductMapper.insert(programProduct);
        programProduct.setId(id);
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
