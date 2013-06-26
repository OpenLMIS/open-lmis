package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DeliveryZoneMember;
import org.openlmis.core.repository.DeliveryZoneMemberRepository;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.DeliveryZoneMemberService;
import org.openlmis.core.service.DeliveryZoneProgramScheduleService;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.UnitTests;

import java.util.Arrays;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;

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
  private DeliveryZoneProgramScheduleService deliveryZoneProgramScheduleService;

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
    when(deliveryZoneProgramScheduleService.getProgramIdsForDeliveryZones(member.getDeliveryZone().getId())).thenReturn(Arrays.asList(new Long[]{1L}));
    when(repository.getDeliveryZoneProgramIdsForFacility(member.getFacility().getId())).thenReturn(Arrays.asList(new Long[]{2L}));
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
  public void shouldThrowErrorIfInvalidFacilityCode() throws Exception {
    when(facilityService.getByCode(member.getFacility())).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.facility.code.invalid");

    service.save(member);
  }

  @Test
  public void shouldThrowErrorIfInvalidDZCode() throws Exception {
    when(deliveryZoneService.getByCode(member.getDeliveryZone().getCode())).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("deliveryZone.code.invalid");

    service.save(member);
  }

  @Test
  public void shouldThrowErrorIfFacilityMappedForSameProgramToDifferentDeliveryZones() throws Exception {
    when(deliveryZoneProgramScheduleService.getProgramIdsForDeliveryZones(member.getDeliveryZone().getId())).thenReturn(Arrays.asList(new Long[]{1L}));
    when(repository.getDeliveryZoneProgramIdsForFacility(member.getFacility().getId())).thenReturn(Arrays.asList(new Long[]{1L}));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("facility.exists.for.program.in.multiple.zones");

    service.save(member);
  }

  @Test
  public void shouldThrowErrorIfNoProgramsMappedForDeliveryZone() throws Exception {
    when(deliveryZoneProgramScheduleService.getProgramIdsForDeliveryZones(member.getDeliveryZone().getId())).thenReturn(Arrays.asList(new Long[]{}));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("no.program.mapped.for.delivery.zone");

    service.save(member);
  }
}
