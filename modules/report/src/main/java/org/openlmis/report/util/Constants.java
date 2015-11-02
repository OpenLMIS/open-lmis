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

package org.openlmis.report.util;

public class Constants {

    /*
        Media Types
     */
    public static final String MEDIA_TYPE_PDF = "application/pdf";
    public static final String MEDIA_TYPE_EXCEL = "application/vnd.ms-excel";
    public static final String MEDIA_TYPE_HTML = "text/html";

    /*
        Master Report Parameter Names
     */
    public static String COUNTRY_NAME = "COUNTRY_NAME";
    public static String REPORT_TITLE = "REPORT_TITLE";
    public static String REPORT_NAME = "REPORT_NAME";
    public static String REPORT_VERSION = "REPORT_VERSION";
    public static String REPORT_OUTPUT_OPTION = "REPORT_OUTPUT_OPTION";
    public static String REPORT_ID = "REPORT_ID";
    public static String GENERATED_BY = "GENERATED_BY";
    public static String LOGO = "LOGO";
    public static String VIMS_LOGO = "VIMS_LOGO";
    public static String OPERATOR_LOGO = "OPERATOR_LOGO";
    public static String OPERATOR_NAME = "OPERATOR_NAME";
    public static String REPORT_SUB_TITLE = "REPORT_SUB_TITLE";
    public static String REPORT_FILTER_PARAM_VALUES = "REPORT_FILTER_PARAM_VALUES";
    public static String REPORT_MESSAGE_WHEN_NO_DATA = "REPORT_MESSAGE_WHEN_NO_DATA";


    /**
     * Configuration keys
     */
    public static String LOGO_FILE_NAME_KEY = "logo_file_name";
    public static String VIMS_LOGO_FILE_NAME_KEY = "VIMS_LOGO_FILE_NAME";
    public static String OPERATOR_LOGO_FILE_NAME_KEY = "operator_logo_file_name";
    public static String START_YEAR = "start_year";
    public static String MONTHS = "months";
    public static String CONF_INDICATOR_PRODUCTS = "INDICATOR_PRODUCTS";

    /**
     *  Miscellaneous
     */
    public static String PERIOD_TYPE_MONTHLY = "monthly";
    public static String PERIOD_TYPE_QUARTERLY = "quarterly";
    public static String PERIOD_TYPE_SEMI_ANNUAL = "semi-anual";
    public static String PERIOD_TYPE_ANNUAL = "annual";
    public static String NOTIFICATION_METHOD_EMAIL = "email";
    public static String NOTIFICATION_METHOD_SMS = "sms";
    public static String USER_PRIMARY_NOTIFICATION_METHOD_CELL_PHONE = "Cell Phone";
    public static String USER_PRIMARY_NOTIFICATION_METHOD_EMAIL = "email";


}
