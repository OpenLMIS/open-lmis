/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;

import java.util.Arrays;

/**
 * RightName is an entity that represents name of a right. It contains all right names as constants for pre defined rights.
 */

public class RightName {
  public static final String CONFIGURE_RNR = "CONFIGURE_RNR";
  public static final String MANAGE_FACILITY = "MANAGE_FACILITY";
  public static final String MANAGE_ROLE = "MANAGE_ROLE";
  public static final String MANAGE_SCHEDULE = "MANAGE_SCHEDULE";
  public static final String MANAGE_USER = "MANAGE_USER";
  public static final String MANAGE_SUPERVISORY_NODE = "MANAGE_SUPERVISORY_NODE";
  public static final String UPLOADS = "UPLOADS";
  public static final String MANAGE_REPORT = "MANAGE_REPORT";
  public static final String VIEW_REQUISITION = "VIEW_REQUISITION";
  public static final String CREATE_REQUISITION = "CREATE_REQUISITION";
  public static final String AUTHORIZE_REQUISITION = "AUTHORIZE_REQUISITION";
  public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
  public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
  public static final String VIEW_ORDER = "VIEW_ORDER";
  public static final String MANAGE_PROGRAM_PRODUCT = "MANAGE_PROGRAM_PRODUCT";
  public static final String MANAGE_DISTRIBUTION = "MANAGE_DISTRIBUTION";
  public static final String SYSTEM_SETTINGS = "SYSTEM_SETTINGS";
  public static final String MANAGE_REGIMEN_TEMPLATE = "MANAGE_REGIMEN_TEMPLATE";
  public static final String FACILITY_FILL_SHIPMENT = "FACILITY_FILL_SHIPMENT";
  public static final String MANAGE_POD = "MANAGE_POD";
  public static final String MANAGE_GEOGRAPHIC_ZONE = "MANAGE_GEOGRAPHIC_ZONE";
  public static final String MANAGE_REQUISITION_GROUP = "MANAGE_REQUISITION_GROUP";
  public static final String MANAGE_SUPPLY_LINE = "MANAGE_SUPPLY_LINE";
  public static final String MANAGE_FACILITY_APPROVED_PRODUCT = "MANAGE_FACILITY_APPROVED_PRODUCT";
  public static final String MANAGE_PRODUCT = "MANAGE_PRODUCT";
  public static final String MANAGE_EQUIPMENT_INVENTORY = "MANAGE_EQUIPMENT_INVENTORY";
  public static final String MANAGE_EQUIPMENT_SETTINGS = "MANAGE_EQUIPMENT_SETTINGS";
  public static final String CREATE_IVD = "CREATE_IVD";
  public static final String VIEW_IVD = "VIEW_IVD";
  public static final String APPROVE_IVD = "APPROVE_IVD";

  public static final String MANAGE_DEMOGRAPHIC_ESTIMATES = "MANAGE_DEMOGRAPHIC_ESTIMATES";
  public static final String MANAGE_DEMOGRAPHIC_PARAMETERS = "MANAGE_DEMOGRAPHIC_PARAMETERS";
  public static final String VIEW_VACCINE_ORDER_REQUISITION = "VIEW_ORDER_REQUISITION";


  public static String commaSeparateRightNames(String... rightNames) {
    return Arrays.toString(rightNames).replace("[", "{").replace("]", "}");
  }
}
