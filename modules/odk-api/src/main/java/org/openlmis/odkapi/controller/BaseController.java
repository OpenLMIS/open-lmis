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
package org.openlmis.odkapi.controller;

public class BaseController {

    public static final String OPEN_ROSA_VERSION_HEADER = "X-OpenRosa-Version";
    public static final String OPEN_ROSA_VERSION = "1.0";
    public static final String OPEN_ROSA_ACCEPT_CONTENT_LENGTH_HEADER = "X-OpenRosa-Accept-Content-Length";
    public static final String OPEN_ROSA_ACCEPT_CONTENT_LENGTH = "2000000";
    public static final String ODK_COLLECT_SUBMISSION_SUCCESSFUL =  "<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\">\n" +
            "    <message nature=\"submit_success\"> Form submitted. </message>\n" +
            "</OpenRosaResponse>";
    public static final String ODK_COLLECT_HEAD_REQUEST_SUCCESSFUL =  "<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\">\n" +
            "    <message nature=\"submit_success\"> Head request accepted. </message>\n" +
            "</OpenRosaResponse>";

    public static final String errorResponseMessage =  "<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\">\n" +
            "    <message nature=\"submit_failure\"> Error during form submission. </message>\n" +
            "</OpenRosaResponse>";
    public static final String ODK_ACCOUNT_NOT_VALID =  "<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\">\n" +
            "    <message nature=\"submit_failure\"> Device not allowed to access the system. </message>\n" +
            "</OpenRosaResponse>";
    public static final String FACILITY_PICTURE_MISSING =  "<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\">\n" +
            "    <message nature=\"submit_failure\"> Facility picture is missing. </message>\n" +
            "</OpenRosaResponse>";
    public static final String ODK_COLLECT_XML_SUBMISSION_FILE_MISSING =  "<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\">\n" +
            "    <message nature=\"submit_failure\"> Submission XML file is missing. </message>\n" +
            "</OpenRosaResponse>";
    public static final String ODK_COLLECT_XML_SUBMISSION_FILE_PARSE_ERROR =  "<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\">\n" +
            "    <message nature=\"submit_failure\"> Submission XML file parse error. </message>\n" +
            "</OpenRosaResponse>";
    public static final String     FACILITY_NOT_FOUND =  "<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\">\n" +
            "    <message nature=\"submit_failure\"> Facility not found. </message>\n" +
            "</OpenRosaResponse>";

    public static final String ODK_COLLECT_ODK_XFORM_NOT_FOUND =  "<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\">\n" +
            "    <message nature=\"submit_failure\"> ODK XForm not found. </message>\n" +
            "</OpenRosaResponse>";

    public static final String ODK_COLLECT_APP_APK_FILE_NAME = "OpenLMIS ODK Collect.apk";
    public static final String ODK_COLLECT_APP_APK_REQUEST_PARAMETER = "apk";
    public static final String ODK_COLLECT_APP_ITEMSETS_CSV_FILE_NAME = "itemsets.csv";
    public static final String ODK_COLLECT_APP_ITEMSETS_REQUEST_PARAMETER = "itemsets";
    public static final String FILE_NOT_FOUND_ERROR_MESSAGE = "File not found.";
    public static final String OUTPUT_STREAM_CANNOT_BE_GENERATED = "Could not generate output stream.";
    public static final String CAN_NOT_READ_FILE = "File could not be read.";

}
