package org.openlmis.pageobjects.edi;

import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static org.openqa.selenium.support.How.ID;


public class ConfigureOrderNumberPage extends Page {

  @FindBy(how = ID, using = "saveOrderNumberConfig")
  private WebElement saveButton = null;

  @FindBy(how = ID, using = "includeSequenceCode")
  private WebElement includeSequenceCodeCheckbox = null;

  @FindBy(how = ID, using = "orderPrefix")
  private WebElement setOrderNumberPrefix = null;

  @FindBy(how = ID, using = "orderPrefix")
  private WebElement orderNumberPrefix = null;

  @FindBy(how = ID, using = "includeProgramCode")
  private WebElement includeProgramCode = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private WebElement successMessageDiv = null;

  @FindBy(how = ID, using = "includeRnrTypeSuffix")
  private WebElement includeRnrTypeSuffixCheckbox = null;

  @FindBy(how = ID, using = "includeOrderNumberPrefix")
  private WebElement orderNumberPrefixCheckBox = null;

  public ConfigureOrderNumberPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void setOrderNumberPrefix(String value) {
    testWebDriver.waitForElementToAppear(setOrderNumberPrefix);
    sendKeys(setOrderNumberPrefix, value);
  }

  public void deletePreExistingData() {
    testWebDriver.waitForElementToAppear(setOrderNumberPrefix);
    testWebDriver.findElement(By.id("orderPrefix")).clear();
    testWebDriver.setImplicitWait(10);
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void verifyMessage(String message) {
    testWebDriver.waitForElementToAppear(successMessageDiv);
    assertEquals(message, successMessageDiv.getText());
  }

  public String getOrderNumberPrefix() {
    testWebDriver.waitForElementToAppear(orderNumberPrefix);
    return testWebDriver.getAttribute(orderNumberPrefix, "value");
  }

  public boolean isProgramCodeChecked() {
    testWebDriver.waitForElementToAppear(includeProgramCode);
    return includeProgramCode.isSelected();
  }

  public boolean isIncludeSequenceCodeChecked() {
    testWebDriver.waitForElementToAppear(includeSequenceCodeCheckbox);
    return includeSequenceCodeCheckbox.isSelected();
  }

  public boolean isIncludeSequenceCodeDisabled() {
    testWebDriver.waitForElementToAppear(includeSequenceCodeCheckbox);
    return includeSequenceCodeCheckbox.isDisplayed();
  }

  public boolean isOrderNumberPrefixSelected() {
    testWebDriver.waitForElementToAppear(orderNumberPrefixCheckBox);
    return orderNumberPrefixCheckBox.isSelected();
  }

  public void unCheckOrderNumberPrefixCheckbox() {
    testWebDriver.waitForElementToAppear(orderNumberPrefixCheckBox);
    orderNumberPrefixCheckBox.click();
  }

  public void unCheckProgramCodeCheckbox() {
    testWebDriver.waitForElementToAppear(includeProgramCode);
    includeProgramCode.click();
  }

  public void unCheckIncludeRnrTypeSuffixCheckbox() {
    testWebDriver.waitForElementToAppear(includeRnrTypeSuffixCheckbox);
    includeRnrTypeSuffixCheckbox.click();
  }

  public boolean isIncludeRnrTypeSuffixChecked() {
    testWebDriver.waitForElementToAppear(includeRnrTypeSuffixCheckbox);
    return includeRnrTypeSuffixCheckbox.isSelected();
  }
}
