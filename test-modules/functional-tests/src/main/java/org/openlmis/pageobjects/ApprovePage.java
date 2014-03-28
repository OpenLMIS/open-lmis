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


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.math.BigDecimal;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class ApprovePage extends RequisitionPage {

  @FindBy(how = ID, using = "NoRequisitionsPendingMessage")
  private static WebElement NoRequisitionsPendingMessage = null;

  @FindBy(how = ID, using = "requisitionListLabel")
  private static WebElement requisitionListHeader = null;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col0 colt0']")
  private static WebElement firstRow = null;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col3 colt3']/span")
  private static WebElement periodStartDate = null;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col4 colt4']/span")
  private static WebElement periodEndDate = null;

  @FindBy(how = ID, using = "requisition-header")
  private static WebElement requisitionHeader = null;

  @FindBy(how = ID, using = "facility")
  private static WebElement facilityLabel = null;

  @FindBy(how = ID, using = "calculatedOrderQuantity_0")
  private static WebElement calculateOrderQuantity = null;

  @FindBy(how = ID, using = "quantityRequested_0")
  private static WebElement requestedOrderQuantity = null;

  @FindBy(how = ID, using = "packsToShip_0")
  private static WebElement packsToShip = null;

  @FindBy(how = ID, using = "price_0")
  private static WebElement pricePerPack = null;

  @FindBy(how = ID, using = "cost_0")
  private static WebElement lineItemCost = null;

  @FindBy(how = ID, using = "totalCost")
  private static WebElement totalRnrCost = null;

  @FindBy(how = ID, using = "quantityApproved_0")
  private static WebElement quantityApproved1 = null;

  @FindBy(how = ID, using = "quantityApproved_1")
  private static WebElement quantityApproved2 = null;

  @FindBy(how = ID, using = "normalizedConsumption_0")
  private static WebElement adjustedTotalConsumption = null;

  @FindBy(how = ID, using = "amc_0")
  private static WebElement amc = null;

  @FindBy(how = ID, using = "maxStockQuantity_0")
  private static WebElement maxStockQuantity = null;

  @FindBy(how = ID, using = "calculatedOrderQuantity_0")
  private static WebElement calculatedOrderQuantity = null;

  @FindBy(how = ID, using = "nonFullSupplyTab")
  private static WebElement nonFullSupplyTab = null;

  @FindBy(how = ID, using = "fullSupplyTab")
  private static WebElement fullSupplyTab = null;

  @FindBy(how = ID, using = "remarks_0")
  private static WebElement remarks = null;

  @FindBy(how = ID, using = "approveButton")
  private static WebElement approveButton = null;

  @FindBy(how = ID, using = "saveButton")
  private static WebElement saveButton = null;

  @FindBy(how = ID, using = "reportingPeriod")
  private static WebElement reportingPeriodInitRnRScreen = null;

  @FindBy(how = ID, using = "geographicZone")
  private static WebElement geoZoneInitRnRScreen = null;

  @FindBy(how = ID, using = "parentLevel")
  private static WebElement parentGeoZoneInitRnRScreen = null;

  @FindBy(how = ID, using = "operatedBy")
  private static WebElement operatedByInitRnRScreen = null;

  @FindBy(how = XPATH, using = "//i[@class='icon-ok']")
  private static WebElement emergencyIcon = null;

  public float totalCostFullSupplyLineItem;

  public ApprovePage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void verifyNoRequisitionPendingMessage() {
    testWebDriver.waitForElementToAppear(requisitionListHeader);
    assertTrue("NoRequisitionsPendingMessage is not displayed", NoRequisitionsPendingMessage.isDisplayed());
  }

  public String clickRequisitionPresentForApproval() {
    testWebDriver.waitForElementToAppear(requisitionListHeader);
    String period = periodStartDate.getText().trim() + " - " + periodEndDate.getText().trim();
    firstRow.click();
    return period;
  }

  public void verifyNoRequisitionMessage() {
    assertTrue("No row of requisition is there for approval", firstRow.isDisplayed());
  }

  public void verifyRnRHeader(String FCode, String FName, String FCString, String program, String periodDetails, String geoZone,
                              String parentGeoZone, String operatedBy, String facilityType) {
    testWebDriver.waitForElementToAppear(requisitionHeader);
    String headerText = testWebDriver.getText(requisitionHeader);
    assertTrue(headerText.contains("Report and Requisition for " + program + " (" + facilityType + ")"));
    String facilityText = testWebDriver.getText(facilityLabel);
    assertTrue(facilityText.contains(FCode + FCString + " - " + FName + FCString));

    assertEquals(periodDetails.trim(), reportingPeriodInitRnRScreen.getText().trim());
    assertEquals(geoZone, geoZoneInitRnRScreen.getText().trim());
    assertEquals(parentGeoZone, parentGeoZoneInitRnRScreen.getText().trim());
    assertEquals(operatedBy, operatedByInitRnRScreen.getText().trim());
  }

  public void verifyEmergencyStatus() {
    testWebDriver.sleep(2000);
    testWebDriver.waitForElementToAppear(emergencyIcon);
    assertTrue("Emergency icon should show up", emergencyIcon.isDisplayed());
  }

  public void verifyFullSupplyApprovedQuantity() {
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    testWebDriver.waitForElementToAppear(nonFullSupplyTab);
    testWebDriver.waitForElementToAppear(quantityApproved1);
    testWebDriver.waitForElementToAppear(calculateOrderQuantity);
    String actualCalculatedOrderQuantity = calculateOrderQuantity.getText();
    String actualApproveQuantity = testWebDriver.getAttribute(quantityApproved1, "value");
    assertEquals(actualApproveQuantity, actualCalculatedOrderQuantity);
  }

  public void accessNonFullSupplyTab() {
    nonFullSupplyTab.click();
  }

  public void verifyNonFullSupplyApprovedQuantity() {
    testWebDriver.waitForElementToAppear(quantityApproved1);
    String actualRequestedOrderQuantity = requestedOrderQuantity.getText();
    String actualApproveQuantityNonFullSupply = testWebDriver.getAttribute(quantityApproved1, "value");
    assertEquals(actualApproveQuantityNonFullSupply, actualRequestedOrderQuantity);
  }

  public void verifyApprovedQuantityApprovedFromLowerHierarchy(String approvedQuantity) {
    testWebDriver.waitForElementToAppear(quantityApproved1);
    String actualApproveQuantity = testWebDriver.getAttribute(quantityApproved1, "value");
    assertEquals(approvedQuantity, actualApproveQuantity);
  }

  public void verifyFullSupplyCost(String approvedQuantity) {
    assertEquals(parseInt(approvedQuantity) / 10, packsToShip.getText().trim());

    int packsToShip = parseInt(ApprovePage.packsToShip.getText().trim());
    float pricePerPack = parseFloat(ApprovePage.pricePerPack.getText().substring(1));

    BigDecimal cost = new BigDecimal((packsToShip * pricePerPack)).setScale(2, ROUND_HALF_UP);
    float lineItemCost = parseFloat(ApprovePage.lineItemCost.getText().substring(1));

    assertEquals(String.valueOf(cost), new BigDecimal(lineItemCost).setScale(2, ROUND_HALF_UP));
    totalCostFullSupplyLineItem = lineItemCost;
  }

  public void verifyNonFullSupplyCost(String approvedQuantity) {
    int packsToShip = parseInt(ApprovePage.packsToShip.getText().trim());
    float pricePerPack = parseFloat(ApprovePage.pricePerPack.getText().substring(1));

    float lineItemCost = parseFloat(ApprovePage.lineItemCost.getText().substring(1));
    assertEquals(parseInt(approvedQuantity) / 10, packsToShip);

    BigDecimal costNonFullSupply = new BigDecimal((packsToShip * pricePerPack)).setScale(2, ROUND_HALF_UP);
    assertEquals(costNonFullSupply, new BigDecimal(lineItemCost).setScale(2, ROUND_HALF_UP));
  }

  public void editFullSupplyApproveQuantity(String approvedQuantity) {
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    fullSupplyTab.click();

    testWebDriver.waitForElementToAppear(quantityApproved1);
    int length = testWebDriver.getAttribute(quantityApproved1, "value").length();
    for (int i = 0; i < length; i++)
      quantityApproved1.sendKeys("\u0008");
    quantityApproved1.sendKeys(approvedQuantity);
    remarks.click();
  }

  public void editNonFullSupplyApproveQuantity(String approvedQuantity) {
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    nonFullSupplyTab.click();

    testWebDriver.waitForElementToAppear(quantityApproved1);
    int length = testWebDriver.getAttribute(quantityApproved1, "value").length();
    for (int i = 0; i < length; i++)
      quantityApproved1.sendKeys("\u0008");
    quantityApproved1.sendKeys(approvedQuantity);
    remarks.click();
  }

  public String getApprovedQuantity() {
    testWebDriver.waitForElementToAppear(quantityApproved1);
    return testWebDriver.getAttribute(quantityApproved1, "value");
  }

  public String getAdjustedTotalConsumption() {
    testWebDriver.waitForElementToAppear(adjustedTotalConsumption);
    return adjustedTotalConsumption.getText();
  }

  public String getAMC() {
    testWebDriver.waitForElementToAppear(amc);
    return amc.getText();
  }

  public String getMaxStockQuantity() {
    testWebDriver.waitForElementToAppear(maxStockQuantity);
    return maxStockQuantity.getText();
  }

  public String getCalculatedOrderQuantity() {
    testWebDriver.waitForElementToAppear(calculatedOrderQuantity);
    return calculatedOrderQuantity.getText();
  }

  public void approveRequisition() {
    clickSaveButton();
    clickApproveButton();
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();

    testWebDriver.sleep(250);
  }

  public void clickApproveButton() {
    testWebDriver.waitForElementToAppear(approveButton);
    approveButton.click();

    testWebDriver.sleep(250);
  }

  public void VerifyTotalCostViewRequisition(String approvedQuantity) {
    assertEquals(parseInt(approvedQuantity) / 10, packsToShip.getText().trim());

    BigDecimal cost = new BigDecimal((parseFloat(packsToShip.getText().trim()) * parseFloat(pricePerPack.getText().substring(1)))).setScale(2, ROUND_HALF_UP);
    assertEquals(String.valueOf(cost), lineItemCost.getText().substring(1));
    String totalCostFullSupplyLineItem = lineItemCost.getText().substring(1);

    BigDecimal totalOverAllCost = new BigDecimal(parseFloat(totalCostFullSupplyLineItem)).setScale(2, ROUND_HALF_UP);

    testWebDriver.waitForElementToAppear(totalRnrCost);
    assertEquals(String.valueOf(totalOverAllCost), totalRnrCost.getText().substring(1).trim());
  }

  public boolean approveQuantityVisible(int row) {
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    fullSupplyTab.click();

    testWebDriver.waitForElementToAppear(quantityApproved1);
    if (row == 1)
      return (quantityApproved1.isEnabled());
    else
      return (quantityApproved2.isEnabled());
  }

  public String getPacksToShip() {
    return packsToShip.getText();
  }

  public void waitForPageToAppear() {
    testWebDriver.waitForElementToAppear(requisitionListHeader);
  }
}