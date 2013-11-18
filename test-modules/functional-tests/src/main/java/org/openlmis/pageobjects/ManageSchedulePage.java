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
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


public class ManageSchedulePage extends Page {

  @FindBy(how = How.ID, using = "schedule-add-new")
  private static WebElement addScheduleButton=null;

  @FindBy(how = How.ID, using = "code")
  private static WebElement codeTextField=null;

  @FindBy(how = How.ID, using = "code_0")
  private static WebElement codeEditTextField=null;

  @FindBy(how = How.ID, using = "name")
  private static WebElement nameTextField=null;

  @FindBy(how = How.ID, using = "name_0")
  private static WebElement nameEditTextField=null;

  @FindBy(how = How.ID, using = "desc_0")
  private static WebElement descriptionEditTextField=null;

  @FindBy(how = How.ID, using = "description")
  private static WebElement descriptionTextField=null;

  @FindBy(how = How.XPATH, using = "//input[@value='Create']")
  private static WebElement createButton=null;

  @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv=null;

  @FindBy(how = How.XPATH, using = "//input[@value='Edit']")
  private static WebElement editFirstButton=null;

  @FindBy(how = How.XPATH, using = "(//input[@value='Edit'])[2]")
  private static WebElement editSecondButton=null;

  @FindBy(how = How.XPATH, using = " //input[@type='submit' and @value='Save']")
  private static WebElement saveButton=null;

  @FindBy(how = How.XPATH, using = "//input[@value='Add Period']")
  private static WebElement addPeriodButton=null;

  @FindBy(how = How.ID, using = "name")
  private static WebElement namePeriod=null;


  public ManageSchedulePage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }


  public void createSchedule() {
    testWebDriver.waitForElementToAppear(addScheduleButton);
    enterCreateScheduleDetails("Q1stM", "QuarterMonthly", "QuarterMonth");
    enterCreateScheduleDetails("M", "Monthly", "Month");
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

  public void editSchedule() {
    testWebDriver.waitForElementToAppear(editFirstButton);
    enterEditScheduleDetails("M1", "Monthly1", "Month");
    testWebDriver.sleep(500);
    enterEditScheduleDetails("M", "Monthly", "Month");
  }

  public void verifyScheduleCode() {
    testWebDriver.sleep(500);
    assertEquals(testWebDriver.getElementByXpath(
            "//form[@id='editScheduleForm_1']/div/div[1]/div[2]/span[1]").getText().trim(), "M1");
    assertEquals(testWebDriver.getElementByXpath("//form[@id='editScheduleForm_0']/div/div[1]/div[2]/span[1]").getText().trim(), "M");
    assertTrue("First edit button is not showing up", editFirstButton.isDisplayed());
    assertTrue("Second edit button is not showing up", editSecondButton.isDisplayed());
  }

  public PeriodsPage navigatePeriods() throws IOException {
    testWebDriver.waitForElementToAppear(addPeriodButton);
    addPeriodButton.click();
    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(namePeriod);
    return new PeriodsPage(testWebDriver);
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