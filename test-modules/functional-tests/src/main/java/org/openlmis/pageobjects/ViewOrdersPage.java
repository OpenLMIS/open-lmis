/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;


public class ViewOrdersPage extends RequisitionPage {

  @FindBy(how = How.ID, using = "NoRequisitionsPendingMessage")
  private static WebElement NoRequisitionsPendingMessage;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col0 colt0']/span")
  private static WebElement orderNumberOnViewOrdersScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
  private static WebElement programOnViewOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCell  col1 colt1']/div[@class='ngCellText ng-scope']/span")
  private static WebElement facilityCodeNameOnOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCell  col3 colt3']/div[@class='ngCellText ng-scope']/span")
  private static WebElement periodDetailsOnViewOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col4 colt4']/span")
  private static WebElement supplyDepotOnViewOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCell col6 colt6']/div[@class='ngCellText ng-scope']/span/div[@id='releasedOrderStaus']")
  private static WebElement orderStatusOnViewOrderScreen;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']")
  private static WebElement successMessageDiv;

  @FindBy(how = How.XPATH, using = "//div[@id='NoRequisitionsPendingMessage']")
  private static WebElement noRequisitionPendingMessage;

  public ViewOrdersPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void verifyOrderListElements(String program, String orderNumber, String facilityCodeName, String periodDetails, String supplyFacilityName, String orderStatus) throws IOException {
    testWebDriver.waitForElementToAppear(programOnViewOrderScreen);
    SeleneseTestNgHelper.assertEquals(programOnViewOrderScreen.getText().trim(), program);
    SeleneseTestNgHelper.assertEquals(orderNumberOnViewOrdersScreen.getText().trim(), orderNumber);
    SeleneseTestNgHelper.assertEquals(facilityCodeNameOnOrderScreen.getText().trim(), facilityCodeName);
    SeleneseTestNgHelper.assertEquals(periodDetailsOnViewOrderScreen.getText().trim(), periodDetails);
    SeleneseTestNgHelper.assertEquals(supplyDepotOnViewOrderScreen.getText().trim(), supplyFacilityName);
    SeleneseTestNgHelper.assertEquals(orderStatusOnViewOrderScreen.getText().trim(), orderStatus);
  }

}