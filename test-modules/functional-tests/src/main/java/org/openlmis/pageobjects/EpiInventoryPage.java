package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.List;
import java.util.Map;

import static org.openqa.selenium.support.How.ID;

public class EpiInventoryPage extends DistributionTab {


  @FindBy(how = ID, using = "applyNRAll")
  private static WebElement applyNRToAllButton = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okButton = null;

  @FindBy(how = ID, using = "noLineItems")
  private static WebElement noLineItems = null;

  @FindBy(how = ID, using = "epiInventoryTabIcon")
  private static WebElement epiInventoryStatusIcon = null;

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
      fillDeliveredQuantity(i + 1, epiInventoryData.get("deliveredQuantity"));
      fillExistingQuantity(i + 1, epiInventoryData.get("existingQuantity"));
      fillSpoiledQuantity(i + 1, epiInventoryData.get("spoiledQuantity"));
    }
  }

  @Override
  public void verifyData(Map<String, String> map) {
  }

  @Override
  public void navigate() {
  }

  public void fillDeliveredQuantity(int rowNumber, String deliveredQuantity) {
    testWebDriver.findElement(By.id("deliveredQuantity" + (rowNumber - 1))).sendKeys(deliveredQuantity);
  }

  public String getProductName(int rowNumber) {
    return testWebDriver.findElement(By.id("productName" + (rowNumber - 1))).getText();
  }

  public String getIsaValue(int rowNumber) {
    return testWebDriver.findElement(By.id("idealQuantity" + (rowNumber - 1))).getText();
  }

  public String getNoProductsAddedMessage() {
    return noLineItems.getText();
  }

  public void applyNRToAll() {
    applyNRToAllButton.click();
    okButton.click();
  }

  public void toggleExistingQuantityNR(int rowNumber) {
    testWebDriver.findElement(By.id("existingQuantityNR" + (rowNumber - 1))).click();
  }

  public void fillExistingQuantity(int rowNumber, String existingQuantity) {
    testWebDriver.findElement(By.id("existingQuantity" + (rowNumber - 1))).sendKeys(existingQuantity);
  }

  public void toggleSpoiledQuantityNR(int rowNumber) {
    testWebDriver.findElement(By.id("spoiledQuantityNR" + (rowNumber - 1))).click();
  }

  public void fillSpoiledQuantity(int rowNumber, String spoiledQuantity) {
    testWebDriver.findElement(By.id("spoiledQuantity" + (rowNumber - 1))).sendKeys(spoiledQuantity);
  }

  public boolean errorMessageDisplayed(int rowNumber) {
    return testWebDriver.findElement(By.id("spoiledQuantityError" + (rowNumber - 1))).isDisplayed();
  }

  public String getDeliveredQuantity(int rowNumber) {
    return testWebDriver.findElement(By.id("deliveredQuantity" + (rowNumber - 1))).getAttribute("value");
  }

}
