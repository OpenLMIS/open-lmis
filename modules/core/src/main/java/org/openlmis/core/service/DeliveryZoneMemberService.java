/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.DeliveryZoneMember;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.DeliveryZoneMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections.ListUtils.intersection;

/**
 * Exposes the services for handling DeliveryZoneMember entity.
 */

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
