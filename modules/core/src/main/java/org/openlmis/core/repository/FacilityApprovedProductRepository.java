package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityApprovedProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Boolean.*;

@Component
@NoArgsConstructor
public class FacilityApprovedProductRepository {

  public static final String FACILITY_APPROVED_PRODUCT_DUPLICATE = "facilityApprovedProduct.duplicate.found";
  public static final String FACILITY_TYPE_DOES_NOT_EXIST = "facilityType.invalid";

  private FacilityApprovedProductMapper facilityApprovedProductMapper;

  @Autowired
  public FacilityApprovedProductRepository(FacilityApprovedProductMapper facilityApprovedProductMapper) {
    this.facilityApprovedProductMapper = facilityApprovedProductMapper;
  }

  public List<FacilityApprovedProduct> getFullSupplyProductsByFacilityAndProgram(Integer facilityId, Integer programId) {
    return facilityApprovedProductMapper.getProductsByFacilityAndProgram(facilityId, programId, TRUE);
  }

  public List<FacilityApprovedProduct> getNonFullSupplyProductsByFacilityAndProgram(Integer facilityId, Integer programId) {
    return facilityApprovedProductMapper.getProductsByFacilityAndProgram(facilityId, programId, FALSE);
  }

  public void insert(FacilityApprovedProduct facilityApprovedProduct) {
    try {
      facilityApprovedProductMapper.insert(facilityApprovedProduct);
    } catch (DuplicateKeyException e) {
      throw new DataException(FACILITY_APPROVED_PRODUCT_DUPLICATE);
    } catch (DataIntegrityViolationException e) {
        throw new DataException(FACILITY_TYPE_DOES_NOT_EXIST);
    }
  }
}
