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

import java.io.IOException;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class DistributionPage extends Page {

  @FindBy(how = ID, using = "selectDeliveryZone")
  private static WebElement selectDeliveryZoneSelectBox = null;

  @FindBy(how = ID, using = "selectProgram")
  private static WebElement selectProgramSelectBox = null;

  @FindBy(how = ID, using = "selectPeriod")
  private static WebElement selectPeriodSelectBox = null;

  @FindBy(how = XPATH, using = "//input[@value='View load amounts']")
  private static WebElement viewLoadAmountButton = null;

  @FindBy(how = ID, using = "initiateDistribution")
  private static WebElement initiateDistributionButton = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMessageDiv = null;

  @FindBy(how = XPATH, using = "//div[@id='cachedDistributions']/div[2]/div/div[6]/a")
  private static WebElement syncLink = null;

  @FindBy(how = XPATH, using = "//div[@id='cachedDistributions']/div[2]/div/div[7]/i[@class='icon-remove-sign']")
  private static WebElement deleteDistributionIcon = null;

  @FindBy(how = ID, using = "button_Cancel")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okButton = null;

  @FindBy(how = XPATH, using = "//div[@id='distributionInitiated']/div[2][@class='modal-body']/p")
  private static WebElement deleteConfirmDialogMessage = null;

  @FindBy(how = XPATH, using = "//div[@id='distributionInitiated']/div[1][@class='modal-header']/h3")
  private static WebElement deleteConfirmDialogHeader = null;

  @FindBy(how = XPATH, using = "//div[@id='noDistributionInitiated']/span")
  private static WebElement noDistributionCachedMessage = null;

  @FindBy(how = XPATH, using = "//div[@id='synchronizationModal']/div[3]/input[1]")
  private static WebElement distributionSyncMessageDone = null;

  @FindBy(how = ID, using = "syncedFacilities")
  private static WebElement syncMessage = null;

  @FindBy(how = XPATH, using = "//div[2][@class='alert alert-info']/span")
  private static WebElement syncAlertMessage = null;

  @FindBy(how = ID, using = "duplicateFacilities")
  private static WebElement facilityAlreadySyncMessage = null;

  public DistributionPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);

  }

  public void selectValueFromDeliveryZone(String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(selectDeliveryZoneSelectBox);
    testWebDriver.selectByVisibleText(selectDeliveryZoneSelectBox, valueToBeSelected);
  }

  public void selectValueFromProgram(String valueToBeSelected) {
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
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
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(initiateDistributionButton);
    initiateDistributionButton.click();
    testWebDriver.sleep(200);
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

  public FacilityListPage clickRecordData(int rowNumber) throws IOException {
    WebElement recordDataButton = testWebDriver.findElement(By.id("recordData" + (rowNumber - 1)));
    recordDataButton.click();
    return new FacilityListPage(testWebDriver);
  }

  public void verifyDownloadSuccessFullMessage(String deliveryZone, String program, String period) {
    testWebDriver.waitForElementToAppear(saveSuccessMessageDiv);

    String message = String.format("Data for the selected %s, %s, %s has been downloaded", deliveryZone, program, period);
    assertEquals(message, saveSuccessMessageDiv.getText());
  }

  public String getFacilityAlreadySyncMessage() {
    testWebDriver.waitForElementToAppear(facilityAlreadySyncMessage);
    return facilityAlreadySyncMessage.getText();
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

  public boolean IsDisplayedSelectDeliveryZoneSelectBox() {
    return selectDeliveryZoneSelectBox.isDisplayed();
  }

  public boolean IsDisplayedSelectProgramSelectBox() {
    return selectProgramSelectBox.isDisplayed();
  }

  public boolean IsDisplayedSelectPeriodSelectBox() {
    return selectPeriodSelectBox.isDisplayed();
  }

  public boolean IsDisplayedViewLoadAmountButton() {
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
    testWebDriver.waitForElementToAppear(deleteDistributionIcon);
    deleteDistributionIcon.click();
  }

  public void verifyDeleteConfirmMessageAndHeader() {
    assertEquals("Are you sure you want to delete this distribution? " +
      "Any data that has not been synced with the server will be lost.", deleteConfirmDialogMessage.getText());

    assertEquals("Delete distribution", deleteConfirmDialogHeader.getText());
  }

  public void confirmDeleteDistribution() {
    testWebDriver.waitForElementToAppear(okButton);
    okButton.click();
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
}