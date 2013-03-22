/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis;

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
