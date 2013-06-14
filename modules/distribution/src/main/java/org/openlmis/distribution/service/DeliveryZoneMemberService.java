package org.openlmis.distribution.service;

import org.openlmis.distribution.domain.DeliveryZone;
import org.openlmis.distribution.domain.DeliveryZoneMember;
import org.openlmis.distribution.repository.DeliveryZoneMemberRepository;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections.ListUtils.intersection;

@Service
public class DeliveryZoneMemberService {

  @Autowired
  DeliveryZoneMemberRepository repository;

  @Autowired
  FacilityService facilityService;

  @Autowired
  DeliveryZoneService deliveryZoneService;

  @Autowired
  DeliveryZoneProgramScheduleService deliveryZoneProgramScheduleService;

  public void save(DeliveryZoneMember member) {
    fillFacility(member);
    fillDeliveryZone(member);
    if (member.getId() == null) {
      validateIfFacilityAlreadyAssignedToDeliveryZoneForProgram(member);
      repository.insert(member);
    } else
      repository.update(member);
  }

  private void validateIfFacilityAlreadyAssignedToDeliveryZoneForProgram(DeliveryZoneMember member) {
    List<Long> commonIds = getCommonProgramsForDeliveryZoneAndFacliity(member);

    if (commonIds.size() > 0)
      throw new DataException("facility.exists.for.program.in.multiple.zones");
  }

  private List<Long> getCommonProgramsForDeliveryZoneAndFacliity(DeliveryZoneMember member) {
    List<Long> programIdsForDeliveryZones = deliveryZoneProgramScheduleService.
        getProgramIdsForDeliveryZones(member.getDeliveryZone().getId());

    if (programIdsForDeliveryZones.size() == 0)
      throw new DataException("no.program.mapped.for.delivery.zone");

    List<Long> deliveryZoneProgramIdsForFacility = repository.
        getDeliveryZoneProgramIdsForFacility(member.getFacility().getId());

    return intersection(programIdsForDeliveryZones, deliveryZoneProgramIdsForFacility);
  }

  private void fillDeliveryZone(DeliveryZoneMember member) {
    DeliveryZone zone = deliveryZoneService.getByCode(member.getDeliveryZone().getCode());
    if (zone == null) throw new DataException("deliveryZone.code.invalid");
    member.setDeliveryZone(zone);
  }

  private void fillFacility(DeliveryZoneMember member) {
    Facility facility = facilityService.getByCode(member.getFacility());
    if (facility == null) throw new DataException("error.facility.code.invalid");
    member.setFacility(facility);
  }

  public DeliveryZoneMember getByDeliveryZoneCodeAndFacilityCode(String deliveryZoneCode, String facilityCode) {
    return repository.getByDeliveryZoneCodeAndFacilityCode(deliveryZoneCode, facilityCode);
  }
}
