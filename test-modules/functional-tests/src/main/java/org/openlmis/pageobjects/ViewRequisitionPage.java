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

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.*;

public class ViewRequisitionPage extends RequisitionPage {

  @FindBy(how = NAME, using = "selectFacility")
  private static WebElement selectFacilityDropDown = null;

  @FindBy(how = NAME, using = "selectProgram")
  private static WebElement selectProgramDropDown = null;

  @FindBy(how = ID, using = "startDate")
  private static WebElement startDate = null;

  @FindBy(how = ID, using = "endDate")
  private static WebElement endDate = null;

  @FindBy(how = XPATH, using = "//input[@value='Search']")
  private static WebElement searchButton = null;

  @FindBy(how = XPATH, using = "//div[contains(text(),'No Requisitions found')]")
  private static WebElement noRequisitionFoundDiv = null;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col7 colt7']/span")
  private static WebElement status = null;

  @FindBy(how = ID, using = "emergency0")
  private static WebElement emergencyIcon = null;

  @FindBy(how = XPATH, using = "//select[@data-handler='selectYear']")
  private static WebElement yearChanger = null;

  @FindBy(how = XPATH, using = "//span[contains(text(),'Next')]")
  private static WebElement nextCalender = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'25')]")
  private static WebElement startDateCalender = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'26')]")
  private static WebElement endDateCalender = null;

  @FindBy(how = ID, using = "cost_0")
  private static WebElement totalCostPreApproval = null;

  @FindBy(how = ID, using = "cost_0")
  private static WebElement totalCostPostApproval = null;

  @FindBy(how = ID, using = "newPatientCount_0")
  private static WebElement newPatient = null;

  @FindBy(how = ID, using = "quantityApproved_0")
  private static WebElement approveQuantity = null;

  @FindBy(how = ID, using = "nonFullSupplyTab")
  private static WebElement nonFullSupplyTab = null;

  @FindBy(how = ID, using = "fullSupplyTab")
  private static WebElement fullSupplyTab = null;

  @FindBy(how = ID, using = "viewReqList")
  private static WebElement viewReqList = null;

  public ViewRequisitionPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public void verifyElementsOnViewRequisitionScreen() {
    assertTrue("Facility name drop down is not displayed", selectFacilityDropDown.isDisplayed());
    assertTrue("Program name drop down is not displayed", selectProgramDropDown.isDisplayed());
    assertTrue("Start date is not displayed", startDate.isDisplayed());
    assertTrue("End date is not displayed", endDate.isDisplayed());
    assertTrue("Search button is not displayed", searchButton.isDisplayed());
  }

  public void enterViewSearchCriteria() {
    testWebDriver.waitForElementToAppear(selectFacilityDropDown);
    testWebDriver.selectByIndex(selectFacilityDropDown, 1);
    testWebDriver.sleep(250);
    enterStartEndDateInCalender("2004", "2016");
  }

  public void enterStartEndDateInCalender(String startDateYear, String endDateYear) {
    startDate.click();
    testWebDriver.sleep(250);
    testWebDriver.selectByValue(yearChanger, startDateYear);
    testWebDriver.sleep(250);
    startDateCalender.click();
    testWebDriver.sleep(250);
    endDate.click();
    testWebDriver.sleep(250);
    testWebDriver.selectByValue(yearChanger, endDateYear);
    testWebDriver.sleep(250);
    testWebDriver.click(nextCalender);
    testWebDriver.sleep(250);
    endDateCalender.click();
  }

  public void verifyNoRequisitionFound() {
    assertTrue("noRequisitionFoundDiv is not showing up", noRequisitionFoundDiv.isDisplayed());
  }

  public void verifyStatus(String statusToBeVerified) {
    testWebDriver.waitForElementToAppear(status);
    assertEquals(status.getText().trim(), statusToBeVerified.trim());
  }

  public void verifyEmergencyStatus() {
    testWebDriver.waitForElementToAppear(emergencyIcon);
    assertTrue("Emergency icon should show up", emergencyIcon.isDisplayed());
  }

  public void clickSearch() {
    testWebDriver.waitForElementToAppear(searchButton);
    searchButton.click();
    testWebDriver.sleep(1500);
  }

  public void clickRnRList() {
    testWebDriver.waitForElementToAppear(emergencyIcon);
    emergencyIcon.click();
  }

  public void clickFullSupplyTab() {
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    fullSupplyTab.click();
  }

  public void clickNonFullSupplyTab() {
    testWebDriver.waitForElementToAppear(nonFullSupplyTab);
    nonFullSupplyTab.click();
  }

  public void verifyApprovedQuantityFieldPresent() {
    testWebDriver.waitForElementToAppear(approveQuantity);
    assertTrue("Quantity Approved field should be displayed", approveQuantity.isDisplayed());
  }

  public HomePage verifyFieldsPreApproval(String cost, String newPatientValue) {
    testWebDriver.waitForElementToAppear(totalCostPreApproval);
    assertEquals(totalCostPreApproval.getText().substring(1), cost);
    assertEquals(newPatient.getText().trim(), newPatientValue);
    return PageObjectFactory.getHomePage(testWebDriver);
  }

  public HomePage verifyFieldsPostApproval(String cost, String newPatientValue) {
    testWebDriver.waitForElementToAppear(totalCostPostApproval);
    assertEquals(totalCostPostApproval.getText().substring(1), cost);
    assertEquals(newPatient.getText().trim(), newPatientValue);
    return PageObjectFactory.getHomePage(testWebDriver);
  }

  public boolean isViewRnRListPresent() {
    return viewReqList.isDisplayed();
  }

  public boolean isRnRListReq1Present() {
    return emergencyIcon.isDisplayed();
  }
}