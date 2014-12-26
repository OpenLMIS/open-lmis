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

import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static org.openqa.selenium.support.How.ID;

public class AdultCoveragePage extends DistributionTab {

  @FindBy(how = ID, using = "adultCoverageTabLabel")
  private static WebElement adultCoverageTab = null;

  @FindBy(how = ID, using = "adultCoverageTabIcon")
  private static WebElement adultCoverageIcon = null;

  @FindBy(how = ID, using = "adultHeader")
  private static WebElement adultHeaderLabel = null;

  @FindBy(how = ID, using = "groupVaccinationLabel")
  private static WebElement groupVaccinationLabel = null;

  @FindBy(how = ID, using = "targetGroupLabel")
  private static WebElement targetGroupLabel = null;

  @FindBy(how = ID, using = "tetanusFirstLabel")
  private static WebElement tetanusFirstLabel = null;

  @FindBy(how = ID, using = "tetanusSecondFifthLabel")
  private static WebElement tetanusSecondFifthLabel = null;

  @FindBy(how = ID, using = "totalTetanusLabel")
  private static WebElement totalTetanusLabel = null;

  @FindBy(how = ID, using = "coverageRateLabel")
  private static WebElement coverageRateLabel = null;

  @FindBy(how = ID, using = "openedVialsLabel")
  private static WebElement openedVialsLabel = null;

  @FindBy(how = ID, using = "wastageRateLabel")
  private static WebElement wastageRateLabel = null;

  @FindBy(how = ID, using = "healthCenter1Label")
  private static WebElement healthCenter1Label = null;

  @FindBy(how = ID, using = "outreach1Label")
  private static WebElement outreach1Label = null;

  @FindBy(how = ID, using = "total1Label")
  private static WebElement total1Label = null;

  @FindBy(how = ID, using = "healthCenter2To5Label")
  private static WebElement healthCenter2To5Label = null;

  @FindBy(how = ID, using = "outreach2To5Label")
  private static WebElement outreach2To5Label = null;

  @FindBy(how = ID, using = "total2To5Label")
  private static WebElement total2To5Label = null;

  @FindBy(how = ID, using = "pregnantWomenLabel")
  private static WebElement pregnantWomenLabel = null;

  @FindBy(how = ID, using = "mifLabel")
  private static WebElement mifLabel = null;

  @FindBy(how = ID, using = "communityCell")
  private static WebElement communityLabel = null;

  @FindBy(how = ID, using = "studentsLabel")
  private static WebElement studentsLabel = null;

  @FindBy(how = ID, using = "workersLabel")
  private static WebElement workersLabel = null;

  @FindBy(how = ID, using = "studentNotMifLabel")
  private static WebElement studentNotMifLabel = null;

  @FindBy(how = ID, using = "workerNotMif")
  private static WebElement workerNotMifLabel = null;

  @FindBy(how = ID, using = "otherNotMifLabel")
  private static WebElement otherNotMifLabel = null;

  @FindBy(how = ID, using = "totalRowLabel")
  private static WebElement totalRowLabel = null;

  @FindBy(how = ID, using = "totalHealthCenterTetanus1")
  private static WebElement totalHealthCenterTetanus1 = null;

  @FindBy(how = ID, using = "totalOutreachTetanus1")
  private static WebElement totalOutreachTetanus1 = null;

  @FindBy(how = ID, using = "totalTetanus1")
  private static WebElement totalTetanus1 = null;

  @FindBy(how = ID, using = "totalHealthCenterTetanus2To5")
  private static WebElement totalHealthCenterTetanus2To5 = null;

  @FindBy(how = ID, using = "totalOutreachTetanus2To5")
  private static WebElement totalOutreachTetanus2To5 = null;

  @FindBy(how = ID, using = "totalTetanus2To5")
  private static WebElement totalTetanus2To5 = null;

  @FindBy(how = ID, using = "totalTetanus")
  private static WebElement totalTetanus = null;

  @FindBy(how = ID, using = "openedVialInputField")
  private static WebElement openedVialInputField = null;

  @FindBy(how = ID, using = "openedVial")
  private static WebElement openedVialNr = null;

  @FindBy(how = ID, using = "wastageRate")
  private static WebElement wastageRate = null;

  @FindBy(how = ID, using = "adultCoverageApplyNRToAll")
  private static WebElement applyNRAllButton = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okButton = null;

  @FindBy(how = ID, using = "button_Cancel")
  private static WebElement cancelButton = null;


  public AdultCoveragePage(TestWebDriver driver) {
    super(driver);
  }

  @Override
  public void verifyIndicator(String color) {
    verifyOverallIndicator(adultCoverageIcon, color);
  }

  @Override
  public void enterValues(List<Map<String, String>> dataMapList) {
    Map<String, String> dataMap = dataMapList.get(0);
    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      enterOutreachFirstInput(rowNumber, dataMap.get("outreach1"));
      enterOutreach2To5Input(rowNumber, dataMap.get("outreach25"));
      if (rowNumber < 3 || rowNumber > 6) {
        enterHealthCenterFirstInput(rowNumber, dataMap.get("healthCenter1"));
        enterHealthCenter2To5Input(rowNumber, dataMap.get("healthCenter25"));
      }
    }
    enterOpenedVialInputField(dataMap.get("openedVial"));
  }

  @Override
  public void verifyData(List<Map<String, String>> map) {
    Map<String, String> dataMap = map.get(0);
    assertEquals(dataMap.get("targetGroup"), getTargetGroup(1));
    assertEquals(dataMap.get("healthCenter1"), getHealthCenterFirstInput(1));
    assertEquals(dataMap.get("outreach1"), getOutreachFirstInput(1));
    assertEquals(dataMap.get("total1"), getTotalTetanusFirst(1));
    assertEquals(dataMap.get("healthCenter25"), getHealthCenter2To5Input(1));
    assertEquals(dataMap.get("outreach25"), getOutreach2To5Input(1));
    assertEquals(dataMap.get("total2"), getTotalTetanus2To5(1));
    assertEquals(dataMap.get("total3"), getTotalTetanus(1));
    assertEquals(dataMap.get("coverageRate"), getCoverageRate(1));
    assertEquals(dataMap.get("openedVial"), getOpenedVialInputField());
    assertEquals(dataMap.get("wastageRate"), getWastageRate());
  }

  @Override
  public void navigate() {
    testWebDriver.waitForElementToAppear(adultCoverageTab);
    adultCoverageTab.click();
    removeFocusFromElement();
  }

  @Override
  public void verifyAllFieldsDisabled() {
    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      assertFalse(isOutreachFirstEnabled(rowNumber));
      assertFalse(isOutreach2To5Enabled(rowNumber));
      if (rowNumber < 3 || rowNumber > 6) {
        assertFalse(isHealthCenterFirstEnabled(rowNumber));
        assertFalse(isHealthCenter2To5Enabled(rowNumber));
      }
    }
    assertFalse(isOpenedVialsEnabled());
  }

  @Override
  public void removeFocusFromElement() {
    testWebDriver.waitForElementToAppear(adultHeaderLabel);
    adultHeaderLabel.click();
  }

  public String getAdultCoveragePageHeader() {
    testWebDriver.waitForElementToAppear(adultHeaderLabel);
    return adultHeaderLabel.getText();
  }

  public String getAdultCoverageTabLabel() {
    testWebDriver.waitForElementToAppear(adultCoverageTab);
    return adultCoverageTab.getText();
  }

  public String getGroupVaccinationLabel() {
    testWebDriver.waitForElementToAppear(groupVaccinationLabel);
    return groupVaccinationLabel.getText();
  }

  public String getTargetGroupLabel() {
    testWebDriver.waitForElementToAppear(targetGroupLabel);
    return targetGroupLabel.getText();
  }

  public String getTetanusFirstLabel() {
    testWebDriver.waitForElementToAppear(tetanusFirstLabel);
    return tetanusFirstLabel.getText();
  }

  public String getTetanusSecondFifthLabel() {
    testWebDriver.waitForElementToAppear(tetanusSecondFifthLabel);
    return tetanusSecondFifthLabel.getText();
  }

  public String getTotalTetanusLabel() {
    testWebDriver.waitForElementToAppear(totalTetanusLabel);
    return totalTetanusLabel.getText();
  }

  public String getCoverageRateLabel() {
    testWebDriver.waitForElementToAppear(coverageRateLabel);
    return coverageRateLabel.getText();
  }

  public String getOpenedVialsLabel() {
    testWebDriver.waitForElementToAppear(openedVialsLabel);
    return openedVialsLabel.getText();
  }

  public String getWastageRateLabel() {
    testWebDriver.waitForElementToAppear(wastageRateLabel);
    return wastageRateLabel.getText();
  }

  public String getHealthCenter1Label() {
    testWebDriver.waitForElementToAppear(healthCenter1Label);
    return healthCenter1Label.getText();
  }

  public String getOutreach1Label() {
    testWebDriver.waitForElementToAppear(outreach1Label);
    return outreach1Label.getText();
  }

  public String getTotal1Label() {
    testWebDriver.waitForElementToAppear(total1Label);
    return total1Label.getText();
  }

  public String getHealthCenter2To5Label() {
    testWebDriver.waitForElementToAppear(healthCenter2To5Label);
    return healthCenter2To5Label.getText();
  }

  public String getOutreach2To5Label() {
    testWebDriver.waitForElementToAppear(outreach2To5Label);
    return outreach2To5Label.getText();
  }

  public String getTotal2To5Label() {
    testWebDriver.waitForElementToAppear(total2To5Label);
    return total2To5Label.getText();
  }

  public String getPregnantWomenLabel() {
    testWebDriver.waitForElementToAppear(pregnantWomenLabel);
    return pregnantWomenLabel.getText();
  }

  public String getMifLabel() {
    testWebDriver.waitForElementToAppear(mifLabel);
    return mifLabel.getText();
  }

  public String getCommunityLabel() {
    testWebDriver.waitForElementToAppear(communityLabel);
    return communityLabel.getText();
  }

  public String getStudentsLabel() {
    testWebDriver.waitForElementToAppear(studentsLabel);
    return studentsLabel.getText();
  }

  public String getWorkersLabel() {
    testWebDriver.waitForElementToAppear(workersLabel);
    return workersLabel.getText();
  }

  public String getStudentNotMifLabel() {
    testWebDriver.waitForElementToAppear(studentNotMifLabel);
    return studentNotMifLabel.getText();
  }

  public String getWorkerNotMifLabel() {
    testWebDriver.waitForElementToAppear(workerNotMifLabel);
    return workerNotMifLabel.getText();
  }

  public String getOtherNotMifLabel() {
    testWebDriver.waitForElementToAppear(otherNotMifLabel);
    return otherNotMifLabel.getText();
  }

  public String getTotalRowLabel() {
    testWebDriver.waitForElementToAppear(totalRowLabel);
    return totalRowLabel.getText();
  }

  public String getTotalHealthCenterTetanus1() {
    testWebDriver.waitForElementToAppear(totalHealthCenterTetanus1);
    return totalHealthCenterTetanus1.getText();
  }

  public String getTotalOutreachTetanus1() {
    testWebDriver.waitForElementToAppear(totalOutreachTetanus1);
    return totalOutreachTetanus1.getText();
  }

  public String getTotalTetanus1() {
    testWebDriver.waitForElementToAppear(totalTetanus1);
    return totalTetanus1.getText();
  }

  public String getTotalHealthCenterTetanus2To5() {
    testWebDriver.waitForElementToAppear(totalHealthCenterTetanus2To5);
    return totalHealthCenterTetanus2To5.getText();
  }

  public String getTotalOutreachTetanus2To5() {
    testWebDriver.waitForElementToAppear(totalOutreachTetanus2To5);
    return totalOutreachTetanus2To5.getText();
  }

  public String getTotalTetanus2To5() {
    testWebDriver.waitForElementToAppear(totalTetanus2To5);
    return totalTetanus2To5.getText();
  }

  public String getTotalTetanus() {
    testWebDriver.waitForElementToAppear(totalTetanus);
    return totalTetanus.getText();
  }

  public void enterHealthCenterFirstInput(int rowNumber, String value) {
    WebElement healthCenter1 = testWebDriver.getElementById("healthCenter1_" + rowNumber);
    testWebDriver.waitForElementToAppear(healthCenter1);
    healthCenter1.sendKeys(value);
    removeFocusFromElement();
  }

  public void enterHealthCenter2To5Input(int rowNumber, String value) {
    WebElement healthCenter2To5 = testWebDriver.getElementById("healthCenter2To5_" + rowNumber);
    testWebDriver.waitForElementToAppear(healthCenter2To5);
    healthCenter2To5.sendKeys(value);
    removeFocusFromElement();
  }

  public void enterOutreachFirstInput(int rowNumber, String value) {
    WebElement outreach1 = testWebDriver.getElementById("outreach1_" + rowNumber);
    testWebDriver.waitForElementToAppear(outreach1);
    outreach1.sendKeys(value);
    removeFocusFromElement();
  }

  public void enterOutreach2To5Input(int rowNumber, String value) {
    WebElement outreach2To5 = testWebDriver.getElementById("outreach2To5_" + rowNumber);
    testWebDriver.waitForElementToAppear(outreach2To5);
    outreach2To5.sendKeys(value);
    removeFocusFromElement();
  }

  public String getHealthCenterFirstInput(int rowNumber) {
    WebElement healthCenter1 = testWebDriver.getElementById("healthCenter1_" + rowNumber);
    testWebDriver.waitForElementToAppear(healthCenter1);
    return healthCenter1.getAttribute("value");
  }

  public String getHealthCenter2To5Input(int rowNumber) {
    WebElement healthCenter2To5 = testWebDriver.getElementById("healthCenter2To5_" + rowNumber);
    testWebDriver.waitForElementToAppear(healthCenter2To5);
    return healthCenter2To5.getAttribute("value");
  }

  public String getOutreachFirstInput(int rowNumber) {
    WebElement outreach1 = testWebDriver.getElementById("outreach1_" + rowNumber);
    testWebDriver.waitForElementToAppear(outreach1);
    return outreach1.getAttribute("value");
  }

  public String getOutreach2To5Input(int rowNumber) {
    WebElement outreach2To5 = testWebDriver.getElementById("outreach2To5_" + rowNumber);
    testWebDriver.waitForElementToAppear(outreach2To5);
    return outreach2To5.getAttribute("value");
  }

  public boolean isHealthCenterFirstEnabled(int rowNumber) {
    WebElement healthCenter1 = testWebDriver.getElementById("healthCenter1_" + rowNumber);
    testWebDriver.waitForElementToAppear(healthCenter1);
    return healthCenter1.isEnabled();
  }

  public boolean isHealthCenter2To5Enabled(int rowNumber) {
    WebElement healthCenter2To5 = testWebDriver.getElementById("healthCenter2To5_" + rowNumber);
    testWebDriver.waitForElementToAppear(healthCenter2To5);
    return healthCenter2To5.isEnabled();
  }

  public boolean isOutreachFirstEnabled(int rowNumber) {
    WebElement outreach1 = testWebDriver.getElementById("outreach1_" + rowNumber);
    testWebDriver.waitForElementToAppear(outreach1);
    return outreach1.isEnabled();
  }

  public boolean isOutreach2To5Enabled(int rowNumber) {
    WebElement outreach2To5 = testWebDriver.getElementById("outreach2To5_" + rowNumber);
    testWebDriver.waitForElementToAppear(outreach2To5);
    return outreach2To5.isEnabled();
  }

  public void applyHealthCenterFirstNr(int rowNumber) {
    WebElement healthCenter1Nr = testWebDriver.getElementById("healthCenter1Nr_" + rowNumber);
    testWebDriver.waitForElementToAppear(healthCenter1Nr);
    healthCenter1Nr.click();
    removeFocusFromElement();
  }

  public void applyHealthCenter2To5Nr(int rowNumber) {
    WebElement healthCenter2To5Nr = testWebDriver.getElementById("healthCenter2To5Nr_" + rowNumber);
    testWebDriver.waitForElementToAppear(healthCenter2To5Nr);
    healthCenter2To5Nr.click();
    removeFocusFromElement();
  }

  public void applyOutreach2To5Nr(int rowNumber) {
    WebElement outreach2To5Nr = testWebDriver.getElementById("outreach2To5Nr_" + rowNumber);
    testWebDriver.waitForElementToAppear(outreach2To5Nr);
    outreach2To5Nr.click();
    removeFocusFromElement();
  }

  public boolean isHealthCenterFirstNrSelected(int rowNumber) {
    WebElement healthCenter1Nr = testWebDriver.getElementById("healthCenter1Nr_" + rowNumber);
    testWebDriver.waitForElementToAppear(healthCenter1Nr);
    return healthCenter1Nr.isSelected();
  }

  public boolean isHealthCenter2To5NrSelected(int rowNumber) {
    WebElement healthCenter2To5Nr = testWebDriver.getElementById("healthCenter2To5Nr_" + rowNumber);
    testWebDriver.waitForElementToAppear(healthCenter2To5Nr);
    return healthCenter2To5Nr.isSelected();
  }

  public boolean isOutreachFirstNrSelected(int rowNumber) {
    WebElement outreach1Nr = testWebDriver.getElementById("outreach1Nr_" + rowNumber);
    testWebDriver.waitForElementToAppear(outreach1Nr);
    return outreach1Nr.isSelected();
  }

  public boolean isOutreach2To5NrSelected(int rowNumber) {
    WebElement outreach2To5Nr = testWebDriver.getElementById("outreach2To5Nr_" + rowNumber);
    testWebDriver.waitForElementToAppear(outreach2To5Nr);
    return outreach2To5Nr.isSelected();
  }

  public void enterOpenedVialInputField(String value) {
    testWebDriver.waitForElementToAppear(openedVialInputField);
    openedVialInputField.sendKeys(value);
    removeFocusFromElement();
  }

  public String getOpenedVialInputField() {
    testWebDriver.waitForElementToAppear(openedVialInputField);
    return openedVialInputField.getAttribute("value");
  }

  public void applyNrToOpenedVials() {
    testWebDriver.waitForElementToAppear(openedVialNr);
    openedVialNr.click();
    removeFocusFromElement();
  }

  public boolean isOpenedVialsEnabled() {
    testWebDriver.waitForElementToAppear(openedVialInputField);
    return openedVialInputField.isEnabled();
  }

  public String getTotalTetanusFirst(int rowNumber) {
    WebElement totalTetanus1 = testWebDriver.getElementById("totalTetanus1_" + rowNumber);
    testWebDriver.waitForElementToAppear(totalTetanus1);
    return totalTetanus1.getText();
  }

  public String getTotalTetanus2To5(int rowNumber) {
    WebElement totalTetanus2To5 = testWebDriver.getElementById("totalTetanus2To5_" + rowNumber);
    testWebDriver.waitForElementToAppear(totalTetanus2To5);
    return totalTetanus2To5.getText();
  }

  public String getTotalTetanus(int rowNumber) {
    WebElement totalTetanus = testWebDriver.getElementById("totalTetanus_" + rowNumber);
    testWebDriver.waitForElementToAppear(totalTetanus);
    return totalTetanus.getText();
  }

  public String getCoverageRate(int rowNumber) {
    WebElement coverageRate = testWebDriver.getElementById("coverageRate_" + rowNumber);
    testWebDriver.waitForElementToAppear(coverageRate);
    return coverageRate.getText();
  }

  public String getTargetGroup(int rowNumber) {
    WebElement targetGroup = testWebDriver.getElementById("targetGroup_" + rowNumber);
    testWebDriver.waitForElementToAppear(targetGroup);
    return targetGroup.getText();
  }

  public String getWastageRate() {
    testWebDriver.waitForElementToAppear(wastageRate);
    return wastageRate.getText();
  }

  public void clickApplyNrToAll() {
    testWebDriver.waitForElementToAppear(applyNRAllButton);
    applyNRAllButton.click();
  }

  public void clickOK() {
    testWebDriver.waitForElementToAppear(okButton);
    okButton.click();
  }

  public void clickCancel() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public void enterDataInAllFields() {
    for (int rowNumber = 1; rowNumber <= 7; rowNumber++) {
      enterOutreachFirstInput(rowNumber, "2" + rowNumber);
      enterOutreach2To5Input(rowNumber, "4" + rowNumber);
      if (rowNumber < 3 || rowNumber > 6) {
        enterHealthCenterFirstInput(rowNumber, "1" + rowNumber);
        enterHealthCenter2To5Input(rowNumber, "3" + rowNumber);
      }
    }
    enterOpenedVialInputField("999");
  }

}
