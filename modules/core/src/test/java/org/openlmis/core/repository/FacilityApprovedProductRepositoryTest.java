package org.openlmis.core.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityApprovedProductMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.repository.FacilityApprovedProductRepository.FACILITY_APPROVED_PRODUCT_DUPLICATE;
import static org.openlmis.core.repository.FacilityApprovedProductRepository.FACILITY_TYPE_DOES_NOT_EXIST;

@RunWith(MockitoJUnitRunner.class)
public class FacilityApprovedProductRepositoryTest {

  @Rule
  public ExpectedException expectedException = none();

  @Mock
  private FacilityApprovedProductMapper facilityApprovedProductMapper;

  @Test
  public void shouldInsertAFacilitySupportedProduct() {
    FacilityApprovedProductRepository facilityApprovedProductRepository = new FacilityApprovedProductRepository(facilityApprovedProductMapper);
    FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct();

    facilityApprovedProductRepository.insert(facilityApprovedProduct);
    verify(facilityApprovedProductMapper).insert(facilityApprovedProduct);
  }

  @Test
  public void shouldThrowExceptionWhenMapperFailsWhenDuplicateFacilityProgramProductIsInserted() throws Exception {
    FacilityApprovedProductRepository facilityApprovedProductRepository = new FacilityApprovedProductRepository(facilityApprovedProductMapper);
    FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct();

    doThrow(DuplicateKeyException.class).when(facilityApprovedProductMapper).insert(facilityApprovedProduct);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(FACILITY_APPROVED_PRODUCT_DUPLICATE);

    facilityApprovedProductRepository.insert(facilityApprovedProduct);
  }

  @Test
  public void shouldThrowExceptionsWhenFacilityCodeDoesNotExist() throws Exception {
    FacilityApprovedProductRepository facilityApprovedProductRepository = new FacilityApprovedProductRepository(facilityApprovedProductMapper);
    FacilityApprovedProduct facilityApprovedProduct = new FacilityApprovedProduct();

    doThrow(DataIntegrityViolationException.class).when(facilityApprovedProductMapper).insert(facilityApprovedProduct);

    expectedException.expect(DataException.class);
    expectedException.expectMessage(FACILITY_TYPE_DOES_NOT_EXIST);

    facilityApprovedProductRepository.insert(facilityApprovedProduct);
  }
}
