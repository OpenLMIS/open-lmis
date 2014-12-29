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


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static org.openqa.selenium.support.How.*;


public class DistributionPage extends Page {

  @FindBy(how = ID, using = "selectDeliveryZone")
  private WebElement selectDeliveryZoneSelectBox = null;

  @FindBy(how = ID, using = "selectProgram")
  private WebElement selectProgramSelectBox = null;

  @FindBy(how = ID, using = "selectPeriod")
  private WebElement selectPeriodSelectBox = null;

  @FindBy(how = XPATH, using = "//input[@value='View load amounts']")
  private WebElement viewLoadAmountButton = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private WebElement saveSuccessMessageDiv = null;

  @FindBy(how = ID, using = "deleteDistribution0")
  private WebElement deleteDistributionIcon = null;

  @FindBy(how = ID, using = "button_Cancel")
  private WebElement cancelButton = null;

  @FindBy(how = ID, using = "button_OK")
  private WebElement okButton = null;

  @FindBy(how = XPATH, using = "//div[@id='distributionInitiated']/div[2][@class='modal-body']/p")
  private WebElement deleteConfirmDialogMessage = null;

  @FindBy(how = XPATH, using = "//div[@id='distributionInitiated']/div[1][@class='modal-header']/h3")
  private WebElement deleteConfirmDialogHeader = null;

  @FindBy(how = XPATH, using = "//div[@id='noDistributionInitiated']/span")
  private WebElement noDistributionCachedMessage = null;

  @FindBy(how = XPATH, using = "//div[@id='synchronizationModal']/div[3]/input[1]")
  private WebElement distributionSyncMessageDone = null;

  @FindBy(how = ID, using = "syncedFacilities")
  private WebElement syncMessage = null;

  @FindBy(how = ID, using = "syncMessage")
  private WebElement syncAlertMessage = null;

  @FindBy(how = ID, using = "failedFacilityHeader")
  private WebElement facilitySyncFailedMessage = null;

  @FindBy(how = ID, using = "retryButton")
  private WebElement retryButton = null;

  @FindBy(how = XPATH, using = "//span[@openlmis-message='syncProgressHeader']")
  private WebElement syncStatusMessage = null;

  @FindBy(how = ID, using = "distributionList")
  private WebElement distributionList = null;

  @FindBy(how = ID, using = "distributionStatus")
  private WebElement distributionStatus = null;

  @FindBy(how = CSS, using = "div.navigation-locale-bar>ng-include.ng-scope>div.ng-scope>div.locale-container>ul>li.ng-scope>a#locale_pt")
  private static WebElement langPortugueseLink = null;

  @FindBy(how = CSS, using = "div.navigation-locale-bar>ng-include.ng-scope>div.ng-scope>div.locale-container>ul>li.ng-scope>a#locale_en")
  private static WebElement langEnglishLink = null;


  public DistributionPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public void selectValueFromDeliveryZone(String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(selectDeliveryZoneSelectBox);
    testWebDriver.selectByVisibleText(selectDeliveryZoneSelectBox, valueToBeSelected);
  }

  public void selectValueFromProgram(String valueToBeSelected) {
    testWebDriver.waitForAjax();
    testWebDriver.selectByVisibleText(selectProgramSelectBox, valueToBeSelected);
  }

  public void selectValueFromPeriod(String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(selectPeriodSelectBox);
    testWebDriver.selectByVisibleText(selectPeriodSelectBox, valueToBeSelected);
  }

  public void clickViewLoadAmount() {
    testWebDriver.waitForElementToAppear(viewLoadAmountButton);
    viewLoadAmountButton.click();
    testWebDriver.sleep(1000);
  }

  public void verifyDataAlreadyCachedMessage(String deliveryZone, String program, String period) {
    String message = String.format("The data for the selected %s, %s, %s is already cached", deliveryZone, program, period);
    assertEquals(message, saveSuccessMessageDiv.getText());
  }

  public void clickInitiateDistribution() {
    WebElement initiateDistributionButton = testWebDriver.findElement(By.id("initiateDistribution"));
    testWebDriver.waitForElementToBeEnabled(initiateDistributionButton);
    initiateDistributionButton.click();
  }

  public void clickSyncDistribution(int rowNumber) {
    testWebDriver.sleep(500);
    testWebDriver.findElement(By.id("sync" + (rowNumber - 1))).click();
  }

  public void syncDistribution(int rowNumber) {
    clickSyncDistribution(rowNumber);
    testWebDriver.sleep(1000);
    okButton.click();
  }

  public void syncDistributionMessageDone() {
    distributionSyncMessageDone.click();
  }

  public String getSyncMessage() {
    testWebDriver.waitForElementToAppear(syncMessage);
    return syncMessage.getText();
  }

  public String getSyncAlertMessage() {
    testWebDriver.waitForElementToAppear(syncAlertMessage);
    return syncAlertMessage.getText();
  }

  public FacilityListPage clickRecordData(int rowNumber) {
    testWebDriver.sleep(1000);
    testWebDriver.waitForAjax();
    testWebDriver.waitForElementToAppear(testWebDriver.findElement(By.id("recordData" + (rowNumber - 1))));
    WebElement recordDataLink = testWebDriver.findElement(By.id("recordData" + (rowNumber - 1)));
    recordDataLink.click();
    return PageObjectFactory.getFacilityListPage(testWebDriver);
  }

  public FacilityListPage clickViewData(int rowNumber) {
    testWebDriver.sleep(1000);
    testWebDriver.waitForAjax();
    testWebDriver.waitForElementToAppear(testWebDriver.findElement(By.id("viewData" + (rowNumber - 1))));
    WebElement viewDataLink = testWebDriver.findElement(By.id("viewData" + (rowNumber - 1)));
    viewDataLink.click();
    return PageObjectFactory.getFacilityListPage(testWebDriver);
  }

  public void verifyDownloadSuccessFullMessage(String deliveryZone, String program, String period) {
    testWebDriver.waitForElementToAppear(saveSuccessMessageDiv);

    String message = String.format("Data for the selected %s, %s, %s has been downloaded", deliveryZone, program, period);
    assertEquals(message, saveSuccessMessageDiv.getText());
  }


  public void verifyFacilityNotSupportedMessage(String programFirst, String deliveryZoneNameFirst) {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(saveSuccessMessageDiv);
    String message = String.format("Program \"%s\" is not supported by any facility in delivery zone \"%s\"",
      programFirst, deliveryZoneNameFirst);
    assertEquals(message, saveSuccessMessageDiv.getText());
  }

  public List<WebElement> getAllSelectOptionsFromDeliveryZone() {
    testWebDriver.waitForElementToAppear(selectDeliveryZoneSelectBox);
    return testWebDriver.getOptions(selectDeliveryZoneSelectBox);
  }

  public List<WebElement> getAllSelectOptionsFromProgram() {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    return testWebDriver.getOptions(selectProgramSelectBox);
  }

  public List<WebElement> getAllSelectOptionsFromPeriod() {
    testWebDriver.waitForElementToAppear(selectPeriodSelectBox);
    return testWebDriver.getOptions(selectPeriodSelectBox);
  }

  public WebElement getFirstSelectedOptionFromDeliveryZone() {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(selectDeliveryZoneSelectBox);
    return testWebDriver.getFirstSelectedOption(selectDeliveryZoneSelectBox);
  }

  public WebElement getFirstSelectedOptionFromProgram() {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    return testWebDriver.getFirstSelectedOption(selectProgramSelectBox);
  }

  public WebElement getFirstSelectedOptionFromPeriod() {
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(selectPeriodSelectBox);
    testWebDriver.sleep(100);
    return testWebDriver.getFirstSelectedOption(selectPeriodSelectBox);
  }

  public boolean isDisplayedSelectDeliveryZoneSelectBox() {
    return selectDeliveryZoneSelectBox.isDisplayed();
  }

  public boolean isDisplayedSelectProgramSelectBox() {
    return selectProgramSelectBox.isDisplayed();
  }

  public boolean isDisplayedSelectPeriodSelectBox() {
    return selectPeriodSelectBox.isDisplayed();
  }

  public boolean isDisplayedViewLoadAmountButton() {
    return viewLoadAmountButton.isDisplayed();
  }

  public boolean verifyDeliveryZoneSelectBoxNotPresent() {
    try {
      return selectDeliveryZoneSelectBox.isDisplayed();
    } catch (NoSuchElementException ignored) {
      return false;
    }
  }

  public boolean verifyProgramSelectBoxNotPresent() {
    try {
      return selectProgramSelectBox.isDisplayed();
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  public boolean verifyPeriodSelectBoxNotPresent() {
    try {
      return selectPeriodSelectBox.isDisplayed();
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  public void deleteDistribution() {
    testWebDriver.waitForElementToBeEnabled(deleteDistributionIcon);
    deleteDistributionIcon.click();
  }

  public void verifyDeleteConfirmMessageAndHeader() {
    assertEquals("Are you sure you want to delete this distribution? " +
      "Any data that has not been synced with the server will be lost.", deleteConfirmDialogMessage.getText());
    assertEquals("Delete distribution", deleteConfirmDialogHeader.getText());
  }

  public void clickOk() {
    testWebDriver.waitForElementToAppear(okButton);
    okButton.click();
  }

  public void clickPortugueseLink() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(langPortugueseLink);
    langPortugueseLink.click();
  }

  public void clickEnglishLink() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(langEnglishLink);
    langEnglishLink.click();
  }

  public void CancelDeleteDistribution() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public void verifyNoDistributionCachedMessage() {
    testWebDriver.waitForElementToAppear(noDistributionCachedMessage);
    assertEquals("No distributions cached", noDistributionCachedMessage.getText());
  }

  public void initiate(String deliveryZoneName, String programName) {
    selectValueFromDeliveryZone(deliveryZoneName);
    selectValueFromProgram(programName);
    clickInitiateDistribution();
  }

  public boolean isFacilitySyncFailed() {
    testWebDriver.waitForElementToAppear(facilitySyncFailedMessage);
    return facilitySyncFailedMessage.isDisplayed();
  }

  public void clickRetryButton() {
    testWebDriver.waitForElementToAppear(retryButton);
    retryButton.click();
  }

  public void clickCancelSyncRetry() {
    WebElement cancelSyncButton = testWebDriver.getElementByXpath("//*[@id='synchronizationModal']/div[3]/input[3]");
    testWebDriver.waitForElementToAppear(cancelSyncButton);
    cancelSyncButton.click();
  }

  public String getSyncStatusMessage() {
    testWebDriver.waitForElementToAppear(syncStatusMessage);
    return syncStatusMessage.getText();
  }

  public String getPeriodDropDownList() {
    testWebDriver.waitForElementToAppear(selectPeriodSelectBox);
    return selectPeriodSelectBox.getText();
  }

  public String getTextDistributionList() {
    testWebDriver.waitForAjax();
    return distributionList.getText();
  }

  public String getDistributionStatus() {
    testWebDriver.waitForAjax();
    return distributionStatus.getText();
  }

  public void clickGoOnlineButton() {
    WebElement goOnlineButton = testWebDriver.getElementByXpath("//*[@id='goOnlineNavigation']/li/a/span");
    testWebDriver.waitForElementToAppear(goOnlineButton);
    goOnlineButton.click();
  }
}