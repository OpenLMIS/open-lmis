/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.vaccine.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.dto.ProductDoseDTO;
import org.openlmis.vaccine.dto.VaccineServiceConfigDTO;
import org.openlmis.vaccine.repository.ProductDoseRepository;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineProductDoseServiceTest {

  @Mock
  private ProductDoseRepository repository;

  @Mock
  private ProgramProductRepository programProductRepository;

  @InjectMocks
  private VaccineProductDoseService service;

  @Test
  public void shouldGetProductDoseForProgram() throws Exception {
    when(programProductRepository.getActiveByProgram(2L)).thenReturn(new ArrayList<ProgramProduct>());
    when(repository.getAllDoses()).thenReturn(null);

    VaccineServiceConfigDTO dto  = service.getProductDoseForProgram(2L);

    verify(programProductRepository).getActiveByProgram(2L);
    verify(repository, never()).getDosesForProduct(anyLong(), anyLong());
    verify(repository).getAllDoses();
  }

  @Test
  public void shouldGetProductDoseForProgramWithProducts() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    ProgramProduct programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));
    programProduct.setProduct(product);
    when(programProductRepository.getActiveByProgram(2L)).thenReturn(asList(programProduct));
    VaccineProductDose vpd = new VaccineProductDose();
    when(repository.getDosesForProduct(2L, product.getId())).thenReturn(asList(vpd));
    when(repository.getAllDoses()).thenReturn(null);

    VaccineServiceConfigDTO dto  = service.getProductDoseForProgram(2L);

    verify(programProductRepository).getActiveByProgram(2L);
    verify(repository, atLeastOnce()).getDosesForProduct(2L, product.getId());
    verify(repository).getAllDoses();

    assertThat(dto.getProtocols().get(0).getProductName(), is(product.getPrimaryName()));
  }

  @Test
  public void shouldGetForProgram() throws Exception {
    service.getForProgram(2L);
    verify(repository).getProgramProductDoses(2L);
  }

  @Test
  public void shouldSave() throws Exception {
    List<ProductDoseDTO> protocols = new ArrayList<>();
    ProductDoseDTO dto = new ProductDoseDTO();
    dto.setDoses(new ArrayList<VaccineProductDose>());
    VaccineProductDose dose = new VaccineProductDose();
    dose.setProgramId(1L);
    dto.getDoses().add(dose);

    protocols.add(dto);

    doNothing().when(repository).deleteAllByProgram(1L);
    service.save(protocols);
    verify(repository).deleteAllByProgram(1L);
    verify(repository).insert(any(VaccineProductDose.class));
  }
}