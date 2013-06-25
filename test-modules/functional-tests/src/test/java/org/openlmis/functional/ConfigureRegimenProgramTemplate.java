/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureRegimenProgramTemplate extends TestCaseHelper {

  private static String adultsRegimen = "Adults";
  private static String paediatricsRegimen = "Paediatrics";

  @BeforeMethod(groups = {"functional"})
  public void setUp() throws Exception {
    super.setup();
  }


  @Test(groups = {"functional2"}, dataProvider = "Data-Provider")
  public void testVerifyNewRegimenCreated(String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RegimenTemplateConfigPage regimenTemplateConfigPage = homePage.navigateToRegimenConfigTemplate();
    regimenTemplateConfigPage.configureProgram(program);
    regimenTemplateConfigPage.AddNewRegimen(adultsRegimen,"Code1","Name1",true);
    regimenTemplateConfigPage.SaveRegime();
    regimenTemplateConfigPage.verifySuccessMessage();
  }



  @AfterMethod(groups = {"functional"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider")
  public Object[][] parameterVerifyRnRScreen() {
    return new Object[][]{
      {"ESSENTIAL MEDICINES", new String[]{"Admin123", "Admin123"}}
    };

  }
}

