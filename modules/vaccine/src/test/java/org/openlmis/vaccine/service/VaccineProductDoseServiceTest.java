/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.dto.ProductDoseProtocolDTO;
import org.openlmis.vaccine.repository.ProductDoseRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


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

  }

  @Test
  public void shouldGetForProgram() throws Exception {
    service.getForProgram(2L);
    verify(repository).getProgramProductDoses(2L);
  }

  @Test
  public void shouldSave() throws Exception {
    List<ProductDoseProtocolDTO> protocols = new ArrayList<>();
    ProductDoseProtocolDTO dto = new ProductDoseProtocolDTO();
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