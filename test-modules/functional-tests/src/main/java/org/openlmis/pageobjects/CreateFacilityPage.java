/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import org.apache.commons.lang.StringUtils;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
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


public class CreateFacilityPage extends Page {

  @FindBy(how = ID, using = "code")
  private static WebElement facilityCode;

  @FindBy(how = ID, using = "name")
  private static WebElement facilityName;

  @FindBy(how = ID, using = "description")
  private static WebElement facilityDescription;

  @FindBy(how = ID, using = "gln")
  private static WebElement gln;

  @FindBy(how = ID, using = "main-phone")
  private static WebElement phoneNumber;

  @FindBy(how = ID, using = "fax-phone")
  private static WebElement faxNumber;

  @FindBy(how = ID, using = "address-1")
  private static WebElement address1;

  @FindBy(how = ID, using = "address-2")
  private static WebElement address2;

  @FindBy(how = ID, using = "geographic-zone")
  private static WebElement geographicZone;

  @FindBy(how = ID, using = "facility-type")
  private static WebElement facilityType;

  @FindBy(how = ID, using = "catchment-population")
  private static WebElement catchmentPopulation;

  @FindBy(how = ID, using = "latitude")
  private static WebElement latitude;

  @FindBy(how = ID, using = "longitude")
  private static WebElement longitude;

  @FindBy(how = ID, using = "altitude")
  private static WebElement altitude;

  @FindBy(how = ID, using = "operated-by")
  private static WebElement operatedBy;

  @FindBy(how = ID, using = "cold-storage-gross-capacity")
  private static WebElement coldStorageGrossCapacity;

  @FindBy(how = ID, using = "cold-storage-net-capacity")
  private static WebElement coldStorageNetCapacity;

  @FindBy(how = XPATH, using = "//input[@name='supplies-others' and @value='true']")
  private static WebElement facilitySuppliesOthers;

  @FindBy(how = XPATH, using = "//input[@name='isSdp' and @value='true']")
  private static WebElement serviceDeliveryPoint;

  @FindBy(how = XPATH, using = "//input[@name='has-electricity' and @value='true']")
  private static WebElement hasElectricity;

  @FindBy(how = XPATH, using = "//input[@name='is-online' and @value='true']")
  private static WebElement isOnline;

  @FindBy(how = XPATH, using = "//input[@name='has-electronic-scc' and @value='true']")
  private static WebElement hasElectronicScc;

  @FindBy(how = XPATH, using = "//input[@name='has-electronic-dar' and @value='true']")
  private static WebElement hasElectronicDar;

  @FindBy(how = XPATH, using = "//input[@name='isActive' and @value='true']")
  private static WebElement isActive;

  @FindBy(how = ID, using = "go-live-date")
  private static WebElement goLiveDate;

  @FindBy(how = ID, using = "go-down-date")
  private static WebElement goDownDate;


  @FindBy(how = ID, using = "comments")
  private static WebElement comments;

  @FindBy(how = ID, using = "programs-supported")
  private static WebElement programsSupported;

  @FindBy(how = ID, using = "supported-program-active")
  private static WebElement programsSupportedActiveFlag;

  @FindBy(how = ID, using = "supported-program-start-date")
  private static WebElement programsSupportedStartDate;

  @FindBy(how = XPATH, using = "//a[contains(text(),'25')]")
  private static WebElement startDateCalender;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement startDateAlert;

  @FindBy(how = ID, using = "supported-program-add")
  private static WebElement addSupportedProgram;

  @FindBy(how = ID, using = "save-button")
  public static WebElement SaveButton;

  @FindBy(how = XPATH, using = "//div[@id='saveSuccessMsgDiv']/span")
  private static WebElement saveSuccessMsgDiv;


  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsgDiv;

  @FindBy(how = XPATH, using = "//a[contains(text(),'25')]")
  private static WebElement goLiveDateCalender;

  @FindBy(how = XPATH, using = "//a[contains(text(),'26')]")
  private static WebElement goDownDateCalender;

  @FindBy(how = XPATH, using = "//div[@class='ng-scope']/div[@ng-hide='facility.id']/h2")
  private static WebElement facilityHeader;

  @FindBy(how = XPATH, using = "//div[contains(@id,'MsgDiv')]")
  private static WebElement errorOrSuccessMessage;

  @FindBy(how = XPATH, using = "(//a[contains(text(),'Modify ISA Values')])[1]")
  private static WebElement modifyIsaValueLink;

  @FindBy(how = ID, using = "override-isa-table")
  private static WebElement overrideIsaTable;

  @FindBy(how = ID, using = "override-isa0")
  private static WebElement overrideIsaTextField;

  @FindBy(how = ID, using = "calculated-isa0")
  private static WebElement calculatedIsaTextField;

  @FindBy(how = ID, using = "use-calculated-button0")
  private static WebElement useCalculatedIsabutton;

  @FindBy(how = XPATH, using = "//input[@value='Done']")
  private static WebElement doneIsaButton;

  @FindBy(how = XPATH, using = "//input[@value='Cancel']")
  private static WebElement cancelIsaButton;

  public CreateFacilityPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }

  private void verifyHeader(String headingToVerify) {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(facilityHeader);
    assertEquals(facilityHeader.getText().trim(), headingToVerify);
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
    String message = null;
    Date dObj = new Date();
    SimpleDateFormat formatter_date_time = new SimpleDateFormat(
      "yyyyMMdd-hhmmss");
    String date_time = formatter_date_time.format(dObj);

    String facilityCodeText = facilityCodePrefix + date_time;
    String facilityNameText = facilityNamePrefix + date_time;
    verifyHeader("Add new facility");
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

  private void addProgram(String program, boolean push) {
    testWebDriver.selectByVisibleText(programsSupported, program);
    if (!push) {
      programsSupportedActiveFlag.click();
      testWebDriver.sleep(500);
      programsSupportedStartDate.click();
      startDateCalender.click();
      testWebDriver.sleep(500);
      startDateAlert.click();
      testWebDriver.sleep(500);
    }
    addSupportedProgram.click();
  }

  public void verifyMessageOnFacilityScreen(String facilityName, String status) {
    String message = null;
    testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
    if (saveSuccessMsgDiv.isDisplayed()) {
      message = testWebDriver.getText(saveSuccessMsgDiv);
    } else {
      message = testWebDriver.getText(saveErrorMsgDiv);
    }
    assertEquals(message, "Facility '" + facilityName + "' " + status + " successfully");
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
    testWebDriver.handleScrollByPixels(0,1000);
    testWebDriver.waitForElementToAppear(modifyIsaValueLink);
    modifyIsaValueLink.click();
    testWebDriver.waitForElementToAppear(overrideIsaTable);

    assertEquals(overrideIsaTextField.getAttribute("value"), expectedIsa);
    clickIsaDoneButton();
    testWebDriver.sleep(1000);
  }
}