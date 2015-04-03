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
import org.openlmis.vaccine.domain.reports.DiseaseLineItem;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportDiseaseLineItemMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineReportDiseaseLineItemRepositoryTest {

  @Mock
  VaccineReportDiseaseLineItemMapper mapper;

  @InjectMocks
  VaccineReportDiseaseLineItemRepository repository;

  @Test
  public void shouldInsert() throws Exception {
    DiseaseLineItem lineItem = new DiseaseLineItem();
    repository.insert(lineItem);
    verify(mapper).insert(lineItem);
  }

  @Test
  public void shouldUpdate() throws Exception {
    DiseaseLineItem lineItem = new DiseaseLineItem();
    repository.update(lineItem);
    verify(mapper).update(lineItem);
  }
}