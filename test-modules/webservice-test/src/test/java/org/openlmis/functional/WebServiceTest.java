/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.testng.annotations.*;




public class WebServiceTest  {

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
  }

  @DataProvider(name = "envData")
  public Object[][] getEnvData() {
    return new Object[][]{};
  }

  @Test(groups = {"webservice"})
  public void testWebServiceTest() throws Exception {

  }

}

