/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
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


public class CreateFacilityPage extends Page {

  @FindBy(how = How.ID, using = "code")
  private static WebElement facilityCode;

  @FindBy(how = How.ID, using = "name")
  private static WebElement facilityName;

  @FindBy(how = How.ID, using = "description")
  private static WebElement facilityDescription;

  @FindBy(how = How.ID, using = "gln")
  private static WebElement gln;

  @FindBy(how = How.ID, using = "main-phone")
  private static WebElement phoneNumber;

  @FindBy(how = How.ID, using = "fax-phone")
  private static WebElement faxNumber;

  @FindBy(how = How.ID, using = "address-1")
  private static WebElement address1;

  @FindBy(how = How.ID, using = "address-2")
  private static WebElement address2;

  @FindBy(how = How.ID, using = "geographic-zone")
  private static WebElement geographicZone;

  @FindBy(how = How.ID, using = "facility-type")
  private static WebElement facilityType;

  @FindBy(how = How.ID, using = "catchment-population")
  private static WebElement catchmentPopulation;

  @FindBy(how = How.ID, using = "latitude")
  private static WebElement latitude;

  @FindBy(how = How.ID, using = "longitude")
  private static WebElement longitude;

  @FindBy(how = How.ID, using = "altitude")
  private static WebElement altitude;

  @FindBy(how = How.ID, using = "operated-by")
  private static WebElement operatedBy;

  @FindBy(how = How.ID, using = "cold-storage-gross-capacity")
  private static WebElement coldStorageGrossCapacity;

  @FindBy(how = How.ID, using = "cold-storage-net-capacity")
  private static WebElement coldStorageNetCapacity;

  @FindBy(how = How.XPATH, using = "//input[@name='supplies-others' and @value='true']")
  private static WebElement facilitySuppliesOthers;

  @FindBy(how = How.XPATH, using = "//input[@name='isSdp' and @value='true']")
  private static WebElement serviceDeliveryPoint;

  @FindBy(how = How.XPATH, using = "//input[@name='has-electricity' and @value='true']")
  private static WebElement hasElectricity;

  @FindBy(how = How.XPATH, using = "//input[@name='is-online' and @value='true']")
  private static WebElement isOnline;

  @FindBy(how = How.XPATH, using = "//input[@name='has-electronic-scc' and @value='true']")
  private static WebElement hasElectronicScc;

  @FindBy(how = How.XPATH, using = "//input[@name='has-electronic-dar' and @value='true']")
  private static WebElement hasElectronicDar;

  @FindBy(how = How.XPATH, using = "//input[@name='isActive' and @value='true']")
  private static WebElement isActive;

  @FindBy(how = How.ID, using = "go-live-date")
  private static WebElement goLiveDate;

  @FindBy(how = How.ID, using = "go-down-date")
  private static WebElement goDownDate;


  @FindBy(how = How.ID, using = "comments")
  private static WebElement comments;

  @FindBy(how = How.ID, using = "programs-supported")
  private static WebElement programsSupported;

  @FindBy(how = How.ID, using = "supported-program-active")
  private static WebElement programsSupportedActiveFlag;

  @FindBy(how = How.ID, using = "supported-program-start-date")
  private static WebElement programsSupportedStartDate;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'25')]")
  private static WebElement startDateCalender;

  @FindBy(how = How.XPATH, using = "//a[@ng-click='setNewStartDate()']")
  private static WebElement startDateAlert;

  @FindBy(how = How.ID, using = "supported-program-add")
  private static WebElement addSupportedProgram;

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement SaveButton;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']/span")
  private static WebElement saveSuccessMsgDiv;


  @FindBy(how = How.ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsgDiv;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'25')]")
  private static WebElement goLiveDateCalender;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'26')]")
  private static WebElement goDownDateCalender;

  @FindBy(how = How.XPATH, using = "//div[@class='ng-scope']/div[@ng-hide='facility.id']/h2")
  private static WebElement facilityHeader;

  @FindBy(how = How.XPATH, using = "//div[contains(@id,'MsgDiv')]")
  private static WebElement errorOrSuccessMessage;


  public CreateFacilityPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);

  }


  public String enterAndVerifyFacility(String geoZone, String facilityTypeValue, String operatedByValue) {
    String message = null;
    Date dObj = new Date();
    SimpleDateFormat formatter_date_time = new SimpleDateFormat(
      "yyyyMMdd-hhmmss");
    String date_time = formatter_date_time.format(dObj);

    String facilityCodeText = "FCcode" + date_time;
    String facilityNameText = "FCname" + date_time;

    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(facilityHeader);
    SeleneseTestNgHelper.assertEquals(facilityHeader.getText().trim(), "Add new facility");

    testWebDriver.waitForElementToAppear(facilityCode);
    facilityCode.clear();
    facilityCode.sendKeys(facilityCodeText);
    facilityName.sendKeys(facilityNameText);
    testWebDriver.selectByVisibleText(operatedBy, operatedByValue);


    serviceDeliveryPoint.click();
    isActive.click();

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

    testWebDriver.handleScrollByPixels(0,1000);
    testWebDriver.selectByVisibleText(programsSupported, "HIV");
    programsSupportedActiveFlag.click();
    testWebDriver.sleep(500);
    programsSupportedStartDate.click();
    startDateCalender.click();
    testWebDriver.sleep(500);
    startDateAlert.click();
    testWebDriver.sleep(500);
    addSupportedProgram.click();

    catchmentPopulation.sendKeys("500000");
    latitude.sendKeys("-555.5555");
    longitude.sendKeys("444.4444");
    altitude.sendKeys("4545.4545");

    coldStorageGrossCapacity.sendKeys("3434.3434");
    coldStorageNetCapacity.sendKeys("3535.3535");
    coldStorageNetCapacity.sendKeys(Keys.TAB);

    hasElectricity.click();
    isOnline.click();
    testWebDriver.handleScrollByPixels(0,2000);

    hasElectronicScc.click();
    hasElectronicDar.click();
    facilitySuppliesOthers.click();
    comments.sendKeys("Comments");

    SaveButton.click();
    testWebDriver.waitForElementsToAppear(saveSuccessMsgDiv, saveErrorMsgDiv);
    if (saveSuccessMsgDiv.isDisplayed()) {
      message = testWebDriver.getText(saveSuccessMsgDiv);
    } else {
      message = testWebDriver.getText(saveErrorMsgDiv);
    }
    SeleneseTestNgHelper.assertEquals(message, "Facility '" + facilityNameText + "' created successfully");
    testWebDriver.sleep(500);

    return date_time;
  }


}