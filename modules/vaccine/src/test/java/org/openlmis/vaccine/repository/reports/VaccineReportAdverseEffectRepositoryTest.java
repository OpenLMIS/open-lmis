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
import org.openlmis.vaccine.domain.reports.AdverseEffectLineItem;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportAdverseEffectMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineReportAdverseEffectRepositoryTest {

  @Mock
  VaccineReportAdverseEffectMapper mapper;

  @InjectMocks
  VaccineReportAdverseEffectRepository repository;

  @Test
  public void shouldInsert() throws Exception {
    AdverseEffectLineItem lineItem = new AdverseEffectLineItem();
    repository.insert(lineItem);
    verify(mapper).insert(lineItem);
  }

  @Test
  public void shouldUpdate() throws Exception {
    AdverseEffectLineItem lineItem = new AdverseEffectLineItem();
    repository.insert(lineItem);
    verify(mapper).insert(lineItem);
  }
}