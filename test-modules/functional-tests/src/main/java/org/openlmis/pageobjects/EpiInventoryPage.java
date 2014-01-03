package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.Map;

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.NAME;
import static org.openqa.selenium.support.How.XPATH;

public class EpiInventoryPage extends DistributionTab {

  @FindBy(how = XPATH, using = "//div[1]/div/div/ng-include/div/ul/li[3]/a/span[2]")
  private static WebElement epiInventoryTab=null;

  @FindBy(how = XPATH, using = "//div[3][@id='epiInventoryTable']/form/table/tbody/tr[1]/td[1]/span")
  private static WebElement productCodeInRow1=null;

  @FindBy(how = XPATH, using = "//div[3][@id='epiInventoryTable']/form/table/tbody/tr[2]/td[1]/span")
  private static WebElement productCodeInRow2=null;

  @FindBy(how = XPATH, using = "//div[3][@id='epiInventoryTable']/form/table/tbody/tr[3]/td[1]/span")
  private static WebElement productCodeInRow3=null;

  @FindBy(how = ID,using = "existingQuantity0")
  private static WebElement NRForExistingQuantityInRow1=null;

  @FindBy(how = ID,using = "spoiledQuantity0")
  private static WebElement NRForSpoiledQuantityInRow1=null;

  @FindBy(how = ID,using = "existingQuantity1")
  private static WebElement NRForExistingQuantityInRow2=null;

  @FindBy(how = ID,using = "spoiledQuantity1")
  private static WebElement NRForSpoiledQuantityInRow2=null;

  @FindBy(how = ID,using = "existingQuantity2")
  private static WebElement NRForExistingQuantityInRow3=null;

  @FindBy(how = ID,using = "spoiledQuantity2")
  private static WebElement NRForSpoiledQuantityInRow3=null;

  @FindBy(how = NAME,using = "existingQuantity0")
  private static WebElement existingQuantityInRow1=null;

  @FindBy(how = NAME,using = "spoiledQuantity0")
  private static WebElement spoiledQuantityInRow1=null;

  @FindBy(how = NAME,using = "existingQuantity1")
  private static WebElement existingQuantityInRow2=null;

  @FindBy(how = NAME,using = "spoiledQuantity1")
  private static WebElement spoiledQuantityInRow2=null;

  @FindBy(how = NAME,using = "existingQuantity2")
  private static WebElement existingQuantityInRow3=null;

  @FindBy(how = NAME,using = "spoiledQuantity2")
  private static WebElement spoiledQuantityInRow3=null;

  @FindBy(how = NAME,using = "deliveredQuantity0")
  private static WebElement deliveredQuantityInRow1=null;

  @FindBy(how = NAME,using = "deliveredQuantity1")
  private static WebElement deliveredQuantityInRow2=null;

  @FindBy(how = NAME,using = "deliveredQuantity2")
  private static WebElement deliveredQuantityInRow3=null;

  @FindBy(how = XPATH, using = "//div[3][@id='epiInventoryTable']/form/table/tbody/tr[1]/td[@class='col-ideal-quantity']/span")
  private static WebElement idealQuantityInRow1=null;

  @FindBy(how = XPATH, using = "//div[3][@id='epiInventoryTable']/form/table/tbody/tr[2]/td[@class='col-ideal-quantity']/span")
  private static WebElement idealQuantityInRow2=null;

  @FindBy(how = XPATH, using = "//div[3][@id='epiInventoryTable']/form/table/tbody/tr[3]/td[@class='col-ideal-quantity']/span")
  private static WebElement idealQuantityInRow3=null;

  @FindBy(how = How.XPATH, using = "//input[@value='Apply NR to all fields']")
  private static WebElement applyNRToAllFieldsCheckbox=null;

  @FindBy(how = ID,using = "button_OK")
  private static WebElement okButton=null;

  public EpiInventoryPage(TestWebDriver driver) {
    super(driver);
  }


  @Override
  public void verifyIndicator(String color) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void enterValues(Map<String, String> map) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void verifyData(Map<String, String> map) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void navigate() {
    testWebDriver.waitForElementToAppear(epiInventoryTab);
    epiInventoryTab.click();
  }
}
