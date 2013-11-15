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

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class ConfigureBudgetPage extends Page {


  @FindBy(how = ID, using = "includeHeadersCheckbox")
  private static WebElement includeHeaders=null;

  @FindBy(how = ID, using = "includeCheckbox0")
  private static WebElement facilityCodeCheckBox=null;

  @FindBy(how = ID, using = "includeCheckbox1")
  private static WebElement programCodeCheckBox=null;

  @FindBy(how = ID, using = "includeCheckbox2")
  private static WebElement periodStartDateCheckBox=null;

  @FindBy(how = ID, using = "includeCheckbox3")
  private static WebElement allocatedBudgetCheckBox=null;

  @FindBy(how = ID, using = "includeCheckbox4")
  private static WebElement notesCheckBox=null;

  @FindBy(how = XPATH, using = "//div[@id='budgetFileColumns']/div[2]/div[1]/div[4]/input")
  private static WebElement facilityCodeTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='budgetFileColumns']/div[2]/div[2]/div[4]/input")
  private static WebElement programCodeTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='budgetFileColumns']/div[2]/div[3]/div[4]/input")
  private static WebElement periodStartDateTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='budgetFileColumns']/div[2]/div[4]/div[4]/input")
  private static WebElement allocatedBudgetTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='budgetFileColumns']/div[2]/div[5]/div[4]/input")
  private static WebElement notesTextField=null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMessageDiv=null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement successMessageDiv=null;

  @FindBy(how = XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton=null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Cancel')]")
  private static WebElement cancelButton=null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private static WebElement periodStartDateSelectBoxTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/ul/li[1]/div")
  private static WebElement periodStartDateSelectBoxSelectableElement=null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen5']/a/div/b")
  private static WebElement periodStartDateSelectBoxClickableLink=null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen5']/a/span")
  private static WebElement periodStartDateSelectBoxDefaultSelected=null;


  public ConfigureBudgetPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(includeHeaders);
  }


  public String getSelectedOptionOfPeriodStartDateDropDown()
  {
    testWebDriver.waitForElementToAppear(periodStartDateSelectBoxDefaultSelected);
    return periodStartDateSelectBoxDefaultSelected.getText();
  }

  public void selectValueFromPeriodStartDateDropDown(String value)
  {
      testWebDriver.waitForElementToAppear(periodStartDateSelectBoxClickableLink);
      periodStartDateSelectBoxClickableLink.click();
      testWebDriver.waitForElementToAppear(periodStartDateSelectBoxTextField);
      sendKeys(periodStartDateSelectBoxTextField,value);
      testWebDriver.waitForElementToAppear(periodStartDateSelectBoxSelectableElement);
      periodStartDateSelectBoxSelectableElement.click();
  }

  public boolean getIncludeHeader() {
    testWebDriver.waitForElementToAppear(includeHeaders);
    return includeHeaders.isSelected();
  }

  public void checkIncludeHeader() {
    testWebDriver.waitForElementToAppear(includeHeaders);
    if (!includeHeaders.isSelected())
        includeHeaders.click();
  }

  public void unCheckIncludeHeader() {
    testWebDriver.waitForElementToAppear(includeHeaders);
    if (includeHeaders.isSelected())
        includeHeaders.click();
  }

  public void checkNotesCheckBox() {
    testWebDriver.waitForElementToAppear(notesCheckBox);
    if (!notesCheckBox.isSelected())
        notesCheckBox.click();
  }

  public void unCheckCostCheckBox() {
    testWebDriver.waitForElementToAppear(notesCheckBox);
    if (notesCheckBox.isSelected())
        notesCheckBox.click();
  }

  public void checkPeriodStartDateCheckBox() {
    testWebDriver.waitForElementToAppear(periodStartDateCheckBox);
    if (!periodStartDateCheckBox.isSelected())
        periodStartDateCheckBox.click();
  }

  public void unCheckPeriodStartDateCheckBox() {
    testWebDriver.waitForElementToAppear(periodStartDateCheckBox);
    if (periodStartDateCheckBox.isSelected())
        periodStartDateCheckBox.click();
  }

  public String getAllocatedBudget() {
    testWebDriver.waitForElementToAppear(allocatedBudgetTextField);
    return testWebDriver.getAttribute(allocatedBudgetTextField, "value");
  }

  public void setAllocatedBudget(String value) {
    testWebDriver.waitForElementToAppear(allocatedBudgetTextField);
    sendKeys(allocatedBudgetTextField, value);
  }

  public void setFacilityCode(String value) {
    testWebDriver.waitForElementToAppear(facilityCodeTextField);
    sendKeys(facilityCodeTextField, value);
  }

  public String getFacilityCode() {
    testWebDriver.waitForElementToAppear(facilityCodeTextField);
    return testWebDriver.getAttribute(facilityCodeTextField,"value");
  }

  public String getNotes() {
    testWebDriver.waitForElementToAppear(notesTextField);
    return testWebDriver.getAttribute(notesTextField, "value");
  }

  public void setNotes(String value) {
    testWebDriver.waitForElementToAppear(notesTextField);
    sendKeys(notesTextField, value);
  }

  public String getProgramCode() {
    testWebDriver.waitForElementToAppear(programCodeTextField);
    return testWebDriver.getAttribute(programCodeTextField, "value");
  }

  public void setProgramCode(String value) {
    testWebDriver.waitForElementToAppear(programCodeTextField);
    sendKeys(programCodeTextField, value);
  }

  public String getPeriodStartDate() {
    testWebDriver.waitForElementToAppear(periodStartDateTextField);
    return testWebDriver.getAttribute(periodStartDateTextField, "value");
  }

  public void setPeriodStartDate(String value) {
    testWebDriver.waitForElementToAppear(periodStartDateTextField);
    sendKeys(periodStartDateTextField, value);
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
    testWebDriver.sleep(2000);
  }


  public void verifyMessage(String message) {
    testWebDriver.waitForElementToAppear(successMessageDiv);
    assertEquals(message,successMessageDiv.getText());
  }

  public void verifyErrorMessage(String message) {
    testWebDriver.waitForElementToAppear(saveErrorMessageDiv);
    assertEquals(message,saveErrorMessageDiv.getText());
  }

    public void verifyDefaultIncludeCheckboxForAllDataFields() {
            assertTrue("facilityCodeCheckBox should be checked", facilityCodeCheckBox.isSelected());
            assertTrue("allocatedBudgetCheckBox should be checked", allocatedBudgetCheckBox.isSelected());
            assertTrue("programCodeCheckBox should be checked", programCodeCheckBox.isSelected());
            assertTrue("periodStartDateCheckBox should be checked", periodStartDateCheckBox.isSelected());
            assertFalse("notesCheckBox should be checked", notesCheckBox.isSelected());

            assertFalse("facilityCodeCheckBox should be disabled", facilityCodeCheckBox.isEnabled());
            assertFalse("allocatedBudgetCheckBox should be disabled", allocatedBudgetCheckBox.isEnabled());
            assertFalse("programCodeCheckBox should be disabled", programCodeCheckBox.isEnabled());
            assertTrue("notesCheckBox should be enabled", notesCheckBox.isEnabled());
            assertFalse("periodStartDateCheckBox should be enabled", periodStartDateCheckBox.isEnabled());

    }

    public void verifyDefaultPositionValues() {
        assertEquals("1", facilityCodeTextField.getAttribute("value")) ;
        assertEquals("4", allocatedBudgetTextField.getAttribute("value"));
        assertEquals("2", programCodeTextField.getAttribute("value"));
        assertEquals("3", periodStartDateTextField.getAttribute("value"));
        assertEquals("5", notesTextField.getAttribute("value"));
    }
}