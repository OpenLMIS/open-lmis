package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductPriceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ProgramProductRepository {

  private ProgramRepository programRepository;
  private ProductMapper productMapper;
  private ProductRepository productRepository;
  private ProgramProductMapper programProductMapper;
  private ProgramProductPriceMapper programProductPriceMapper;


  @Autowired
  public ProgramProductRepository(ProgramRepository programRepository, ProductMapper productMapper, ProgramProductMapper programProductMapper,
                                  ProductRepository productRepository, ProgramProductPriceMapper programProductPriceMapper) {
    this.programProductMapper = programProductMapper;
    this.programRepository = programRepository;
    this.productMapper = productMapper;
    this.productRepository = productRepository;
    this.programProductPriceMapper = programProductPriceMapper;
  }



  public void insert(ProgramProduct programProduct) {
    programProduct.getProgram().setId(programRepository.getIdByCode(programProduct.getProgram().getCode()));

    validateProductCode(programProduct.getProduct().getCode());

    try {
      programProductMapper.insert(programProduct);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("Duplicate entry for Product Code and Program Code combination found");
    }
  }

  public Integer getIdByProgramIdAndProductId(Integer programId, Integer productId){
    Integer programProductId = programProductMapper.getIdByProgramAndProductId(programId, productId);

    if(programProductId == null)
      throw new DataException("programProduct.product.program.invalid");

    return programProductId;
  }

  private void validateProductCode(String code) {
    if (code == null || code.isEmpty() || productMapper.getIdByCode(code) == null) {
      throw new DataException("Invalid Product Code");
    }
  }

  public void updateCurrentPrice(ProgramProduct programProduct) {
    programProduct.getProgram().setId(programRepository.getIdByCode(programProduct.getProgram().getCode()));
    programProduct.getProduct().setId(productRepository.getIdByCode(programProduct.getProduct().getCode()));
    programProductMapper.updateCurrentPrice(programProduct);
  }

  public void updatePriceHistory(ProgramProductPrice programProductPrice) {
    programProductPriceMapper.closeLastActivePrice(programProductPrice);
    programProductPriceMapper.insertNewCurrentPrice(programProductPrice);
  }
}
