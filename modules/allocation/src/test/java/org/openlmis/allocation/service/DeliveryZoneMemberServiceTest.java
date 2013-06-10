package org.openlmis.allocation.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.allocation.domain.DeliveryZoneMember;
import org.openlmis.allocation.repository.DeliveryZoneMemberRepository;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.allocation.builder.DeliveryZoneBuilder.defaultDeliveryZone;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneMemberServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @InjectMocks
  DeliveryZoneMemberService service;

  @Mock
  DeliveryZoneMemberRepository repository;

  @Mock
  private FacilityService facilityService;

  @Mock
  private DeliveryZoneService deliveryZoneService;

  DeliveryZoneMember member;
  @Before
  public void setUp() throws Exception {
    member = new DeliveryZoneMember();
    member.setFacility(make(a(FacilityBuilder.defaultFacility)));
    member.setDeliveryZone(make(a(defaultDeliveryZone)));
    when(facilityService.getByCode(member.getFacility())).thenReturn(member.getFacility());
    when(deliveryZoneService.getByCode(member.getDeliveryZone().getCode())).thenReturn(member.getDeliveryZone());
  }

  @Test
  public void shouldSaveDeliveryZoneMember() throws Exception {
    service.save(member);
    verify(repository).insert(member);
  }

  @Test
  public void shouldUpdateDeliveryZoneMemberIfIdExists() throws Exception {
    member.setId(1l);
    service.save(member);
    verify(repository).update(member);
  }

  @Test
  public void shouldThrowErrorIfInvalidProgramCode() throws Exception {
    when(facilityService.getByCode(member.getFacility())).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("facility.code.invalid");

    service.save(member);
  }

  @Test
  public void shouldThrowErrorIfInvalidDZCode() throws Exception {
    when(deliveryZoneService.getByCode(member.getDeliveryZone().getCode())).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("deliveryZone.code.invalid");

    service.save(member);
  }

}
