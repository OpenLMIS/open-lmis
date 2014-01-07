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
import static org.openqa.selenium.support.How.XPATH;

public class GeneralObservationPage extends DistributionTab {

  public static final String VERIFIED_BY_NAME = "verifiedByName";
  public static final String VERIFIED_BY_TITLE = "verifiedByTitle";
  public static final String CONFIRMED_BY_NAME = "confirmedByName";
  public static final String CONFIRMED_BY_TITLE = "confirmedByTitle";
  public static final String OBSERVATIONS = "observations";
  public static final String VALUE = "value";

  @FindBy(how = How.XPATH, using = "//div[1]/div/div/ng-include/div/ul/li[6]/a")
  private static WebElement generalObservationTab=null;

  @FindBy(how = XPATH, using = "//div[@class='left-navigation ng-scope']/ul/li[6]/a/span[1][@class='status-icon']")
  public static WebElement generalObservationsIndicator=null;

  @FindBy(how = ID, using = OBSERVATIONS)
  public static WebElement observationsField=null;

  @FindBy(how = ID, using = VERIFIED_BY_NAME)
  public static WebElement verifiedByNameField=null;

  @FindBy(how = ID, using = VERIFIED_BY_TITLE)
  public static WebElement verifiedByTitleField=null;

  @FindBy(how = ID, using = CONFIRMED_BY_NAME)
  public static WebElement confirmedByNameField=null;

  @FindBy(how = ID, using = CONFIRMED_BY_TITLE)
  public static WebElement confirmedByTitleField=null;

  public Map<String, WebElement> fieldMap = new HashMap<String, WebElement>() {{
    put(OBSERVATIONS, observationsField);
    put(CONFIRMED_BY_NAME, confirmedByNameField);
    put(CONFIRMED_BY_TITLE, confirmedByTitleField);
    put(VERIFIED_BY_TITLE, verifiedByTitleField);
    put(VERIFIED_BY_NAME, verifiedByNameField);
  }};

  public GeneralObservationPage(TestWebDriver driver) {
    super(driver);
  }

  @Override
  public void verifyIndicator(String color) {
    verifyOverallIndicator(generalObservationsIndicator, color);
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
  public void verifyData(Map<String, String> data) {
    assertEquals(fieldMap.get(OBSERVATIONS).getAttribute(VALUE), data.get(OBSERVATIONS));
    assertEquals(fieldMap.get(VERIFIED_BY_NAME).getAttribute(VALUE), data.get(VERIFIED_BY_NAME));
    assertEquals(fieldMap.get(VERIFIED_BY_TITLE).getAttribute(VALUE), data.get(VERIFIED_BY_TITLE));
    assertEquals(fieldMap.get(CONFIRMED_BY_NAME).getAttribute(VALUE), data.get(CONFIRMED_BY_NAME));
    assertEquals(fieldMap.get(CONFIRMED_BY_TITLE).getAttribute(VALUE), data.get(CONFIRMED_BY_TITLE));
  }

  public void enterObservations(String observations) {
    testWebDriver.waitForElementToAppear(observationsField);
    sendKeys(observationsField, observations);
  }

  public void enterConfirmedByName(String confirmedByName) {
    testWebDriver.waitForElementToAppear(confirmedByNameField);
    sendKeys(confirmedByNameField, confirmedByName);
  }

  public void enterConfirmedByTitle(String confirmedByTitle) {
    testWebDriver.waitForElementToAppear(confirmedByTitleField);
    sendKeys(confirmedByTitleField, confirmedByTitle);
  }

  public void enterVerifiedByName(String verifiedByName) {
    testWebDriver.waitForElementToAppear(verifiedByNameField);
    sendKeys(verifiedByNameField, verifiedByName);
  }

  public void enterVerifiedByTitle(String verifiedByTitle) {
    testWebDriver.waitForElementToAppear(verifiedByTitleField);
    sendKeys(verifiedByTitleField, verifiedByTitle);
  }

  @Override
  public void navigate() {
    generalObservationTab.click();
  }

  public void verifyAllFieldsDisabled() {
    assertFalse("Observation field enabled.", observationsField.isEnabled());
    assertFalse("ConfirmedBy name field enabled.", confirmedByNameField.isEnabled());
    assertFalse("ConfirmedBy title field enabled.", confirmedByTitleField.isEnabled());
    assertFalse("VerifiedBy name field enabled.", verifiedByNameField.isEnabled());
    assertFalse("VerifiedBy title Field field enabled.", verifiedByTitleField.isEnabled());
  }

  public void enterData(String observation, String confirmName, String confirmTitle, String verifierName,
                        String verifierTitle) {
    enterObservations(observation);
    enterConfirmedByName(confirmName);
    enterConfirmedByTitle(confirmTitle);
    enterVerifiedByName(verifierName);
    enterVerifiedByTitle(verifierTitle);
  }
}
