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
import org.openlmis.vaccine.domain.reports.ColdChainLineItem;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportColdChainMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineReportColdChainRepositoryTest {

  @Mock
  VaccineReportColdChainMapper mapper;

  @InjectMocks
  VaccineReportColdChainRepository repository;

  @Test
  public void shouldInsert() throws Exception {
    ColdChainLineItem lineItem = new ColdChainLineItem();
    repository.insert(lineItem);
    verify(mapper).insert(lineItem);
  }

  @Test
  public void shouldUpdate() throws Exception {
    ColdChainLineItem lineItem = new ColdChainLineItem();
    repository.update(lineItem);
    verify(mapper).update(lineItem);
  }

  @Test
  public void shouldGetNewEquipmentLineItems() throws Exception {
    repository.getNewEquipmentLineItems(2L, 2L);
    verify(mapper).getNewEquipmentLineItems(2L, 2L);
  }
}