/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static java.lang.String.valueOf;
import static org.openlmis.pageobjects.CreateFacilityPage.SaveButton;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class PageAccessAuthentication extends TestCaseHelper {

  @Before
  public void setUp() throws Exception {
    super.setup();
  }

    @When("^I access initiate requisition page through URL$")
    public void accessInitiateRequisitionPageThroughURL() throws Exception {
        testWebDriver.waitForElementToAppear(new HomePage(testWebDriver).getLogoutLink());
        testWebDriver.getUrl(baseUrlGlobal + "public/pages/logistics/rnr/index.html#/init-rnr");
        testWebDriver.sleep(2000);
    }

    @When("^I access create facility page through URL$")
    public void accessCreateFacilityPageThroughURL() throws Exception {
        testWebDriver.waitForElementToAppear(new HomePage(testWebDriver).getLogoutLink());
        testWebDriver.getUrl(baseUrlGlobal + "public/pages/admin/facility/index.html#/create-facility");
        testWebDriver.sleep(2000);
    }

    @Then("^I should see unauthorized access message$")
    public void verifyUnauthorizedAccessPage() throws Exception {
        assertEquals("You are not authorized to view the requested page.", new AccessDeniedPage(testWebDriver).getAccessDeniedText());
    }

  @After
  public void tearDown() throws Exception {
      if (!testWebDriver.getElementById("username").isDisplayed()) {
          HomePage homePage = new HomePage(testWebDriver);
          homePage.logout(baseUrlGlobal);
          dbWrapper.deleteData();
          dbWrapper.closeConnection();
      }
  }

}

