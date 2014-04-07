/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
/**
 * Created with IntelliJ IDEA.
 * User: Messay Yohannes <deliasmes@gmail.com>
 * To change this template use File | Settings | File Templates.
 */
package org.openlmis.odkapi.controller;

public class BaseController {

    public static final String OPEN_ROSA_VERSION_HEADER = "X-OpenRosa-Version";
    public static final String OPEN_ROSA_VERSION = "1.0";
    public static final String OPEN_ROSA_ACCEPT_CONTENT_LENGTH_HEADER = "X-OpenRosa-Accept-Content-Length";
    public static final String OPEN_ROSA_ACCEPT_CONTENT_LENGTH = "2000000";
    public static final String successResponseMessage =  "<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\">\n" +
            "    <message nature=\"submit_success\"> Form submitted. </message>\n" +
            "</OpenRosaResponse>";
    public static final String errorResponseMessage =  "<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\">\n" +
            "    <message nature=\"submit_failure\"> Error during form submission. </message>\n" +
            "</OpenRosaResponse>";
    public static final String ODK_COLLECT_APP_APK_FILE_NAME = "ODK Collect v1.4 rev 1038.apk";
    public static final String FILE_NOT_FOUND_ERROR_MESSAGE = "File not found.";
    public static final String OUTPUT_STREAM_CANNOT_BE_GENERATED = "Could not generate output stream.";
    public static final String CAN_NOT_READ_FILE = "File could not be read.";

}
