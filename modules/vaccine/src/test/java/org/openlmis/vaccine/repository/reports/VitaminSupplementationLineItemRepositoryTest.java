package org.openlmis.vaccine.repository.reports;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.reports.VitaminSupplementationLineItem;
import org.openlmis.vaccine.repository.mapper.reports.VitaminSupplementationLineItemMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VitaminSupplementationLineItemRepositoryTest {

  @Mock
  VitaminSupplementationLineItemMapper mapper;

  @InjectMocks
  VitaminSupplementationLineItemRepository repository;

  @Test
  public void shouldInsert() throws Exception {
    VitaminSupplementationLineItem lineItem = new VitaminSupplementationLineItem();
    repository.insert(lineItem);
    verify(mapper).insert(lineItem);
  }

  @Test
  public void shouldUpdate() throws Exception {
    VitaminSupplementationLineItem lineItem = new VitaminSupplementationLineItem();
    repository.update(lineItem);
    verify(mapper).update(lineItem);
  }
}