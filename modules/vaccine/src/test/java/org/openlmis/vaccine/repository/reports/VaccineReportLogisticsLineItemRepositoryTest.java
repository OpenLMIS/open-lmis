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
import org.openlmis.vaccine.domain.reports.LogisticsLineItem;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportLogisticsLineItemMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineReportLogisticsLineItemRepositoryTest {

  @Mock
  VaccineReportLogisticsLineItemMapper mapper;

  @InjectMocks
  VaccineReportLogisticsLineItemRepository repository;

  @Test
  public void shouldInsert() throws Exception {
    LogisticsLineItem lineItem = new LogisticsLineItem();
    repository.insert(lineItem);
    verify(mapper).insert(lineItem);
  }

  @Test
  public void shouldUpdate() throws Exception {
    LogisticsLineItem lineItem = new LogisticsLineItem();
    repository.update(lineItem);
    verify(mapper).update(lineItem);
  }
}