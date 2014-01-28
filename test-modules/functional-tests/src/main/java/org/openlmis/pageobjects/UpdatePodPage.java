package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class UpdatePodPage extends Page {

  @FindBy(how = ID, using = "podTable")
  private WebElement podTable = null;

  @FindBy(how = ID, using = "requisition-header")
  private WebElement updatePodPageHeader = null;

  @FindBy(how = XPATH, using = "//div/h2[@openlmis-message='header.proof.of.delivery']")
  private WebElement podPageTitle = null;

  @FindBy(how = XPATH, using = "//div[@class='alert alert-info']")
  private WebElement noProductsMessage = null;

  @FindBy(how = XPATH, using = "//div//i[@class='icon-ok']")
  private WebElement fullSupplyTickIcon= null;

  @FindBy(how = ID, using = "firstPageLink")
  private WebElement firstPageLink = null;

  @FindBy(how = ID, using = "previousPageLink")
  private WebElement previousPageLink = null;

  @FindBy(how = ID, using = "nextPageLink")
  private WebElement nextPageLink = null;

  @FindBy(how = ID, using = "lastPageLink")
  private WebElement lastPageLink = null;

  public UpdatePodPage(TestWebDriver testWebDriver) {
    super(testWebDriver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public String getProductCode(int rowNumber) {
    WebElement productCode = testWebDriver.findElement(By.id("productCode_" + (rowNumber - 1)));
    testWebDriver.waitForElementToAppear(productCode);
    return productCode.getText();
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
    return testWebDriver.getText(updatePodPageHeader);
  }

  public String getPodTableData() {
    testWebDriver.waitForAjax();
    return testWebDriver.getText(podTable);
  }

  public String getNoProductsMessage() {
    testWebDriver.waitForAjax();
    return testWebDriver.getText(noProductsMessage);
  }

  public Boolean getFullSupplyTickIcon() {
    testWebDriver.waitForAjax();
    return fullSupplyTickIcon.isDisplayed();
  }

  public boolean isFirstPageLinkEnabled(){
    testWebDriver.waitForElementToAppear(firstPageLink);
    return firstPageLink.getCssValue("color").contains( "rgba(119, 119, 119, 1)");
  }

  public boolean isFirstPageLinkDisplayed(){
    testWebDriver.waitForElementToAppear(firstPageLink);
    return firstPageLink.isDisplayed();
  }

  public boolean isPreviousPageLinkEnabled(){
    testWebDriver.waitForElementToAppear(previousPageLink);
    return previousPageLink.getCssValue("color").contains( "rgba(119, 119, 119, 1)");
  }

  public boolean isPreviousPageLinkDisplayed(){
    testWebDriver.waitForElementToAppear(previousPageLink);
    return previousPageLink.isDisplayed();
  }

  public boolean isNextPageLinkEnabled(){
    testWebDriver.waitForElementToAppear(nextPageLink);
    return nextPageLink.getCssValue("color").contains( "rgba(119, 119, 119, 1)");
  }

  public boolean isNextPageLinkDisplayed(){
    testWebDriver.waitForElementToAppear(nextPageLink);
    return nextPageLink.isDisplayed();
  }

  public boolean isLastPageLinkEnabled(){
    testWebDriver.waitForElementToAppear(lastPageLink);
    return lastPageLink.getCssValue("color").contains( "rgba(119, 119, 119, 1)");
  }

  public boolean isLastPageLinkDisplayed(){
    testWebDriver.waitForElementToAppear(lastPageLink);
    return lastPageLink.isDisplayed();
  }

  public void navigateToNextPage() {
    testWebDriver.waitForElementToAppear(nextPageLink);
    nextPageLink.click();
  }

  public void navigateToFirstPage() {
    testWebDriver.waitForElementToAppear(firstPageLink);
    firstPageLink.click();
  }

  public void navigateToLastPage() {
    testWebDriver.waitForElementToAppear(lastPageLink);
    lastPageLink.click();
  }

  public void navigateToPreviousPage() {
    testWebDriver.waitForElementToAppear(previousPageLink);
    previousPageLink.click();
  }
}
