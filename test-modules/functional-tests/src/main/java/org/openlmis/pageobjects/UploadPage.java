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
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.FileNotFoundException;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;

public class UploadPage extends Page {

  String uploadFilePath = null;

  @FindBy(how = How.XPATH, using = "//input[@value='Upload']")
  private static WebElement uploadButton = null;

  @FindBy(how = How.XPATH, using = "//input[@value='Choose CSV File to upload']")
  private static WebElement setCsvPath = null;

  @FindBy(how = How.XPATH, using = "//select[@id='model']")
  private static WebElement uploadDropDown = null;

  @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv = null;

  @FindBy(how = How.ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsgDiv = null;


  private int implicitWait = 2000;

  public UploadPage(TestWebDriver driver) {
    super(driver);

    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(30);
    testWebDriver.waitForElementToAppear(uploadButton);
    verifyUploadPage();
  }


  private void verifyUploadPage() {
    assertTrue(uploadButton.isDisplayed());
    assertTrue(uploadDropDown.isDisplayed());
  }

  public void selectUploadType(String uploadType) {
    testWebDriver.waitForElementToAppear(uploadDropDown);
    testWebDriver.selectByVisibleText(uploadDropDown, uploadType);
  }


  public void uploadFile(String fileName) {
    uploadFilePath = this.getClass().getClassLoader().getResource(fileName).getFile();
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

  public void uploadAndVerifyGeographicZone(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Geographic Zones");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadGeographicZoneInvalidScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Geographic Zones");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }


  public void uploadFacilities(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Facilities");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadFacilitiesInvalidScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Facilities");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadDeliveryZones(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Delivery Zones");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadDeliveryZonesInvalidScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Delivery Zones");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadDeliveryZoneProgramSchedule(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Map Delivery Zones To Program Schedules");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadDeliveryZoneProgramScheduleValidScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Map Delivery Zones To Program Schedules");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadDeliveryZoneMembers(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Delivery Zone Members");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadDeliveryZoneMembersValidScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Delivery Zone Members");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadDeliveryZoneWarehouses(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Delivery Zone Warehouses");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadDeliveryZoneWarehousesValidScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Delivery Zone Warehouses");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProducts(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Products");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProductsInvalidScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Products");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProductGroupsScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Product Groups");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProductCategory(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Product Category");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadUsers(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Users");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadInvalidUserScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Users");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProgramProductMapping(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Program Product");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProgramProductMappingInvalidScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Program Product");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProgramProductPrice(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Product Prices per Program");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProgramSupportedByFacilities(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Programs supported by facilities");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadProgramSupportedByFacilitiesInvalidScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Programs supported by facilities");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadFacilityTypeToProductMapping(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Facility Approved Products");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadFacilityTypeToProductMappingInvalidScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Facility Approved Products");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadSupervisoryNodes(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Supervisory Nodes");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadRequisitionGroup(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Requisition Groups");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadRequisitionGroupMembers(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Requisition Group Members");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadRequisitionGroupProgramSchedule(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Map Requisition Groups to Programs + Schedule");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadSupplyLines(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Supply Lines");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }

  public void uploadFacilityFTPDetails(String uploadFileNameWithExtension) throws FileNotFoundException {
    selectUploadType("Facility FTP details");
    uploadFile(uploadFileNameWithExtension);
    testWebDriver.sleep(250);
  }


}
