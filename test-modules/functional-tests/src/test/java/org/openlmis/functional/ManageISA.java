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

import java.io.IOException;

import static java.lang.String.valueOf;
import static org.openlmis.pageobjects.CreateFacilityPage.SaveButton;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageISA extends TestCaseHelper {

  @BeforeMethod(groups = {"functional2"})
  public void setUp() throws Exception {
    super.setup();
    setupProgramProductTestDataWithCategories("P1", "antibiotic1", "C1", "VACCINES");
  }


  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void shouldOverrideIsa(String userSIC, String password, String program) throws Exception {
    ProgramProductISAPage programProductISAPage = navigateProgramProductISAPage(userSIC, password, program);
    programProductISAPage.fillProgramProductISA("1", "2", "3", "4", "5", "10");
    programProductISAPage.saveISA();

    CreateFacilityPage createFacilityPage = new HomePage(testWebDriver).navigateCreateFacility();

    String geoZone = "Ngorongoro";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "FCcode";
    String facilityNamePrefix = "FCname";

    createFacilityPage.enterValuesInFacility(facilityCodePrefix, facilityNamePrefix,
      program, geoZone, facilityType, operatedBy, valueOf(333), true);

    createFacilityPage.overrideIsa(24);
    createFacilityPage.verifyCalculatedIsa(671);
    createFacilityPage.clickIsaDoneButton();
    createFacilityPage.verifyOverriddenIsa(24);

    SaveButton.click();
  }


  private ProgramProductISAPage navigateProgramProductISAPage(String userSIC, String password, String program) throws IOException {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    ProgramProductISAPage programProductISAPage = homePage.navigateProgramProductISA();
    programProductISAPage.selectProgram(program);
    programProductISAPage.editFormula();
    return programProductISAPage;
  }


  @AfterMethod(groups = {"functional2"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123", "Admin123", "VACCINES"}
    };

  }

}

