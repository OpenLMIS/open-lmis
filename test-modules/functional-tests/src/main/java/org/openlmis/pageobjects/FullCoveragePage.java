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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.ID;

public class FullCoveragePage extends DistributionTab {

  @FindBy(how = ID, using = "coverageTabLabel")
  private static WebElement fullCoverageTabLabel = null;

  @FindBy(how = ID, using = "coverageTabIcon")
  public static WebElement fullCoverageIndicator = null;

  @FindBy(how = ID, using = "coverageHeader")
  public static WebElement fullCoverageHeader = null;

  @FindBy(how = ID, using = "completeVaccinatedHeader")
  public static WebElement completeVaccinatedHeader = null;

  @FindBy(how = ID, using = "healthCenterHeader")
  public static WebElement healthCenterHeader = null;

  @FindBy(how = ID, using = "mobileBrigadeHeader")
  public static WebElement mobileBrigadeHeader = null;

  @FindBy(how = ID, using = "femaleHeader")
  public static WebElement femaleHeader = null;

  @FindBy(how = ID, using = "maleHeader")
  public static WebElement maleHeader = null;

  @FindBy(how = ID, using = "femaleHealthCenter")
  public static WebElement femaleHealthCenterField = null;

  @FindBy(how = ID, using = "femaleMobileBrigade")
  public static WebElement femaleMobileBrigadeField = null;

  @FindBy(how = ID, using = "maleHealthCenter")
  public static WebElement maleHealthCenterField = null;

  @FindBy(how = ID, using = "maleMobileBrigade")
  public static WebElement maleMobileBrigadeField = null;

  @FindBy(how = ID, using = "CoverageFormApplyNRToAll")
  public static WebElement fullCoverageFormApplyNRToAll = null;

  @FindBy(how = ID, using = "coverageFemaleMB")
  public static WebElement femaleMobileBrigadeNR = null;

  @FindBy(how = ID, using = "coverageMaleHC")
  public static WebElement maleHealthCenterNR = null;

  @FindBy(how = ID, using = "button_OK")
  public static WebElement okButton = null;

  @FindBy(how = ID, using = "coverageMaleMB")
  public static WebElement maleMobileBrigadeNR = null;

  private Map<String, WebElement> fullCoveragePageElements = new HashMap<String, WebElement>() {{
    put("femaleHealthCenter", femaleHealthCenterField);
    put("femaleMobileBrigade", femaleMobileBrigadeField);
    put("maleHealthCenter", maleHealthCenterField);
    put("maleMobileBrigade", maleMobileBrigadeField);
  }};

  public FullCoveragePage(TestWebDriver driver) {
    super(driver);
  }

  @Override
  public void verifyIndicator(String color) {
    verifyOverallIndicator(fullCoverageIndicator, color);
  }

  @Override
  public void enterValues(List<Map<String, String>> dataMapList) {
    Map<String, String> data = dataMapList.get(0);
    enterFemaleHealthCenter(Integer.valueOf(data.get("femaleHealthCenter")));
    enterFemaleMobileBrigade(Integer.valueOf(data.get("femaleMobileBrigade")));
    enterMaleHealthCenter(Integer.valueOf(data.get("maleHealthCenter")));
    enterMaleMobileBrigade(data.get("maleMobileBrigade"));
  }

  @Override
  public void verifyData(List<Map<String, String>> data) {
    for (Map<String, String> fullCoverageData : data) {
      assertEquals(fullCoveragePageElements.get("femaleHealthCenter").getAttribute("value"), fullCoverageData.get("femaleHealthCenter"));
      assertEquals(fullCoveragePageElements.get("femaleMobileBrigade").getAttribute("value"), fullCoverageData.get("femaleMobileBrigade"));
      assertEquals(fullCoveragePageElements.get("maleHealthCenter").getAttribute("value"), fullCoverageData.get("maleHealthCenter"));
      assertEquals(fullCoveragePageElements.get("maleMobileBrigade").getAttribute("value"), fullCoverageData.get("maleMobileBrigade"));
    }
  }

  @Override
  public void navigate() {
    fullCoverageTabLabel.click();
    removeFocusFromElement();
  }

  @Override
  public void verifyAllFieldsDisabled() {
    assertFalse(getStatusForField("femaleHealthCenter"));
    assertFalse(getStatusForField("femaleMobileBrigade"));
    assertFalse(getStatusForField("maleHealthCenter"));
    assertFalse(getStatusForField("maleMobileBrigade"));
  }

  public String getFullCoverageTabLabel() {
    return fullCoverageTabLabel.getText();
  }

  public void enterFemaleHealthCenter(Integer femaleHealthCenter) {
    testWebDriver.waitForElementToAppear(femaleHealthCenterField);
    sendKeys(femaleHealthCenterField, femaleHealthCenter.toString());
    femaleHealthCenterField.sendKeys(Keys.TAB);
  }

  public void enterFemaleMobileBrigade(Integer femaleMobileBrigade) {
    testWebDriver.waitForElementToAppear(femaleMobileBrigadeField);
    sendKeys(femaleMobileBrigadeField, femaleMobileBrigade.toString());
    femaleMobileBrigadeField.sendKeys(Keys.TAB);
  }

  public void enterMaleHealthCenter(Integer maleHealthCenter) {
    testWebDriver.waitForElementToAppear(maleHealthCenterField);
    sendKeys(maleHealthCenterField, maleHealthCenter.toString());
    maleHealthCenterField.sendKeys(Keys.TAB);
  }

  public void enterMaleMobileBrigade(String maleMobileBrigade) {
    testWebDriver.waitForElementToAppear(maleMobileBrigadeField);
    sendKeys(maleMobileBrigadeField, maleMobileBrigade);
    maleMobileBrigadeField.sendKeys(Keys.TAB);
  }

  public void enterData(Integer femaleHealthCenter, Integer femaleMobileBrigade, Integer maleHealthCenter, String maleMobileBrigade) {
    enterFemaleHealthCenter(femaleHealthCenter);
    enterFemaleMobileBrigade(femaleMobileBrigade);
    enterMaleHealthCenter(maleHealthCenter);
    enterMaleMobileBrigade(maleMobileBrigade);
  }

  public void clickApplyNRToAll() {
    testWebDriver.waitForElementToAppear(fullCoverageFormApplyNRToAll);
    fullCoverageFormApplyNRToAll.click();
    clickOkButton();
  }

  public void toggleApplyNRToMaleHealthCenter() {
    testWebDriver.waitForElementToAppear(maleHealthCenterNR);
    maleHealthCenterNR.click();
    removeFocusFromElement();
  }

  public void toggleApplyNRToFemaleMobileBrigade() {
    testWebDriver.waitForElementToAppear(femaleMobileBrigadeNR);
    femaleMobileBrigadeNR.click();
    removeFocusFromElement();
  }

  public void toggleApplyNRToMaleMobileBrigade() {
    testWebDriver.waitForElementToAppear(maleMobileBrigadeNR);
    maleMobileBrigadeNR.click();
    removeFocusFromElement();
  }

  public String getTextOfFullCoverageHeader() {
    testWebDriver.waitForElementToAppear(fullCoverageHeader);
    return fullCoverageHeader.getText();
  }

  public String getTextOfCompletelyVaccinatedHeader() {
    testWebDriver.waitForElementToAppear(completeVaccinatedHeader);
    return completeVaccinatedHeader.getText();
  }

  public String getTextOfFemaleHeader() {
    testWebDriver.waitForElementToAppear(femaleHeader);
    return femaleHeader.getText();
  }

  public String getTextOfMaleHeader() {
    testWebDriver.waitForElementToAppear(maleHeader);
    return maleHeader.getText();
  }

  public String getTextOfHealthCenterHeader() {
    testWebDriver.waitForElementToAppear(healthCenterHeader);
    return healthCenterHeader.getText();
  }

  public String getTextOfMobileBrigadeHeader() {
    testWebDriver.waitForElementToAppear(mobileBrigadeHeader);
    return mobileBrigadeHeader.getText();
  }

  public boolean getStatusForField(String fieldName) {
    WebElement field = fullCoveragePageElements.get(fieldName);
    testWebDriver.waitForElementToAppear(field);
    return field.isEnabled();
  }

  public String getValueForField(String fieldName) {
    WebElement field = fullCoveragePageElements.get(fieldName);
    testWebDriver.waitForElementToAppear(field);
    return field.getAttribute("value");
  }

  private void clickOkButton() {
    testWebDriver.waitForElementToAppear(okButton);
    okButton.click();
  }

  @Override
  public void removeFocusFromElement() {
    testWebDriver.waitForElementToAppear(fullCoverageHeader);
    fullCoverageHeader.click();
  }
}
