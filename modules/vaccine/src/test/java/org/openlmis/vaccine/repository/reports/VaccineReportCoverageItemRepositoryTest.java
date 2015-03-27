package org.openlmis.vaccine.repository.reports;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.reports.VaccineCoverageItem;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportCoverageMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineReportCoverageItemRepositoryTest {

  @Mock
  VaccineReportCoverageMapper mapper;

  @InjectMocks
  VaccineReportCoverageItemRepository repository;

  @Test
  public void shouldInsert() throws Exception {
    VaccineCoverageItem item = new VaccineCoverageItem();
    repository.insert(item);
    verify(mapper).insert(item);
  }

  @Test
  public void shouldUpdate() throws Exception {
    VaccineCoverageItem item = new VaccineCoverageItem();
    repository.update(item);
    verify(mapper).update(item);
  }

  @Test
  public void shouldGetByParams() throws Exception {
    repository.getByParams(1L,2L,3L);
    verify(mapper).getCoverageByReportProductDosage(1L,2L,3L);
  }

  @Test
  public void shouldGetById() throws Exception {
    repository.getById(1L);
    verify(mapper).getById(1L);
  }
}