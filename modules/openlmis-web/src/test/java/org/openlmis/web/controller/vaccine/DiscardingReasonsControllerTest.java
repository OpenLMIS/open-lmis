package org.openlmis.web.controller.vaccine;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.service.DiscardingReasonsService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;


@Category(UnitTests.class)
@PrepareForTest(DiscardingReasonsController.class)
public class DiscardingReasonsControllerTest {

  @Mock
  DiscardingReasonsService service;

  @InjectMocks
  DiscardingReasonsController controller;

  @Rule
  public PowerMockRule rule = new PowerMockRule();

  @Test
  public void shouldGetAll() throws Exception {
    controller.getAll();
    verify(service).getAllReasons();
  }
}