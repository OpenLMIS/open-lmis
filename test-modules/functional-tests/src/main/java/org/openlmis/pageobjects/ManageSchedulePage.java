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


public class ManageSchedulePage extends Page {

  @FindBy(how = How.LINK_TEXT, using = "Add new")
  private static WebElement addScheduleButton;

  @FindBy(how = How.ID, using = "code")
  private static WebElement codeTextField;

  @FindBy(how = How.ID, using = "code_0")
  private static WebElement codeEditTextField;

  @FindBy(how = How.ID, using = "name")
  private static WebElement nameTextField;

  @FindBy(how = How.ID, using = "name_0")
  private static WebElement nameEditTextField;

  @FindBy(how = How.ID, using = "desc_0")
  private static WebElement descriptionEditTextField;

  @FindBy(how = How.ID, using = "description")
  private static WebElement descriptionTextField;

  @FindBy(how = How.XPATH, using = "//input[@value='Create']")
  private static WebElement createButton;

  @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv;

  @FindBy(how = How.XPATH, using = "//input[@value='Edit']")
  private static WebElement editFirstButton;

  @FindBy(how = How.XPATH, using = "(//input[@value='Edit'])[2]")
  private static WebElement editSecondButton;

  @FindBy(how = How.XPATH, using = "(//span[@class='ng-binding' and @ng-bind='schedule.code'])[1] ")
  private static WebElement codeFirstNonEditableField;

  @FindBy(how = How.XPATH, using = " //input[@type='submit' and @value='Save']")
  private static WebElement saveButton;

  @FindBy(how = How.XPATH, using = "//input[@value='Add Period']")
  private static WebElement addPeriodButton;

  @FindBy(how = How.ID, using = "name")
  private static WebElement namePeriod;


  public ManageSchedulePage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }


  public void createAndVerifySchedule() {
    testWebDriver.waitForElementToAppear(addScheduleButton);
    enterCreateScheduleDetails("Q1stM", "QuarterMonthly", "QuarterMonth");
    SeleneseTestNgHelper.assertTrue("'QuarterMonthly' created successfully message not showing up", saveSuccessMsgDiv.isDisplayed());
    enterCreateScheduleDetails("M", "Monthly", "Month");
    SeleneseTestNgHelper.assertTrue("'Monthly' created successfully message not showing up", saveSuccessMsgDiv.isDisplayed());
    SeleneseTestNgHelper.assertTrue("First edit button is not showing up", editFirstButton.isDisplayed());
    SeleneseTestNgHelper.assertTrue("Second edit button is not showing up", editSecondButton.isDisplayed());
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

  public void editAndVerifySchedule() {
    testWebDriver.waitForElementToAppear(editFirstButton);
    enterEditScheduleDetails("M1", "Monthly1", "Month");
    //SeleneseTestNgHelper.assertTrue("'Monthly1' updated successfully message not showing up", saveSuccessMsgDiv.isDisplayed());
    testWebDriver.sleep(500);
    SeleneseTestNgHelper.assertEquals(codeFirstNonEditableField.getText().trim(), "M1");

    testWebDriver.sleep(500);
    enterEditScheduleDetails("M", "Monthly", "Month");
    //SeleneseTestNgHelper.assertTrue("'Monthly' updated successfully message not showing up", saveSuccessMsgDiv.isDisplayed());
    testWebDriver.sleep(500);
    SeleneseTestNgHelper.assertEquals(codeFirstNonEditableField.getText().trim(), "M");


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
    //testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);

  }

  public void verifyMessage(String message) {
    String successMessage = saveSuccessMsgDiv.getText();
    SeleneseTestNgHelper.assertEquals(successMessage, message);

  }


}