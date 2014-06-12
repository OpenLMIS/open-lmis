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

import org.hamcrest.core.Is;
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
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.SupervisoryNodeRepository;
import org.openlmis.core.repository.SupplyLineRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class SupplyLineServiceTest {

  @Mock
  private SupplyLineRepository repository;

  @Mock
  private ProgramRepository programRepository;

  @Mock
  private FacilityRepository facilityRepository;

  @Mock
  private SupervisoryNodeRepository supervisoryNodeRepository;

  @InjectMocks
  private SupplyLineService service;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  private SupplyLine supplyLine;

  @Before
  public void setUp() throws Exception {
    supplyLine = make(a(SupplyLineBuilder.defaultSupplyLine));
  }

  @Test
  public void shouldReturnSupplyLineBySupervisoryNodeAndProgram() {
    Program program = new Program();
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    SupplyLine supplyLine = new SupplyLine();
    when(repository.getSupplyLineBy(supervisoryNode, program)).thenReturn(supplyLine);

    SupplyLine returnedSupplyLine = service.getSupplyLineBy(supervisoryNode, program);

    verify(repository).getSupplyLineBy(supervisoryNode, program);
    assertThat(returnedSupplyLine, is(supplyLine));
  }

  @Test
  public void shouldThrowErrorIfSupervisoryNodeIsNotTheParentNode() {
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(supplyLine.getSupervisoryNode().getId())).thenThrow(new DataException("Supervising Node is not the Top node"));
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Supervising Node is not the Top node");
    service.save(supplyLine);
  }

  @Test
  public void shouldInsertSupplyLineIfDoesNotExist() throws Exception {
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1L);
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1L);
    when(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode())).thenReturn(1L);

    supplyLine.getSupervisoryNode().setId(1L);
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(1L)).thenReturn(null);

    supplyLine.setId(null);

    service.save(supplyLine);

    verify(repository).insert(supplyLine);
  }

  @Test
  public void shouldUpdateSupplyLineIfExists() throws Exception {
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1L);
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1L);
    when(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode())).thenReturn(1L);
    supplyLine.getSupervisoryNode().setId(1L);
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(1L)).thenReturn(null);

    supplyLine.setId(1L);

    service.save(supplyLine);

    verify(repository).update(supplyLine);
  }

  @Test
  public void shouldGetExistingSupplyLine() throws Exception {
    when(programRepository.getIdByCode(supplyLine.getProgram().getCode())).thenReturn(1L);
    when(facilityRepository.getIdForCode(supplyLine.getSupplyingFacility().getCode())).thenReturn(1L);
    when(supervisoryNodeRepository.getIdForCode(supplyLine.getSupervisoryNode().getCode())).thenReturn(1L);
    supplyLine.getSupervisoryNode().setId(1L);
    when(supervisoryNodeRepository.getSupervisoryNodeParentId(1L)).thenReturn(null);
    when(repository.getSupplyLineBy(supplyLine.getSupervisoryNode(), supplyLine.getProgram())).thenReturn(supplyLine);
    service.save(supplyLine);

    SupplyLine result = service.getExisting(supplyLine);

    assertThat(result, is(supplyLine));
  }

  @Test
  public void shouldGetSupplyLineById() throws Exception {
    SupplyLine expectedSupplyLine = new SupplyLine();
    when(repository.getById(3L)).thenReturn(expectedSupplyLine);

    SupplyLine returnedSupplyLine = service.getById(3L);

    assertThat(returnedSupplyLine, is(expectedSupplyLine));
    verify(repository).getById(3L);
  }

  @Test
  public void shouldSearch() {
    String searchParam = "supply";
    String column = "name";
    List<SupplyLine> supplyLines = asList(new SupplyLine());

    Pagination pagination = new Pagination(2, 10);
    when(repository.search(searchParam, column, pagination)).thenReturn(supplyLines);

    List<SupplyLine> result = service.search(searchParam, column, pagination);
    assertThat(result, Is.is(supplyLines));
  }

  @Test
  public void shouldGetTotalSearchResultCount() throws Exception {
    String searchParam = "fac";
    String column = "facility";

    when(repository.getTotalSearchResultCount(searchParam, column)).thenReturn(1);

    Integer result = service.getTotalSearchResultCount(searchParam, column);
    assertThat(result, is(1));
    verify(repository).getTotalSearchResultCount(searchParam, column);
  }
}
