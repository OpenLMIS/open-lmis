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
    uploadFilePath = this.getClass().getClassLoader().getResource(fileName).getFile();
    setCsvPath.sendKeys(uploadFilePath);
    uploadButton.click();
    testWebDriver.sleep(500);
  }


  public void verifySuccessMessageOnUploadScreen() {
    testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
    SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());
    testWebDriver.setImplicitWait(implicitWait);
  }

  public void verifyErrorMessageOnUploadScreen() {
    testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
    SeleneseTestNgHelper.assertTrue("Error Message Not Displayed", saveErrorMsgDiv.isDisplayed());
    testWebDriver.setImplicitWait(implicitWait);
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
        selectUploadType("Map Delivery Zones To Program Schedule");
        uploadFile(uploadFileNameWithExtension);
        testWebDriver.sleep(250);
    }

    public void uploadDeliveryZoneProgramScheduleInvalidScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
        selectUploadType("Map Delivery Zones To Program Schedule");
        uploadFile(uploadFileNameWithExtension);
        testWebDriver.sleep(250);
    }

    public void uploadDeliveryZoneMembers(String uploadFileNameWithExtension) throws FileNotFoundException {
        selectUploadType("Delivery Zone Members");
        uploadFile(uploadFileNameWithExtension);
        testWebDriver.sleep(250);
    }

    public void uploadDeliveryZoneMembersInvalidScenarios(String uploadFileNameWithExtension) throws FileNotFoundException {
        selectUploadType("Delivery Zone Members");
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


}
