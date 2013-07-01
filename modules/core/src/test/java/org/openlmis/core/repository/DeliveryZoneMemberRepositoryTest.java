package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DeliveryZoneMember;
import org.openlmis.core.repository.DeliveryZoneMemberRepository;
import org.openlmis.core.repository.mapper.DeliveryZoneMemberMapper;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneMemberRepositoryTest {

  @InjectMocks
  private DeliveryZoneMemberRepository repository;

  @Mock
  private DeliveryZoneMemberMapper mapper;

  @Test
  public void shouldInsertDeliveryZoneMember() throws Exception {
    DeliveryZoneMember member = new DeliveryZoneMember();
    repository.insert(member);

    verify(mapper).insert(member);
  }

  @Test
  public void testUpdate() throws Exception {
    DeliveryZoneMember member = new DeliveryZoneMember();
    repository.update(member);

    verify(mapper).update(member);
  }
}
