/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pageobjects.edi;


import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.Page;
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
  private WebElement includeHeaders = null;

  @FindBy(how = ID, using = "facilityCodeIncludeCheckbox")
  private WebElement facilityCodeCheckbox = null;

  @FindBy(how = ID, using = "programCodeIncludeCheckbox")
  private WebElement programCodeCheckbox = null;

  @FindBy(how = ID, using = "periodStartDateIncludeCheckbox")
  private WebElement periodStartDateCheckbox = null;

  @FindBy(how = ID, using = "allocatedBudgetIncludeCheckbox")
  private WebElement allocatedBudgetCheckbox = null;

  @FindBy(how = ID, using = "notesIncludeCheckbox")
  private WebElement notesCheckbox = null;

  @FindBy(how = ID, using = "facilityCodePosition")
  private WebElement facilityCodePosition = null;

  @FindBy(how = ID, using = "programCodePosition")
  private WebElement programCodePosition = null;

  @FindBy(how = ID, using = "periodStartDatePosition")
  private WebElement periodStartDatePosition = null;

  @FindBy(how = ID, using = "allocatedBudgetPosition")
  private WebElement allocatedBudgetPosition = null;

  @FindBy(how = ID, using = "notesPosition")
  private WebElement notesTextPosition = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private WebElement saveErrorMessageDiv = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private WebElement successMessageDiv = null;

  @FindBy(how = XPATH, using = "//input[@value='Save']")
  private WebElement saveButton = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Cancel')]")
  private WebElement cancelButton = null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private WebElement periodStartDateSelectBoxTextField = null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/ul/li[1]/div")
  private WebElement periodStartDateSelectBoxSelectableElement = null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen5']/a/div/b")
  private WebElement periodStartDateSelectBoxClickableLink = null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen5']/a/span")
  private WebElement periodStartDateSelectBoxDefaultSelected = null;


  public ConfigureBudgetPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(includeHeaders);
  }


  public String getPeriodStartDateFormat() {
    testWebDriver.waitForElementToAppear(periodStartDateSelectBoxDefaultSelected);
    return periodStartDateSelectBoxDefaultSelected.getText();
  }

  public void selectValueFromPeriodStartDateDropDown(String value) {
    testWebDriver.waitForElementToAppear(periodStartDateSelectBoxClickableLink);
    periodStartDateSelectBoxClickableLink.click();
    testWebDriver.waitForElementToAppear(periodStartDateSelectBoxTextField);
    sendKeys(periodStartDateSelectBoxTextField, value);
    testWebDriver.waitForElementToAppear(periodStartDateSelectBoxSelectableElement);
    periodStartDateSelectBoxSelectableElement.click();
  }

  public boolean isHeaderIncluded() {
    testWebDriver.waitForElementToAppear(includeHeaders);
    return includeHeaders.isSelected();
  }

  public void checkIncludeHeader() {
    testWebDriver.waitForElementToAppear(includeHeaders);
    if (!includeHeaders.isSelected())
      includeHeaders.click();
  }

  public void checkNotesCheckBox() {
    testWebDriver.waitForElementToAppear(notesCheckbox);
    if (!notesCheckbox.isSelected())
      notesCheckbox.click();
  }

  public String getAllocatedBudgetPosition() {
    testWebDriver.waitForElementToAppear(allocatedBudgetPosition);
    return testWebDriver.getAttribute(allocatedBudgetPosition, "value");
  }

  public void setAllocatedBudgetPosition(String value) {
    testWebDriver.waitForElementToAppear(allocatedBudgetPosition);
    sendKeys(allocatedBudgetPosition, value);
  }

  public void setFacilityCodePosition(String value) {
    testWebDriver.waitForElementToAppear(facilityCodePosition);
    sendKeys(facilityCodePosition, value);
  }

  public String getFacilityCodePosition() {
    testWebDriver.waitForElementToAppear(facilityCodePosition);
    return testWebDriver.getAttribute(facilityCodePosition, "value");
  }

  public String getNotesPosition() {
    testWebDriver.waitForElementToAppear(notesTextPosition);
    return testWebDriver.getAttribute(notesTextPosition, "value");
  }

  public void setNotesPosition(String value) {
    testWebDriver.waitForElementToAppear(notesTextPosition);
    sendKeys(notesTextPosition, value);
  }

  public String getProgramCodePosition() {
    testWebDriver.waitForElementToAppear(programCodePosition);
    return testWebDriver.getAttribute(programCodePosition, "value");
  }

  public void setProgramCodePosition(String value) {
    testWebDriver.waitForElementToAppear(programCodePosition);
    sendKeys(programCodePosition, value);
  }

  public String getPeriodStartDatePosition() {
    testWebDriver.waitForElementToAppear(periodStartDatePosition);
    return testWebDriver.getAttribute(periodStartDatePosition, "value");
  }

  public void setPeriodStartDatePosition(String value) {
    testWebDriver.waitForElementToAppear(periodStartDatePosition);
    sendKeys(periodStartDatePosition, value);
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
    assertEquals(message, successMessageDiv.getText());
  }

  public void verifyErrorMessage(String message) {
    testWebDriver.waitForElementToAppear(saveErrorMessageDiv);
    assertEquals(message, saveErrorMessageDiv.getText());
  }

  public void verifyDefaultIncludeCheckboxForAllDataFields() {
    assertTrue("facilityCodeCheckBox should be checked", facilityCodeCheckbox.isSelected());
    assertTrue("allocatedBudgetCheckBox should be checked", allocatedBudgetCheckbox.isSelected());
    assertTrue("programCodeCheckBox should be checked", programCodeCheckbox.isSelected());
    assertTrue("periodStartDateCheckBox should be checked", periodStartDateCheckbox.isSelected());
    assertFalse("notesCheckBox should not be checked", notesCheckbox.isSelected());

    assertFalse("facilityCodeCheckBox should be disabled", facilityCodeCheckbox.isEnabled());
    assertFalse("allocatedBudgetCheckBox should be disabled", allocatedBudgetCheckbox.isEnabled());
    assertFalse("programCodeCheckBox should be disabled", programCodeCheckbox.isEnabled());
    assertTrue("notesCheckBox should be enabled", notesCheckbox.isEnabled());
    assertFalse("periodStartDateCheckBox should not be enabled", periodStartDateCheckbox.isEnabled());

  }

  public void verifyDefaultPositionValues() {
    assertEquals("1", facilityCodePosition.getAttribute("value"));
    assertEquals("4", allocatedBudgetPosition.getAttribute("value"));
    assertEquals("2", programCodePosition.getAttribute("value"));
    assertEquals("3", periodStartDatePosition.getAttribute("value"));
    assertEquals("5", notesTextPosition.getAttribute("value"));
  }

  public boolean isNotesChecked() {
    testWebDriver.waitForElementToAppear(notesCheckbox);
    return notesCheckbox.isSelected();
  }
}