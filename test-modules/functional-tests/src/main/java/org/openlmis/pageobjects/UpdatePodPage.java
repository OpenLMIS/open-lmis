package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class UpdatePodPage extends Page {

  @FindBy(how = ID, using = "podTable")
  private WebElement podTable = null;

  @FindBy(how = ID, using = "requisition-header")
  private WebElement updatePodScreenHeader = null;


  @FindBy(how = XPATH, using = "//div/h2[@openlmis-message='header.proof.of.delivery']")
  private WebElement podPageTitle = null;

  public UpdatePodPage(TestWebDriver testWebDriver) {
    super(testWebDriver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public String getProductCode(int rowNumber) {
    return testWebDriver.findElement(By.id("productCode_" + (rowNumber - 1))).getText();
  }

  public String getProductName(int rowNumber) {
    return testWebDriver.findElement(By.id("productName_" + (rowNumber - 1))).getText();
  }

  public String getUnitOfIssue(int rowNumber) {
    return testWebDriver.findElement(By.id("dispensingUnit_" + (rowNumber - 1))).getText();
  }

  public String getPacksToShip(int rowNumber) {
    return testWebDriver.findElement(By.id("packsToShip_" + (rowNumber - 1))).getText();
  }

  public String getQuantityShipped(int rowNumber) {
    return testWebDriver.findElement(By.id("quantityShipped_" + (rowNumber - 1))).getText();
  }

  public String getQuantityReceived(int rowNumber) {
    return testWebDriver.findElement(By.id("quantityReceived" + (rowNumber - 1))).getText();
  }

  public String getNotes(int rowNumber) {
    return testWebDriver.findElement(By.id("notes_" + (rowNumber - 1))).getText();
  }

  public void setQuantityReceived(int rowNumber, String quantityReceived) {
    testWebDriver.findElement(By.id("quantityReceived_" + (rowNumber - 1))).sendKeys(quantityReceived);
  }

  public void setNotes(int rowNumber, String notes) {
    testWebDriver.findElement(By.id("notes_" + (rowNumber - 1))).sendKeys(notes);
  }

  public void selectRequisitionToUpdatePod(int rowNumber) {
    testWebDriver.waitForAjax();
    testWebDriver.waitForElementToAppear(testWebDriver.findElement(By.id("updatePod"+(rowNumber-1))));
    testWebDriver.findElement(By.id("updatePod"+(rowNumber-1))).click();
  }

  public String getTitle() {
    testWebDriver.waitForAjax();
    return testWebDriver.getText(podPageTitle);
  }

  public String getHeaders() {
    testWebDriver.waitForAjax();
    return testWebDriver.getText(updatePodScreenHeader);
  }

  public String getPodTableHeaders() {
    testWebDriver.waitForAjax();
    return testWebDriver.getText(podTable);
  }

}
