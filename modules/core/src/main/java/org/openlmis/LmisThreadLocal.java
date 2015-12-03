/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis;

/**
 * LmisThreadLocal is used to get/set/remove current thread's value of a thread-local
 */
public class LmisThreadLocal {

    public static final ThreadLocal<String> lmisThreadLocal = new ThreadLocal<>();

    public static void set(String userName) {
        lmisThreadLocal.set(userName);
    }

    public static void unset() {
        lmisThreadLocal.remove();
    }

    public static String get() {
        return lmisThreadLocal.get();
    }
}
