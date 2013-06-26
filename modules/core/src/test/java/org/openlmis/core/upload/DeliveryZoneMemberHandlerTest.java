package org.openlmis.core.upload;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DeliveryZoneMember;
import org.openlmis.core.service.DeliveryZoneMemberService;
import org.openlmis.core.upload.DeliveryZoneMemberHandler;
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
