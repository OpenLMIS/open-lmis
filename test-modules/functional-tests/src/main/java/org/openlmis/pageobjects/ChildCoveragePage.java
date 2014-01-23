package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.Map;

import static org.openqa.selenium.support.How.ID;

public class ChildCoveragePage extends DistributionTab {

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

  @FindBy(how = ID, using = "BCGContainer")
  private static WebElement OpenedVialsBCG = null;

  @FindBy(how = ID, using = "Polio10Container")
  private static WebElement OpenedVialsPolio10 = null;

  @FindBy(how = ID, using = "Polio20Container")
  private static WebElement OpenedVialsPolio20 = null;

  @FindBy(how = ID, using = "Penta1Container")
  private static WebElement OpenedVialsPenta1 = null;

  @FindBy(how = ID, using = "Penta10Container")
  private static WebElement OpenedVialsPenta10 = null;

  @FindBy(how = ID, using = "PCVContainer")
  private static WebElement OpenedVialsPCV = null;

  @FindBy(how = ID, using = "MeaslesContainer")
  private static WebElement OpenedVialsMeasles = null;

  public ChildCoveragePage(TestWebDriver driver) {
    super(driver);
  }

  @Override
  public void verifyIndicator(String color) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void enterValues(List<Map<String, String>> dataMapList) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void verifyData(List<Map<String, String>> map) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void navigate() {
    //To change body of implemented methods use File | Settings | File Templates.
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
    testWebDriver.waitForElementToAppear(OpenedVialsBCG);
    return OpenedVialsBCG.getText();
  }

  public String getTextOfOpenedVialsPolio10() {
    testWebDriver.waitForElementToAppear(OpenedVialsPolio10);
    return OpenedVialsPolio10.getText();
  }

  public String getTextOfOpenedVialsPolio20() {
    testWebDriver.waitForElementToAppear(OpenedVialsPolio20);
    return OpenedVialsPolio20.getText();
  }

  public String getTextOfOpenedVialsPenta1() {
    testWebDriver.waitForElementToAppear(OpenedVialsPenta1);
    return OpenedVialsPenta1.getText();
  }

  public String getTextOfOpenedVialsPenta10() {
    testWebDriver.waitForElementToAppear(OpenedVialsPenta10);
    return OpenedVialsPenta10.getText();
  }

  public String getTextOfOpenedVialsPCV() {
    testWebDriver.waitForElementToAppear(OpenedVialsPCV);
    return OpenedVialsPCV.getText();
  }

  public String getTextOfOpenedVialsMeasles() {
    testWebDriver.waitForElementToAppear(OpenedVialsMeasles);
    return OpenedVialsMeasles.getText();
  }
}
