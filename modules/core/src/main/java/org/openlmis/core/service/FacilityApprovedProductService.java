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

  @Autowired
  public FacilityApprovedProductService(FacilityApprovedProductRepository repository, ProgramService programService, ProductService productService) {
    this.repository = repository;
    this.programService = programService;
    this.productService = productService;
  }

  public List<FacilityApprovedProduct> getByFacilityAndProgram(Integer facilityId, Integer programId) {
    return repository.getByFacilityAndProgram(facilityId, programId);
  }

  public void save(FacilityApprovedProduct facilityApprovedProduct) {
    Integer programId = programService.getIdForCode(facilityApprovedProduct.getProgramProduct().getProgram().getCode());
    Integer productId = productService.getIdForCode(facilityApprovedProduct.getProgramProduct().getProduct().getCode());

    facilityApprovedProduct.getProgramProduct().getProgram().setId(programId);
    facilityApprovedProduct.getProgramProduct().getProduct().setId(productId);

    repository.insert(facilityApprovedProduct);
  }
}
