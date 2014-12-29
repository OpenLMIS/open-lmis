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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.ID;

public class VisitInformationPage extends DistributionTab {

  public static final String VERIFIED_BY_NAME = "verifiedByName";
  public static final String VERIFIED_BY_TITLE = "verifiedByTitle";
  public static final String CONFIRMED_BY_NAME = "confirmedByName";
  public static final String CONFIRMED_BY_TITLE = "confirmedByTitle";
  public static final String OBSERVATIONS = "observations";
  public static final String VALUE = "value";
  public static final String VEHICLE_ID = "vehicleId";

  @FindBy(how = ID, using = "facilityVisitTab")
  private static WebElement facilityVisitTab = null;

  @FindBy(how = ID, using = "facilityVisitTabIcon")
  public static WebElement facilityVisitTabIcon = null;

  @FindBy(how = ID, using = "visitInformationLabel")
  private static WebElement visitInformationLabel = null;

  @FindBy(how = ID, using = "facilityVisitTabLabel")
  public static WebElement facilityVisitTabLabel = null;

  @FindBy(how = ID, using = OBSERVATIONS)
  public static WebElement observationsField = null;

  @FindBy(how = ID, using = VERIFIED_BY_NAME)
  public static WebElement verifiedByNameField = null;

  @FindBy(how = ID, using = VERIFIED_BY_TITLE)
  public static WebElement verifiedByTitleField = null;

  @FindBy(how = ID, using = CONFIRMED_BY_NAME)
  public static WebElement confirmedByNameField = null;

  @FindBy(how = ID, using = CONFIRMED_BY_TITLE)
  public static WebElement confirmedByTitleField = null;

  @FindBy(how = ID, using = "wasFacilityVisited")
  public static WebElement wasFacilityVisitedLabel = null;

  @FindBy(how = ID, using = "facilityVisitedYes")
  public static WebElement facilityVisitedYesRadioButton = null;

  @FindBy(how = ID, using = "facilityVisitedNo")
  public static WebElement facilityVisitedNoRadioButton = null;

  @FindBy(how = ID, using = "visitDate")
  public static WebElement visitDateField = null;

  @FindBy(how = ID, using = "vehicleId")
  public static WebElement vehicleIdField = null;

  @FindBy(how = How.XPATH, using = "//a[contains(@class,'ui-state-highlight')]")
  private static WebElement calender = null;

  @FindBy(how = ID, using = "badWeather")
  public static WebElement badWeather = null;

  @FindBy(how = ID, using = "noTransport")
  public static WebElement noTransport = null;

  @FindBy(how = ID, using = "facilityClosed")
  public static WebElement facilityClosed = null;

  @FindBy(how = ID, using = "unavailableFunds")
  public static WebElement unavailableFunds = null;

  @FindBy(how = ID, using = "notPartOfProgram")
  public static WebElement notPartOfProgram = null;

  @FindBy(how = ID, using = "other")
  public static WebElement other = null;

  @FindBy(how = ID, using = "otherTextBox")
  public static WebElement otherTextBox = null;

  public Map<String, WebElement> fieldMap = new HashMap<String, WebElement>() {{
    put(VEHICLE_ID, vehicleIdField);
    put(OBSERVATIONS, observationsField);
    put(CONFIRMED_BY_NAME, confirmedByNameField);
    put(CONFIRMED_BY_TITLE, confirmedByTitleField);
    put(VERIFIED_BY_TITLE, verifiedByTitleField);
    put(VERIFIED_BY_NAME, verifiedByNameField);
  }};

  public VisitInformationPage(TestWebDriver driver) {
    super(driver);
  }

  @Override
  public void verifyIndicator(String color) {
    removeFocusFromElement();
    verifyOverallIndicator(facilityVisitTabIcon, color);
  }

  @Override
  public void enterValues(List<Map<String, String>> data) {
    Map<String, String> map = data.get(0);
    sendKeys(fieldMap.get(OBSERVATIONS), map.get(OBSERVATIONS));
    fieldMap.get(OBSERVATIONS).sendKeys(Keys.TAB);
    sendKeys(fieldMap.get(VERIFIED_BY_NAME), map.get(VERIFIED_BY_NAME));
    fieldMap.get(VERIFIED_BY_NAME).sendKeys(Keys.TAB);
    sendKeys(fieldMap.get(VERIFIED_BY_TITLE), map.get(VERIFIED_BY_TITLE));
    fieldMap.get(VERIFIED_BY_TITLE).sendKeys(Keys.TAB);
    sendKeys(fieldMap.get(CONFIRMED_BY_NAME), map.get(CONFIRMED_BY_NAME));
    fieldMap.get(CONFIRMED_BY_NAME).sendKeys(Keys.TAB);
    sendKeys(fieldMap.get(CONFIRMED_BY_TITLE), map.get(CONFIRMED_BY_TITLE));
    fieldMap.get(CONFIRMED_BY_TITLE).sendKeys(Keys.TAB);
  }

  @Override
  public void verifyData(List<Map<String, String>> data) {
    for (Map<String, String> visitInformationData : data) {
      assertEquals(fieldMap.get(VEHICLE_ID).getAttribute(VALUE), visitInformationData.get(VEHICLE_ID));
      assertEquals(fieldMap.get(OBSERVATIONS).getAttribute(VALUE), visitInformationData.get(OBSERVATIONS));
      assertEquals(fieldMap.get(VERIFIED_BY_NAME).getAttribute(VALUE), visitInformationData.get(VERIFIED_BY_NAME));
      assertEquals(fieldMap.get(VERIFIED_BY_TITLE).getAttribute(VALUE), visitInformationData.get(VERIFIED_BY_TITLE));
      assertEquals(fieldMap.get(CONFIRMED_BY_NAME).getAttribute(VALUE), visitInformationData.get(CONFIRMED_BY_NAME));
      assertEquals(fieldMap.get(CONFIRMED_BY_TITLE).getAttribute(VALUE), visitInformationData.get(CONFIRMED_BY_TITLE));
    }
  }

  @Override
  public void navigate() {
    testWebDriver.sleep(1000);
    facilityVisitTab.click();
    removeFocusFromElement();
  }

  public String getVisitInformationPageLabel() {
    testWebDriver.waitForElementToAppear(visitInformationLabel);
    return visitInformationLabel.getText();
  }

  public void enterObservations(String observations) {
    testWebDriver.waitForElementToAppear(observationsField);
    sendKeys(observationsField, observations);
    observationsField.sendKeys(Keys.TAB);
  }

  public void enterConfirmedByName(String confirmedByName) {
    testWebDriver.waitForElementToAppear(confirmedByNameField);
    sendKeys(confirmedByNameField, confirmedByName);
    confirmedByNameField.sendKeys(Keys.TAB);
  }

  public void enterConfirmedByTitle(String confirmedByTitle) {
    testWebDriver.waitForElementToAppear(confirmedByTitleField);
    sendKeys(confirmedByTitleField, confirmedByTitle);
    confirmedByTitleField.sendKeys(Keys.TAB);
  }

  public void enterVerifiedByName(String verifiedByName) {
    testWebDriver.waitForElementToAppear(verifiedByNameField);
    sendKeys(verifiedByNameField, verifiedByName);
    verifiedByNameField.sendKeys(Keys.TAB);
  }

  public void enterVerifiedByTitle(String verifiedByTitle) {
    testWebDriver.waitForElementToAppear(verifiedByTitleField);
    sendKeys(verifiedByTitleField, verifiedByTitle);
    verifiedByTitleField.sendKeys(Keys.TAB);
  }

  public void enterVisitDateAsCurrentDate() {
    testWebDriver.waitForElementToAppear(visitDateField);
    visitDateField.click();
    testWebDriver.waitForElementToAppear(calender);
    calender.click();
    visitDateField.sendKeys(Keys.TAB);
  }

  public void enterVehicleId(String vehicleId) {
    testWebDriver.waitForElementToAppear(vehicleIdField);
    sendKeys(vehicleIdField, vehicleId);
    vehicleIdField.sendKeys(Keys.TAB);
  }

  public String getFacilityVisitTabLabel() {
    testWebDriver.waitForElementToAppear(facilityVisitTabLabel);
    return facilityVisitTabLabel.getText();
  }

  public String getWasFacilityVisitedLabel() {
    testWebDriver.waitForElementToAppear(wasFacilityVisitedLabel);
    return wasFacilityVisitedLabel.getText();
  }

  public void selectFacilityVisitedYes() {
    testWebDriver.waitForElementToAppear(facilityVisitedYesRadioButton);
    facilityVisitedYesRadioButton.click();
    removeFocusFromElement();
  }

  public void selectFacilityVisitedNo() {
    testWebDriver.waitForElementToAppear(facilityVisitedNoRadioButton);
    facilityVisitedNoRadioButton.click();
    removeFocusFromElement();
  }

  public boolean isYesRadioButtonSelected() {
    testWebDriver.waitForElementToAppear(facilityVisitedYesRadioButton);
    return facilityVisitedYesRadioButton.isSelected();
  }

  public boolean isNoRadioButtonSelected() {
    testWebDriver.waitForElementToAppear(facilityVisitedNoRadioButton);
    return facilityVisitedNoRadioButton.isSelected();
  }

  public String getVisitDate() {
    testWebDriver.waitForElementToAppear(visitDateField);
    return visitDateField.getAttribute("value");
  }

  public void selectReasonBadWeather() {
    testWebDriver.waitForElementToAppear(badWeather);
    badWeather.click();
    removeFocusFromElement();
  }

  public void selectReasonNoTransport() {
    testWebDriver.waitForElementToAppear(noTransport);
    noTransport.click();
    removeFocusFromElement();
  }

  public void selectReasonOther() {
    testWebDriver.waitForElementToAppear(other);
    other.click();
    removeFocusFromElement();
  }

  public void enterOtherReasonInTextBox(String reason) {
    testWebDriver.waitForElementToAppear(otherTextBox);
    sendKeys(otherTextBox, reason);
    otherTextBox.sendKeys(Keys.TAB);
  }

  public void verifyAllFieldsDisabled() {
    assertFalse("Yes radio button was enabled.", facilityVisitedYesRadioButton.isEnabled());
    assertFalse("No radio button was enabled.", facilityVisitedNoRadioButton.isEnabled());
    if (isYesRadioButtonSelected()) {
      assertFalse("Observation field enabled.", observationsField.isEnabled());
      assertFalse("ConfirmedBy name field enabled.", confirmedByNameField.isEnabled());
      assertFalse("ConfirmedBy title field enabled.", confirmedByTitleField.isEnabled());
      assertFalse("VerifiedBy name field enabled.", verifiedByNameField.isEnabled());
      assertFalse("VerifiedBy title Field field enabled.", verifiedByTitleField.isEnabled());
      assertFalse("Visit date field was enabled.", visitDateField.isEnabled());
      assertFalse("vehicle id field was enabled.", vehicleIdField.isEnabled());
    } else if (isNoRadioButtonSelected()) {
      assertFalse("Bad Weather reason radio button enabled.", badWeather.isEnabled());
      assertFalse("No Transport reason radio button enabled.", noTransport.isEnabled());
      assertFalse("Facility Closed radio button enabled.", facilityClosed.isEnabled());
      assertFalse("Unavailable Funds radio button enabled.", unavailableFunds.isEnabled());
      assertFalse("Not Part Of Program radio button enabled.", notPartOfProgram.isEnabled());
      assertFalse("Other radio button enabled.", other.isEnabled());
      assertFalse("Other reason text field was enabled.", otherTextBox.isEnabled());
    }
  }

  public void enterDataWhenFacilityVisited(String confirmName, String confirmTitle, String verifierName,
                                           String verifierTitle) {
    selectFacilityVisitedYes();
    enterVisitDateAsCurrentDate();
    enterConfirmedByName(confirmName);
    enterConfirmedByTitle(confirmTitle);
    enterVerifiedByName(verifierName);
    enterVerifiedByTitle(verifierTitle);
  }

  @Override
  public void removeFocusFromElement() {
    testWebDriver.waitForElementToAppear(visitInformationLabel);
    visitInformationLabel.click();
  }

  public boolean isOtherReasonSelected() {
    testWebDriver.waitForElementToAppear(other);
    return other.isSelected();
  }

  public String getOtherReason() {
    testWebDriver.waitForElementToAppear(otherTextBox);
    return otherTextBox.getAttribute("value");
  }
}
