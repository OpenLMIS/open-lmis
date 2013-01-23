package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductPriceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ProgramProductRepository {

  public static final String PROGRAM_PRODUCT_INVALID = "programProduct.product.program.invalid";
  private ProgramProductMapper mapper;
  private ProgramRepository programRepository;
  private ProductRepository productRepository;
  private ProgramProductPriceMapper programProductPriceMapper;


  @Autowired
  public ProgramProductRepository(ProgramRepository programRepository, ProgramProductMapper programProductMapper,
                                  ProductRepository productRepository, ProgramProductPriceMapper programProductPriceMapper) {
    this.mapper = programProductMapper;
    this.programRepository = programRepository;
    this.productRepository = productRepository;
    this.programProductPriceMapper = programProductPriceMapper;
  }


  public void insert(ProgramProduct programProduct) {
    programProduct.getProgram().setId(programRepository.getIdByCode(programProduct.getProgram().getCode()));

    validateProductCode(programProduct.getProduct().getCode());

    try {
      mapper.insert(programProduct);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("Duplicate entry for Product Code and Program Code combination found");
    }
  }

  public Integer getIdByProgramIdAndProductId(Integer programId, Integer productId) {
    Integer programProductId = mapper.getIdByProgramAndProductId(programId, productId);

    if (programProductId == null)
      throw new DataException(PROGRAM_PRODUCT_INVALID);

    return programProductId;
  }

  private void validateProductCode(String code) {
    if (code == null || code.isEmpty() || productRepository.getIdByCode(code) == null) {
      throw new DataException("Invalid Product Code");
    }
  }

  public void updateCurrentPrice(ProgramProduct programProduct) {
    mapper.updateCurrentPrice(programProduct);
  }

  public ProgramProduct getProgramProductByProgramAndProductCode(ProgramProduct programProduct) {
    return getByProgramIdAndProductId(programRepository.getIdByCode(programProduct.getProgram().getCode()),
        productRepository.getIdByCode(programProduct.getProduct().getCode()));
  }

  private ProgramProduct getByProgramIdAndProductId(Integer programId, Integer productId) {
    final ProgramProduct programProduct = mapper.getByProgramAndProductId(programId, productId);
    if (programProduct == null)
      throw new DataException(PROGRAM_PRODUCT_INVALID);

    return programProduct;
  }

  public void updatePriceHistory(ProgramProductPrice programProductPrice) {
    programProductPriceMapper.closeLastActivePrice(programProductPrice);
    programProductPriceMapper.insertNewCurrentPrice(programProductPrice);
  }
}
