package org.openlmis.allocation.service;

import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.allocation.domain.DeliveryZoneMember;
import org.openlmis.allocation.repository.DeliveryZoneMemberRepository;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections.ListUtils.intersection;

@Service
public class DeliveryZoneMemberService {

  public static final String FACILITY_CODE_INVALID = "facility.code.invalid";
  public static final String DELIVERY_ZONE_CODE_INVALID = "deliveryZone.code.invalid";
  public static final String FACILITY_PROGRAM_EXISTS_IN_MULTIPLE_DELIVERY_ZONES = "facility.exists.for.program.in.multiple.zones";
  public static final String NO_PROGRAMS_MAPPED_FOR_DELIVERY_ZONES = "no.program.mapped.for.delivery.zone";

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
    validateIfFacilityAlreadyAssignedToDeliveryZoneForProgram(member);
    if (member.getId() == null)
      repository.insert(member);
    else
      repository.update(member);
  }

  private void validateIfFacilityAlreadyAssignedToDeliveryZoneForProgram(DeliveryZoneMember member) {
    List<Long> commonIds = getCommonProgramsForDeliveryZoneAndFacliity(member);

    if (commonIds.size() > 0)
      throw new DataException(FACILITY_PROGRAM_EXISTS_IN_MULTIPLE_DELIVERY_ZONES);
  }

  private List<Long> getCommonProgramsForDeliveryZoneAndFacliity(DeliveryZoneMember member) {
    List<Long> programIdsForDeliveryZones = deliveryZoneProgramScheduleService.
        getProgramIdsForDeliveryZones(member.getDeliveryZone().getId());

    if (programIdsForDeliveryZones.size() == 0)
      throw new DataException(NO_PROGRAMS_MAPPED_FOR_DELIVERY_ZONES);

    List<Long> deliveryZoneProgramIdsForFacility = repository.
        getDeliveryZoneProgramIdsForFacility(member.getFacility().getId());

    return intersection(programIdsForDeliveryZones, deliveryZoneProgramIdsForFacility);
  }

  private void fillDeliveryZone(DeliveryZoneMember member) {
    DeliveryZone zone = deliveryZoneService.getByCode(member.getDeliveryZone().getCode());
    if (zone == null) throw new DataException(DELIVERY_ZONE_CODE_INVALID);
    member.setDeliveryZone(zone);
  }

  private void fillFacility(DeliveryZoneMember member) {
    Facility facility = facilityService.getByCode(member.getFacility());
    if (facility == null) throw new DataException(FACILITY_CODE_INVALID);
    member.setFacility(facility);
  }

  public DeliveryZoneMember getByDeliveryZoneCodeAndFacilityCode(String deliveryZoneCode, String facilityCode) {
    return repository.getByDeliveryZoneCodeAndFacilityCode(deliveryZoneCode, facilityCode);
  }
}
