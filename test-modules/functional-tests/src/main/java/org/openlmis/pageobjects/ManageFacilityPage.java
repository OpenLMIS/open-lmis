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

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.apache.commons.lang.StringUtils;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.lang.String.valueOf;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class ManageFacilityPage extends Page {


  @FindBy(how = How.ID, using = "searchFacility")
  private static WebElement searchFacilityTextField=null;

  @FindBy(how = How.XPATH, using = "//input[@class='btn btn-danger delete-button']")
  private static WebElement disableButton=null;

  @FindBy(how = How.LINK_TEXT, using = "OK")
  private static WebElement okButton=null;

  @FindBy(how = How.XPATH, using = "//div[@id='disableFacilityDialog']/div[@class='modal-body']/p")
  private static WebElement disableMessageOnAlert=null;

  @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
  private static WebElement successMessageDiv=null;

  @FindBy(how = How.ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsgDiv=null;

  @FindBy(how = How.XPATH, using = "//ng-switch/span")
  private static WebElement enabledFlag=null;

  @FindBy(how = How.XPATH, using = "//input[@name='isActive' and @value='false']")
  private static WebElement isActiveRadioNoOption=null;

  @FindBy(how = How.XPATH, using = "//input[@name='isActive' and @value='true']")
  private static WebElement isActiveRadioYesOption=null;

  @FindBy(how = How.XPATH, using = "//input[@class='btn btn-primary enable-button']")
  private static WebElement enableButton=null;

  @FindBy(how = How.XPATH, using = "//div[@id='enableConfirmModal']/div[@class='modal-body']/p")
  private static WebElement enableMessageOnAlert=null;

  @FindBy(how = How.LINK_TEXT, using = "OK")
  private static WebElement okLink=null;

  @FindBy(how = How.XPATH, using = " //div[@id='activeConfirmModel']/div[@class='modal-body']/p")
  private static WebElement isActiveMessageOnAlert=null;

  @FindBy(how = How.ID, using = "remove0")
  private static WebElement removeSupportedProgram=null;

  @FindBy(how = ID, using = "code")
  private static WebElement facilityCode=null;

  @FindBy(how = ID, using = "name")
  private static WebElement facilityName=null;

  @FindBy(how = ID, using = "description")
  private static WebElement facilityDescription=null;

  @FindBy(how = ID, using = "gln")
  private static WebElement gln=null;

  @FindBy(how = ID, using = "main-phone")
  private static WebElement phoneNumber=null;

  @FindBy(how = ID, using = "fax-phone")
  private static WebElement faxNumber=null;

  @FindBy(how = ID, using = "address-1")
  private static WebElement address1=null;

  @FindBy(how = ID, using = "address-2")
  private static WebElement address2=null;

  @FindBy(how = ID, using = "geographic-zone")
  private static WebElement geographicZone=null;

  @FindBy(how = ID, using = "facility-type")
  private static WebElement facilityType=null;

  @FindBy(how = ID, using = "catchment-population")
  private static WebElement catchmentPopulation=null;

  @FindBy(how = ID, using = "latitude")
  private static WebElement latitude=null;

  @FindBy(how = ID, using = "longitude")
  private static WebElement longitude=null;

  @FindBy(how = ID, using = "altitude")
  private static WebElement altitude=null;

  @FindBy(how = ID, using = "operated-by")
  private static WebElement operatedBy=null;

  @FindBy(how = ID, using = "cold-storage-gross-capacity")
  private static WebElement coldStorageGrossCapacity=null;

  @FindBy(how = ID, using = "cold-storage-net-capacity")
  private static WebElement coldStorageNetCapacity=null;

  @FindBy(how = XPATH, using = "//input[@name='supplies-others' and @value='true']")
  private static WebElement facilitySuppliesOthers=null;

  @FindBy(how = XPATH, using = "//input[@name='isSdp' and @value='true']")
  private static WebElement serviceDeliveryPoint=null;

  @FindBy(how = XPATH, using = "//input[@name='has-electricity' and @value='true']")
  private static WebElement hasElectricity=null;

  @FindBy(how = XPATH, using = "//input[@name='is-online' and @value='true']")
  private static WebElement isOnline=null;

  @FindBy(how = XPATH, using = "//input[@name='has-electronic-scc' and @value='true']")
  private static WebElement hasElectronicScc=null;

  @FindBy(how = XPATH, using = "//input[@name='has-electronic-dar' and @value='true']")
  private static WebElement hasElectronicDar=null;

  @FindBy(how = XPATH, using = "//input[@name='isActive' and @value='true']")
  private static WebElement isActive=null;

  @FindBy(how = ID, using = "go-live-date")
  private static WebElement goLiveDate=null;

  @FindBy(how = ID, using = "go-down-date")
  private static WebElement goDownDate=null;


  @FindBy(how = ID, using = "comments")
  private static WebElement comments=null;

  @FindBy(how = ID, using = "programs-supported")
  private static WebElement programsSupported=null;

  @FindBy(how = ID, using = "supported-program-active")
  private static WebElement programsSupportedActiveFlag=null;

  @FindBy(how = XPATH, using = "//form[@id='create-facility']/div/div[3]/div/div/table/tbody/tr[1][@class='ng-scope']/td[2]/input")
  private static WebElement programsSupportedFirstActiveFlag=null;

  @FindBy(how = XPATH, using = "//form/div/div[3]/div/div/table/tbody/tr[1]/td[3]/input")
  private static WebElement programsSupportedFirstStartDate=null;

  @FindBy(how = ID, using = "supported-program-start-date")
  private static WebElement programsSupportedStartDate=null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'25')]")
  private static WebElement startDateCalender=null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okAlert=null;

  @FindBy(how = ID, using = "supported-program-add")
  private static WebElement addSupportedProgram=null;

  @FindBy(how = ID, using = "save-button")
  public static WebElement SaveButton=null;

  @FindBy(how = XPATH, using = "//div[@id='saveSuccessMsgDiv']/span")
  private static WebElement saveSuccessMsgDiv=null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'25')]")
  private static WebElement goLiveDateCalender=null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'26')]")
  private static WebElement goDownDateCalender=null;

  @FindBy(how = XPATH, using = "//div[@class='ng-scope']/div[@ng-hide='facility.id']/h2")
  private static WebElement facilityHeader=null;

  @FindBy(how = ID, using = "edit-facility-header")
  private static WebElement editFacilityHeader=null;

  @FindBy(how = XPATH, using = "(//a[contains(text(),'Modify ISA Values')])[1]")
  private static WebElement modifyIsaValueLink=null;

  @FindBy(how = ID, using = "overrideIsaTable")
  private static WebElement overrideIsaTable=null;

  @FindBy(how = ID, using = "override-isa0")
  private static WebElement overrideIsaTextField=null;

  @FindBy(how = ID, using = "calculated-isa0")
  private static WebElement calculatedIsaTextField=null;

  @FindBy(how = ID, using = "use-calculated-button0")
  private static WebElement useCalculatedIsabutton=null;

  @FindBy(how = XPATH, using = "//input[@value='Done']")
  private static WebElement doneIsaButton=null;

  @FindBy(how = XPATH, using = "//input[@value='Cancel']")
  private static WebElement cancelIsaButton=null;

  @FindBy(how = XPATH, using = "//a[@id='remove0']")
  private static WebElement removeFirstProgramSupportedLink=null;

  public void verifyNewFacilityHeader(String headerToBeVerified) {
    testWebDriver.waitForElementToAppear(facilityHeader);
    assertEquals(facilityHeader.getText().trim(), headerToBeVerified);
  }

  public void verifyEditFacilityHeader(String headerToBeVerified) {
    testWebDriver.waitForElementToAppear(editFacilityHeader);
    assertEquals(editFacilityHeader.getText().trim(), headerToBeVerified);
  }

    public String enterValuesInFacilityAndClickSave(String facilityCodePrefix, String facilityNamePrefix,
                                                    String program, String geoZone, String facilityTypeValue, String operatedByValue, String population) {
        String date_time = enterValuesInFacility(facilityCodePrefix, facilityNamePrefix, program, geoZone, facilityTypeValue, operatedByValue, population, false);

        SaveButton.click();

        return date_time;
    }

    public String enterValuesInFacility(String facilityCodePrefix, String facilityNamePrefix, String program,
                                        String geoZone, String facilityTypeValue, String operatedByValue,
                                        String population, boolean push) {
        Date dObj = new Date();
        SimpleDateFormat formatter_date_time = new SimpleDateFormat(
                "yyyyMMdd-hhmmss");
        String date_time = formatter_date_time.format(dObj);

        String facilityCodeText = facilityCodePrefix + date_time;
        String facilityNameText = facilityNamePrefix + date_time;
        verifyNewFacilityHeader("Add new facility");
        testWebDriver.waitForElementToAppear(facilityCode);
        facilityCode.clear();
        facilityCode.sendKeys(facilityCodeText);
        facilityName.sendKeys(facilityNameText);
        testWebDriver.selectByVisibleText(operatedBy, operatedByValue);

        testWebDriver.clickForRadio(serviceDeliveryPoint);
        testWebDriver.clickForRadio(isActive);

        facilityDescription.sendKeys("Testing description");
        gln.sendKeys("Testing Gln");
        phoneNumber.sendKeys("9711231305");
        faxNumber.sendKeys("9711231305");
        address1.sendKeys("Address1");
        address2.sendKeys("Address2");

        testWebDriver.selectByVisibleText(geographicZone, geoZone);
        testWebDriver.selectByVisibleText(facilityType, facilityTypeValue);

        testWebDriver.sleep(500);
        goLiveDate.click();
        testWebDriver.sleep(500);
        goLiveDateCalender.click();
        testWebDriver.sleep(500);
        goDownDate.click();
        testWebDriver.sleep(500);
        goDownDateCalender.click();

        testWebDriver.handleScrollByPixels(0, 1000);
        addProgram(program, push);

        catchmentPopulation.sendKeys(population);
        latitude.sendKeys("-555.5555");
        longitude.sendKeys("444.4444");
        altitude.sendKeys("4545.4545");

        coldStorageGrossCapacity.sendKeys("3434.3434");
        coldStorageNetCapacity.sendKeys("3535.3535");
        coldStorageNetCapacity.sendKeys(Keys.TAB);

        hasElectricity.click();
        isOnline.click();
        testWebDriver.handleScrollByPixels(0, 2000);

        hasElectronicScc.click();
        hasElectronicDar.click();
        facilitySuppliesOthers.click();
        comments.sendKeys("Comments");
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
        String message;
        testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
        if (saveSuccessMsgDiv.isDisplayed()) {
            message = testWebDriver.getText(saveSuccessMsgDiv);
        } else {
            message = testWebDriver.getText(saveErrorMsgDiv);
        }
        assertEquals(message, String.format("Facility \"%s\" %s successfully", facilityName, status));
        testWebDriver.sleep(500);
    }

    public void verifySuccessMessage() {
        testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
        assertTrue("Save success message should show up", saveSuccessMsgDiv.isDisplayed());
    }


    public void overrideIsa(int overriddenIsa) {
        modifyIsaValueLink.click();
        testWebDriver.waitForElementToAppear(overrideIsaTable);
        while (!StringUtils.isEmpty(overrideIsaTable.getAttribute("value")))
            overrideIsaTable.sendKeys("\u0008"); // "\u0008" - is backspace char
        overrideIsaTextField.sendKeys(valueOf(overriddenIsa));
    }

    public void editPopulation(String population) {
        testWebDriver.waitForElementToAppear(catchmentPopulation);
        while (!StringUtils.isEmpty(catchmentPopulation.getAttribute("value")))
            catchmentPopulation.sendKeys("\u0008"); // "\u0008" - is backspace char
        catchmentPopulation.sendKeys(valueOf(population));
    }

    public void verifyCalculatedIsa(int calculatedIsa) {
        assertEquals(calculatedIsaTextField.getText(), valueOf(calculatedIsa));
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
        testWebDriver.waitForElementToAppear(useCalculatedIsabutton);
        useCalculatedIsabutton.click();
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

  public ManageFacilityPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  public void saveFacility(){
    testWebDriver.waitForElementToAppear(SaveButton);
    SaveButton.click();
  }


  public void searchFacility(String facilityCodeValue) {
    testWebDriver.waitForElementToAppear(searchFacilityTextField);
    sendKeys(searchFacilityTextField, facilityCodeValue);
    //testWebDriver.sleep(2000);
  }

  public void clickFacilityList(String facility){
    //testWebDriver.sleep(2000);
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//a[contains(text(),'"+facility+"')]"));
    testWebDriver.getElementByXpath("//a[contains(text(),'"+facility+"')]").click();
    testWebDriver.waitForElementToAppear(facilityCode);
  }


  public void disableFacility(String facilityCodeValue, String facilityNameValue) {

    String expectedMessageOnAlert = String.format("\"%s\" / \"%s\" will be disabled in the system.", facilityNameValue, facilityCodeValue);
    verifyEditFacilityHeader("Edit facility");
    clickDisableButtonOnFacilityScreen();
    verifyDisableAlert(expectedMessageOnAlert);
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

  private void verifyDisableAlert(String expectedMessageOnAlert) {
    testWebDriver.waitForElementToAppear(disableMessageOnAlert);

    String disableMessageOnAlertValue = disableMessageOnAlert.getText();
    SeleneseTestNgHelper.assertEquals(disableMessageOnAlertValue, expectedMessageOnAlert);
  }

  public void verifyDisabledFacility(String facilityCodeValue, String facilityNameValue) {
    String expectedMessageOnFacilityScreenAfterDisable = "\"" + facilityNameValue + "\" / \"" + facilityCodeValue + "\" is now disabled";

    testWebDriver.waitForElementToAppear(successMessageDiv);

    testWebDriver.sleep(1000);
    String disableMessageOnFacilityScreenValue = successMessageDiv.getText();
    SeleneseTestNgHelper.assertEquals(disableMessageOnFacilityScreenValue, expectedMessageOnFacilityScreenAfterDisable);

    String enableValue = enabledFlag.getText();
    SeleneseTestNgHelper.assertEquals(enableValue.trim(), "No");

    SeleneseTestNgHelper.assertTrue(isActiveRadioNoOption.isSelected());
  }

  public void verifyEnabledFacility() {

    testWebDriver.sleep(1000);
    String enableValue = enabledFlag.getText();
    SeleneseTestNgHelper.assertEquals(enableValue.trim(), "Yes");
    SeleneseTestNgHelper.assertTrue(isActiveRadioYesOption.isSelected());
    verifyEditFacilityHeader("Edit facility");
  }

  public HomePage enableFacility() throws IOException {
    String expectedIsActiveMessageOnAlert = "Do you want to set facility as active?";

    testWebDriver.waitForElementToAppear(enableButton);
    testWebDriver.sleep(1000);
    enableButton.click();
    testWebDriver.waitForElementToAppear(enableMessageOnAlert);


    testWebDriver.sleep(1000);
    okLink.click();
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(isActiveMessageOnAlert);
    String isActiveMessageOnAlertValue = isActiveMessageOnAlert.getText();
    SeleneseTestNgHelper.assertEquals(isActiveMessageOnAlertValue, expectedIsActiveMessageOnAlert);
    testWebDriver.waitForElementToAppear(okLink);
    testWebDriver.sleep(1000);
    okLink.click();

    verifyEnabledFacility();

    return new HomePage(testWebDriver);
  }

  public HomePage editAndVerifyFacility(String program, String facilityNameValue) throws IOException {
    String catchmentPopulationValue = "600000";
    String latitudeValue = "955.5555";
    String longitudeValue = "644.4444";
    String altitudeValue = "6545.4545";

    verifyEditFacilityHeader("Edit facility") ;

    testWebDriver.waitForElementToAppear(disableButton);
    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(facilityCode);
    catchmentPopulation.clear();
    catchmentPopulation.sendKeys(catchmentPopulationValue);
    latitude.clear();
    latitude.sendKeys(latitudeValue);
    longitude.clear();
    longitude.sendKeys(longitudeValue);
    altitude.clear();
    altitude.sendKeys(altitudeValue);

    testWebDriver.selectByVisibleText(programsSupported, program);
    programsSupportedActiveFlag.click();
    testWebDriver.sleep(500);
    programsSupportedStartDate.click();
    startDateCalender.click();
    testWebDriver.sleep(500);
    okAlert.click();
    testWebDriver.sleep(500);
    addSupportedProgram.click();

    verifyEditedFacility(catchmentPopulationValue, latitudeValue, longitudeValue, altitudeValue);

    SaveButton.click();
    verifyMessageOnFacilityScreen(facilityNameValue, "updated");

    return new HomePage(testWebDriver);
  }

  private void verifyEditedFacility(String catchmentPopulationValue, String latitudeValue, String longitudeValue, String altitudeValue) {
    SeleneseTestNgHelper.assertEquals(testWebDriver.getAttribute(catchmentPopulation, "value"), catchmentPopulationValue);
    SeleneseTestNgHelper.assertEquals(testWebDriver.getAttribute(latitude, "value"), latitudeValue);
    SeleneseTestNgHelper.assertEquals(testWebDriver.getAttribute(longitude, "value"), longitudeValue);
    SeleneseTestNgHelper.assertEquals(testWebDriver.getAttribute(altitude, "value"), altitudeValue);

    SeleneseTestNgHelper.assertTrue(removeSupportedProgram.isDisplayed());
  }

  public HomePage verifyProgramSupported(java.util.ArrayList<String> programsSupported, String date_time) throws IOException {
    int i = 1;
    clickFacilityList(date_time);
    verifyEditFacilityHeader("Edit facility") ;
    testWebDriver.waitForElementToAppear(disableButton);
    testWebDriver.sleep(1500);
    for (String program : programsSupported) {
      WebElement programsSupportedElement = testWebDriver.getElementByXpath("//table[@class='table table-striped table-bordered']/tbody/tr[" + i + "]/td[1]");
      WebElement programsActiveElement = testWebDriver.getElementByXpath("//table[@class='table table-striped table-bordered']/tbody/tr[" + i + "]/td[2]/input");
      SeleneseTestNgHelper.assertEquals(programsSupportedElement.getText().trim(), program);
      SeleneseTestNgHelper.assertTrue("Program " + i + " should be active", programsActiveElement.isSelected());

      i++;
    }
    SeleneseTestNgHelper.assertTrue(removeSupportedProgram.isDisplayed());

    return new HomePage(testWebDriver);
  }

  public void editFacilityType(String facilityTypeValue){
    testWebDriver.waitForElementToAppear(facilityType);
    testWebDriver.selectByVisibleText(facilityType, facilityTypeValue);
  }

  public String getFacilityType(){
    testWebDriver.waitForElementToAppear(facilityType);
    return testWebDriver.getFirstSelectedOption(facilityType).getText();
  }

  public void editGeographicZone(String geographicZoneValue){
    testWebDriver.waitForElementToAppear(geographicZone);
    testWebDriver.selectByVisibleText(geographicZone, geographicZoneValue);
  }

  public String getGeographicZone(){
    testWebDriver.waitForElementToAppear(geographicZone);
    return testWebDriver.getFirstSelectedOption(geographicZone).getText();
  }

  public String getProgramSupported(int serialNumber){
    testWebDriver.waitForElementToAppear(programsSupported);
    return testWebDriver.getElementByXpath("//form[@id='create-facility']/div/div[3]/div/div/table/tbody/tr[" + serialNumber +"]/td[1]").getText();
  }

  public boolean getProgramSupportedActive(int serialNumber){
      testWebDriver.waitForElementToAppear(programsSupported);
      return testWebDriver.getElementByXpath("//form[@id='create-facility']/div/div[3]/div/div/table/tbody/tr[" + serialNumber +"]/td[2]/input").isSelected();
  }
}
