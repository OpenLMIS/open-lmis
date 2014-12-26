/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.FacilityTypeApprovedProduct;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.FacilityApprovedProductMapper;
import org.openlmis.db.categories.UnitTests;

import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityApprovedProductRepositoryTest {

  @Rule
  public ExpectedException expectedException = none();

  @Mock
  private FacilityApprovedProductMapper mapper;

  @InjectMocks
  private FacilityApprovedProductRepository repository;

  @Test
  public void shouldInsertAFacilitySupportedProduct() {
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = new FacilityTypeApprovedProduct();
    ProgramProduct programProduct = new ProgramProduct();
    programProduct.setId(1L);
    facilityTypeApprovedProduct.setProgramProduct(programProduct);
    facilityTypeApprovedProduct.setFacilityType(new FacilityType("warehouse"));

    when(mapper.getBy(1L, "warehouse")).thenReturn(null);

    repository.insert(facilityTypeApprovedProduct);
    verify(mapper).insert(facilityTypeApprovedProduct);
  }

  @Test
  public void shouldGetFullSupplyFacilityApprovedProducts() {
    repository.getFullSupplyProductsByFacilityAndProgram(5L, 8L);
    verify(mapper).getFullSupplyProductsBy(5L, 8L);
  }

  @Test
  public void shouldGetNonFullSupplyFacilityApprovedProducts() {
    repository.getNonFullSupplyProductsByFacilityAndProgram(5L, 8L);
    verify(mapper).getNonFullSupplyProductsBy(5L, 8L);
  }

  @Test
  public void shouldUpdateFacilityApprovedProductIfExists() throws Exception {
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = new FacilityTypeApprovedProduct();
    repository.update(facilityTypeApprovedProduct);
    verify(mapper).update(facilityTypeApprovedProduct);
  }

  @Test
  public void shouldGetFacilityApprovedProduct(){
    repository.get(2L);
    verify(mapper).get(2L);
  }
}
