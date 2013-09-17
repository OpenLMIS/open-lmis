/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;

import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class EPIUse extends DistributionTab {

  @FindBy(how = XPATH, using = "//div[1]/div/div/ng-include/div/ul/li[4]/a/span[2]")
  private static WebElement epiUseTab;

  @FindBy(how = XPATH, using = "//div[@class='left-navigation ng-scope']/ul/li[4]/a/span[1][@class='status-icon']")
  public static WebElement overallEPIUseIcon;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Apply NR to all fields')]")
  private static WebElement applyNRToAllFieldsCheckbox;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okButton;

  @FindBy(how = ID, using = "button_Cancel")
  private static WebElement cancelButton;

  public EPIUse(TestWebDriver driver) {
    super(driver);
  }

  @Override
  public void verifyIndicator(String color) {
    verifyOverallIndicator(overallEPIUseIcon, color);
  }

  @Override
  public void enterValues(Map<String, String> data) {
    enterValueInDistributed(data.get("distributed"), 1);
    enterValueInExpirationDate(data.get("expirationDate"), 1);
    enterValueInLoss(data.get("loss"), 1);
    enterValueInReceived(data.get("received"), 1);
    enterValueInStockAtFirstOfMonth(data.get("firstOfMonth"), 1);
  }

  @Override
  public void navigate() {
    testWebDriver.waitForElementToAppear(epiUseTab);
    epiUseTab.click();
  }

  public void enterValueInStockAtFirstOfMonth(String value, int rownumber) {
    rownumber = rownumber - 1;
    WebElement stockAtFirstOfMonth = testWebDriver.getElementByName("stockAtFirstOfMonth" + rownumber);
    sendKeys(stockAtFirstOfMonth, value);
  }

  public void enterValueInReceived(String value, int rownumber) {
    rownumber = rownumber - 1;
    WebElement received = testWebDriver.getElementByName("received" + rownumber);
    sendKeys(received, value);
  }

  public void enterValueInDistributed(String value, int rownumber) {
    rownumber = rownumber - 1;
    WebElement distributed = testWebDriver.getElementByName("distributed" + rownumber);
    sendKeys(distributed, value);
  }

  public void enterValueInLoss(String value, int rownumber) {
    rownumber = rownumber - 1;
    WebElement loss = testWebDriver.getElementByName("loss" + rownumber);
    sendKeys(loss, value);
  }

  public void enterValueInStockAtEndOfMonth(String value, int rownumber) {
    rownumber = rownumber - 1;
    WebElement stockAtEndOfMonth = testWebDriver.getElementByName("stockAtEndOfMonth" + rownumber);
    sendKeys(stockAtEndOfMonth, value);
  }

  public void enterValueInExpirationDate(String value, int rownumber) {
    rownumber = rownumber - 1;
    WebElement expirationDate = testWebDriver.getElementByName("expirationDate" + rownumber);
    sendKeys(expirationDate, value);
  }

  public void checkUncheckStockAtFirstOfMonthNotRecorded(int rownumber) {
    rownumber = rownumber - 1;
    WebElement stockAtFirstOfMonthNotRecordedCheckBox = testWebDriver.getElementById("stockAtFirstOfMonth" + rownumber);
    stockAtFirstOfMonthNotRecordedCheckBox.click();
  }

  public void checkUncheckReceivedNotRecorded(int rownumber) {
    rownumber = rownumber - 1;
    WebElement receivedNotRecordedCheckBox = testWebDriver.getElementById("received" + rownumber);
    receivedNotRecordedCheckBox.click();
  }

  public void checkUncheckDistributedNotRecorded(int rownumber) {
    rownumber = rownumber - 1;
    WebElement distributedNotRecordedCheckBox = testWebDriver.getElementById("distributed" + rownumber);
    distributedNotRecordedCheckBox.click();
  }

  public void checkUncheckLossNotRecorded(int rownumber) {
    rownumber = rownumber - 1;
    WebElement lossNotRecordedCheckBox = testWebDriver.getElementById("loss" + rownumber);
    lossNotRecordedCheckBox.click();
  }

  public void checkApplyNRToAllFields(boolean confirm) {
    applyNRToAllFieldsCheckbox.click();
    if (confirm)
      okButton.click();
    else
      cancelButton.click();
  }

  public void checkUncheckStockAtEndOfMonthNotRecorded(int rownumber) {
    rownumber = rownumber - 1;
    WebElement stockAtEndOfMonthNotRecordedCheckBox = testWebDriver.getElementById("stockAtEndOfMonth" + rownumber);
    stockAtEndOfMonthNotRecordedCheckBox.click();
  }

  public void checkUncheckExpirationDateNotRecorded(int rownumber) {
    rownumber = rownumber - 1;
    WebElement expirationDateNotRecordedCheckBox = testWebDriver.getElementById("expirationDate" + rownumber);
    expirationDateNotRecordedCheckBox.click();
  }

  public void verifyProductGroup(String productGroup, int rownumber) {
    testWebDriver.sleep(2000);
    WebElement productGroupLbl = testWebDriver.getElementByXpath(".//*[@id='epiUseTable']/table/tbody/tr[" + rownumber + "]/td[1]/span");
    testWebDriver.waitForElementToAppear(productGroupLbl);
    assertEquals(productGroup, productGroupLbl.getText());
  }

  public void verifyStockAtFirstOfMonth(String stockAtFirstOfMonth, int rownumber) {
    rownumber = rownumber - 1;
    WebElement stockAtFirstOfMonthTxt = testWebDriver.getElementByName("stockAtFirstOfMonth" + rownumber);
    assertEquals(stockAtFirstOfMonth, stockAtFirstOfMonthTxt.getAttribute("value"));
  }

  public void verifyReceived(String received, int rownumber) {
    rownumber = rownumber - 1;
    WebElement receivedTxt = testWebDriver.getElementByName("received" + rownumber);
    assertEquals(received, receivedTxt.getAttribute("value"));
  }

  public void verifyDistributed(String distributed, int rownumber) {
    rownumber = rownumber - 1;
    WebElement distributedTxt = testWebDriver.getElementByName("distributed" + rownumber);
    assertEquals(distributed, distributedTxt.getAttribute("value"));
  }

  public void verifyLoss(String loss, int rownumber) {
    rownumber = rownumber - 1;
    WebElement lossTxt = testWebDriver.getElementByName("loss" + rownumber);
    assertEquals(loss, lossTxt.getAttribute("value"));
  }

  public void verifyStockAtEndOfMonth(String stockAtEndOfMonth, int rownumber) {
    rownumber = rownumber - 1;
    WebElement stockAtEndOfMonthTxt = testWebDriver.getElementByName("stockAtEndOfMonth" + rownumber);
    assertEquals(stockAtEndOfMonth, stockAtEndOfMonthTxt.getAttribute("value"));
  }

  public void verifyExpirationDate(String expirationDate, int rownumber) {
    rownumber = rownumber - 1;
    WebElement expirationDateTxt = testWebDriver.getElementByName("expirationDate" + rownumber);
    assertEquals(expirationDate, expirationDateTxt.getAttribute("value"));
  }

  public void verifyStockAtFirstOfMonthStatus(boolean status, int rownumber) {
    rownumber = rownumber - 1;
    WebElement stockAtFirstOfMonthTxt = testWebDriver.getElementByName("stockAtFirstOfMonth" + rownumber);
    if (status)
      assertTrue("Stock At First Of Month Disabled.", stockAtFirstOfMonthTxt.isEnabled());
    else
      assertFalse("Stock At First Of Month Enabled.", stockAtFirstOfMonthTxt.isEnabled());
  }

  public void verifyReceivedStatus(boolean status, int rownumber) {
    rownumber = rownumber - 1;
    WebElement receivedTxt = testWebDriver.getElementByName("received" + rownumber);
    if (status)
      assertTrue("Received Disabled.", receivedTxt.isEnabled());
    else
      assertFalse("Received Enabled.", receivedTxt.isEnabled());
  }

  public void verifyDistributedStatus(boolean status, int rownumber) {
    rownumber = rownumber - 1;
    WebElement distributedTxt = testWebDriver.getElementByName("distributed" + rownumber);
    if (status)
      assertTrue("Distributed Disabled.", distributedTxt.isEnabled());
    else
      assertFalse("Distributed Enabled.", distributedTxt.isEnabled());
  }

  public void verifyLossStatus(boolean status, int rownumber) {
    rownumber = rownumber - 1;
    WebElement lossTxt = testWebDriver.getElementByName("loss" + rownumber);
    if (status)
      assertTrue("Loss Disabled.", lossTxt.isEnabled());
    else
      assertFalse("Loss Enabled.", lossTxt.isEnabled());
  }

  public void verifyStockAtEndOfMonthStatus(boolean status, int rownumber) {
    rownumber = rownumber - 1;
    WebElement stockAtEndOfMonthTxt = testWebDriver.getElementByName("stockAtEndOfMonth" + rownumber);
    if (status)
      assertTrue("Stock At End Of Month Disabled.", stockAtEndOfMonthTxt.isEnabled());
    else
      assertFalse("Stock At End Of Month Enabled.", stockAtEndOfMonthTxt.isEnabled());
  }

  public void verifyExpirationDateStatus(boolean status, int rownumber) {
    rownumber = rownumber - 1;
    WebElement expirationDateTxt = testWebDriver.getElementByName("expirationDate" + rownumber);
    if (status)
      assertTrue("Expiration Date Disabled.", expirationDateTxt.isEnabled());
    else
      assertFalse("Expiration Date Enabled.", expirationDateTxt.isEnabled());
  }

  public void verifyTotal(String total, int rownumber) {
    WebElement totalLbl = testWebDriver.getElementByXpath(".//*[@id='epiUseTable']/table/tbody/tr[" + rownumber + "]/td[4]/span");
    assertEquals(total, totalLbl.getText());
  }

  @Override
  public void verifyData(Map<String, String> epiData) {
    verifyDistributed(epiData.get("distributed"), 1);
    verifyLoss(epiData.get("loss"), 1);
    verifyExpirationDate(epiData.get("expirationDate"), 1);
    verifyReceived(epiData.get("received"), 1);
    verifyStockAtEndOfMonth(epiData.get("endOfMonth"), 1);
    verifyStockAtFirstOfMonth(epiData.get("firstOfMonth"), 1);
    verifyTotal(epiData.get("total"), 1);
  }
}