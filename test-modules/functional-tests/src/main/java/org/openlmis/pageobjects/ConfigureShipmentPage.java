/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestBase;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class ConfigureShipmentPage extends Page {


  @FindBy(how = ID, using = "includeHeadersCheckbox")
  private static WebElement includeHeaders;

  @FindBy(how = ID, using = "includeCheckbox0")
  private static WebElement orderNumberCheckBox;

  @FindBy(how = ID, using = "includeCheckbox1")
  private static WebElement productCodeCheckBox;

  @FindBy(how = ID, using = "includeCheckbox2")
  private static WebElement quantityShippedCheckBox;

  @FindBy(how = ID, using = "includeCheckbox3")
  private static WebElement costCheckBox;

  @FindBy(how = ID, using = "includeCheckbox4")
  private static WebElement packedDateCheckBox;

  @FindBy(how = ID, using = "includeCheckbox5")
  private static WebElement shippedDateCheckBox;

  @FindBy(how = XPATH, using = "//div[@id='shipmentFileColumns']/div[2]/div[1]/div[4]/input")
  private static WebElement orderNumberTextField;

  @FindBy(how = XPATH, using = "//div[@id='shipmentFileColumns']/div[2]/div[2]/div[4]/input")
  private static WebElement productCodeTextField;

  @FindBy(how = XPATH, using = "//div[@id='shipmentFileColumns']/div[2]/div[3]/div[4]/input")
  private static WebElement quantityShippedTextField;

  @FindBy(how = XPATH, using = "//div[@id='shipmentFileColumns']/div[2]/div[4]/div[4]/input")
  private static WebElement costTextField;

  @FindBy(how = XPATH, using = "//div[@id='shipmentFileColumns']/div[2]/div[5]/div[4]/input")
  private static WebElement packedDateTextField;

  @FindBy(how = XPATH, using = "//div[@id='shipmentFileColumns']/div[2]/div[6]/div[4]/input")
  private static WebElement shippedDateTextField;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMessageDiv;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement successMessageDiv;

  @FindBy(how = XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Cancel')]")
  private static WebElement cancelButton;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private static WebElement packedDateSelectBoxTextField;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private static WebElement shippedDateSelectBoxTextField;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/ul/li[1]/div")
  private static WebElement packedDateSelectBoxSelectableElement;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/ul/li[1]/div")
  private static WebElement shippedDateSelectBoxSelectableElement;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen17']/a/div/b")
  private static WebElement packedDateSelectBoxClickableLink;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen23']/a/div/b")
  private static WebElement shippedDateSelectBoxClickableLink;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen17']/a/span")
  private static WebElement packedDateSelectBoxDefaultSelected;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen23']/a/span")
  private static WebElement shippedDateSelectBoxDefaultSelected;




  public ConfigureShipmentPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(includeHeaders);
  }


  public String getSelectedOptionOfPackedDateDropDown()
  {
    testWebDriver.waitForElementToAppear(packedDateSelectBoxDefaultSelected);
    return packedDateSelectBoxDefaultSelected.getText();
  }

  public String getSelectedOptionOfShippedDateDropDown()
  {
    testWebDriver.waitForElementToAppear(shippedDateSelectBoxDefaultSelected);
    return shippedDateSelectBoxDefaultSelected.getText();
  }

  public void selectValueFromPackedDateDropDown(String value)
  {
      testWebDriver.waitForElementToAppear(packedDateSelectBoxClickableLink);
      packedDateSelectBoxClickableLink.click();
      testWebDriver.waitForElementToAppear(packedDateSelectBoxTextField);
      sendKeys(packedDateSelectBoxTextField,value);
      testWebDriver.waitForElementToAppear(packedDateSelectBoxSelectableElement);
      packedDateSelectBoxSelectableElement.click();
  }

  public void selectValueFromShippedDateDropDown(String value)
  {
    testWebDriver.waitForElementToAppear(shippedDateSelectBoxClickableLink);
    shippedDateSelectBoxClickableLink.click();
    testWebDriver.waitForElementToAppear(shippedDateSelectBoxTextField);
    sendKeys(shippedDateSelectBoxTextField,value);
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


  public boolean getOrderNumberCheckBox() {
    testWebDriver.waitForElementToAppear(orderNumberCheckBox);
    return orderNumberCheckBox.isSelected();
  }

  public boolean getQuantityShippedCheckBox() {
    testWebDriver.waitForElementToAppear(quantityShippedCheckBox);
    return quantityShippedCheckBox.isSelected();
  }


  public boolean getProductCodeCheckBox() {
    testWebDriver.waitForElementToAppear(productCodeCheckBox);
    return productCodeCheckBox.isSelected();
  }

  public boolean getCostCheckBox() {
    testWebDriver.waitForElementToAppear(costCheckBox);
    return costCheckBox.isSelected();
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


  public boolean getPackedDateCheckBox() {
    testWebDriver.waitForElementToAppear(packedDateCheckBox);
    return packedDateCheckBox.isSelected();
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

  public boolean getShippedDateCheckBox() {
    testWebDriver.waitForElementToAppear(shippedDateCheckBox);
    return shippedDateCheckBox.isSelected();
  }

  public void checkShippedDateCheckBox() {
    testWebDriver.waitForElementToAppear(shippedDateCheckBox);
    if (!shippedDateCheckBox.isSelected())
        shippedDateCheckBox.click();
  }

  public void unCheckShippedDateCheckBox() {
    testWebDriver.waitForElementToAppear(shippedDateCheckBox);
    if (shippedDateCheckBox.isSelected())
        shippedDateCheckBox.click();
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
    testWebDriver.waitForElementToAppear(orderNumberTextField);
    sendKeys(orderNumberTextField, value);
  }

  public String getOrderNumber() {
    testWebDriver.waitForElementToAppear(orderNumberTextField);
    return testWebDriver.getAttribute(orderNumberTextField,"value");
  }

  public String getCost() {
    testWebDriver.waitForElementToAppear(costTextField);
    return testWebDriver.getAttribute(costTextField, "value");
  }

  public void setCost(String value) {
    testWebDriver.waitForElementToAppear(costTextField);
    sendKeys(costTextField, value);
  }

  public String getProductCode() {
    testWebDriver.waitForElementToAppear(productCodeTextField);
    return testWebDriver.getAttribute(productCodeTextField, "value");
  }

  public void setProductCode(String value) {
    testWebDriver.waitForElementToAppear(productCodeTextField);
    sendKeys(productCodeTextField, value);
  }

  public String getPackedDate() {
    testWebDriver.waitForElementToAppear(packedDateTextField);
    return testWebDriver.getAttribute(packedDateTextField, "value");
  }

  public void setPackedDate(String value) {
    testWebDriver.waitForElementToAppear(packedDateTextField);
    sendKeys(packedDateTextField, value);
  }

  public String getShippedDate() {
    testWebDriver.waitForElementToAppear(shippedDateTextField);
    return testWebDriver.getAttribute(shippedDateTextField, "value");
  }

  public void setShippedDate(String value) {
    testWebDriver.waitForElementToAppear(shippedDateTextField);
    sendKeys(shippedDateTextField, value);
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
            assertTrue("orderNumberCheckBox should be checked", orderNumberCheckBox.isSelected());
            assertTrue("quantityShippedCheckBox should be checked", quantityShippedCheckBox.isSelected());
            assertTrue("productCodeCheckBox should be checked", productCodeCheckBox.isSelected());
            assertFalse("packedDateCheckBox should be checked", packedDateCheckBox.isSelected());
            assertFalse("shippedDateCheckBox should be checked", shippedDateCheckBox.isSelected());
            assertFalse("costCheckBox should be checked", costCheckBox.isSelected());

            assertFalse("orderNumberCheckBox should be disabled", orderNumberCheckBox.isEnabled());
            assertFalse("quantityShippedCheckBox should be disabled", quantityShippedCheckBox.isEnabled());
            assertFalse("productCodeCheckBox should be disabled", productCodeCheckBox.isEnabled());
            assertTrue("costCheckBox should be enabled", costCheckBox.isEnabled());
            assertTrue("packedDateCheckBox should be enabled", packedDateCheckBox.isEnabled());
            assertTrue("shippedDateCheckBox should be enabled", shippedDateCheckBox.isEnabled());

    }

    public void verifyDefaultPositionValues() {
        assertEquals("1", orderNumberTextField.getAttribute("value")) ;
        assertEquals("3", quantityShippedTextField.getAttribute("value"));
        assertEquals("2", productCodeTextField.getAttribute("value"));
        assertEquals("5", packedDateTextField.getAttribute("value"));
        assertEquals("6", shippedDateTextField.getAttribute("value"));
        assertEquals("4", costTextField.getAttribute("value"));
    }
}