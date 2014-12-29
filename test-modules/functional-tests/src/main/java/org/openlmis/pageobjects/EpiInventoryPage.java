package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.ID;

public class EpiInventoryPage extends DistributionTab {


  @FindBy(how = ID, using = "epiInventoryApplyNRAll")
  private static WebElement applyNRToAllButton = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okButton = null;

  @FindBy(how = ID, using = "noLineItems")
  private static WebElement noLineItems = null;

  @FindBy(how = ID, using = "epiInventoryTabIcon")
  private static WebElement epiInventoryStatusIcon = null;

  @FindBy(how = ID, using = "epiInventoryPageLabel")
  private static WebElement epiInventoryPageLabel = null;

  public EpiInventoryPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  @Override
  public void verifyIndicator(String color) {
    verifyOverallIndicator(epiInventoryStatusIcon, color);
  }

  @Override
  public void enterValues(List<Map<String, String>> dataMapList) {
    for (int i = 0; i < dataMapList.size(); ++i) {
      Map<String, String> epiInventoryData = dataMapList.get(i);
      fillExistingQuantity(i + 1, epiInventoryData.get("existingQuantity"));
      fillSpoiledQuantity(i + 1, epiInventoryData.get("spoiledQuantity"));
      fillDeliveredQuantity(i + 1, epiInventoryData.get("deliveredQuantity"));
    }
  }

  @Override
  public void verifyData(List<Map<String, String>> data) {
    for (int i = 0; i < data.size(); ++i) {
      Map<String, String> epiInventoryData = data.get(i);
      assertEquals(getDeliveredQuantity(i + 1), epiInventoryData.get("deliveredQuantity"));
      assertEquals(getExistingQuantity(i + 1), epiInventoryData.get("existingQuantity"));
      assertEquals(getSpoiledQuantity(i + 1), epiInventoryData.get("spoiledQuantity"));
      testWebDriver.findElement(By.id("productName0")).click();
    }
  }

  public void navigate() {
    testWebDriver.waitForElementToAppear(epiInventoryStatusIcon);
    epiInventoryStatusIcon.click();
    removeFocusFromElement();
  }

  @Override
  public void verifyAllFieldsDisabled() {
    int numberOfProducts = testWebDriver.getElementsSizeByXpath("//table[@id='epiInventoryTable']/tbody");
    for (int rowNumber = 1; rowNumber < numberOfProducts; rowNumber++) {
      assertFalse(getExistingQuantityStatus(rowNumber));
      assertFalse(getDeliveredQuantityStatus(rowNumber));
      assertFalse(getSpoiledQuantityStatus(rowNumber));
    }
  }

  public void fillDeliveredQuantity(int rowNumber, String deliveredQuantity) {
    WebElement element = testWebDriver.findElement(By.id("deliveredQuantity" + (rowNumber - 1)));
    element.sendKeys(deliveredQuantity);
    element.sendKeys(Keys.TAB);

  }

  public String getProductName(int rowNumber) {
    return testWebDriver.findElement(By.id("productName" + (rowNumber - 1))).getText();
  }

  public String getIsaValue(int rowNumber) {
    return testWebDriver.findElement(By.id("idealQuantity" + (rowNumber - 1))).getText();
  }

  public String getNoProductsAddedMessage() {
    if (noLineItems.getSize().getHeight() == 0 && noLineItems.getSize().getWidth() == 0) {
      return null;
    }
    return noLineItems.getText();
  }

  public void applyNRToAll() {
    applyNRToAllButton.click();
    okButton.click();
    removeFocusFromElement();
  }

  public void toggleExistingQuantityNR(int rowNumber) {
    testWebDriver.findElement(By.id("existingQuantityNR" + (rowNumber - 1))).click();
    removeFocusFromElement();
  }

  public void fillExistingQuantity(int rowNumber, String existingQuantity) {
    WebElement existingQuantityField = testWebDriver.findElement(By.id("existingQuantity" + (rowNumber - 1)));
    testWebDriver.waitForElementToAppear(existingQuantityField);
    existingQuantityField.sendKeys(existingQuantity);
    existingQuantityField.sendKeys(Keys.TAB);
  }

  public void toggleSpoiledQuantityNR(int rowNumber) {
    testWebDriver.findElement(By.id("spoiledQuantityNR" + (rowNumber - 1))).click();
    removeFocusFromElement();
  }

  public void fillSpoiledQuantity(int rowNumber, String spoiledQuantity) {
    testWebDriver.findElement(By.id("spoiledQuantity" + (rowNumber - 1))).sendKeys(spoiledQuantity);
    removeFocusFromElement();
  }

  public String getDeliveredQuantity(int rowNumber) {
    return testWebDriver.findElement(By.id("deliveredQuantity" + (rowNumber - 1))).getAttribute("value");
  }

  public String getLabelVialsUnitsLabel() {
    return testWebDriver.findElement(By.id("vialsUnitsLabel")).getText();
  }

  public String getLabelIdealQuantity() {
    return testWebDriver.findElement(By.id("idealQuantityLabel")).getText();
  }

  public String getLabelExistingQuantity() {
    return testWebDriver.findElement(By.id("existingQuantityLabel")).getText();
  }

  public String getLabelDeliveredQuantity() {
    return testWebDriver.findElement(By.id("deliveredQuantityLabel")).getText();
  }

  public String getLabelSpoiledQuantity() {
    return testWebDriver.findElement(By.id("spoiledQuantityLabel")).getText();
  }

  public String getDataEpiInventory() {
    return testWebDriver.findElement(By.id("epiInventoryContainer")).getText();
  }

  public boolean getDeliveredQuantityStatus(int rowNumber) {
    return testWebDriver.findElement(By.id("deliveredQuantity" + (rowNumber - 1))).isEnabled();
  }

  public boolean getExistingQuantityStatus(int rowNumber) {
    return testWebDriver.findElement(By.id("existingQuantity" + (rowNumber - 1))).isEnabled();
  }

  public boolean getSpoiledQuantityStatus(int rowNumber) {
    return testWebDriver.findElement(By.id("spoiledQuantity" + (rowNumber - 1))).isEnabled();
  }

  public String getSpoiledQuantity(int rowNumber) {
    return testWebDriver.findElement(By.id("spoiledQuantity" + (rowNumber - 1))).getAttribute("value");
  }

  public String getExistingQuantity(int rowNumber) {
    return testWebDriver.findElement(By.id("existingQuantity" + (rowNumber - 1))).getAttribute("value");
  }

  @Override
  public void removeFocusFromElement() {
    testWebDriver.waitForElementToAppear(epiInventoryPageLabel);
    epiInventoryPageLabel.click();
  }
}
