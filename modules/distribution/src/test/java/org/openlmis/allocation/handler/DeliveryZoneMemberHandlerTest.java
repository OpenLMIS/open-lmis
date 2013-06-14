package org.openlmis.allocation.handler;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.allocation.domain.DeliveryZoneMember;
import org.openlmis.allocation.service.DeliveryZoneMemberService;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneMemberHandlerTest {

  @InjectMocks
  DeliveryZoneMemberHandler handler;

  @Mock
  DeliveryZoneMemberService service;

  @Test
  public void shouldSaveDeliveryZoneMember() throws Exception {
    DeliveryZoneMember member = new DeliveryZoneMember();
    handler.save(member);
    verify(service).save(member);
  }
}
