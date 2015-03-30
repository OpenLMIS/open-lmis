package org.openlmis.vaccine.repository.reports;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineReportRepositoryTest {

  @Mock
  VaccineReportMapper mapper;

  @InjectMocks
  VaccineReportRepository repository;

  @Test
  public void shouldInsert() throws Exception {
    VaccineReport report = new VaccineReport();
    repository.insert(report);
    verify(mapper).insert(report);
  }

  @Test
  public void shouldUpdate() throws Exception {
    VaccineReport report = new VaccineReport();
    repository.update(report);
    verify(mapper).update(report);
  }

  @Test
  public void shouldGetById() throws Exception {
    repository.getById(20L);
    verify(mapper).getById(20L);
  }

  @Test
  public void shouldGetByIdWithFullDetails() throws Exception {
    repository.getByIdWithFullDetails(20L);
    verify(mapper).getByIdWithFullDetails(20L);
  }

  @Test
  public void shouldGetByProgramPeriod() throws Exception {
    repository.getByProgramPeriod(20L, 10L, 3L);
    verify(mapper).getByPeriodFacilityProgram(20L, 10L, 3L);
  }

  @Test
  public void shouldGetLastReport() throws Exception {
    repository.getLastReport(20L, 2L);
    verify(mapper).getLastReport(20L, 2L);
  }

  @Test
  public void shouldGetScheduleFor() throws Exception {
    repository.getScheduleFor(29L, 2L);
    verify(mapper).getScheduleFor(29L, 2L);
  }
}