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
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.math.BigDecimal;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class ApprovePage extends RequisitionPage {

  @FindBy(how = ID, using = "NoRequisitionsPendingMessage")
  private static WebElement NoRequisitionsPendingMessage;

  @FindBy(how = XPATH, using = "//div[@class='form-group']/h3")
  private static WebElement requisitionListHeader;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col0 colt0']")
  private static WebElement firstRow;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col3 colt3']/span")
  private static WebElement periodStartDate;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col4 colt4']/span")
  private static WebElement periodEndDate;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/h2")
  private static WebElement requisitionHeader;

  @FindBy(how = XPATH, using = "//div[@id='requisition-header']/div[@class='info-box']/div[@class='row-fluid'][1]/div[1]")
  private static WebElement facilityLabel;

  @FindBy(how = ID, using = "calculatedOrderQuantity_0")
  private static WebElement calculateOrderQuantity;

  @FindBy(how = ID, using = "quantityRequested_0")
  private static WebElement requestedOrderQuantity;

  @FindBy(how = ID, using = "packsToShip_0")
  private static WebElement packsToShip;

  @FindBy(how = ID, using = "price_0")
  private static WebElement pricePerPack;

  @FindBy(how = ID, using = "cost_0")
  private static WebElement lineItemCost;

  @FindBy(how = ID, using = "totalCost")
  private static WebElement totalRnrCost;

  @FindBy(how = ID, using = "quantityApproved_0")
  private static WebElement quantityApproved;

  @FindBy(how = ID, using = "nonFullSupplyTab")
  private static WebElement nonFullSupplyTab;

  @FindBy(how = ID, using = "fullSupplyTab")
  private static WebElement fullSupplyTab;

  @FindBy(how = ID, using = "remarks_0")
  private static WebElement remarks;

  @FindBy(how = XPATH, using = "//input[@value='Approve']")
  private static WebElement approveButton;

  @FindBy(how = XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv;

  @FindBy(how = XPATH, using = "//div[@class='info-box']/div[2]/div[3]")
  private static WebElement reportingPeriodInitRnRScreen;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.geographicZone.name']")
  private static WebElement geoZoneInitRnRScreen;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.geographicZone.parent.name']")
  private static WebElement parentGeoZoneInitRnRScreen;

  @FindBy(how = XPATH, using = "//span[@ng-bind='rnr.facility.operatedBy.text']")
  private static WebElement operatedByInitRnRScreen;

  @FindBy(how = XPATH, using = "//div[contains(text(),'R&R approved successfully!')]")
  private static WebElement approvedSuccessMessage;


  public ApprovePage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
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

    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(requisitionHeader);
    String headerText = testWebDriver.getText(requisitionHeader);
    assertTrue(headerText.contains("Report and Requisition for " + program + " (" + facilityType + ")"));
    String facilityText = testWebDriver.getText(facilityLabel);
    assertTrue(facilityText.contains(FCode + FCstring + " - " + FName + FCstring));

    assertEquals(reportingPeriodInitRnRScreen.getText().trim().substring("Reporting Period: ".length()), periodDetails.trim());
    assertEquals(geoZone, geoZoneInitRnRScreen.getText().trim());
    assertEquals(parentgeoZone, parentGeoZoneInitRnRScreen.getText().trim());
    assertEquals(operatedBy, operatedByInitRnRScreen.getText().trim());
  }


  public void verifyApprovedQuantity() {
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    testWebDriver.waitForElementToAppear(nonFullSupplyTab);
    testWebDriver.waitForElementToAppear(quantityApproved);
    testWebDriver.sleep(1500);
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
    assertEquals(actualApproveQuantity, approvedQuantity);
  }

  public void editApproveQuantityAndVerifyTotalCost(String approvedQuantity) {
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    fullSupplyTab.click();
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(quantityApproved);
    quantityApproved.clear();
    quantityApproved.sendKeys(approvedQuantity);
    remarks.click();
    assertEquals(packsToShip.getText().trim(), parseInt(approvedQuantity) / 10);

    int packsToShip = parseInt(ApprovePage.packsToShip.getText().trim());
    float pricePerPack = parseFloat(ApprovePage.pricePerPack.getText().substring(1));

    BigDecimal cost = new BigDecimal((packsToShip * pricePerPack)).setScale(2, ROUND_HALF_UP);
    float lineItemCost = parseFloat(ApprovePage.lineItemCost.getText().substring(1));

    assertEquals(String.valueOf(cost), new BigDecimal(lineItemCost).setScale(2, ROUND_HALF_UP));
    float totalCostFullSupplyLineItem = lineItemCost;
    testWebDriver.sleep(1000);

    nonFullSupplyTab.click();

    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(quantityApproved);
    quantityApproved.clear();
    quantityApproved.sendKeys(approvedQuantity);
    remarks.click();

    packsToShip = parseInt(ApprovePage.packsToShip.getText().trim());
    pricePerPack = parseFloat(ApprovePage.pricePerPack.getText().substring(1));

    lineItemCost = parseFloat(ApprovePage.lineItemCost.getText().substring(1));
    assertEquals(packsToShip, parseInt(approvedQuantity) / 10);

    BigDecimal costNonFullSupply = new BigDecimal((packsToShip * pricePerPack)).setScale(2, ROUND_HALF_UP);
    assertEquals(costNonFullSupply, new BigDecimal(lineItemCost).setScale(2, ROUND_HALF_UP));

    BigDecimal totalOverAllCost = new BigDecimal((totalCostFullSupplyLineItem + lineItemCost)).setScale(2, ROUND_HALF_UP);
    assertEquals(new BigDecimal(totalRnrCost.getText().trim().substring(1)).setScale(2, ROUND_HALF_UP), totalOverAllCost);
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
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    fullSupplyTab.click();
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(quantityApproved);
    quantityApproved.clear();
    quantityApproved.sendKeys(approvedQuantity);
    remarks.click();
    assertEquals(packsToShip.getText().trim(), parseInt(approvedQuantity) / 10);

    BigDecimal cost = new BigDecimal((parseFloat(packsToShip.getText().trim()) * parseFloat(pricePerPack.getText().substring(1)))).setScale(2, ROUND_HALF_UP);
    assertEquals(String.valueOf(cost), lineItemCost.getText().substring(1));
    String totalCostFullSupplyLineItem = lineItemCost.getText().substring(1);
    testWebDriver.sleep(1000);


    BigDecimal totalOverAllCost = new BigDecimal(parseFloat(totalCostFullSupplyLineItem)).setScale(2, ROUND_HALF_UP);
    assertEquals(totalRnrCost.getText().substring(1).trim(), String.valueOf(totalOverAllCost));


  }


}