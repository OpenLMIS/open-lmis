package org.openlmis.vaccine.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.repository.DiscardingReasonsRepository;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DiscardingReasonsServiceTest {

  @Mock
  DiscardingReasonsRepository repository;

  @InjectMocks
  DiscardingReasonsService service;

  @Test
  public void shouldGetAllReasons() throws Exception {
    service.getAllReasons();
    verify(repository).getAllReasons();
  }
}