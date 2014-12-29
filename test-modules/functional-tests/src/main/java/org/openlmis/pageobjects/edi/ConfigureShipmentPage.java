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
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;
import static org.openqa.selenium.support.PageFactory.initElements;


public class ConfigureShipmentPage extends Page {

  @FindBy(how = ID, using = "includeHeadersCheckbox")
  private WebElement includeHeaders = null;

  @FindBy(how = ID, using = "orderNumberIncludeCheckbox")
  private WebElement orderNumberCheckBox = null;

  @FindBy(how = ID, using = "productCodeIncludeCheckbox")
  private WebElement productCodeCheckBox = null;

  @FindBy(how = ID, using = "quantityShippedIncludeCheckbox")
  private WebElement quantityShippedCheckBox = null;

  @FindBy(how = ID, using = "costIncludeCheckbox")
  private WebElement costCheckBox = null;

  @FindBy(how = ID, using = "packedDateIncludeCheckbox")
  private WebElement packedDateCheckBox = null;

  @FindBy(how = ID, using = "replacedProductCodeIncludeCheckbox")
  private WebElement replacedProductCodeIncludeCheckbox = null;

  @FindBy(how = ID, using = "shippedDateIncludeCheckbox")
  private WebElement shippedDateCheckBox = null;

  @FindBy(how = ID, using = "orderNumberPosition")
  private WebElement orderNumberPositionTextField = null;

  @FindBy(how = ID, using = "productCodePosition")
  private WebElement productCodePositionTextField = null;

  @FindBy(how = ID, using = "quantityShippedPosition")
  private WebElement quantityShippedTextField = null;

  @FindBy(how = ID, using = "costPosition")
  private WebElement costPositionTextField = null;

  @FindBy(how = ID, using = "packedDatePosition")
  private WebElement packedDatePositionTextField = null;

  @FindBy(how = ID, using = "replacedProductCodePosition")
  private WebElement replacedProductCodePositionTextField = null;

  @FindBy(how = ID, using = "shippedDatePosition")
  private WebElement shippedDatePositionTextField = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private WebElement saveErrorMessageDiv = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private WebElement successMessageDiv = null;

  @FindBy(how = ID, using = "saveShipmentConfig")
  private WebElement saveButton = null;

  @FindBy(how = ID, using = "cancelShipmentConfig")
  private WebElement cancelButton = null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private WebElement packedDateSelectBoxTextField = null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private WebElement shippedDateSelectBoxTextField = null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/ul/li[1]/div")
  private WebElement packedDateSelectBoxSelectableElement = null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/ul/li[1]/div")
  private WebElement shippedDateSelectBoxSelectableElement = null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen17']/a/div/b")
  private WebElement packedDateSelectBoxClickAbleLink = null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen23']/a/div/b")
  private WebElement shippedDateSelectBoxClickAbleLink = null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen17']/a/span")
  private WebElement packedDateSelectBoxDefaultSelected = null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen23']/a/span")
  private WebElement shippedDateSelectBoxDefaultSelected = null;


  public ConfigureShipmentPage(TestWebDriver driver) {
    super(driver);
    initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(includeHeaders);
  }

  public String getSelectedOptionOfPackedDateDropDown() {
    testWebDriver.waitForElementToAppear(packedDateSelectBoxDefaultSelected);
    return packedDateSelectBoxDefaultSelected.getText();
  }

  public String getSelectedOptionOfShippedDateDropDown() {
    testWebDriver.waitForElementToAppear(shippedDateSelectBoxDefaultSelected);
    return shippedDateSelectBoxDefaultSelected.getText();
  }

  public void selectValueFromPackedDateDropDown(String value) {
    testWebDriver.waitForElementToAppear(packedDateSelectBoxClickAbleLink);
    packedDateSelectBoxClickAbleLink.click();
    testWebDriver.waitForElementToAppear(packedDateSelectBoxTextField);
    sendKeys(packedDateSelectBoxTextField, value);
    testWebDriver.waitForElementToAppear(packedDateSelectBoxSelectableElement);
    packedDateSelectBoxSelectableElement.click();
  }

  public void selectValueFromShippedDateDropDown(String value) {
    testWebDriver.waitForElementToAppear(shippedDateSelectBoxClickAbleLink);
    shippedDateSelectBoxClickAbleLink.click();
    testWebDriver.waitForElementToAppear(shippedDateSelectBoxTextField);
    sendKeys(shippedDateSelectBoxTextField, value);
    testWebDriver.waitForElementToAppear(shippedDateSelectBoxSelectableElement);
    shippedDateSelectBoxSelectableElement.click();
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

  public void checkCostCheckBox() {
    testWebDriver.waitForElementToAppear(costCheckBox);
    if (!costCheckBox.isSelected())
      costCheckBox.click();
  }

  public void unCheckCostCheckBox() {
    testWebDriver.waitForElementToAppear(costCheckBox);
    if (costCheckBox.isSelected())
      costCheckBox.click();
  }

  public void checkPackedDateCheckBox() {
    testWebDriver.waitForElementToAppear(packedDateCheckBox);
    if (!packedDateCheckBox.isSelected())
      packedDateCheckBox.click();
  }

  public void unCheckPackedDateCheckBox() {
    testWebDriver.waitForElementToAppear(packedDateCheckBox);
    if (packedDateCheckBox.isSelected())
      packedDateCheckBox.click();
  }

  public void checkShippedDateCheckBox() {
    testWebDriver.waitForElementToAppear(shippedDateCheckBox);
    if (!shippedDateCheckBox.isSelected())
      shippedDateCheckBox.click();
  }

  public void checkReplacedProductCodeCheckBox() {
    testWebDriver.waitForElementToAppear(replacedProductCodeIncludeCheckbox);
    if (!replacedProductCodeIncludeCheckbox.isSelected())
      replacedProductCodeIncludeCheckbox.click();
  }


  public void unCheckShippedDateCheckBox() {
    testWebDriver.waitForElementToAppear(shippedDateCheckBox);
    if (shippedDateCheckBox.isSelected())
      shippedDateCheckBox.click();
  }

  public void unCheckReplacedProductCode() {
    testWebDriver.waitForElementToAppear(replacedProductCodeIncludeCheckbox);
    if (replacedProductCodeIncludeCheckbox.isSelected())
      replacedProductCodeIncludeCheckbox.click();
  }

  public String getQuantityShipped() {
    testWebDriver.waitForElementToAppear(quantityShippedTextField);
    return testWebDriver.getAttribute(quantityShippedTextField, "value");
  }

  public void setQuantityShipped(String value) {
    testWebDriver.waitForElementToAppear(quantityShippedTextField);
    sendKeys(quantityShippedTextField, value);
  }

  public void setOrderNumber(String value) {
    testWebDriver.waitForElementToAppear(orderNumberPositionTextField);
    sendKeys(orderNumberPositionTextField, value);
  }

  public String getOrderNumber() {
    testWebDriver.waitForElementToAppear(orderNumberPositionTextField);
    return testWebDriver.getAttribute(orderNumberPositionTextField, "value");
  }

  public String getCost() {
    testWebDriver.waitForElementToAppear(costPositionTextField);
    return testWebDriver.getAttribute(costPositionTextField, "value");
  }

  public void setCost(String value) {
    testWebDriver.waitForElementToAppear(costPositionTextField);
    sendKeys(costPositionTextField, value);
  }

  public String getProductCode() {
    testWebDriver.waitForElementToAppear(productCodePositionTextField);
    return testWebDriver.getAttribute(productCodePositionTextField, "value");
  }

  public void setProductCode(String value) {
    testWebDriver.waitForElementToAppear(productCodePositionTextField);
    sendKeys(productCodePositionTextField, value);
  }

  public String getPackedDate() {
    testWebDriver.waitForElementToAppear(packedDatePositionTextField);
    return testWebDriver.getAttribute(packedDatePositionTextField, "value");
  }

  public void setPackedDate(String value) {
    testWebDriver.waitForElementToAppear(packedDatePositionTextField);
    sendKeys(packedDatePositionTextField, value);
  }

  public String getShippedDate() {
    testWebDriver.waitForElementToAppear(shippedDatePositionTextField);
    return testWebDriver.getAttribute(shippedDatePositionTextField, "value");
  }

  public String getReplacedProductCode() {
    testWebDriver.waitForElementToAppear(replacedProductCodePositionTextField);
    return testWebDriver.getAttribute(replacedProductCodePositionTextField, "value");
  }

  public void setShippedDate(String value) {
    testWebDriver.waitForElementToAppear(shippedDatePositionTextField);
    sendKeys(shippedDatePositionTextField, value);
  }

  public void setReplacedProductCode(String value) {
    testWebDriver.waitForElementToAppear(replacedProductCodePositionTextField);
    sendKeys(replacedProductCodePositionTextField, value);
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
    assertTrue("orderNumberCheckBox should be checked", orderNumberCheckBox.isSelected());
    assertTrue("quantityShippedCheckBox should be checked", quantityShippedCheckBox.isSelected());
    assertTrue("productCodeCheckBox should be checked", productCodeCheckBox.isSelected());
    assertFalse("packedDateCheckBox should be checked", packedDateCheckBox.isSelected());
    assertFalse("shippedDateCheckBox should be checked", shippedDateCheckBox.isSelected());
    assertFalse("costCheckBox should not be checked", costCheckBox.isSelected());

    assertFalse("orderNumberCheckBox should be disabled", orderNumberCheckBox.isEnabled());
    assertFalse("quantityShippedCheckBox should be disabled", quantityShippedCheckBox.isEnabled());
    assertFalse("productCodeCheckBox should be disabled", productCodeCheckBox.isEnabled());
    assertTrue("costCheckBox should be enabled", costCheckBox.isEnabled());
    assertTrue("packedDateCheckBox should be enabled", packedDateCheckBox.isEnabled());
    assertTrue("shippedDateCheckBox should be enabled", shippedDateCheckBox.isEnabled());
  }

  public void verifyDefaultPositionValues() {
    assertEquals("1", orderNumberPositionTextField.getAttribute("value"));
    assertEquals("2", productCodePositionTextField.getAttribute("value"));
    assertEquals("3", quantityShippedTextField.getAttribute("value"));
    assertEquals("5", packedDatePositionTextField.getAttribute("value"));
    assertEquals("6", shippedDatePositionTextField.getAttribute("value"));
    assertEquals("4", costPositionTextField.getAttribute("value"));
  }
}