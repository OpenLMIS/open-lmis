/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.domain.MaintenanceLog;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class MaintenanceLogBuilder {

  public static final Property<MaintenanceLog, Long> id = newProperty();
  public static final Property<MaintenanceLog, Long> userId = newProperty();
  public static final Property<MaintenanceLog, Long> equipmentId = newProperty();
  public static final Property<MaintenanceLog, Long> vendorId = newProperty();
  public static final Property<MaintenanceLog, Long> facilityId = newProperty();
  public static final Property<MaintenanceLog, Long> contractId = newProperty();
  public static final Property<MaintenanceLog, Long> requestId = newProperty();

  public static final Property<MaintenanceLog, Date> maintenanceDate = newProperty();
  public static final Property<MaintenanceLog, Date> nextVisitDate = newProperty();
  public static final Property<MaintenanceLog, String> servicePerformed = newProperty();
  public static final Property<MaintenanceLog, String> finding = newProperty();
  public static final Property<MaintenanceLog, String> recommendation = newProperty();


  public static final Property<MaintenanceLog, Long> modifiedBy = newProperty();

  public static final Long USER_ID = 1L;
  public static final Long EQUIPMENT_ID = 1L;
  public static final Long VENDOR_ID = 1L;
  public static final Long FACILITY_ID = 1L;
  public static final Long CONTRACT_ID = 1L;
  public static final Long REQUEST_ID = 1L;
  public static final Date MAINTENANCE_DATE  = new Date();
  public static final Date NEXT_VISIT_DATE = new Date();
  public static final String SERVICE_PERFORMED = "The service Performed";
  public static final String FINDING = "There was no finding at all";
  public static final String RECOMMENDATION = "The vendor recommends that you use your equipment now. ";


  public static final Instantiator<MaintenanceLog> defaultMaintenanceLog = new Instantiator<MaintenanceLog>() {

    @Override
    public MaintenanceLog instantiate(PropertyLookup<MaintenanceLog> lookup) {
      MaintenanceLog item = new MaintenanceLog();
      item.setUserId(lookup.valueOf(userId, USER_ID));
      item.setEquipmentId(lookup.valueOf(equipmentId, EQUIPMENT_ID));
      item.setVendorId(lookup.valueOf(vendorId, VENDOR_ID));
      item.setContractId(lookup.valueOf(contractId, CONTRACT_ID));
      item.setContractId(lookup.valueOf(requestId, REQUEST_ID));
      item.setMaintenanceDate(lookup.valueOf(maintenanceDate, MAINTENANCE_DATE));
      item.setNextVisitDate(lookup.valueOf(nextVisitDate, NEXT_VISIT_DATE));
      item.setServicePerformed(lookup.valueOf(servicePerformed, SERVICE_PERFORMED));
      item.setFinding(lookup.valueOf(finding, FINDING));
      item.setRecommendation(lookup.valueOf(recommendation, RECOMMENDATION));

      return item;
    }
  };
}
