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

import org.apache.commons.lang.StringUtils;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class FacilityPage extends Page {

  @FindBy(how = ID, using = "searchFacility")
  private static WebElement searchFacilityTextField = null;

  @FindBy(how = ID, using = "searchIcon")
  private static WebElement searchIcon = null;

  @FindBy(how = ID, using = "disableButton")
  private static WebElement disableButton = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okButton = null;

  @FindBy(how = ID, using = "dialogMessage")
  private static WebElement dialogMessageOnAlert = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement successMessageDiv = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsgDiv = null;

  @FindBy(how = XPATH, using = "//ng-switch/span")
  private static WebElement enabledFlag = null;

  @FindBy(how = ID, using = "facilityActiveFalse")
  private static WebElement isActiveRadioNoOption = null;

  @FindBy(how = ID, using = "enableButton")
  private static WebElement enableButton = null;

  @FindBy(how = ID, using = "remove0")
  private static WebElement removeSupportedProgram = null;

  @FindBy(how = ID, using = "code")
  private static WebElement facilityCode = null;

  @FindBy(how = ID, using = "name")
  private static WebElement facilityName = null;

  @FindBy(how = ID, using = "description")
  private static WebElement facilityDescription = null;

  @FindBy(how = ID, using = "gln")
  private static WebElement gln = null;

  @FindBy(how = ID, using = "main-phone")
  private static WebElement phoneNumber = null;

  @FindBy(how = ID, using = "fax-phone")
  private static WebElement faxNumber = null;

  @FindBy(how = ID, using = "address-1")
  private static WebElement address1 = null;

  @FindBy(how = ID, using = "address-2")
  private static WebElement address2 = null;

  @FindBy(how = ID, using = "geographic-zone")
  private static WebElement geographicZone = null;

  @FindBy(how = ID, using = "facility-type")
  private static WebElement facilityType = null;

  @FindBy(how = ID, using = "catchment-population")
  private static WebElement catchmentPopulation = null;

  @FindBy(how = ID, using = "latitude")
  private static WebElement latitude = null;

  @FindBy(how = ID, using = "longitude")
  private static WebElement longitude = null;

  @FindBy(how = ID, using = "altitude")
  private static WebElement altitude = null;

  @FindBy(how = ID, using = "operated-by")
  private static WebElement operatedBy = null;

  @FindBy(how = ID, using = "cold-storage-gross-capacity")
  private static WebElement coldStorageGrossCapacity = null;

  @FindBy(how = ID, using = "cold-storage-net-capacity")
  private static WebElement coldStorageNetCapacity = null;

  @FindBy(how = ID, using = "suppliesOthersYes")
  private static WebElement facilitySuppliesOthersYes = null;

  @FindBy(how = ID, using = "facilitySdpTrue")
  private static WebElement serviceDeliveryPoint = null;

  @FindBy(how = ID, using = "hasElectricityTrue")
  private static WebElement hasElectricityTrue = null;

  @FindBy(how = ID, using = "isOnlineTrue")
  private static WebElement isOnlineTrue = null;

  @FindBy(how = ID, using = "hasElectronicSccTrue")
  private static WebElement hasElectronicSccTrue = null;

  @FindBy(how = ID, using = "hasElectronicDARTrue")
  private static WebElement hasElectronicDarTrue = null;

  @FindBy(how = ID, using = "facilityActiveTrue")
  private static WebElement isActiveTrue = null;

  @FindBy(how = ID, using = "go-live-date")
  private static WebElement goLiveDate = null;

  @FindBy(how = ID, using = "go-down-date")
  private static WebElement goDownDate = null;

  @FindBy(how = ID, using = "comments")
  private static WebElement comments = null;

  @FindBy(how = ID, using = "programs-supported")
  private static WebElement programsSupported = null;

  @FindBy(how = ID, using = "supported-program-active")
  private static WebElement programsSupportedActiveFlag = null;

  @FindBy(how = XPATH, using = "//form[@id='create-facility']/div/div[3]/div/div/table/tbody/tr[1][@class='ng-scope']/td[2]/input")
  private static WebElement programsSupportedFirstActiveFlag = null;

  @FindBy(how = XPATH, using = "//form/div/div[3]/div/div/table/tbody/tr[1]/td[3]/input")
  private static WebElement programsSupportedFirstStartDate = null;

  @FindBy(how = ID, using = "supported-program-start-date")
  private static WebElement programsSupportedStartDate = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'25')]")
  private static WebElement startDateCalender = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okAlert = null;

  @FindBy(how = ID, using = "supported-program-add")
  private static WebElement addSupportedProgram = null;

  @FindBy(how = ID, using = "saveButton")
  public static WebElement saveButton = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'25')]")
  private static WebElement goLiveDateCalender = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'26')]")
  private static WebElement goDownDateCalender = null;

  @FindBy(how = ID, using = "addNewFacilityHeader")
  private static WebElement facilityHeader = null;

  @FindBy(how = ID, using = "edit-facility-header")
  private static WebElement editFacilityHeader = null;

  @FindBy(how = ID, using = "overrideIsaVACCINES")
  private static WebElement modifyIsaValueLink = null;

  @FindBy(how = ID, using = "overrideIsaTable")
  private static WebElement overrideIsaTable = null;

  @FindBy(how = ID, using = "override-isa0")
  private static WebElement overrideIsaTextField = null;

  @FindBy(how = ID, using = "calculated-isa0")
  private static WebElement calculatedIsaTextField = null;

  @FindBy(how = ID, using = "override-isa-values-button0")
  private static WebElement overrideISAValuesButton = null;

  @FindBy(how = ID, using = "isaDoneButton")
  private static WebElement doneIsaButton = null;

  @FindBy(how = ID, using = "isaCancelButton")
  private static WebElement cancelIsaButton = null;

  @FindBy(how = ID, using = "remove0")
  private static WebElement removeFirstProgramSupportedLink = null;

  @FindBy(how = ID, using = "noResultMessage")
  private static WebElement noResultMessage = null;

  @FindBy(how = ID, using = "nResultsMessage")
  private static WebElement nResultsMessage = null;

  @FindBy(how = ID, using = "closeButton")
  private static WebElement closeButton = null;

  @FindBy(how = ID, using = "facilitySearchLabel")
  private static WebElement searchFacilityLabel = null;

  @FindBy(how = ID, using = "searchOptionButton")
  private static WebElement searchOptionButton = null;

  @FindBy(how = ID, using = "searchOption1")
  private static WebElement searchOption2 = null;

  @FindBy(how = ID, using = "nameHeader")
  private static WebElement nameHeader = null;

  @FindBy(how = ID, using = "codeHeader")
  private static WebElement codeHeader = null;

  @FindBy(how = ID, using = "geographicZoneHeader")
  private static WebElement geographicZoneHeader = null;

  @FindBy(how = ID, using = "facilityTypeHeader")
  private static WebElement facilityTypeHeader = null;

  @FindBy(how = ID, using = "activeHeader")
  private static WebElement activeHeader = null;

  @FindBy(how = ID, using = "enabledHeader")
  private static WebElement enabledHeader = null;

  public FacilityPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public String getNewFacilityHeader() {
    testWebDriver.waitForElementToAppear(facilityHeader);
    return facilityHeader.getText().trim();
  }

  public String getEditFacilityHeader() {
    testWebDriver.waitForElementToAppear(editFacilityHeader);
    return editFacilityHeader.getText().trim();
  }

  public String enterValuesInFacilityAndClickSave(String facilityCodePrefix, String facilityNamePrefix, String program, String geoZone,
                                                  String facilityTypeValue, String operatedByValue, String population) {
    String date_time = enterValuesInFacility(facilityCodePrefix, facilityNamePrefix, program, geoZone, facilityTypeValue,
      operatedByValue, population, false);
    saveButton.click();
    return date_time;
  }

  public String enterValuesInFacility(String facilityCodePrefix, String facilityNamePrefix, String program,
                                      String geoZone, String facilityTypeValue, String operatedByValue,
                                      String population, boolean push) {

    Date dObj = new Date();
    SimpleDateFormat formatter_date_time = new SimpleDateFormat("yyyyMMdd-hhmmss");
    String date_time = formatter_date_time.format(dObj);

    String facilityCodeText = facilityCodePrefix + date_time;
    String facilityNameText = facilityNamePrefix + date_time;
    testWebDriver.waitForElementToAppear(facilityCode);
    sendKeys(facilityCode, facilityCodeText);
    sendKeys(facilityName, facilityNameText);

    testWebDriver.selectByVisibleText(operatedBy, operatedByValue);

    testWebDriver.clickForRadio(serviceDeliveryPoint);
    testWebDriver.clickForRadio(isActiveTrue);

    sendKeys(facilityDescription, "Testing description");
    sendKeys(gln, "Testing Gln");
    sendKeys(phoneNumber, "9711231305");
    sendKeys(faxNumber, "9711231305");
    sendKeys(address1, "Address1");
    sendKeys(address2, "Address2");


    testWebDriver.selectByVisibleText(facilityType, facilityTypeValue);

    testWebDriver.scrollToElement(geographicZone);
    testWebDriver.sleep(500);

    testWebDriver.selectByVisibleText(geographicZone, geoZone);
    testWebDriver.sleep(500);

    goLiveDate.click();
    testWebDriver.sleep(500);
    goLiveDateCalender.click();
    testWebDriver.sleep(500);
    goDownDate.click();
    testWebDriver.sleep(500);
    goDownDateCalender.click();

    addProgram(program, push);

    testWebDriver.scrollToElement(altitude);
    testWebDriver.sleep(500);
    sendKeys(catchmentPopulation, population);
    sendKeys(latitude, "-555.5555");
    sendKeys(longitude, "444.4444");
    sendKeys(altitude, "4545.4545");

    testWebDriver.scrollToElement(coldStorageGrossCapacity);
    testWebDriver.sleep(500);
    sendKeys(coldStorageGrossCapacity, "3434.3434");
    sendKeys(coldStorageNetCapacity, "3535.3535");
    coldStorageNetCapacity.sendKeys(Keys.TAB);


    hasElectricityTrue.click();
    isOnlineTrue.click();
    testWebDriver.handleScrollByPixels(0, 2000);
    testWebDriver.sleep(500);
    hasElectronicSccTrue.click();
    hasElectronicDarTrue.click();
    facilitySuppliesOthersYes.click();
    sendKeys(comments, "Comments");
    return date_time;
  }

  public void addProgram(String program, boolean push) {
    testWebDriver.selectByVisibleText(programsSupported, program);
    if (!push) {
      programsSupportedActiveFlag.click();
      testWebDriver.sleep(500);
      programsSupportedStartDate.click();
      startDateCalender.click();
      testWebDriver.sleep(500);
      okAlert.click();
      testWebDriver.sleep(500);
    }
    addSupportedProgram.click();
  }

  public void activeInactiveFirstProgram() {
    programsSupportedFirstActiveFlag.click();
    testWebDriver.sleep(500);
    programsSupportedStartDate.click();
    programsSupportedFirstStartDate.click();
    testWebDriver.handleScrollByPixels(0, 1000);
    startDateCalender.click();
    testWebDriver.sleep(500);
  }

  public void removeFirstProgram() {
    removeFirstProgramSupportedLink.click();
    okAlert.click();
  }

  public void verifyMessageOnFacilityScreen(String facilityName, String status) {
    testWebDriver.sleep(500);
    String message;
    testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
    if (saveSuccessMsgDiv.isDisplayed()) {
      message = testWebDriver.getText(saveSuccessMsgDiv);
    } else {
      message = testWebDriver.getText(saveErrorMsgDiv);
    }
    assertEquals(message, String.format("Facility \"%s\" %s successfully.   View Here", facilityName, status));
  }

  public boolean isSuccessMessageDisplayed() {
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    return saveSuccessMsgDiv.isDisplayed();
  }

  public void overrideIsa(String overriddenIsa, int rowNumber) {
    modifyIsaValueLink.click();
    testWebDriver.waitForElementToAppear(overrideIsaTable);
    while (!StringUtils.isEmpty(overrideIsaTable.getAttribute("value")))
      overrideIsaTable.sendKeys("\u0008"); // "\u0008" - is backspace char
    sendKeys(testWebDriver.getElementById("override-isa" + (rowNumber - 1)), overriddenIsa);
  }

  public void editPopulation(String population) {
    testWebDriver.waitForElementToAppear(catchmentPopulation);
    sendKeys(catchmentPopulation, population);
  }

  public String getCalculatedIsa() {
    testWebDriver.waitForElementToAppear(calculatedIsaTextField);
    return calculatedIsaTextField.getText();
  }

  public void clickIsaDoneButton() {
    testWebDriver.waitForElementToAppear(doneIsaButton);
    doneIsaButton.click();
  }

  public void clickIsaCancelButton() {
    testWebDriver.waitForElementToAppear(cancelIsaButton);
    cancelIsaButton.click();
  }

  public void clickUseCalculatedIsaButton() {
    testWebDriver.waitForElementToAppear(overrideISAValuesButton);
    overrideISAValuesButton.click();
  }

  public void verifyOverriddenIsa(String expectedIsa) {
    testWebDriver.handleScrollByPixels(0, 1000);
    testWebDriver.waitForElementToAppear(modifyIsaValueLink);
    modifyIsaValueLink.click();
    testWebDriver.waitForElementToAppear(overrideIsaTable);

    assertEquals(overrideIsaTextField.getAttribute("value"), expectedIsa);
    clickIsaDoneButton();
    testWebDriver.sleep(1000);
  }

  public void saveFacility() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void searchFacility(String facilityCodeValue) {
    testWebDriver.waitForElementToAppear(searchFacilityTextField);
    sendKeys(searchFacilityTextField, facilityCodeValue);
    searchIcon.click();
    testWebDriver.waitForAjax();
  }

  public void clickFirstFacilityList() {
    WebElement facilityName = testWebDriver.getElementById("name0");
    testWebDriver.waitForElementToAppear(facilityName);
    facilityName.click();
  }

  public void disableFacility(String facilityCodeValue, String facilityNameValue) {
    String expectedMessageOnAlert = String.format("\"%s\" / \"%s\" will be disabled in the system.", facilityNameValue, facilityCodeValue);
    testWebDriver.waitForElementToAppear(editFacilityHeader);
    clickDisableButtonOnFacilityScreen();
    assertEquals(getDisableAlertMessage(), expectedMessageOnAlert);
    clickOkButtonOnAlert();
  }

  private void clickDisableButtonOnFacilityScreen() {
    testWebDriver.waitForElementToAppear(disableButton);
    disableButton.click();
  }

  private void clickOkButtonOnAlert() {
    testWebDriver.sleep(1000);
    okButton.click();
  }

  private String getDisableAlertMessage() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(dialogMessageOnAlert);
    return dialogMessageOnAlert.getText();
  }

  public void verifyDisabledFacility(String facilityCodeValue, String facilityNameValue) {
    String expectedMessageOnFacilityScreenAfterDisable = "\"" + facilityNameValue + "\" / \"" + facilityCodeValue + "\" is now disabled";
    testWebDriver.waitForElementToAppear(successMessageDiv);
    testWebDriver.sleep(1000);

    String disableMessageOnFacilityScreenValue = successMessageDiv.getText();
    assertEquals(disableMessageOnFacilityScreenValue, expectedMessageOnFacilityScreenAfterDisable);
    String enableValue = enabledFlag.getText();
    assertEquals(enableValue.trim(), "No");
    assertTrue(isActiveRadioNoOption.isSelected());
  }

  public String getEnabledFacilityText() {
    testWebDriver.sleep(1000);
    return enabledFlag.getText();
  }

  public HomePage enableFacility() {
    testWebDriver.waitForElementToAppear(enableButton);
    testWebDriver.sleep(1000);
    enableButton.click();
    testWebDriver.waitForElementToAppear(dialogMessageOnAlert);
    testWebDriver.sleep(1000);
    okButton.click();
    testWebDriver.sleep(1000);
    return PageObjectFactory.getHomePage(testWebDriver);
  }

  public HomePage editFacility(String program, String catchmentPopulationValue, String latitudeValue, String longitudeValue,
                               String altitudeValue) {
    testWebDriver.waitForElementToAppear(disableButton);
    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(facilityCode);
    sendKeys(catchmentPopulation, catchmentPopulationValue);
    sendKeys(latitude, latitudeValue);
    sendKeys(longitude, longitudeValue);
    sendKeys(altitude, altitudeValue);

    testWebDriver.selectByVisibleText(programsSupported, program);
    programsSupportedActiveFlag.click();
    testWebDriver.sleep(500);
    programsSupportedStartDate.click();
    startDateCalender.click();
    testWebDriver.sleep(500);
    okAlert.click();
    testWebDriver.sleep(500);
    addSupportedProgram.click();
    saveButton.click();
    return PageObjectFactory.getHomePage(testWebDriver);
  }

  public void verifyEditedFacility(String catchmentPopulationValue, String latitudeValue, String longitudeValue, String altitudeValue) {
    assertEquals(testWebDriver.getAttribute(catchmentPopulation, "value"), catchmentPopulationValue);
    assertEquals(testWebDriver.getAttribute(latitude, "value"), latitudeValue);
    assertEquals(testWebDriver.getAttribute(longitude, "value"), longitudeValue);
    assertEquals(testWebDriver.getAttribute(altitude, "value"), altitudeValue);
    assertTrue(removeSupportedProgram.isDisplayed());
  }

  public HomePage verifyProgramSupported(List<String> programsSupported) {
    testWebDriver.waitForElementToAppear(editFacilityHeader);
    testWebDriver.sleep(1500);
    String program;
    for (int i = 0; i < programsSupported.size(); i++) {
      program = programsSupported.get(i);
      WebElement programsSupportedElement = testWebDriver.getElementByXpath("//table[@class='table table-striped table-bordered']/tbody/tr[" + (i + 1) + "]/td[1]");
      WebElement programsActiveElement = testWebDriver.getElementByXpath("//table[@class='table table-striped table-bordered']/tbody/tr[" + (i + 1) + "]/td[2]/input");
      assertEquals(programsSupportedElement.getText().trim(), program);
      assertTrue("Program " + (i + 1) + " should be active", programsActiveElement.isSelected());
    }
    assertTrue(removeSupportedProgram.isDisplayed());
    return PageObjectFactory.getHomePage(testWebDriver);
  }

  public void editFacilityType(String facilityTypeValue) {
    testWebDriver.waitForElementToAppear(facilityType);
    testWebDriver.selectByVisibleText(facilityType, facilityTypeValue);
  }

  public String getFacilityType() {
    testWebDriver.waitForElementToAppear(facilityType);
    return testWebDriver.getFirstSelectedOption(facilityType).getText();
  }

  public void editGeographicZone(String geographicZoneValue) {
    testWebDriver.waitForElementToAppear(geographicZone);
    testWebDriver.selectByVisibleText(geographicZone, geographicZoneValue);
  }

  public String getGeographicZone() {
    testWebDriver.waitForElementToAppear(geographicZone);
    return testWebDriver.getFirstSelectedOption(geographicZone).getText();
  }

  public String getProgramSupported(int serialNumber) {
    testWebDriver.waitForElementToAppear(programsSupported);
    return testWebDriver.getElementByXpath("//form[@id='create-facility']/div/div[3]/div/div/table/tbody/tr[" + serialNumber + "]/td[1]").getText();
  }

  public boolean getProgramSupportedActive(int serialNumber) {
    testWebDriver.waitForElementToAppear(programsSupported);
    return testWebDriver.getElementByXpath("//form[@id='create-facility']/div/div[3]/div/div/table/tbody/tr[" + serialNumber + "]/td[2]/input").isSelected();
  }

  public void overrideISA(String overriddenIsa, int productRowNumber, String facilityCode) {
    searchFacility(facilityCode);
    clickFirstFacilityList();
    overrideIsa(overriddenIsa, productRowNumber);
    clickIsaDoneButton();
    saveFacility();
  }

  public String getSearchFacilityLabel() {
    testWebDriver.waitForElementToAppear(searchFacilityLabel);
    return searchFacilityLabel.getText();
  }

  public String getNResultsMessage() {
    testWebDriver.waitForElementToAppear(nResultsMessage);
    return nResultsMessage.getText();
  }

  public String getNoResultMessage() {
    testWebDriver.waitForElementToAppear(noResultMessage);
    return noResultMessage.getText();
  }

  public void closeSearchResults() {
    testWebDriver.waitForElementToAppear(closeButton);
    closeButton.click();
  }

  public void clickSearchOptionButton() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    searchOptionButton.click();
  }

  public String getSelectedSearchOption() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    return searchOptionButton.getText();
  }

  public void selectGeographicZoneAsSearchOption() {
    testWebDriver.waitForElementToAppear(searchOption2);
    searchOption2.click();
  }

  public String getNameHeader() {
    testWebDriver.waitForElementToAppear(nameHeader);
    return nameHeader.getText();
  }

  public String getCodeHeader() {
    testWebDriver.waitForElementToAppear(codeHeader);
    return codeHeader.getText();
  }

  public String getGeographicZoneHeader() {
    testWebDriver.waitForElementToAppear(geographicZoneHeader);
    return geographicZoneHeader.getText();
  }

  public String getTypeHeader() {
    testWebDriver.waitForElementToAppear(facilityTypeHeader);
    return facilityTypeHeader.getText();
  }

  public String getEnabledHeader() {
    testWebDriver.waitForElementToAppear(enabledHeader);
    return enabledHeader.getText();
  }

  public String getActiveHeader() {
    testWebDriver.waitForElementToAppear(activeHeader);
    return activeHeader.getText();
  }

  public String getName(int rowNumber) {
    WebElement element = testWebDriver.getElementById("name" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(element);
    return element.getText();
  }

  public String getCode(int rowNumber) {
    WebElement element = testWebDriver.getElementById("code" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(element);
    return element.getText();
  }

  public String getGeographicZone(int rowNumber) {
    WebElement element = testWebDriver.getElementById("geographicZone" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(element);
    return element.getText();
  }

  public String getFacilityType(int rowNumber) {
    WebElement element = testWebDriver.getElementById("type" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(element);
    return element.getText();
  }

  public boolean getIsEnabled(int rowNumber) {
    WebElement element = testWebDriver.getElementById("enabledIconOk" + (rowNumber - 1));
    return element.isDisplayed();
  }

  public boolean getIsActive(int rowNumber) {
    WebElement element = testWebDriver.getElementById("activeIconOk" + (rowNumber - 1));
    return element.isDisplayed();
  }

  public boolean isNameHeaderPresent() {
    return nameHeader.isDisplayed();
  }
}
