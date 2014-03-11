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
import static org.openqa.selenium.support.How.ID;


public class ManageSchedulePage extends Page {

  @FindBy(how = ID, using = "schedule-add-new")
  private static WebElement addScheduleButton = null;

  @FindBy(how = ID, using = "code")
  private static WebElement codeTextField = null;

  @FindBy(how = ID, using = "code_0")
  private static WebElement codeEditTextField = null;

  @FindBy(how = ID, using = "name")
  private static WebElement nameTextField = null;

  @FindBy(how = ID, using = "name_0")
  private static WebElement nameEditTextField = null;

  @FindBy(how = ID, using = "desc_0")
  private static WebElement descriptionEditTextField = null;

  @FindBy(how = ID, using = "description")
  private static WebElement descriptionTextField = null;

  @FindBy(how = ID, using = "createSchedule")
  private static WebElement createButton = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv = null;

  @FindBy(how = ID, using = "editButton0")
  private static WebElement editFirstButton = null;

  @FindBy(how = ID, using = "editButton1")
  private static WebElement editSecondButton = null;

  @FindBy(how = ID, using = "saveSchedule")
  private static WebElement saveButton = null;

  @FindBy(how = ID, using = "addPeriod")
  private static WebElement addPeriodButton = null;

  @FindBy(how = ID, using = "name")
  private static WebElement namePeriod = null;


  public ManageSchedulePage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void createSchedule(String periodCode1, String periodCode2) {
    testWebDriver.waitForElementToAppear(addScheduleButton);
    enterCreateScheduleDetails(periodCode1, "QuarterMonthly", "QuarterMonth");
    enterCreateScheduleDetails(periodCode2, "Monthly", "Month");
  }

  public void enterCreateScheduleDetails(String code, String name, String desc) {
    addScheduleButton.click();
    testWebDriver.waitForElementToAppear(codeTextField);
    codeTextField.clear();
    codeTextField.sendKeys(code);
    nameTextField.clear();
    nameTextField.sendKeys(name);
    descriptionTextField.clear();
    descriptionTextField.sendKeys(desc);
    createButton.click();
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
  }

  public void editSchedule(String periodCode1, String periodCode2) {
    testWebDriver.waitForElementToAppear(editFirstButton);
    enterEditScheduleDetails(periodCode1, "Monthly1", "Month");
    testWebDriver.sleep(500);
    enterEditScheduleDetails(periodCode2, "Monthly", "Month");
  }

  public void verifyScheduleCode(String periodCode1, String periodCode2) {
    testWebDriver.sleep(500);
    assertEquals(testWebDriver.getElementByXpath(
      "//form[@id='editScheduleForm_0']/div/div[1]/div[2]/span[1]").getText().trim(), periodCode2);
    assertEquals(testWebDriver.getElementByXpath("//form[@id='editScheduleForm_1']/div/div[1]/div[2]/span[1]").getText().trim(), periodCode1);
    assertTrue("First edit button is not showing up", editFirstButton.isDisplayed());
    assertTrue("Second edit button is not showing up", editSecondButton.isDisplayed());
  }

  public PeriodsPage navigatePeriods() {
    testWebDriver.waitForElementToBeEnabled(addPeriodButton);
    addPeriodButton.click();
    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(namePeriod);
    return PageObjectFactory.getPeriodsPage(testWebDriver);
  }

  public void enterEditScheduleDetails(String code, String name, String desc) {
    editFirstButton.click();
    testWebDriver.waitForElementToAppear(codeEditTextField);
    codeEditTextField.clear();
    codeEditTextField.sendKeys(code);
    nameEditTextField.clear();
    nameEditTextField.sendKeys(name);
    descriptionEditTextField.clear();
    descriptionEditTextField.sendKeys(desc);
    saveButton.click();

  }
}