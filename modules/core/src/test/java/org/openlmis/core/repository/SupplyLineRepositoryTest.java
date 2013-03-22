/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.builder.SupplyLineBuilder;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.SupplyLineMapper;
import org.springframework.dao.DuplicateKeyException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class SupplyLineRepositoryTest {
  @Mock
  private SupplyLineMapper supplyLineMapper;
  @Mock
  private SupervisoryNodeRepository supervisoryNodeRepository;
  @Mock
  private ProgramRepository programRepository;
  @Mock
  private FacilityRepository facilityRepository;

  private SupplyLineRepository supplyLineRepository;
  private SupplyLine supplyLine;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() {
    supplyLineRepository = new SupplyLineRepository(supplyLineMapper, supervisoryNodeRepository, programRepository, facilityRepository);
    supplyLine = make(a(SupplyLineBuilder.defaultSupplyLine));
  }

  @Test
  public void shouldInsertSupplyLine() {
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1);
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1);
    when(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode())).thenReturn(1);
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(1)).thenReturn(null);

    supplyLineRepository.insert(supplyLine);
    verify(supplyLineMapper).insert(supplyLine);
  }

  @Test
  public void shouldThrowExceptionForDuplicateSupplyLines() {
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1);
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1);
    when(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode())).thenReturn(1);
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(1)).thenReturn(null);
    doThrow(new DuplicateKeyException("Duplicate entry for Supply Line found")).when(supplyLineMapper).insert(supplyLine);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate entry for Supply Line found");

    supplyLineRepository.insert(supplyLine);
  }

  @Test
  public void shouldThrowErrorIfProgramDoesNotExist() {
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenThrow(new DataException("Invalid program code"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid program code");
    supplyLineRepository.insert(supplyLine);
  }

  @Test
  public void shouldThrowErrorIfFacilityDoesNotExist() {
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1);
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenThrow(new DataException("Invalid Facility Code"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Facility Code");
    supplyLineRepository.insert(supplyLine);
  }

  @Test
  public void shouldThrowErrorIfSupervisoryNodeDoesNotExist() {
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1);
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1);
    when(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode())).thenThrow(new DataException("Invalid SupervisoryNode Code"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid SupervisoryNode Code");
    supplyLineRepository.insert(supplyLine);
  }

  @Test
  public void shouldThrowErrorIfSupervisoryNodeIsNotTheParentNode() {
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1);
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1);
    when(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode())).thenReturn(1);
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(supplyLine.getSupervisoryNode().getId())).thenReturn(2);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Supervising Node is not the Top node");
    supplyLineRepository.insert(supplyLine);
  }

  @Test
  public void shouldReturnSupplyLineBySupervisoryNodeAndProgram(){
    Program program = new Program();
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    when(supplyLineMapper.getSupplyLineBy(supervisoryNode, program)).thenReturn(supplyLine);

    SupplyLine returnedSupplyLine = supplyLineRepository.getSupplyLineBy(supervisoryNode, program);

    verify(supplyLineMapper).getSupplyLineBy(supervisoryNode, program);
    assertThat(returnedSupplyLine, is(supplyLine));
  }
}
