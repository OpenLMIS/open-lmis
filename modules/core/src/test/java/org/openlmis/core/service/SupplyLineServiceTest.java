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
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.SupplyLineBuilder;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.SupplyLineRepository;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class SupplyLineServiceTest {

  @Mock
  private SupplyLineRepository supplyLineRepository;

  @Mock
  private ProgramRepository programRepository;

  @Mock
  private FacilityRepository facilityRepository;

  @Mock
  private SupervisoryNodeRepository supervisoryNodeRepository;

  @InjectMocks
  private SupplyLineService supplyLineService;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  private SupplyLine supplyLine;

  @Before
  public void setUp() throws Exception {
    supplyLine = make(a(SupplyLineBuilder.defaultSupplyLine));
    supplyLineService = new SupplyLineService(supplyLineRepository, programRepository, facilityRepository, supervisoryNodeRepository);
  }

  @Test
  public void shouldReturnSupplyLineBySupervisoryNodeAndProgram() {
    Program program = new Program();
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    SupplyLine supplyLine = new SupplyLine();
    when(supplyLineRepository.getSupplyLineBy(supervisoryNode, program)).thenReturn(supplyLine);

    SupplyLine returnedSupplyLine = supplyLineService.getSupplyLineBy(supervisoryNode, program);

    verify(supplyLineRepository).getSupplyLineBy(supervisoryNode, program);
    assertThat(returnedSupplyLine, is(supplyLine));
  }

  @Test
  public void shouldThrowErrorIfSupervisoryNodeIsNotTheParentNode() {
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(supplyLine.getSupervisoryNode().getId())).thenThrow(new DataException("Supervising Node is not the Top node"));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Supervising Node is not the Top node");
    supplyLineService.save(supplyLine);
  }

  @Test
  public void shouldInsertSupplyLineIfDoesNotExist() throws Exception {
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1L);
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1L);
    when(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode())).thenReturn(1L);
    supplyLine.getSupervisoryNode().setId(1L);
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(1L)).thenReturn(null);

    supplyLine.setId(null);

    supplyLineService.save(supplyLine);

    verify(supplyLineRepository).insert(supplyLine);
  }

  @Test
  public void shouldUpdateSupplyLineIfExists() throws Exception {
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1L);
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1L);
    when(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode())).thenReturn(1L);
    supplyLine.getSupervisoryNode().setId(1L);
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(1L)).thenReturn(null);

    supplyLine.setId(1L);

    supplyLineService.save(supplyLine);

    verify(supplyLineRepository).update(supplyLine);

  }

  @Test
  public void shouldGetExistingSupplyLine() throws Exception {
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1L);
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1L);
    when(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode())).thenReturn(1L);
    supplyLine.getSupervisoryNode().setId(1L);
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(1L)).thenReturn(null);
    // this is to get past the validation that checks for unique supply lines for a given program.
    // the supply line will not be saved otherwise.
    when(supplyLineRepository.getSupplyLineBy(supplyLine.getSupervisoryNode(), supplyLine.getProgram())).thenReturn(null);
    supplyLineService.save(supplyLine);

    when(supplyLineRepository.getSupplyLineBy(supplyLine.getSupervisoryNode(), supplyLine.getProgram())).thenReturn(supplyLine);
    SupplyLine result = supplyLineService.getExisting(supplyLine);

    assertThat(result, is(supplyLine));
  }

  @Test
  public void shouldGetSupplyLinebyId() throws Exception {
    SupplyLine expectedSupplyLine = new SupplyLine();
    when(supplyLineRepository.getById(3L)).thenReturn(expectedSupplyLine);

    SupplyLine returnedSupplyLine = supplyLineService.getById(3L);

    assertThat(returnedSupplyLine, is(expectedSupplyLine));
    verify(supplyLineRepository).getById(3L);
  }
}
