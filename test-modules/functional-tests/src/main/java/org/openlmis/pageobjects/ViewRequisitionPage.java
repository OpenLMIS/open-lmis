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

import static org.openqa.selenium.support.How.*;

public class ViewRequisitionPage extends RequisitionPage {

  @FindBy(how = NAME, using = "selectFacility")
  private static WebElement selectFacilityDropDown;

  @FindBy(how = NAME, using = "selectProgram")
  private static WebElement selectProgramDropDown;

  @FindBy(how = ID, using = "startDate")
  private static WebElement startDate;

  @FindBy(how = ID, using = "endDate")
  private static WebElement endDate;

  @FindBy(how = XPATH, using = "//input[@value='Search']")
  private static WebElement searchButton;

  @FindBy(how = XPATH, using = "//div[contains(text(),'No Requisitions found')]")
  private static WebElement noRequisitionFoundDiv;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col7 colt7']/span")
  private static WebElement status;

  @FindBy(how = XPATH, using = "//select[@data-handler='selectYear']")
  private static WebElement yearChanger;

  @FindBy(how = XPATH, using = "//span[contains(text(),'Prev')]")
  private static WebElement prevCalender;

  @FindBy(how = XPATH, using = "//span[contains(text(),'Next')]")
  private static WebElement nextCalender;

  @FindBy(how = XPATH, using = "//a[contains(text(),'25')]")
  private static WebElement startDateCalender;

  @FindBy(how = XPATH, using = "//a[contains(text(),'26')]")
  private static WebElement endDateCalender;

  @FindBy(how = XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[1]")
  private static WebElement viewRnRList;

  @FindBy(how = ID, using = "cost_0")
  private static WebElement totalCostPreApproval;

  @FindBy(how = ID, using = "cost_0")
  private static WebElement totalCostPostApproval;


  @FindBy(how = ID, using = "newPatientCount_0")
  private static WebElement newPatient;

  @FindBy(how = ID, using = "quantityApproved_0")
  private static WebElement approveQuantity;

  @FindBy(how = ID, using = "nonFullSupplyTab")
  private static WebElement nonFullSupplyTab;

  @FindBy(how = ID, using = "fullSupplyTab")
  private static WebElement fullSupplyTab;

  public ViewRequisitionPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public void verifyElementsOnViewRequisitionScreen() throws IOException
  {
     SeleneseTestNgHelper.assertTrue("Facility name drop down is not displayed", selectFacilityDropDown.isDisplayed());
     SeleneseTestNgHelper.assertTrue("Program name drop down is not displayed", selectProgramDropDown.isDisplayed());
     SeleneseTestNgHelper.assertTrue("Start date is not displayed", startDate.isDisplayed());
     SeleneseTestNgHelper.assertTrue("End date is not displayed", endDate.isDisplayed());
     SeleneseTestNgHelper.assertTrue("Search button is not displayed", searchButton.isDisplayed());
  }

  public void enterViewSearchCriteria() throws IOException
  {
    testWebDriver.waitForElementToAppear(selectFacilityDropDown);
    testWebDriver.selectByIndex(selectFacilityDropDown,1);
    testWebDriver.sleep(250);
    enterStartEndDateInCalender("2004", "2013");
  }

  public void enterStartEndDateInCalender(String startDateYear, String endDateYear){
      startDate.click();
      testWebDriver.sleep(250);
      testWebDriver.selectByValue(yearChanger,startDateYear);
      testWebDriver.sleep(250);
      startDateCalender.click();
      testWebDriver.sleep(250);
      endDate.click();
      testWebDriver.sleep(250);
      testWebDriver.selectByValue(yearChanger,endDateYear);
      testWebDriver.sleep(250);
      testWebDriver.click(nextCalender);
      testWebDriver.sleep(250);
      endDateCalender.click();
  }

  public void verifyNoRequisitionFound() throws IOException
  {

    SeleneseTestNgHelper.assertTrue("noRequisitionFoundDiv is not showing up",noRequisitionFoundDiv.isDisplayed());

  }

  public void verifyStatus(String statusToBeVerified) throws IOException
  {
    testWebDriver.waitForElementToAppear(status);
    SeleneseTestNgHelper.assertEquals(status.getText().trim(), statusToBeVerified.trim());
  }

  public void clickSearch()
  {
    testWebDriver.waitForElementToAppear(searchButton);
    searchButton.click();
    testWebDriver.sleep(1500);
  }

  public void clickRnRList()
  {
    testWebDriver.waitForElementToAppear(viewRnRList);
    viewRnRList.click();
  }

    public void clickFullSupplyTab()
    {
        testWebDriver.waitForElementToAppear(fullSupplyTab);
        fullSupplyTab.click();
    }

    public void clickNonFullSupplyTab()
    {
        testWebDriver.waitForElementToAppear(nonFullSupplyTab);
        nonFullSupplyTab.click();
    }

  public void verifyApprovedQuantityFieldPresent()
  {
    testWebDriver.waitForElementToAppear(approveQuantity);
    SeleneseTestNgHelper.assertTrue("Quantity Approved field should be displayed",approveQuantity.isDisplayed());
  }

  public HomePage verifyFieldsPreApproval(String cost, String newPatientValue) throws IOException
  {
    testWebDriver.waitForElementToAppear(totalCostPreApproval);
    SeleneseTestNgHelper.assertEquals(totalCostPreApproval.getText().substring(1), cost);
    SeleneseTestNgHelper.assertEquals(newPatient.getText().trim(), newPatientValue);

    return new HomePage(testWebDriver);
  }


  public HomePage verifyFieldsPostApproval(String cost, String newPatientValue) throws IOException
  {
    testWebDriver.waitForElementToAppear(totalCostPostApproval);
    SeleneseTestNgHelper.assertEquals(totalCostPostApproval.getText().substring(1), cost);
    SeleneseTestNgHelper.assertEquals(newPatient.getText().trim(), newPatientValue);

    return new HomePage(testWebDriver);
  }


}