package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestCase.assertEquals;
import static org.openqa.selenium.support.How.ID;

public class UpdatePodPage extends Page {

  @FindBy(how = ID, using = "podTable")
  private WebElement podTable = null;

  @FindBy(how = ID, using = "podPageTitle")
  private WebElement podPageTitle = null;

  @FindBy(how = ID, using = "noProductsLabel")
  private WebElement noProductsMessage = null;

  @FindBy(how = ID, using = "firstPageLink")
  private WebElement firstPageLink = null;

  @FindBy(how = ID, using = "previousPageLink")
  private WebElement previousPageLink = null;

  @FindBy(how = ID, using = "nextPageLink")
  private WebElement nextPageLink = null;

  @FindBy(how = ID, using = "lastPageLink")
  private WebElement lastPageLink = null;

  @FindBy(how = ID, using = "requisitionTypeClass")
  private WebElement requisitionTypeClass = null;

  @FindBy(how = ID, using = "requisitionType")
  private WebElement requisitionType = null;

  @FindBy(how = ID, using = "orderNumberLabel")
  private WebElement orderNumberLabel = null;

  @FindBy(how = ID, using = "orderId")
  private WebElement orderId = null;

  @FindBy(how = ID, using = "facilityLabel")
  private WebElement facilityLabel = null;

  @FindBy(how = ID, using = "facilityCode")
  private WebElement facilityCode = null;

  @FindBy(how = ID, using = "orderDateTimeLabel")
  private WebElement orderDateTimeLabel = null;

  @FindBy(how = ID, using = "orderCreatedDate")
  private WebElement orderCreatedDate = null;

  @FindBy(how = ID, using = "supplyingDepotLabel")
  private WebElement supplyingDepotLabel = null;

  @FindBy(how = ID, using = "supplyingDepot")
  private WebElement supplyingDepot = null;

  @FindBy(how = ID, using = "reportingPeriodLabel")
  private WebElement reportingPeriodLabel = null;

  @FindBy(how = ID, using = "periodStartDate")
  private WebElement periodStartDate = null;

  @FindBy(how = ID, using = "saveButton")
  private WebElement saveButton = null;

  @FindBy(how = ID, using = "submitButton")
  private WebElement submitButton = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private WebElement PodSaveSuccessMsg = null;

  public UpdatePodPage(TestWebDriver testWebDriver) {
    super(testWebDriver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public String getProductCode(int rowNumber) {
    WebElement productCode = testWebDriver.findElement(By.id("productCode" + (rowNumber - 1)));
    testWebDriver.waitForElementToAppear(productCode);
    return productCode.getText();
  }

  public String getProductName(int rowNumber) {
    return testWebDriver.findElement(By.id("productName" + (rowNumber - 1))).getText();
  }

  public String getCategoryName(int rowNumber) {
    return testWebDriver.findElement(By.id("category" + (rowNumber - 1))).getText();
  }

  public String getUnitOfIssue(int rowNumber) {
    return testWebDriver.findElement(By.id("dispensingUnit" + (rowNumber - 1))).getText();
  }

  public String getPacksToShip(int rowNumber) {
    return testWebDriver.findElement(By.id("packsToShip" + (rowNumber - 1))).getText();
  }

  public String getQuantityShipped(int rowNumber) {
    return testWebDriver.findElement(By.id("quantityShipped" + (rowNumber - 1))).getText();
  }

  public String getQuantityReceived(int rowNumber) {
    testWebDriver.scrollToElement(testWebDriver.findElement(By.id("quantityReceived" + (rowNumber - 1))));
    return testWebDriver.findElement(By.id("quantityReceived" + (rowNumber - 1))).getAttribute("value");
  }

  public String getNotes(int rowNumber) {
    return testWebDriver.findElement(By.id("notes" + (rowNumber - 1))).getAttribute("value");
  }

  public void setQuantityReceived(int rowNumber, String quantityReceived) {
    WebElement elementQuantityReceived = testWebDriver.findElement(By.id("quantityReceived" + (rowNumber - 1)));
    elementQuantityReceived.clear();
    elementQuantityReceived.sendKeys(quantityReceived);
  }

  public void setNotes(int rowNumber, String notes) {
    WebElement elementNotes = testWebDriver.findElement(By.id("notes" + (rowNumber - 1)));
    elementNotes.clear();
    elementNotes.sendKeys(notes);
  }

  public String getTitle() {
    testWebDriver.waitForAjax();
    return testWebDriver.getText(podPageTitle);
  }

  public String getPodTableData() {
    testWebDriver.waitForAjax();
    return testWebDriver.getText(podTable);
  }

  public String getNoProductsMessage() {
    testWebDriver.waitForAjax();
    return testWebDriver.getText(noProductsMessage);
  }

  public Boolean isFullSupplyTickIconDisplayed(int rowNumber) {
    testWebDriver.waitForAjax();
    WebElement tickIcon = testWebDriver.getElementById("tickIcon" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(tickIcon);
    return tickIcon.isDisplayed();
  }

  public boolean isFirstPageLinkEnabled() {
    testWebDriver.waitForElementToAppear(firstPageLink);
    return firstPageLink.getCssValue("color").contains("rgba(119, 119, 119, 1)");
  }

  public boolean isFirstPageLinkDisplayed() {
    testWebDriver.waitForElementToAppear(firstPageLink);
    return firstPageLink.isDisplayed();
  }

  public boolean isPreviousPageLinkEnabled() {
    testWebDriver.waitForElementToAppear(previousPageLink);
    return previousPageLink.getCssValue("color").contains("rgba(119, 119, 119, 1)");
  }

  public boolean isPreviousPageLinkDisplayed() {
    testWebDriver.waitForElementToAppear(previousPageLink);
    return previousPageLink.isDisplayed();
  }

  public boolean isNextPageLinkEnabled() {
    testWebDriver.waitForElementToAppear(nextPageLink);
    return nextPageLink.getCssValue("color").contains("rgba(119, 119, 119, 1)");
  }

  public boolean isNextPageLinkDisplayed() {
    testWebDriver.waitForElementToAppear(nextPageLink);
    return nextPageLink.isDisplayed();
  }

  public boolean isLastPageLinkEnabled() {
    testWebDriver.waitForElementToAppear(lastPageLink);
    return lastPageLink.getCssValue("color").contains("rgba(119, 119, 119, 1)");
  }

  public boolean isLastPageLinkDisplayed() {
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

  public String getRequisitionTypeColor() {
    testWebDriver.waitForElementToAppear(requisitionTypeClass);
    return requisitionTypeClass.getCssValue("background-color");
  }

  public String getRequisitionType() {
    testWebDriver.waitForElementToAppear(requisitionType);
    return requisitionType.getText();
  }

  public String getOrderNumberLabel() {
    testWebDriver.waitForElementToAppear(orderNumberLabel);
    return orderNumberLabel.getText();
  }

  public String getOrderId() {
    testWebDriver.waitForElementToAppear(orderId);
    return orderId.getText();
  }

  public String getOrderDateTimeLabel() {
    testWebDriver.waitForElementToAppear(orderDateTimeLabel);
    return orderDateTimeLabel.getText();
  }

  public String getOrderCreatedDate() {
    testWebDriver.waitForElementToAppear(orderCreatedDate);
    return orderCreatedDate.getText();
  }

  public String getSupplyingDepotLabel() {
    testWebDriver.waitForElementToAppear(supplyingDepotLabel);
    return supplyingDepotLabel.getText();
  }

  public String getSupplyingDepot() {
    testWebDriver.waitForElementToAppear(supplyingDepot);
    return supplyingDepot.getText();
  }

  public String getFacilityLabel() {
    testWebDriver.waitForElementToAppear(facilityLabel);
    return facilityLabel.getText();
  }

  public String getFacilityCode() {
    testWebDriver.waitForElementToAppear(facilityCode);
    return facilityCode.getText();
  }

  public String getReportingPeriodLabel() {
    testWebDriver.waitForElementToAppear(reportingPeriodLabel);
    return reportingPeriodLabel.getText();
  }

  public String getPeriodStartDate() {
    testWebDriver.waitForElementToAppear(periodStartDate);
    return periodStartDate.getText();
  }

  public void clickSave() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public Boolean isPodSaveSuccessMessageDisplayed() {
    testWebDriver.waitForAjax();
    testWebDriver.waitForElementToAppear(PodSaveSuccessMsg);
    return PodSaveSuccessMsg.isDisplayed();
  }

  public void enterPodData(String quantityReceived, String notes, int rowNumber) {
    setQuantityReceived(rowNumber, quantityReceived);
    setNotes(rowNumber, notes);
  }

  public void verifyQuantityReceivedAndNotes(String quantityReceived, String notes, Integer rowNumber) {
    assertEquals(quantityReceived, getQuantityReceived(rowNumber));
    assertEquals(notes, getNotes(rowNumber));
  }
}
