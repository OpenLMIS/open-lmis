/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.core.domain;

public class ConfigurationSettingKey {

  public static final String CSV_APPLY_QUOTES = "CSV_APPLY_QUOTES";
  public static final String CSV_LINE_SEPARATOR = "CSV_LINE_SEPARATOR";
  public static final String VENDOR_MAINTENANCE_REQUEST_EMAIL_TEMPLATE = "VENDOR_MAINTENANCE_REQUEST_EMAIL_TEMPLATE";
  public static final String ORDER_SUMMARY_SHOW_DISCREPANCY_SECTION = "ORDER_SUMMARY_SHOW_DISCREPANCY_SECTION";
  public static final String ORDER_SUMMARY_SHOW_SIGNATURE_SPACE_FOR_CUSTOMER = "ORDER_SUMMARY_SHOW_SIGNATURE_SPACE_FOR_CUSTOMER";
  public static final String ORDER_REPORT_TITLE = "ORDER_REPORT_TITLE";
  public static final String ORDER_REPORT_ADDRESS = "ORDER_REPORT_ADDRESS";
  public static final String LOGIN_SUCCESS_DEFAULT_LANDING_PAGE = "LOGIN_SUCCESS_DEFAULT_LANDING_PAGE";
  public static final String EMAIL_SUBJECT_APPROVAL = "EMAIL_SUBJECT_APPROVAL";
  public static final String EMAIL_TEMPLATE_APPROVAL = "EMAIL_TEMPLATE_APPROVAL";

  private ConfigurationSettingKey(){

  }
}
