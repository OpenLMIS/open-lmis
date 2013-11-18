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

import java.io.IOException;
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
  private static WebElement NoRequisitionsPendingMessage=null;

  @FindBy(how = XPATH, using = "//div[@class='form-group']/h3")
  private static WebElement requisitionListHeader=null;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col0 colt0']")
  private static WebElement firstRow=null;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col3 colt3']/span")
  private static WebElement periodStartDate=null;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col4 colt4']/span")
  private static WebElement periodEndDate=null;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/h2")
  private static WebElement requisitionHeader=null;

  @FindBy(how = XPATH, using = "//div[@id='requisition-head-block']/div[1]/span")
  private static WebElement facilityLabel=null;

  @FindBy(how = ID, using = "calculatedOrderQuantity_0")
  private static WebElement calculateOrderQuantity=null;

  @FindBy(how = ID, using = "quantityRequested_0")
  private static WebElement requestedOrderQuantity=null;

  @FindBy(how = ID, using = "packsToShip_0")
  private static WebElement packsToShip=null;

  @FindBy(how = ID, using = "price_0")
  private static WebElement pricePerPack=null;

  @FindBy(how = ID, using = "cost_0")
  private static WebElement lineItemCost=null;

  @FindBy(how = ID, using = "totalCost")
  private static WebElement totalRnrCost=null;

  @FindBy(how = ID, using = "quantityApproved_0")
  private static WebElement quantityApproved = null;

  @FindBy(how = ID, using = "normalizedConsumption_0")
  private static WebElement adjustedTotalConsumption=null;

  @FindBy(how = ID, using = "amc_0")
  private static WebElement amc=null;

  @FindBy(how = ID, using = "maxStockQuantity_0")
  private static WebElement maxStockQuantity=null;

  @FindBy(how = ID, using = "calculatedOrderQuantity_0")
  private static WebElement calculatedOrderQuantity=null;

  @FindBy(how = ID, using = "nonFullSupplyTab")
  private static WebElement nonFullSupplyTab=null;

  @FindBy(how = ID, using = "fullSupplyTab")
  private static WebElement fullSupplyTab=null;

  @FindBy(how = ID, using = "remarks_0")
  private static WebElement remarks=null;

  @FindBy(how = XPATH, using = "//input[@value='Approve']")
  private static WebElement approveButton=null;

  @FindBy(how = XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton=null;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/div/div[2]/div[2]/div[3]/span")
  private static WebElement reportingPeriodInitRnRScreen=null;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.geographicZone.name']")
  private static WebElement geoZoneInitRnRScreen=null;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.geographicZone.parent.name']")
  private static WebElement parentGeoZoneInitRnRScreen=null;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.operatedBy.text']")
  private static WebElement operatedByInitRnRScreen=null;

  @FindBy(how = XPATH, using = "//i[@class='icon-ok']")
  private static WebElement emergencyIcon=null;


  public ApprovePage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public void verifyNoRequisitionPendingMessage() {
    testWebDriver.waitForElementToAppear(requisitionListHeader);
    assertTrue("NoRequisitionsPendingMessage is not displayed", NoRequisitionsPendingMessage.isDisplayed());
  }


  public String verifyAndClickRequisitionPresentForApproval() {

    testWebDriver.waitForElementToAppear(requisitionListHeader);
    assertTrue("No row of requisition is there for approval", firstRow.isDisplayed());
    String period = periodStartDate.getText().trim() + " - " + periodEndDate.getText().trim();
    firstRow.click();
    return period;
  }

  public void verifyRnRHeader(String FCode, String FName, String FCstring, String program, String periodDetails, String geoZone, String parentgeoZone, String operatedBy, String facilityType) {

    testWebDriver.waitForElementToAppear(requisitionHeader);
    String headerText = testWebDriver.getText(requisitionHeader);
    assertTrue(headerText.contains("Report and Requisition for " + program + " (" + facilityType + ")"));
    String facilityText = testWebDriver.getText(facilityLabel);
    assertTrue(facilityText.contains(FCode + FCstring + " - " + FName + FCstring));

    assertEquals(periodDetails.trim(), reportingPeriodInitRnRScreen.getText().trim());
    assertEquals(geoZone, geoZoneInitRnRScreen.getText().trim());
    assertEquals(parentgeoZone, parentGeoZoneInitRnRScreen.getText().trim());
    assertEquals(operatedBy, operatedByInitRnRScreen.getText().trim());
  }

  public void verifyEmergencyStatus() throws IOException {
    testWebDriver.sleep(2000);
    testWebDriver.waitForElementToAppear(emergencyIcon);
    assertTrue("Emergency icon should show up", emergencyIcon.isDisplayed());
  }

  public void verifyApprovedQuantity() {
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    testWebDriver.waitForElementToAppear(nonFullSupplyTab);
    testWebDriver.waitForElementToAppear(quantityApproved);
    testWebDriver.waitForElementToAppear(calculateOrderQuantity);
    String actualCalculatedOrderQuantity = calculateOrderQuantity.getText();
    String actualApproveQuantity = testWebDriver.getAttribute(quantityApproved, "value");
    assertEquals(actualApproveQuantity, actualCalculatedOrderQuantity);

    nonFullSupplyTab.click();
    testWebDriver.waitForElementToAppear(quantityApproved);
    String actualRequestedOrderQuantity = requestedOrderQuantity.getText();
    String actualApproveQuantityNonFullSupply = testWebDriver.getAttribute(quantityApproved, "value");
    assertEquals(actualApproveQuantityNonFullSupply, actualRequestedOrderQuantity);

  }

  public void verifyApprovedQuantityApprovedFromLowerHierarchy(String approvedQuantity) {
    testWebDriver.waitForElementToAppear(quantityApproved);
    String actualApproveQuantity = testWebDriver.getAttribute(quantityApproved, "value");
    assertEquals(approvedQuantity, actualApproveQuantity);
  }

  public void editApproveQuantityAndVerifyTotalCost(String approvedQuantity) {
    editApproveQuantity(approvedQuantity);
    remarks.click();
    assertEquals(parseInt(approvedQuantity) / 10, packsToShip.getText().trim());

    int packsToShip = parseInt(ApprovePage.packsToShip.getText().trim());
    float pricePerPack = parseFloat(ApprovePage.pricePerPack.getText().substring(1));

    BigDecimal cost = new BigDecimal((packsToShip * pricePerPack)).setScale(2, ROUND_HALF_UP);
    float lineItemCost = parseFloat(ApprovePage.lineItemCost.getText().substring(1));

    assertEquals(String.valueOf(cost), new BigDecimal(lineItemCost).setScale(2, ROUND_HALF_UP));
    float totalCostFullSupplyLineItem = lineItemCost;

    testWebDriver.waitForElementToAppear(nonFullSupplyTab);
    nonFullSupplyTab.click();

    testWebDriver.waitForElementToAppear(quantityApproved);
    quantityApproved.clear();
    quantityApproved.sendKeys(approvedQuantity);
    remarks.click();

    packsToShip = parseInt(ApprovePage.packsToShip.getText().trim());
    pricePerPack = parseFloat(ApprovePage.pricePerPack.getText().substring(1));

    lineItemCost = parseFloat(ApprovePage.lineItemCost.getText().substring(1));
    assertEquals(parseInt(approvedQuantity) / 10, packsToShip);

    BigDecimal costNonFullSupply = new BigDecimal((packsToShip * pricePerPack)).setScale(2, ROUND_HALF_UP);
    assertEquals(costNonFullSupply, new BigDecimal(lineItemCost).setScale(2, ROUND_HALF_UP));

    BigDecimal totalOverAllCost = new BigDecimal((totalCostFullSupplyLineItem + lineItemCost)).setScale(2, ROUND_HALF_UP);
    assertEquals(new BigDecimal(totalRnrCost.getText().trim().substring(1)).setScale(2, ROUND_HALF_UP), totalOverAllCost);
  }

  public void editApproveQuantity(String approvedQuantity) {
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    fullSupplyTab.click();

    testWebDriver.waitForElementToAppear(quantityApproved);
    int length = testWebDriver.getAttribute(quantityApproved, "value").length();
    for (int i = 0; i < length; i++)
      quantityApproved.sendKeys("\u0008");
    quantityApproved.sendKeys(approvedQuantity);

  }

  public String getApprovedQuantity() {
    testWebDriver.waitForElementToAppear(quantityApproved);
    return testWebDriver.getAttribute(quantityApproved, "value");
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

  public void editApproveQuantityAndVerifyTotalCostViewRequisition(String approvedQuantity) {
    editApproveQuantity(approvedQuantity);
    remarks.click();
    assertEquals(parseInt(approvedQuantity) / 10, packsToShip.getText().trim());

    BigDecimal cost = new BigDecimal((parseFloat(packsToShip.getText().trim()) * parseFloat(pricePerPack.getText().substring(1)))).setScale(2, ROUND_HALF_UP);
    assertEquals(String.valueOf(cost), lineItemCost.getText().substring(1));
    String totalCostFullSupplyLineItem = lineItemCost.getText().substring(1);

    BigDecimal totalOverAllCost = new BigDecimal(parseFloat(totalCostFullSupplyLineItem)).setScale(2, ROUND_HALF_UP);

    testWebDriver.waitForElementToAppear(totalRnrCost);
    assertEquals(String.valueOf(totalOverAllCost), totalRnrCost.getText().substring(1).trim());


  }


}