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
import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;


public class ViewRequisitionPage extends Page {

  @FindBy(how = How.NAME, using = "selectFacility")
  private static WebElement selectFacilityDropDown;

  @FindBy(how = How.NAME, using = "selectProgram")
  private static WebElement selectProgramDropDown;

  @FindBy(how = How.ID, using = "startDate")
  private static WebElement startDate;

  @FindBy(how = How.ID, using = "endDate")
  private static WebElement endDate;

  @FindBy(how = How.XPATH, using = "//input[@value='Search']")
  private static WebElement searchButton;

  @FindBy(how = How.XPATH, using = "//div[contains(text(),'No Requisitions found')]")
  private static WebElement noRequisitionFoundDiv;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col7 colt7']/span")
  private static WebElement status;

  @FindBy(how = How.XPATH, using = "//select[@data-handler='selectYear']")
  private static WebElement yearChanger;

  @FindBy(how = How.XPATH, using = "//span[contains(text(),'Prev')]")
  private static WebElement prevCalender;

  @FindBy(how = How.XPATH, using = "//span[contains(text(),'Next')]")
  private static WebElement nextCalender;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'25')]")
  private static WebElement startDateCalender;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'26')]")
  private static WebElement endDateCalender;

  @FindBy(how = How.XPATH, using = "//div[@class='ngViewport ng-scope']/div/div/div[1]")
  private static WebElement viewRnRList;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCell  col18 colt18']/span[3]")
  private static WebElement totalCostPreApproval;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCell  col19 colt19']/span[3]")
  private static WebElement totalCostPostApproval;


  @FindBy(how = How.XPATH, using = "//div[@class='ngCell  col8 colt8']/div/span")
  private static WebElement newPatient;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCell  col16 colt16']/div/span")
  private static WebElement approveQuantity;

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

  public void enterSearchCriteria() throws IOException
  {
    testWebDriver.waitForElementToAppear(selectFacilityDropDown);
    testWebDriver.selectByIndex(selectFacilityDropDown,1);
    testWebDriver.sleep(250);
    startDate.click();
    testWebDriver.sleep(250);
    testWebDriver.selectByValue(yearChanger,"2004");
    testWebDriver.sleep(250);
    startDateCalender.click();
    testWebDriver.sleep(250);
    endDate.click();
    testWebDriver.sleep(250);
    testWebDriver.selectByValue(yearChanger,"2013");
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

  public HomePage verifyFieldsPreApproval(String cost, String newPatientValue) throws IOException
  {
    testWebDriver.waitForElementToAppear(totalCostPreApproval);
    SeleneseTestNgHelper.assertEquals(totalCostPreApproval.getText().trim(), cost);
//    SeleneseTestNgHelper.assertEquals(newPatient.getText().trim(), newPatientValue);

    return new HomePage(testWebDriver);
  }


  public HomePage verifyFieldsPostApproval(String cost, String newPatientValue) throws IOException
  {
    testWebDriver.waitForElementToAppear(totalCostPostApproval);
    SeleneseTestNgHelper.assertEquals(totalCostPostApproval.getText().trim(), cost);
//    SeleneseTestNgHelper.assertEquals(newPatient.getText().trim(), newPatientValue);

    return new HomePage(testWebDriver);
  }


}