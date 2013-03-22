/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.authentication;

import lombok.Data;

@Data
public class UserToken {

    private final String userName;
    private final Integer userId;
    private final boolean authenticated;


    public UserToken(String userName, Integer userId, boolean authenticated) {
        this.userName = userName;
        this.userId = userId;
        this.authenticated = authenticated;
    }
}
