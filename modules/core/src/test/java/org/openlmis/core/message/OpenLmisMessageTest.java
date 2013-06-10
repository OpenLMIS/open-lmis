/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.message;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ResourceBundle;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(ResourceBundle.class)
public class OpenLmisMessageTest {

  @Test
  public void shouldGetMessageWithCodeAndParams() {
    OpenLmisMessage openLmisMessage = new OpenLmisMessage("code", "1", "2", "3");
    assertThat(openLmisMessage.toString(), is("code: code, params: { 1; 2; 3 }"));
  }


}
