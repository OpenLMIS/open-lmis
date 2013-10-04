/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.repository.RegimenRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegimenServiceTest {

  @Mock
  RegimenRepository repository;

  @Mock
  ProgramService programService;

  @InjectMocks
  RegimenService service;

  List<Regimen> regimens;

  final Regimen regimen = new Regimen();

  @Before
  public void setUp() throws Exception {
    regimens = new ArrayList<Regimen>() {{
      add(regimen);
    }};
  }

  @Test
  public void shouldSaveARegimens() {
    service.save(regimens, 1L);
    verify(repository).save(regimens, 1L);
  }

  @Test
  public void shouldGetRegimensByProgram() {
    List<Regimen> expectedRegimens = new ArrayList<>();
    Long programId = 1l;
    when(repository.getByProgram(programId)).thenReturn(expectedRegimens);

    List<Regimen> regimenList = service.getByProgram(programId);

    assertThat(regimenList, is(expectedRegimens));
    verify(repository).getByProgram(programId);
  }

  @Test
  public void shouldGetAllRegimenCategories() {
    List<RegimenCategory> expectedRegimenCategories = new ArrayList<>();
    when(repository.getAllRegimenCategories()).thenReturn(expectedRegimenCategories);

    List<RegimenCategory> regimenCategories = service.getAllRegimenCategories();

    assertThat(regimenCategories, is(expectedRegimenCategories));
    verify(repository).getAllRegimenCategories();
  }

}
