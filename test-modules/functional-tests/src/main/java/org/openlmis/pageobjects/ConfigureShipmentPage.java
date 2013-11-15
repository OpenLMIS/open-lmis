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
  private static WebElement includeHeaders=null;

  @FindBy(how = ID, using = "includeCheckbox0")
  private static WebElement orderNumberCheckBox=null;

  @FindBy(how = ID, using = "includeCheckbox1")
  private static WebElement productCodeCheckBox=null;

  @FindBy(how = ID, using = "includeCheckbox2")
  private static WebElement quantityShippedCheckBox=null;

  @FindBy(how = ID, using = "includeCheckbox3")
  private static WebElement costCheckBox=null;

  @FindBy(how = ID, using = "includeCheckbox4")
  private static WebElement packedDateCheckBox=null;

  @FindBy(how = ID, using = "includeCheckbox5")
  private static WebElement shippedDateCheckBox=null;

  @FindBy(how = XPATH, using = "//div[@id='shipmentFileColumns']/div[2]/div[1]/div[4]/input")
  private static WebElement orderNumberTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='shipmentFileColumns']/div[2]/div[2]/div[4]/input")
  private static WebElement productCodeTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='shipmentFileColumns']/div[2]/div[3]/div[4]/input")
  private static WebElement quantityShippedTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='shipmentFileColumns']/div[2]/div[4]/div[4]/input")
  private static WebElement costTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='shipmentFileColumns']/div[2]/div[5]/div[4]/input")
  private static WebElement packedDateTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='shipmentFileColumns']/div[2]/div[6]/div[4]/input")
  private static WebElement shippedDateTextField=null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMessageDiv=null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement successMessageDiv=null;

  @FindBy(how = XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton=null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Cancel')]")
  private static WebElement cancelButton=null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private static WebElement packedDateSelectBoxTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/div/input")
  private static WebElement shippedDateSelectBoxTextField=null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/ul/li[1]/div")
  private static WebElement packedDateSelectBoxSelectableElement=null;

  @FindBy(how = XPATH, using = "//div[@id='select2-drop']/ul/li[1]/div")
  private static WebElement shippedDateSelectBoxSelectableElement=null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen17']/a/div/b")
  private static WebElement packedDateSelectBoxClickableLink=null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen23']/a/div/b")
  private static WebElement shippedDateSelectBoxClickableLink=null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen17']/a/span")
  private static WebElement packedDateSelectBoxDefaultSelected=null;

  @FindBy(how = XPATH, using = "//div[@id='s2id_autogen23']/a/span")
  private static WebElement shippedDateSelectBoxDefaultSelected=null;




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