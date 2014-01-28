/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ManagePodPage;
import org.openlmis.pageobjects.UpdatePodPage;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.*;

public class PODPagination extends TestCaseHelper{


  public static final String USER = "user";
  public static final String PASSWORD = "password";
  public static final String PROGRAM = "program";

  public Map<String, String> podPaginationData = new HashMap<String, String>() {{
    put(USER, "storeInCharge");
    put(PASSWORD, "Admin123");
    put(PROGRAM, "HIV");
  }};

  UpdatePodPage updatePodPage;

  @BeforeMethod(groups = {"requisition"})
  public void setUp() throws Exception {
    super.setup();
    updatePodPage = new UpdatePodPage(testWebDriver);

    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(podPaginationData.get(PROGRAM));
    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_POD");

    setupTestUserRoleRightsData("200", podPaginationData.get(USER), rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", podPaginationData.get(PROGRAM), "F10", true);
    dbWrapper.insertFulfilmentRoleAssignment("storeInCharge", "store in-charge", "F10");
  }

  @Test(groups = {"requisition"})
  public void testRnRPaginationAndDefaultDisplayOrder() throws Exception {
    dbWrapper.setupMultipleProducts(podPaginationData.get(PROGRAM), "Lvl3 Hospital", 11, true);
    dbWrapper.insertRequisitionWithMultipleLineItems(11, podPaginationData.get(PROGRAM), true, "F10", false);
    dbWrapper.convertRequisitionToOrder(dbWrapper.getMaxRnrID(), "READY_TO_PACK");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(podPaginationData.get(USER), podPaginationData.get(PASSWORD));
    ManagePodPage managePodPage = homePage.navigateManagePOD();
    managePodPage.clickUpdatePODLink();
    verifyNumberOFPageLinksDisplayed(25,10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyFirstAndPreviousPageLinksDisabled();
    verifyNumberOfProductsVisibleOnPage(10);
    verifyDisplayOrderOnPage(10, new String[]{"F0", "F1", "F10", "F2", "F3", "F4", "F5", "F6", "F7", "F8"});

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyFirstAndPreviousPageLinksEnabled();
    verifyNumberOfProductsVisibleOnPage(10);
    verifyDisplayOrderOnPage(10, new String[]{"F9", "NF0", "NF1", "NF10", "NF2", "NF3", "NF4", "NF5", "NF6", "NF7"});

    updatePodPage.navigateToNextPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyFirstAndPreviousPageLinksEnabled();
    verifyNumberOfProductsVisibleOnPage(2);
    verifyDisplayOrderOnPage(2, new String[]{"NF8", "NF9"});

    updatePodPage.navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyFirstAndPreviousPageLinksDisabled();
    verifyNumberOfProductsVisibleOnPage(10);

    updatePodPage.navigateToLastPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyFirstAndPreviousPageLinksEnabled();
    verifyNumberOfProductsVisibleOnPage(2);

    updatePodPage.navigateToPreviousPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyFirstAndPreviousPageLinksEnabled();
    verifyNumberOfProductsVisibleOnPage(10);
  }

  private void verifyDisplayOrderOnPage(int numberOfProducts, String[] productCodes) {
    for (int i = 1; i < numberOfProducts; i++) {
      assertEquals(productCodes[i-1],updatePodPage.getProductCode(i));
    }
  }

  private void verifyNextAndLastPageLinksDisabled() {
    assertFalse(updatePodPage.isNextPageLinkEnabled());
    assertFalse(updatePodPage.isLastPageLinkEnabled());
  }

  private void verifyFirstAndPreviousPageLinksEnabled() {
    assertTrue(updatePodPage.isFirstPageLinkEnabled());
    assertTrue(updatePodPage.isPreviousPageLinkEnabled());
  }

  private void navigateToPage(int pageNumber) {
    WebElement page = testWebDriver.getElementById(String.valueOf(pageNumber));
    testWebDriver.waitForElementToAppear(page);
    page.click();
  }

  private void verifyNumberOfProductsVisibleOnPage(int numberOfProducts) {
    assertEquals(numberOfProducts, testWebDriver.getElementsSizeByXpath("//table[@id='podTable']/tbody"));
  }

  private void verifyFirstAndPreviousPageLinksDisabled() {
    assertFalse(updatePodPage.isFirstPageLinkEnabled());
    assertFalse(updatePodPage.isPreviousPageLinkEnabled());
  }

  private void verifyNextAndLastPageLinksEnabled() {
    assertTrue(updatePodPage.isNextPageLinkEnabled());
    assertTrue(updatePodPage.isLastPageLinkEnabled());
  }

  private void verifyPageNumberSelected(int pageNumber) {
    WebElement page = testWebDriver.getElementById(String.valueOf(pageNumber));
    testWebDriver.waitForElementToAppear(page);
    assertEquals("rgba(96, 172, 175, 1)", page.getCssValue("background-color"));
  }

  private void verifyPageNumberLinksDisplayed() {
    assertTrue(updatePodPage.isFirstPageLinkDisplayed());
    assertTrue(updatePodPage.isPreviousPageLinkDisplayed());
    assertTrue(updatePodPage.isNextPageLinkDisplayed());
    assertTrue(updatePodPage.isLastPageLinkDisplayed());
  }

  private void verifyNumberOFPageLinksDisplayed(int numberOfProducts, int numberOfLineItemsPerPage) {
    testWebDriver.waitForAjax();
    int numberOfPages = numberOfProducts / numberOfLineItemsPerPage;
    if (numberOfProducts % numberOfLineItemsPerPage != 0) {
      numberOfPages = numberOfPages + 1;
    }
    for (int i = 1; i <= numberOfPages; i++) {
      WebElement page = testWebDriver.getElementById(String.valueOf(i));
      testWebDriver.waitForElementToAppear(page);
      assertTrue(page.isDisplayed());
    }
  }

}
