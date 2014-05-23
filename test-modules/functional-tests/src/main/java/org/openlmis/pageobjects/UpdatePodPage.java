package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

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
  private WebElement podSuccessMsg = null;

  @FindBy(how = ID, using = "saveFailMessage")
  private WebElement podFailMsg = null;

  @FindBy(how = ID, using = "pageErrors")
  private WebElement pageErrors = null;

  @FindBy(how = ID, using = "button_OK")
  private WebElement okButton = null;

  @FindBy(how = ID, using = "button_Cancel")
  private WebElement cancelButton = null;

  @FindBy(how = ID, using = "deliveredBy")
  private WebElement deliveredBy = null;

  @FindBy(how = ID, using = "receivedBy")
  private WebElement receivedBy = null;

  @FindBy(how = ID, using = "receivedDate")
  private WebElement receivedDate = null;

  @FindBy(how = ID, using = "printButton")
  private WebElement printButton = null;

  public UpdatePodPage(TestWebDriver testWebDriver) {
    super(testWebDriver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public String getProductCode(int rowNumber) {
    WebElement productCode = testWebDriver.getElementById("productCode" + (rowNumber - 1));
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
    testWebDriver.scrollToElement(testWebDriver.getElementById("quantityReceived" + (rowNumber - 1)));
    return testWebDriver.getElementById("quantityReceived" + (rowNumber - 1)).getAttribute("value");
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

  public Boolean isPodSuccessMessageDisplayed() {
    testWebDriver.waitForElementToAppear(podSuccessMsg);
    return podSuccessMsg.isDisplayed();
  }

  public void enterPodData(String quantityReceived, String notes, String quantityReturned, int rowNumber) {
    setQuantityReceived(rowNumber, quantityReceived);
    setNotes(rowNumber, notes);
    setQuantityReturned(rowNumber, quantityReturned);
  }

  public void setQuantityReturned(int rowNumber, String quantityReturned) {
    WebElement elementQuantityReceived = testWebDriver.findElement(By.id("quantityReturned" + (rowNumber - 1)));
    elementQuantityReceived.clear();
    elementQuantityReceived.sendKeys(quantityReturned);
  }

  public void verifyQuantityReceivedAndNotes(String quantityReceived, String notes, Integer rowNumber) {
    assertEquals(quantityReceived, getQuantityReceived(rowNumber));
    assertEquals(notes, getNotes(rowNumber));
  }

  public void verifyQuantityReturnedOnUI(String quantityReturned, Integer rowNumber) {
    assertEquals(quantityReturned, getQuantityReturned(rowNumber));
  }

  public String getQuantityReturned(int rowNumber) {
    WebElement quantityReturned = testWebDriver.getElementById("quantityReturned" + (rowNumber - 1));
    testWebDriver.scrollToElement(quantityReturned);
    return quantityReturned.getAttribute("value");
  }

  public String getReplacedProductCode(int rowNumber) {
    WebElement replacedProductCode = testWebDriver.getElementById("replacedProductCode" + (rowNumber - 1));
    testWebDriver.scrollToElement(replacedProductCode);
    return replacedProductCode.getText();
  }

  public void clickSubmitButton() {
    testWebDriver.waitForElementToAppear(submitButton);
    submitButton.click();
  }

  public Boolean isPodFailMessageDisplayed() {
    testWebDriver.waitForAjax();
    testWebDriver.waitForElementToAppear(podFailMsg);
    return podFailMsg.isDisplayed();
  }

  public String getPodFailMessage() {
    testWebDriver.waitForElementToAppear(podFailMsg);
    return podFailMsg.getText();
  }

  public String getPodSuccessMessage() {
    testWebDriver.waitForElementToAppear(podSuccessMsg);
    return podSuccessMsg.getText();
  }

  public String getPageErrorsMessage() {
    testWebDriver.waitForElementToAppear(pageErrors);
    return pageErrors.getText();
  }

  public void clickPageErrorsMessage() {
    WebElement pageErrorClick = testWebDriver.getElementByXpath("//div[@id='pageErrors']/div/a");
    testWebDriver.waitForElementToAppear(pageErrorClick);
    pageErrorClick.click();
  }

  public void clickErrorPage(int pageNumber) {
    WebElement errorPage = testWebDriver.getElementById("errorPageLink" + pageNumber);
    testWebDriver.waitForElementToAppear(errorPage);
    errorPage.click();
  }

  public void clickOkButton() {
    testWebDriver.waitForElementToAppear(okButton);
    okButton.click();
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public boolean isQuantityReceivedEnabled(int rowNumber) {
    WebElement quantityReceived = testWebDriver.getElementById("quantityReceived" + (rowNumber - 1));
    testWebDriver.scrollToElement(quantityReceived);
    return quantityReceived.isEnabled();
  }

  public boolean isNotesEnabled(int rowNumber) {
    WebElement notes = testWebDriver.getElementById("notes" + (rowNumber - 1));
    testWebDriver.scrollToElement(notes);
    return notes.isEnabled();
  }

  public boolean isQuantityReturnedEnabled(int rowNumber) {
    WebElement quantityReturned = testWebDriver.getElementById("quantityReturned" + (rowNumber - 1));
    testWebDriver.scrollToElement(quantityReturned);
    return quantityReturned.isEnabled();
  }

  public void enterDeliveryDetailsInPodScreen(String deliveredByValue, String receivedByValue, String receivedDateValue) {
    testWebDriver.waitForElementToAppear(deliveredBy);
    testWebDriver.scrollToElement(deliveredBy);
    deliveredBy.clear();
    sendKeys(deliveredBy, deliveredByValue);
    testWebDriver.waitForElementToAppear(receivedBy);
    receivedBy.clear();
    sendKeys(receivedBy, receivedByValue);
    testWebDriver.waitForElementToAppear(receivedDate);
    receivedDate.clear();
    sendKeys(receivedDate, receivedDateValue);
  }

  public String getDeliveredByValue() {
    testWebDriver.waitForElementToAppear(deliveredBy);
    return deliveredBy.getAttribute("value");
  }

  public String getReceivedByValue() {
    testWebDriver.waitForElementToAppear(receivedBy);
    return receivedBy.getAttribute("value");
  }

  public String getReceivedDate() {
    testWebDriver.waitForElementToAppear(receivedDate);
    return receivedDate.getAttribute("value");
  }

  public void verifyDeliveryDetailsOnPodScreenUI(String deliveredByValue, String receivedByValue, String receivedDateValue) {
    assertEquals(deliveredByValue, getDeliveredByValue());
    assertEquals(receivedByValue, getReceivedByValue());
    assertEquals(receivedDateValue, getReceivedDate());
  }

  public boolean isDeliveryByFieldEnabled() {
    testWebDriver.waitForElementToAppear(deliveredBy);
    testWebDriver.scrollToElement(deliveredBy);
    return deliveredBy.isEnabled();
  }

  public boolean isReceivedByFieldEnabled() {
    testWebDriver.waitForElementToAppear(receivedBy);
    testWebDriver.scrollToElement(receivedBy);
    return receivedBy.isEnabled();
  }

  public boolean isReceivedDateFieldEnabled() {
    testWebDriver.waitForElementToAppear(receivedDate);
    testWebDriver.scrollToElement(receivedDate);
    return receivedDate.isEnabled();
  }

  public void clickPrintButton() {
    testWebDriver.waitForElementToAppear(printButton);
    printButton.click();
  }
}
