/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UploadPage extends Page {

  String uploadFilePath = null;

  @FindBy(how = How.XPATH, using = "//input[@value='Upload']")
  private static WebElement uploadButton;

  @FindBy(how = How.XPATH, using = "//input[@value='Choose CSV File to upload']")
  private static WebElement setCsvPath;

  @FindBy(how = How.XPATH, using = "//select[@id='model']")
  private static WebElement uploadDropDown;

  @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv;

  @FindBy(how = How.ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsgDiv;


  private int implicitWait = 2000;

  public UploadPage(TestWebDriver driver) throws IOException {
    super(driver);

    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(30);
    testWebDriver.waitForElementToAppear(uploadButton);
    verifyUploadPage();
  }


  private void verifyUploadPage() {
    SeleneseTestNgHelper.assertTrue(uploadButton.isDisplayed());
    SeleneseTestNgHelper.assertTrue(uploadDropDown.isDisplayed());
  }

  public void selectUploadType(String uploadType) {
    testWebDriver.waitForElementToAppear(uploadDropDown);
    testWebDriver.selectByVisibleText(uploadDropDown, uploadType);
  }

  public void uploadFile(String fileName) {
    String Separator = System.getProperty("file.separator");
    File parentDir = new File(System.getProperty("user.dir"));
    uploadFilePath = parentDir.getParent() + Separator + "src" + Separator + "main" + Separator + "resources" + Separator + fileName;
    setCsvPath.sendKeys(uploadFilePath);
    uploadButton.click();
  }

  public void verifySuccessMessageOnUploadScreen() {
    String successMessage = "File uploaded successfully. 'Number of records created: *', 'Number of records updated : *'";
    testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
    SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    SeleneseTestNgHelper.assertEquals(saveSuccessMsgDiv.getText().trim(), successMessage);
    testWebDriver.setImplicitWait(implicitWait);
  }

  private void verifyErrorMessageOnUploadScreen() {
    testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
    SeleneseTestNgHelper.assertTrue("Error Message Not Displayed", saveErrorMsgDiv.isDisplayed());
    testWebDriver.setImplicitWait(implicitWait);
  }

  public void uploadAndVerifyGeographicZone(int noOfRuns) throws FileNotFoundException {
    for (int i = 1; i <= noOfRuns; i++) {
      selectUploadType("Geographic Zones");
      uploadFile("Geographic_Data.csv");
      verifySuccessMessageOnUploadScreen();
    }

  }

  public void uploadGeographicZoneInvalid() throws FileNotFoundException {
    selectUploadType("Geographic Zones");
    uploadFile("Geographic_Data_Invalid.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(500);
    selectUploadType("Geographic Zones");
    uploadFile("Geographic_Data_Duplicate.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(500);
    selectUploadType("Geographic Zones");
    uploadFile("Geographic_Data_Invalid_Code.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(500);
  }

  public void uploadFacilitiesNotAssignedToLowestGeoCode() throws FileNotFoundException {
    selectUploadType("Facilities");
    uploadFile("facilities_Lowest_Code.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(500);
  }

  public void uploadAndVerifyFacilities(int noOfRuns) throws FileNotFoundException {
    for (int i = 1; i <= noOfRuns; i++) {
      selectUploadType("Facilities");
      uploadFile("facilities.csv");
      verifySuccessMessageOnUploadScreen();
    }
  }

  public void uploadAndVerifyFacilitiesInvalidScenarios() throws FileNotFoundException {
    selectUploadType("Facilities");
    uploadFile("facilities_Duplicate_Code.csv");
    verifyErrorMessageOnUploadScreen();
  }


  public void uploadAndVerifyProducts(int noOfRuns) throws FileNotFoundException {
    for (int i = 1; i <= noOfRuns; i++) {
      selectUploadType("Products");
      uploadFile("products.csv");
      verifySuccessMessageOnUploadScreen();
    }
  }

  public void uploadAndVerifyProductsInvalidScenarios() throws FileNotFoundException {
    selectUploadType("Products");
    uploadFile("products_Duplicate_Code.csv");
    verifyErrorMessageOnUploadScreen();
  }

  public void uploadProductCategory() throws FileNotFoundException {
    selectUploadType("Product Category");
    uploadFile("Productcategoryupload.csv");
  }

  public void uploadAndVerifyUsers(int noOfRuns) throws FileNotFoundException {
    for (int i = 1; i <= noOfRuns; i++) {
      selectUploadType("Users");
      uploadFile("Users.csv");
      verifySuccessMessageOnUploadScreen();
    }
  }

  public void uploadAndVerifyInvalidUserScenarios() throws FileNotFoundException {
    selectUploadType("Users");
    uploadFile("Users_Duplicate_Email.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(250);
    selectUploadType("Users");
    uploadFile("Users_Duplicate_EmployeeId.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(250);
    selectUploadType("Users");
    uploadFile("Users_Duplicate_UserName.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(250);
    selectUploadType("Users");
    uploadFile("Users_Invalid_Supervisor.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(250);
  }

  public void uploadAndVerifyProgramProductMapping(int noOfRuns) throws FileNotFoundException {
    for (int i = 1; i <= noOfRuns; i++) {
      selectUploadType("Program Product");
      uploadFile("program_product.csv");
      verifySuccessMessageOnUploadScreen();
    }
  }

  public void uploadAndVerifyProgramProductMappingInvalidScenarios() throws FileNotFoundException {
    selectUploadType("Program Product");
    uploadFile("program_product_Invalid_ProductCode.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(250);
    selectUploadType("Program Product");
    uploadFile("program_product_Invalid_ProgramCode.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(250);
  }

  public void uploadProgramProductPrice() throws FileNotFoundException {
    selectUploadType("Product Prices per Program");
    uploadFile("Product_Cost.csv");
  }

  public void uploadProgramSupportedByFacilities(int noOfRuns) throws FileNotFoundException {
    for (int i = 1; i <= noOfRuns; i++) {
      selectUploadType("Programs supported by facilities");
      uploadFile("program_supported.csv");
      verifySuccessMessageOnUploadScreen();
    }
  }

  public void uploadProgramSupportedByFacilitiesInvalidScenarios() throws FileNotFoundException {
    selectUploadType("Programs supported by facilities");
    uploadFile("program_supported_Invalid_FacilityCode.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(250);
    selectUploadType("Programs supported by facilities");
    uploadFile("program_supported_Invalid_ProgramCode.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(250);
  }

  public void uploadAndVerifyFacilityTypeToProductMapping(int noOfRuns) throws FileNotFoundException {
    for (int i = 1; i <= noOfRuns; i++) {
      selectUploadType("Facility Approved Products");
      uploadFile("Facility_Type_To_Product_Mapping.csv");
      verifySuccessMessageOnUploadScreen();
    }
  }

  public void uploadAndVerifyFacilityTypeToProductMappingInvalidScenarios() throws FileNotFoundException {
    selectUploadType("Facility Approved Products");
    uploadFile("Facility_Type_To_Product_Mapping_Invalid_Combination.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(250);
    selectUploadType("Facility Approved Products");
    uploadFile("Facility_Type_To_Product_Mapping_Invalid_FacilityType.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(250);
    selectUploadType("Facility Approved Products");
    uploadFile("Facility_Type_To_Product_Mapping_Invalid_ProductCode.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(250);
    selectUploadType("Facility Approved Products");
    uploadFile("Facility_Type_To_Product_Mapping_Invalid_ProgramCode.csv");
    verifyErrorMessageOnUploadScreen();
    testWebDriver.sleep(250);
  }

  public void uploadSupervisoryNodes() throws FileNotFoundException {
    selectUploadType("Supervisory Nodes");
    uploadFile("Supervisory_Nodes.csv");
  }

  public void uploadRequisitionGroup() throws FileNotFoundException {
    selectUploadType("Requisition Groups");
    uploadFile("Requisition_Groups.csv");
  }

  public void uploadRequisitionGroupMembers() throws FileNotFoundException {
    selectUploadType("Requisition Group Members");
    uploadFile("Requisition_Group_Members.csv");
  }

  public void uploadRequisitionGroupProgramSchedule() throws FileNotFoundException {
    selectUploadType("Map Requisition Groups to Programs + Schedule");
    uploadFile("Requisition_Group_Program_Schedule.csv");
  }

  public void uploadSupplyLines() throws FileNotFoundException {
    selectUploadType("Supply Lines");
    uploadFile("Supply_Lines.csv");
  }


}
