package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static org.openqa.selenium.support.How.ID;

public class ChildCoveragePage extends DistributionTab {

  @FindBy(how = ID, using = "childCoverageTabLabel")
  private static WebElement childCoverageTab = null;

  @FindBy(how = ID, using = "coverageHeader")
  private static WebElement childCoverageHeader = null;

  @FindBy(how = ID, using = "colBCG")
  private static WebElement regimenBCG = null;

  @FindBy(how = ID, using = "colPolio (Newborn)")
  private static WebElement regimenPolioNewborn = null;

  @FindBy(how = ID, using = "colPolio 1st dose")
  private static WebElement regimenPolioDose1 = null;

  @FindBy(how = ID, using = "colPolio 2nd dose")
  private static WebElement regimenPolioDose2 = null;

  @FindBy(how = ID, using = "colPolio 3rd dose")
  private static WebElement regimenPolioDose3 = null;

  @FindBy(how = ID, using = "colPenta 1st dose")
  private static WebElement regimenPentaDose1 = null;

  @FindBy(how = ID, using = "colPenta 2nd dose")
  private static WebElement regimenPentaDose2 = null;

  @FindBy(how = ID, using = "colPenta 3rd dose")
  private static WebElement regimenPentaDose3 = null;

  @FindBy(how = ID, using = "colPCV10 1st dose")
  private static WebElement regimenPCV10Dose1 = null;

  @FindBy(how = ID, using = "colPCV10 2nd dose")
  private static WebElement regimenPCV10Dose2 = null;

  @FindBy(how = ID, using = "colPCV10 3rd dose")
  private static WebElement regimenPCV10Dose3 = null;

  @FindBy(how = ID, using = "colMeasles")
  private static WebElement regimenMeasles = null;

  @FindBy(how = ID, using = "vaccination")
  private static WebElement headerChildVaccination = null;

  @FindBy(how = ID, using = "targetGroup")
  private static WebElement headerTargetGroup = null;

  @FindBy(how = ID, using = "categoryOneHealthCenter")
  private static WebElement headerCategoryOneHealthCenter = null;

  @FindBy(how = ID, using = "categoryOneMobileBrigade")
  private static WebElement headerCategoryOneMobileBrigade = null;

  @FindBy(how = ID, using = "categoryOneTotal")
  private static WebElement headerCategoryOneTotal = null;

  @FindBy(how = ID, using = "coverageRate")
  private static WebElement headerCoverageRate = null;

  @FindBy(how = ID, using = "categoryTwoHealthCenter")
  private static WebElement headerCategoryTwoHealthCenter = null;

  @FindBy(how = ID, using = "categoryTwoMobileBrigade")
  private static WebElement headerCategoryTwoMobileBrigade = null;

  @FindBy(how = ID, using = "categoryTwoTotal")
  private static WebElement headerCategoryTwoTotal = null;

  @FindBy(how = ID, using = "totalVaccination")
  private static WebElement headerTotalVaccination = null;

  @FindBy(how = ID, using = "openedVials")
  private static WebElement headerOpenedVials = null;

  @FindBy(how = ID, using = "openedVialsWastageRate")
  private static WebElement headerOpenedVialsWastageRate = null;

  @FindBy(how = ID, using = "childrenAgeGroup1")
  private static WebElement headerChildrenAgeGroup1 = null;

  @FindBy(how = ID, using = "childrenAgeGroup2")
  private static WebElement headerChildrenAgeGroup2 = null;

  @FindBy(how = ID, using = "BCG")
  private static WebElement openedVialsBCGLabel = null;

  @FindBy(how = ID, using = "Polio10")
  private static WebElement openedVialsPolio10Label = null;

  @FindBy(how = ID, using = "Polio20")
  private static WebElement openedVialsPolio20Label = null;

  @FindBy(how = ID, using = "Penta1")
  private static WebElement openedVialsPenta1Label = null;

  @FindBy(how = ID, using = "Penta10")
  private static WebElement openedVialsPenta10Label = null;

  @FindBy(how = ID, using = "PCV")
  private static WebElement openedVialsPCVLabel = null;

  @FindBy(how = ID, using = "Measles")
  private static WebElement openedVialsMeaslesLabel = null;

  @FindBy(how = ID, using = "coverageOpenedVial00")
  private static WebElement openedVialsBcgNR = null;

  @FindBy(how = ID, using = "coverageOpenedVial11")
  private static WebElement openedVialsPolioNR = null;

  @FindBy(how = ID, using = "childCoverageTable")
  private static WebElement childCoverageTable = null;

  @FindBy(how = ID, using = "childCoverageApplyNRAll")
  private static WebElement applyNrToAllButton = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okButton = null;

  @FindBy(how = ID, using = "button_Cancel")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "childCoverageTabIcon")
  private static WebElement childCoverageStatusIcon = null;

  public ChildCoveragePage(TestWebDriver driver) {
    super(driver);
  }

  @Override
  public void verifyIndicator(String color) {
    verifyOverallIndicator(childCoverageStatusIcon, color);
  }

  @Override
  public void enterValues(List<Map<String, String>> data) {
    Map<String, String> dataMap = data.get(0);
    for (int rowNumber = 1; rowNumber <= 12; rowNumber++) {
      enterHealthCenter11MonthsDataForGivenRow(rowNumber, dataMap.get("healthCenter11"));
      enterOutreach11MonthsDataForGivenRow(rowNumber, dataMap.get("outreach11"));
      if (rowNumber != 2) {
        enterHealthCenter23MonthsDataForGivenRow(rowNumber, dataMap.get("healthCenter23"));
        enterOutreach23MonthsDataForGivenRow(rowNumber, dataMap.get("outreach23"));
      }
    }
    enterOpenedVialsCountForGivenGroupAndRow(1, 1, dataMap.get("openedVial"));
    enterOpenedVialsCountForGivenGroupAndRow(2, 1, dataMap.get("openedVial"));
    enterOpenedVialsCountForGivenGroupAndRow(2, 2, dataMap.get("openedVial"));
    enterOpenedVialsCountForGivenGroupAndRow(6, 1, dataMap.get("openedVial"));
    enterOpenedVialsCountForGivenGroupAndRow(6, 2, dataMap.get("openedVial"));
    enterOpenedVialsCountForGivenGroupAndRow(9, 1, dataMap.get("openedVial"));
    enterOpenedVialsCountForGivenGroupAndRow(12, 1, dataMap.get("openedVial"));
  }

  @Override
  public void verifyData(List<Map<String, String>> data) {
    Map<String, String> dataMap = data.get(0);
    assertEquals(dataMap.get("targetGroup"), getTextOfTargetGroupValue(9));
    assertEquals(dataMap.get("healthCenter11"), getHealthCenter11MonthsDataForGivenRow(9));
    assertEquals(dataMap.get("outreach11"), getOutreach11MonthsDataForGivenRow(9));
    assertEquals(dataMap.get("total1"), getTotalForGivenColumnAndRow(1, 9));
    assertEquals(dataMap.get("coverageRate"), getCoverageRateForGivenRow(9));
    assertEquals(dataMap.get("healthCenter23"), getHealthCenter23MonthsDataForGivenRow(9));
    assertEquals(dataMap.get("outreach23"), getOutreach23MonthsDataForGivenRow(9));
    assertEquals(dataMap.get("total2"), getTotalForGivenColumnAndRow(2, 9));
    assertEquals(dataMap.get("total3"), getTotalForGivenColumnAndRow(3, 9));
    assertEquals(dataMap.get("openedVial"), getOpenedVialsCountForGivenGroupAndRow(9, 1));
    assertEquals(dataMap.get("wastageRate"), getWastageRateForGivenRow(9));
  }

  @Override
  public void navigate() {
    testWebDriver.waitForElementToAppear(childCoverageTab);
    childCoverageTab.click();
    removeFocusFromElement();
  }

  @Override
  public void verifyAllFieldsDisabled() {
    for (int rowNumber = 1; rowNumber <= 12; rowNumber++) {
      assertFalse(isHealthCenter11MonthsEnabledForGivenRow(rowNumber));
      assertFalse(isOutreach11MonthsEnabledForGivenRow(rowNumber));
      if (rowNumber != 2) {
        assertFalse(isHealthCenter23MonthsEnabledForGivenRow(rowNumber));
        assertFalse(isOutreach23MonthsEnabledForGivenRow(rowNumber));
      }
    }
    assertFalse(isOpenVialEnabled(1, 1));
    assertFalse(isOpenVialEnabled(2, 1));
    assertFalse(isOpenVialEnabled(2, 2));
    assertFalse(isOpenVialEnabled(6, 1));
    assertFalse(isOpenVialEnabled(6, 2));
    assertFalse(isOpenVialEnabled(9, 1));
    assertFalse(isOpenVialEnabled(12, 1));
  }

  public String getTextOfRegimenBCG() {
    testWebDriver.waitForElementToAppear(regimenBCG);
    return regimenBCG.getText();
  }

  public String getTextOfRegimenPolioNewBorn() {
    testWebDriver.waitForElementToAppear(regimenPolioNewborn);
    return regimenPolioNewborn.getText();
  }

  public String getTextOfRegimenPolioDose1() {
    testWebDriver.waitForElementToAppear(regimenPolioDose1);
    return regimenPolioDose1.getText();
  }

  public String getTextOfRegimenPolioDose2() {
    testWebDriver.waitForElementToAppear(regimenPolioDose2);
    return regimenPolioDose2.getText();
  }

  public String getTextOfRegimenPolioDose3() {
    testWebDriver.waitForElementToAppear(regimenPolioDose3);
    return regimenPolioDose3.getText();
  }

  public String getTextOfRegimenPentaDose1() {
    testWebDriver.waitForElementToAppear(regimenPentaDose1);
    return regimenPentaDose1.getText();
  }

  public String getTextOfRegimenPentaDose2() {
    testWebDriver.waitForElementToAppear(regimenPentaDose2);
    return regimenPentaDose2.getText();
  }

  public String getTextOfRegimenPentaDose3() {
    testWebDriver.waitForElementToAppear(regimenPentaDose3);
    return regimenPentaDose3.getText();
  }

  public String getTextOfRegimenPCV10Dose1() {
    testWebDriver.waitForElementToAppear(regimenPCV10Dose1);
    return regimenPCV10Dose1.getText();
  }

  public String getTextOfRegimenPCV10Dose2() {
    testWebDriver.waitForElementToAppear(regimenPCV10Dose2);
    return regimenPCV10Dose2.getText();
  }

  public String getTextOfRegimenPCV10Dose3() {
    testWebDriver.waitForElementToAppear(regimenPCV10Dose3);
    return regimenPCV10Dose3.getText();
  }

  public String getTextOfRegimenMeasles() {
    testWebDriver.waitForElementToAppear(regimenMeasles);
    return regimenMeasles.getText();
  }

  public String getTextOfHeaderChildrenVaccination() {
    testWebDriver.waitForElementToAppear(headerChildVaccination);
    return headerChildVaccination.getText();
  }

  public String getTextOfHeaderTargetGroup() {
    testWebDriver.waitForElementToAppear(headerTargetGroup);
    return headerTargetGroup.getText();
  }

  public String getTextOfHeaderHealthCenter1() {
    testWebDriver.waitForElementToAppear(headerCategoryOneHealthCenter);
    return headerCategoryOneHealthCenter.getText();
  }

  public String getTextOfHeaderMobileBrigade1() {
    testWebDriver.waitForElementToAppear(headerCategoryOneMobileBrigade);
    return headerCategoryOneMobileBrigade.getText();
  }

  public String getTextOfHeaderTotal1() {
    testWebDriver.waitForElementToAppear(headerCategoryOneTotal);
    return headerCategoryOneTotal.getText();
  }

  public String getTextOfHeaderCoverageRate() {
    testWebDriver.waitForElementToAppear(headerCoverageRate);
    return headerCoverageRate.getText();
  }

  public String getTextOfHeaderHealthCenter2() {
    testWebDriver.waitForElementToAppear(headerCategoryTwoHealthCenter);
    return headerCategoryTwoHealthCenter.getText();
  }

  public String getTextOfHeaderMobileBrigade2() {
    testWebDriver.waitForElementToAppear(headerCategoryTwoMobileBrigade);
    return headerCategoryTwoMobileBrigade.getText();
  }

  public String getTextOfHeaderTotal2() {
    testWebDriver.waitForElementToAppear(headerCategoryTwoTotal);
    return headerCategoryTwoTotal.getText();
  }

  public String getTextOfHeaderTotalVaccination() {
    testWebDriver.waitForElementToAppear(headerTotalVaccination);
    return headerTotalVaccination.getText();
  }

  public String getTextOfHeaderOpenedVials() {
    testWebDriver.waitForElementToAppear(headerOpenedVials);
    return headerOpenedVials.getText();
  }

  public String getTextOfHeaderWastageRate() {
    testWebDriver.waitForElementToAppear(headerOpenedVialsWastageRate);
    return headerOpenedVialsWastageRate.getText();
  }

  public String getTextOfHeaderCategory1() {
    testWebDriver.waitForElementToAppear(headerChildrenAgeGroup1);
    return headerChildrenAgeGroup1.getText();
  }

  public String getTextOfHeaderCategory2() {
    testWebDriver.waitForElementToAppear(headerChildrenAgeGroup2);
    return headerChildrenAgeGroup2.getText();
  }

  public String getTextOfOpenedVialsBCG() {
    testWebDriver.waitForElementToAppear(openedVialsBCGLabel);
    return openedVialsBCGLabel.getText();
  }

  public String getTextOfOpenedVialsPolio10() {
    testWebDriver.waitForElementToAppear(openedVialsPolio10Label);
    return openedVialsPolio10Label.getText();
  }

  public String getTextOfOpenedVialsPolio20() {
    testWebDriver.waitForElementToAppear(openedVialsPolio20Label);
    return openedVialsPolio20Label.getText();
  }

  public String getTextOfOpenedVialsPenta1() {
    testWebDriver.waitForElementToAppear(openedVialsPenta1Label);
    return openedVialsPenta1Label.getText();
  }

  public String getTextOfOpenedVialsPenta10() {
    testWebDriver.waitForElementToAppear(openedVialsPenta10Label);
    return openedVialsPenta10Label.getText();
  }

  public String getTextOfOpenedVialsPCV() {
    testWebDriver.waitForElementToAppear(openedVialsPCVLabel);
    return openedVialsPCVLabel.getText();
  }

  public String getTextOfOpenedVialsMeasles() {
    testWebDriver.waitForElementToAppear(openedVialsMeaslesLabel);
    return openedVialsMeaslesLabel.getText();
  }

  public String getTextOfTargetGroupValue(int rowNumber) {
    testWebDriver.waitForElementToAppear(testWebDriver.findElement(By.id("target" + (rowNumber - 1))));
    return testWebDriver.findElement(By.id("target" + (rowNumber - 1))).getText();
  }

  @Override
  public void removeFocusFromElement() {
    testWebDriver.waitForElementToAppear(childCoverageHeader);
    childCoverageHeader.click();
  }

  public String getTextOfChildCoverageTable() {
    testWebDriver.waitForElementToAppear(childCoverageTable);
    return childCoverageTable.getText();
  }

  public void enterHealthCenter11MonthsDataForGivenRow(int rowNumber, String value) {
    WebElement healthCenter11Months = testWebDriver.getElementById("healthCenter11Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(healthCenter11Months);
    healthCenter11Months.clear();
    healthCenter11Months.sendKeys(value);
    healthCenter11Months.sendKeys(Keys.TAB);
    testWebDriver.setImplicitWait(100);
  }

  public void enterOutreach11MonthsDataForGivenRow(int rowNumber, String value) {
    WebElement outreach11Months = testWebDriver.getElementById("outreach11Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(outreach11Months);
    outreach11Months.clear();
    outreach11Months.sendKeys(value);
    outreach11Months.sendKeys(Keys.TAB);
  }

  public void enterHealthCenter23MonthsDataForGivenRow(int rowNumber, String value) {
    WebElement healthCenter23Months = testWebDriver.getElementById("healthCenter23Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(healthCenter23Months);
    healthCenter23Months.clear();
    healthCenter23Months.sendKeys(value);
    healthCenter23Months.sendKeys(Keys.TAB);
  }

  public void enterOutreach23MonthsDataForGivenRow(int rowNumber, String value) {
    WebElement outreach23Months = testWebDriver.getElementById("outreach23Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(outreach23Months);
    outreach23Months.clear();
    outreach23Months.sendKeys(value);
    outreach23Months.sendKeys(Keys.TAB);
  }

  public String getHealthCenter11MonthsDataForGivenRow(int rowNumber) {
    WebElement healthCenter11Months = testWebDriver.getElementById("healthCenter11Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(healthCenter11Months);
    return healthCenter11Months.getAttribute("value");
  }

  public String getOutreach11MonthsDataForGivenRow(int rowNumber) {
    WebElement outreach11Months = testWebDriver.getElementById("outreach11Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(outreach11Months);
    return outreach11Months.getAttribute("value");
  }

  public String getHealthCenter23MonthsDataForGivenRow(int rowNumber) {
    WebElement healthCenter23Months = testWebDriver.getElementById("healthCenter23Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(healthCenter23Months);
    return healthCenter23Months.getAttribute("value");
  }

  public String getOutreach23MonthsDataForGivenRow(int rowNumber) {
    WebElement outreach23Months = testWebDriver.getElementById("outreach23Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(outreach23Months);
    return outreach23Months.getAttribute("value");
  }

  public void applyNRToHealthCenter11MonthsForGivenRow(int rowNumber) {
    WebElement healthCenter11MonthsNR = testWebDriver.getElementById("coverageHealthCenter11Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(healthCenter11MonthsNR);
    healthCenter11MonthsNR.click();
    removeFocusFromElement();
  }

  public void applyNRToOutreach11MonthsForGivenRow(int rowNumber) {
    WebElement outreach11MonthsNR = testWebDriver.getElementById("coverageOutreach11Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(outreach11MonthsNR);
    outreach11MonthsNR.click();
    removeFocusFromElement();
  }

  public void applyNRToHealthCenter23MonthsForGivenRow(int rowNumber) {
    WebElement healthCenter23MonthsNR = testWebDriver.getElementById("coverageHealthCenter23Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(healthCenter23MonthsNR);
    healthCenter23MonthsNR.click();
    removeFocusFromElement();
  }

  public void applyNRToOutreach23MonthsDataForGivenRow(int rowNumber) {
    WebElement outreach23MonthsNR = testWebDriver.getElementById("coverageOutreach23Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(outreach23MonthsNR);
    outreach23MonthsNR.click();
    removeFocusFromElement();
  }

  public void enterOpenedVialsCountForGivenGroupAndRow(int rowNumber, int position, String value) {
    WebElement openedVialsTextField = testWebDriver.getElementById("openedVial" + (rowNumber - 1) + (position - 1));
    testWebDriver.waitForElementToAppear(openedVialsTextField);
    openedVialsTextField.clear();
    openedVialsTextField.sendKeys(value);
    openedVialsTextField.sendKeys(Keys.TAB);
  }

  public String getOpenedVialsCountForGivenGroupAndRow(int rowNumber, int position) {
    WebElement openedVialsTextField = testWebDriver.getElementById("openedVial" + (rowNumber - 1) + (position - 1));
    testWebDriver.waitForElementToAppear(openedVialsTextField);
    return openedVialsTextField.getAttribute("value");
  }

  public void applyNrToBcgOpenedVials() {
    testWebDriver.waitForElementToAppear(openedVialsBcgNR);
    openedVialsBcgNR.click();
    removeFocusFromElement();
  }

  public void applyNrToPolioOpenedVials() {
    testWebDriver.waitForElementToAppear(openedVialsPolioNR);
    openedVialsPolioNR.click();
    removeFocusFromElement();
  }

  public String getTotalForGivenColumnAndRow(int columnNumber, int rowNumber) {
    WebElement total = testWebDriver.getElementById("total" + (columnNumber - 1) + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(total);
    return total.getText();
  }

  public String getWastageRateForGivenRow(int rowNumber) {
    WebElement wastageRate = testWebDriver.getElementById("wastageRateCalculated" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(wastageRate);
    return wastageRate.getText();
  }

  public String getCoverageRateForGivenRow(int rowNumber) {
    WebElement coverageRate = testWebDriver.getElementById("coverageRateCalculated" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(coverageRate);
    return coverageRate.getText();
  }

  public boolean isOpenVialEnabled(int groupNumber, int rowNumber) {
    WebElement openedVialsTextField = testWebDriver.getElementById("openedVial" + (groupNumber - 1) + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(openedVialsTextField);
    return openedVialsTextField.isEnabled();
  }

  public boolean isHealthCenter11MonthsEnabledForGivenRow(int rowNumber) {
    WebElement healthCenter11Months = testWebDriver.getElementById("healthCenter11Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(healthCenter11Months);
    return healthCenter11Months.isEnabled();
  }

  public boolean isOutreach11MonthsEnabledForGivenRow(int rowNumber) {
    WebElement outreach11Months = testWebDriver.getElementById("outreach11Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(outreach11Months);
    return outreach11Months.isEnabled();
  }

  public boolean isHealthCenter23MonthsEnabledForGivenRow(int rowNumber) {
    WebElement healthCenter23Months = testWebDriver.getElementById("healthCenter23Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(healthCenter23Months);
    return healthCenter23Months.isEnabled();
  }

  public boolean isOutreach23MonthsEnabledForGivenRow(int rowNumber) {
    WebElement outreach23Months = testWebDriver.getElementById("outreach23Months" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(outreach23Months);
    return outreach23Months.isEnabled();
  }

  public void applyNRToAll() {
    testWebDriver.waitForElementToAppear(applyNrToAllButton);
    applyNrToAllButton.click();
  }

  public void clickOK() {
    testWebDriver.waitForElementToAppear(okButton);
    okButton.click();
  }

  public void clickCancel() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public void enterOpenedVialsData() {
    enterOpenedVialsCountForGivenGroupAndRow(1, 1, "100");
    enterOpenedVialsCountForGivenGroupAndRow(2, 1, "200");
    enterOpenedVialsCountForGivenGroupAndRow(2, 2, "300");
    enterOpenedVialsCountForGivenGroupAndRow(6, 1, "400");
    enterOpenedVialsCountForGivenGroupAndRow(6, 2, "500");
    enterOpenedVialsCountForGivenGroupAndRow(9, 1, "600");
    enterOpenedVialsCountForGivenGroupAndRow(12, 1, "700");
  }

  public void enterAllChildCoverageValues() {
    for (int i = 1; i <= 12; i++) {
      if (i == 2) {
        enterHealthCenter11MonthsDataForGivenRow(i, String.valueOf(i));
        enterOutreach11MonthsDataForGivenRow(i, String.valueOf(i + 10));
      } else {
        enterHealthCenter11MonthsDataForGivenRow(i, String.valueOf(i));
        enterOutreach11MonthsDataForGivenRow(i, String.valueOf(i + 10));
        enterHealthCenter23MonthsDataForGivenRow(i, String.valueOf(i + 100));
        enterOutreach23MonthsDataForGivenRow(i, String.valueOf(i + 11));
      }
    }
  }
}