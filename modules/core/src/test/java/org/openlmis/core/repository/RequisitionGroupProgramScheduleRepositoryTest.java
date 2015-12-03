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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.RequisitionGroupBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RequisitionGroupProgramSchedule;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupMapper;
import org.openlmis.core.repository.mapper.RequisitionGroupProgramScheduleMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DuplicateKeyException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
public class RequisitionGroupProgramScheduleRepositoryTest {

  RequisitionGroupProgramScheduleRepository repository;
  RequisitionGroupProgramSchedule requisitionGroupProgramSchedule;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  @Mock
  RequisitionGroupMapper requisitionGroupMapper;
  @Mock
  ProgramRepository programRepository;
  @Mock
  ProcessingScheduleMapper processingScheduleMapper;
  @Mock
  FacilityMapper facilityMapper;
  @Mock
  RequisitionGroupProgramScheduleMapper requisitionGroupProgramScheduleMapper;

  private Facility dropOffFacility;


  @Before
  public void setUp() throws Exception {
    initMocks(this);

    dropOffFacility = make(a(FacilityBuilder.defaultFacility));

    repository = new RequisitionGroupProgramScheduleRepository(requisitionGroupProgramScheduleMapper, requisitionGroupMapper, programRepository, processingScheduleMapper, facilityMapper);
    requisitionGroupProgramSchedule = new RequisitionGroupProgramSchedule();
    requisitionGroupProgramSchedule.setRequisitionGroup(make(a(RequisitionGroupBuilder.defaultRequisitionGroup)));
    requisitionGroupProgramSchedule.setProgram(make(a(ProgramBuilder.defaultProgram)));
    requisitionGroupProgramSchedule.setProcessingSchedule(new ProcessingSchedule());
  }

  @Test
  public void shouldGiveErrorIfRequisitionGroupCodeDoesNotExist() throws Exception {
    when(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode())).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.requisition.group.not.exists"));
    repository.insert(requisitionGroupProgramSchedule);

    verify(requisitionGroupMapper).getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode());
    verify(requisitionGroupProgramScheduleMapper, never()).insert(requisitionGroupProgramSchedule);
  }

  @Test
  public void shouldGiveErrorIfProgramIsOfTypePushWhileInsert() {
    Program program = new Program();
    program.setPush(true);
    Long programId = 1L;
    Long facilityId = 99L;
    Long scheduleId = 1L;
    when(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode())).thenReturn(1L);
    when(programRepository.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode())).thenReturn(programId);
    when(processingScheduleMapper.getIdForCode(requisitionGroupProgramSchedule.getProcessingSchedule().getCode())).thenReturn(scheduleId);
    requisitionGroupProgramSchedule.setDropOffFacility(dropOffFacility);
    when(programRepository.getById(programId)).thenReturn(program);
    when(facilityMapper.getIdForCode(dropOffFacility.getCode())).thenReturn(facilityId);

    expectedEx.expect(dataExceptionMatcher("error.program.type.not.supported.requisitions"));

    repository.insert(requisitionGroupProgramSchedule);
  }


  @Test
  public void shouldGiveErrorIfProgramIsOfTypePushWhileUpdate() {
    Program program = new Program();
    program.setPush(true);
    Long programId = 1L;
    Long facilityId = 99L;
    Long scheduleId = 1L;
    when(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode())).thenReturn(1L);
    when(programRepository.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode())).thenReturn(programId);
    when(processingScheduleMapper.getIdForCode(requisitionGroupProgramSchedule.getProcessingSchedule().getCode())).thenReturn(scheduleId);
    requisitionGroupProgramSchedule.setDropOffFacility(dropOffFacility);
    when(programRepository.getById(programId)).thenReturn(program);
    when(facilityMapper.getIdForCode(dropOffFacility.getCode())).thenReturn(facilityId);

    expectedEx.expect(dataExceptionMatcher("error.program.type.not.supported.requisitions"));

    repository.update(requisitionGroupProgramSchedule);
  }

  @Test
  public void shouldGiveErrorIfProgramCodeDoesNotExist() throws Exception {
    when(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode())).thenReturn(1L);
    when(programRepository.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode())).thenThrow(new DataException("Invalid Program Code"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid Program Code");
    repository.insert(requisitionGroupProgramSchedule);
  }

  @Test
  public void shouldGiveErrorIfScheduleCodeDoesNotExist() throws Exception {
    when(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode())).thenReturn(1L);
    when(programRepository.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode())).thenReturn(1L);
    when(processingScheduleMapper.getIdForCode(requisitionGroupProgramSchedule.getProcessingSchedule().getCode())).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.schedule.not.exists"));

    repository.insert(requisitionGroupProgramSchedule);
  }

  @Test
  public void shouldGiveDuplicateRecordErrorIfDuplicateRGCodeAndProgramCodeFound() throws Exception {
    doThrow(new DuplicateKeyException("")).when(requisitionGroupProgramScheduleMapper).insert(requisitionGroupProgramSchedule);
    Program program = new Program();
    program.setPush(false);
    Long programId = 1L;
    when(programRepository.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode())).thenReturn(programId);
    when(programRepository.getById(programId)).thenReturn(program);
    requisitionGroupProgramSchedule.setDirectDelivery(true);
    expectedEx.expect(dataExceptionMatcher("error.duplicate.requisition.group.program.combination"));
    repository.insert(requisitionGroupProgramSchedule);
  }

  @Test
  public void shouldSaveMappingIfAllCorrect() throws Exception {
    Long facilityId = 99L;

    Program program = new Program();
    program.setPush(false);
    Long programId = 1L;

    requisitionGroupProgramSchedule.setDropOffFacility(facility(dropOffFacility.getCode()));
    when(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode())).thenReturn(1L);
    when(programRepository.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode())).thenReturn(programId);
    when(programRepository.getById(programId)).thenReturn(program);
    when(processingScheduleMapper.getIdForCode(requisitionGroupProgramSchedule.getProcessingSchedule().getCode())).thenReturn(1L);
    when(facilityMapper.getIdForCode(dropOffFacility.getCode())).thenReturn(facilityId);

    repository.insert(requisitionGroupProgramSchedule);

    verify(requisitionGroupProgramScheduleMapper).insert(requisitionGroupProgramSchedule);
    verify(requisitionGroupMapper).getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode());
    verify(programRepository).getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode());
    verify(processingScheduleMapper).getIdForCode(requisitionGroupProgramSchedule.getProcessingSchedule().getCode());
    verify(facilityMapper).getIdForCode(dropOffFacility.getCode());

    assertThat(requisitionGroupProgramSchedule.getProcessingSchedule().getId(), is(1L));
    assertThat(requisitionGroupProgramSchedule.getProgram().getId(), is(1L));
    assertThat(requisitionGroupProgramSchedule.getRequisitionGroup().getId(), is(1L));
    assertThat(requisitionGroupProgramSchedule.isDirectDelivery(), is(false));
    assertThat(requisitionGroupProgramSchedule.getDropOffFacility().getId(), is(facilityId));
  }

  @Test
  public void shouldGiveErrorWhenDropOffFacilityIsNotProvidedAndDirectDeliveryIsFalse() {
    requisitionGroupProgramSchedule.setDirectDelivery(false);

    expectedEx.expect(DataException.class);
    expectedEx.expect(dataExceptionMatcher("error.drop.off.facility.not.defined"));
    repository.insert(requisitionGroupProgramSchedule);
  }

  @Test
  public void shouldGiveErrorIfFacilityCodeDoesNotExist() {
    when(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode())).thenReturn(1L);
    when(programRepository.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode())).thenReturn(1L);
    when(processingScheduleMapper.getIdForCode(requisitionGroupProgramSchedule.getProcessingSchedule().getCode())).thenReturn(1L);
    requisitionGroupProgramSchedule.setDropOffFacility(dropOffFacility);
    when(facilityMapper.getIdForCode(requisitionGroupProgramSchedule.getDropOffFacility().getCode())).thenReturn(null);

    requisitionGroupProgramSchedule.setDirectDelivery(false);

    expectedEx.expect(dataExceptionMatcher("error.drop.off.facility.not.present"));
    repository.insert(requisitionGroupProgramSchedule);
  }

  @Test
  public void shouldGetScheduleIdForRequisitionGroupAndProgram() throws Exception {
    when(requisitionGroupProgramScheduleMapper.getScheduleForRequisitionGroupIdAndProgramId(1L, 2L)).thenReturn(requisitionGroupProgramSchedule);
    RequisitionGroupProgramSchedule schedule = repository.getScheduleForRequisitionGroupAndProgram(1L, 2L);

    assertThat(schedule, is(requisitionGroupProgramSchedule));
  }

  @Test
  public void shouldUpdateRequisitionGroupProgramSchedule() throws Exception {

    Program program = new Program();
    program.setPush(false);
    Long programId = 1L;

    when(requisitionGroupMapper.getIdForCode(requisitionGroupProgramSchedule.getRequisitionGroup().getCode())).thenReturn(1L);
    when(programRepository.getIdByCode(requisitionGroupProgramSchedule.getProgram().getCode())).thenReturn(programId);
    when(programRepository.getById(programId)).thenReturn(program);
    when(processingScheduleMapper.getIdForCode(requisitionGroupProgramSchedule.getProcessingSchedule().getCode())).thenReturn(1L);
    when(facilityMapper.getIdForCode(dropOffFacility.getCode())).thenReturn(1L);
    requisitionGroupProgramSchedule.setDropOffFacility(dropOffFacility);

    repository.update(requisitionGroupProgramSchedule);
    verify(requisitionGroupProgramScheduleMapper).update(requisitionGroupProgramSchedule);
  }

  private Facility facility(String facilityCode) {
    requisitionGroupProgramSchedule.setDirectDelivery(false);
    Facility dropOffFacilityForImport = new Facility();
    dropOffFacilityForImport.setCode(facilityCode);
    return dropOffFacilityForImport;
  }


}
