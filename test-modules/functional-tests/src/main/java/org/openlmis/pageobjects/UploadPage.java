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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;
import static org.openqa.selenium.support.How.ID;

public class UploadPage extends Page {

  String uploadFilePath = null;

  @FindBy(how = ID, using = "uploadButton")
  private static WebElement uploadButton = null;

  @FindBy(how = ID, using = "csvFile")
  private static WebElement setCsvPath = null;

  @FindBy(how = ID, using = "model")
  private static WebElement uploadDropDown = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsgDiv = null;

  private int implicitWait = 2000;

  public UploadPage(TestWebDriver driver) {
    super(driver);

    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(30);
  }

  public void verifyUploadPage() {
    testWebDriver.waitForElementToAppear(uploadButton);
    assertTrue(uploadButton.isDisplayed());
    assertTrue(uploadDropDown.isDisplayed());
  }

  public void selectUploadType(String uploadType) {
    testWebDriver.waitForElementToAppear(uploadDropDown);
    testWebDriver.selectByVisibleText(uploadDropDown, uploadType);
  }

  public void uploadFile(String fileName) {
    uploadFilePath = this.getClass().getClassLoader().getResource(fileName).toExternalForm();//.getFile();
    setCsvPath.sendKeys(uploadFilePath);
    uploadButton.click();
    testWebDriver.sleep(500);
  }

  public void verifySuccessMessageOnUploadScreen() {
    testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
    assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    String successMessage = "File uploaded successfully";
    assertTrue("Success Message '" + successMessage + "' should show up", saveSuccessMsgDiv.getText().toUpperCase().trim().contains(successMessage.toUpperCase().trim()));
    testWebDriver.setImplicitWait(implicitWait);
  }

  public void verifyErrorMessageOnUploadScreen() {
    testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
    assertTrue("Error Message Not Displayed", saveErrorMsgDiv.isDisplayed());
    testWebDriver.setImplicitWait(implicitWait);
  }

  public void validateErrorMessageOnUploadScreen(String message) {
    assertTrue("Error Message incorrect : " + "Expected '" + message.toLowerCase() + "' but saw '" + saveErrorMsgDiv.getText().toLowerCase() + "'.", saveErrorMsgDiv.getText().trim().toLowerCase().contains(message.trim().toLowerCase()));
  }

  public void validateSuccessMessageOnUploadScreen(String message) {
    assertTrue("Message Message incorrect : " + "Expected '" + message.toLowerCase() + "' but saw '" + saveSuccessMsgDiv.getText().toLowerCase() + "'.", saveSuccessMsgDiv.getText().trim().toLowerCase().contains(message.trim().toLowerCase()));
  }

  public void uploadGeographicZone(String uploadFileNameWithExtension) {
    selectUploadType("Geographic Zones");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadFacilities(String uploadFileNameWithExtension) {
    selectUploadType("Facilities");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadDeliveryZones(String uploadFileNameWithExtension) {
    selectUploadType("Delivery Zones");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadDeliveryZoneProgramSchedule(String uploadFileNameWithExtension) {
    selectUploadType("Map Delivery Zones To Program Schedules");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadDeliveryZoneMembers(String uploadFileNameWithExtension) {
    selectUploadType("Delivery Zone Members");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadDeliveryZoneWarehouses(String uploadFileNameWithExtension) {
    selectUploadType("Delivery Zone Warehouses");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProducts(String uploadFileNameWithExtension) {
    selectUploadType("Products");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProductGroupsScenarios(String uploadFileNameWithExtension) {
    selectUploadType("Product Groups");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProductCategory(String uploadFileNameWithExtension) {
    selectUploadType("Product Category");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadUsers(String uploadFileNameWithExtension) {
    selectUploadType("Users");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProgramProductMapping(String uploadFileNameWithExtension) {
    selectUploadType("Program Product");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProgramProductPrice(String uploadFileNameWithExtension) {
    selectUploadType("Product Prices per Program");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProgramSupportedByFacilities(String uploadFileNameWithExtension) {
    selectUploadType("Programs supported by facilities");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadFacilityTypeToProductMapping(String uploadFileNameWithExtension) {
    selectUploadType("Facility Approved Products");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadSupervisoryNodes(String uploadFileNameWithExtension) {
    selectUploadType("Supervisory Nodes");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadRequisitionGroup(String uploadFileNameWithExtension) {
    selectUploadType("Requisition Groups");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadRequisitionGroupMembers(String uploadFileNameWithExtension) {
    selectUploadType("Requisition Group Members");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadRequisitionGroupProgramSchedule(String uploadFileNameWithExtension) {
    selectUploadType("Map Requisition Groups to Programs + Schedule");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadSupplyLines(String uploadFileNameWithExtension) {
    selectUploadType("Supply Lines");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadFacilityFTPDetails(String uploadFileNameWithExtension) {
    selectUploadType("Facility FTP details");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }
}
