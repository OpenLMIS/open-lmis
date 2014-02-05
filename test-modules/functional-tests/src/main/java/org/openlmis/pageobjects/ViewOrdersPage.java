/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pageobjects;

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestCase.assertEquals;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class ViewOrdersPage extends RequisitionPage {

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col0 colt0']/span")
  private static WebElement orderNumberOnViewOrdersScreen = null;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
  private static WebElement programOnViewOrderScreen = null;

  @FindBy(how = How.XPATH, using = "//div[@class='ng-scope ngRow even']/div[2]/div[2]/div/span")
  private static WebElement facilityCodeNameOnOrderScreen = null;

  @FindBy(how = How.XPATH, using = "//div[@class='ng-scope ngRow even']/div[4]/div[2]/div/span")
  private static WebElement periodDetailsOnViewOrderScreen = null;

  @FindBy(how = How.XPATH, using = "//div[@class='ng-scope ngRow even']/div[5]/div[2]/div/span")
  private static WebElement supplyDepotOnViewOrderScreen = null;

  @FindBy(how = How.XPATH, using = "(//div[@id='orderStatus'])[1]")
  private static WebElement orderStatusOnViewOrderScreen = null;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Download CSV')]")
  private static WebElement downloadCSVLink = null;

  @FindBy(how = How.XPATH, using = "//span[contains(text(),'No products in this order')]")
  private static WebElement noOrderMessage = null;

  @FindBy(how = XPATH, using = "//i[@class='icon-ok']")
  private static WebElement emergencyIcon = null;

  @FindBy(how = ID, using = "ordersGrid")
  private static WebElement ordersGrid = null;

  @FindBy(how = XPATH, using = "//span[@openlmis-message='message.no.order']")
  private static WebElement noRequisitionReleasedAsOrderYet = null;

  public ViewOrdersPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void verifyNoRequisitionReleasedAsOrderMessage() {
    testWebDriver.waitForPageToLoad();
    noRequisitionReleasedAsOrderYet.isDisplayed();
  }

  public void isFirstRowPresent() {
    testWebDriver.waitForPageToLoad();
    assertTrue("First row should show up", programOnViewOrderScreen.isDisplayed());
  }

  public void verifyOrderListElements(String program, long orderId, String facilityCodeName, String periodDetails, String supplyFacilityName, String orderStatus, boolean downloadLinkPresent) {
    testWebDriver.refresh();
    testWebDriver.waitForElementToAppear(programOnViewOrderScreen);
    assertEquals(programOnViewOrderScreen.getText().trim(), program);
    assertEquals(orderNumberOnViewOrdersScreen.getText().trim(), String.valueOf(orderId));
    assertEquals(facilityCodeNameOnOrderScreen.getText().trim(), facilityCodeName);
    assertEquals(periodDetailsOnViewOrderScreen.getText().trim(), periodDetails);
    assertEquals(supplyDepotOnViewOrderScreen.getText().trim(), supplyFacilityName);
    SeleneseTestNgHelper.assertEquals(orderStatusOnViewOrderScreen.getText().trim(), orderStatus);
    if (downloadLinkPresent)
      assertTrue("'Download CSV' link should show up", downloadCSVLink.isDisplayed());
    else
      assertTrue("'No products in this order' message should show up", noOrderMessage.isDisplayed());
  }

  public void downloadCSV() throws InterruptedException {
    testWebDriver.waitForElementToAppear(programOnViewOrderScreen);
    downloadFileWhileSaveDialogOPen(downloadCSVLink);
  }

  public void verifyEmergencyStatus() {
    testWebDriver.waitForElementToAppear(emergencyIcon);
    assertTrue("Emergency icon should show up", emergencyIcon.isDisplayed());
  }

  public int getNumberOfLineItems() {
    testWebDriver.waitForElementToAppear(ordersGrid);
    return ordersGrid.findElements(By.className("ngRow")).size();
  }

  public void verifyProgram(int row, String program) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("(//div[@class='ngCellText ng-scope col2 colt2']/span)[" + row + "]"));
    String actualProgram = testWebDriver.getElementByXpath("(//div[@class='ngCellText ng-scope col2 colt2']/span)[" + row + "]").getText();
    SeleneseTestNgHelper.assertEquals(actualProgram, program);
  }
}