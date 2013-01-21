package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.repository.FacilityApprovedProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@NoArgsConstructor
public class FacilityApprovedProductService {

  private FacilityApprovedProductRepository repository;
  private ProgramService programService;
  private ProductService productService;
  private ProgramProductService programProductService;

  @Autowired
  public FacilityApprovedProductService(FacilityApprovedProductRepository repository, ProgramService programService, ProductService productService, ProgramProductService programProductService) {
    this.repository = repository;
    this.programService = programService;
    this.productService = productService;
    this.programProductService = programProductService;
  }

  public List<FacilityApprovedProduct> getFullSupplyFacilityApprovedProductByFacilityAndProgram(Integer facilityId, Integer programId) {
    return repository.getFullSupplyProductsByFacilityAndProgram(facilityId, programId);
  }

  public List<FacilityApprovedProduct> getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(Integer facilityId, Integer programId){
    return repository.getNonFullSupplyProductsByFacilityAndProgram(facilityId, programId);
  }

  public void save(FacilityApprovedProduct facilityApprovedProduct) {
    Integer programId = programService.getIdForCode(facilityApprovedProduct.getProgramProduct().getProgram().getCode());
    Integer productId = productService.getIdForCode(facilityApprovedProduct.getProgramProduct().getProduct().getCode());
    Integer programProductId = programProductService.getIdByProgramIdAndProductId(programId, productId);

    facilityApprovedProduct.getProgramProduct().getProgram().setId(programId);
    facilityApprovedProduct.getProgramProduct().getProduct().setId(productId);
    facilityApprovedProduct.getProgramProduct().setId(programProductId);

    repository.insert(facilityApprovedProduct);
  }
}
