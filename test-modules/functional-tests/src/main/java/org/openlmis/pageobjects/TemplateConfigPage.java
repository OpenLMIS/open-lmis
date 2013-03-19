package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.Select;


public class TemplateConfigPage extends Page {


  @FindBy(how = How.LINK_TEXT, using = "Logout")
  private static WebElement logoutLink;

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement SaveButton;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-show='message']")
  private static WebElement saveSuccessMsg;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-show='error']")
  private static WebElement saveErrorMsgDiv;

  @FindBy(how = How.XPATH, using = "//li[@id='E']/span[@class='tpl-source']/span/select")
  private static WebElement stockInHandDropDown;

  @FindBy(how = How.XPATH, using = "//li[@id='A']/span/span/span/span/input[@type='checkbox']")
  private static WebElement checkboxBeginningBalance;

  @FindBy(how = How.XPATH, using = "//li[@id='C']/span[@class='tpl-source']/span/select")
  private static WebElement dropDownTotalConsumedQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='C']/span[@class='tpl-error']/div")
  private static WebElement totalConsumedQtyErrorMessage;

  @FindBy(how = How.XPATH, using = "//li[@id='E']/span[@class='tpl-error']/div")
  private static WebElement stockOnHandQtyErrorMessage;

  @FindBy(how = How.XPATH, using = "//li[@id='O']/span[@class='tpl-label']/input")
  private static WebElement productCode;

  @FindBy(how = How.XPATH, using = "//li[@id='O']/span[@class='tpl-source']/span[2]")
  private static WebElement productCodeSource;

  @FindBy(how = How.XPATH, using = "//li[@id='R']/span[@class='tpl-label']/input")
  private static WebElement productName;

  @FindBy(how = How.XPATH, using = "//li[@id='R']/span[@class='tpl-source']/span[2]")
  private static WebElement productNameSource;

  @FindBy(how = How.XPATH, using = "//li[@id='U']/span[@class='tpl-label']/input")
  private static WebElement unitOfIssue;

  @FindBy(how = How.XPATH, using = "//li[@id='U']/span[@class='tpl-source']/span[2]")
  private static WebElement unitOfIssueSource;

  @FindBy(how = How.XPATH, using = "//li[@id='A']/span[@class='tpl-label']/input")
  private static WebElement beginningBalance;

  @FindBy(how = How.XPATH, using = "//li[@id='A']/span[@class='tpl-source']/span[2]")
  private static WebElement beginningBalanceSource;

  @FindBy(how = How.XPATH, using = "//li[@id='B']/span[@class='tpl-label']/input")
  private static WebElement totalReceivedQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='B']/span[@class='tpl-source']/span[2]")
  private static WebElement totalReceivedQuantitySource;

  @FindBy(how = How.XPATH, using = "//li[@id='C']/span[@class='tpl-label']/input")
  private static WebElement totalConsumedQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='C']/span[@class='tpl-source']/span/select/option[@selected='selected']")
  private static WebElement totalConsumedQuantitySource;

  @FindBy(how = How.XPATH, using = "//li[@id='D']/span[@class='tpl-label']/input")
  private static WebElement lossesAndAdj;

  @FindBy(how = How.XPATH, using = "//li[@id='D']/span[@class='tpl-source']/span[2]")
  private static WebElement lossesAndAdjSource;

  @FindBy(how = How.XPATH, using = "//li[@id='E']/span[@class='tpl-label']/input")
  private static WebElement stockOnHand;

  @FindBy(how = How.XPATH, using = "//li[@id='E']/span[@class='tpl-source']/span/select/option[@selected='selected']")
  private static WebElement stockOnHandSource;

  @FindBy(how = How.XPATH, using = "//li[@id='E']/span/input")
  private static WebElement stockOnHandCheckBox;

  @FindBy(how = How.XPATH, using = "//li[@id='F']/span[@class='tpl-label']/input")
  private static WebElement newPatients;

  @FindBy(how = How.XPATH, using = "//li[@id='F']/span[@class='tpl-source']/span[2]")
  private static WebElement newPatientsSource;

  @FindBy(how = How.XPATH, using = "//li[@id='X']/span[@class='tpl-label']/input")
  private static WebElement stockOutDays;

  @FindBy(how = How.XPATH, using = "//li[@id='X']/span[@class='tpl-source']/span[2]")
  private static WebElement stockOutDaysSource;

  @FindBy(how = How.XPATH, using = "//li[@id='N']/span[@class='tpl-label']/input")
  private static WebElement adjustedTotalConsumption;

  @FindBy(how = How.XPATH, using = "//li[@id='N']/span[@class='tpl-source']/span[2]")
  private static WebElement adjustedTotalConsumptionSource;

  @FindBy(how = How.XPATH, using = "//li[@id='P']/span[@class='tpl-label']/input")
  private static WebElement AMC;

  @FindBy(how = How.XPATH, using = "//li[@id='P']/span[@class='tpl-source']/span[2]")
  private static WebElement AMCSource;

  @FindBy(how = How.XPATH, using = "//li[@id='H']/span[@class='tpl-label']/input")
  private static WebElement maxStockQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='H']/span[@class='tpl-source']/span[2]")
  private static WebElement maxStockQuantitySource;

  @FindBy(how = How.XPATH, using = "//li[@id='I']/span[@class='tpl-label']/input")
  private static WebElement calculatedOrderQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='I']/span[@class='tpl-source']/span[2]")
  private static WebElement calculatedOrderQuantitySource;

  @FindBy(how = How.XPATH, using = "//li[@id='J']/span[@class='tpl-label']/input")
  private static WebElement requestedQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='J']/span[@class='tpl-source']/span[2]")
  private static WebElement requestedQuantitySource;

  @FindBy(how = How.XPATH, using = "//li[@id='W']/span[@class='tpl-label']/input")
  private static WebElement requestedQuantityExplanation;

  @FindBy(how = How.XPATH, using = "//li[@id='W']/span[@class='tpl-source']/span[2]")
  private static WebElement requestedQuantityExplanationSource;

  @FindBy(how = How.XPATH, using = "//li[@id='V']/span[@class='tpl-label']/input")
  private static WebElement packsToShip;

  @FindBy(how = How.XPATH, using = "//li[@id='V']/span[@class='tpl-source']/span[2]")
  private static WebElement packsToShipSource;

  @FindBy(how = How.XPATH, using = "//li[@id='T']/span[@class='tpl-label']/input")
  private static WebElement pricePerPack;

  @FindBy(how = How.XPATH, using = "//li[@id='T']/span[@class='tpl-source']/span[2]")
  private static WebElement pricePerPackSource;

  @FindBy(how = How.XPATH, using = "//li[@id='Q']/span[@class='tpl-label']/input")
  private static WebElement totalCost;

  @FindBy(how = How.XPATH, using = "//li[@id='Q']/span[@class='tpl-source']/span[2]")
  private static WebElement totalCostSource;

  @FindBy(how = How.XPATH, using = "//li[@id='L']/span[@class='tpl-label']/input")
  private static WebElement remarks;

  @FindBy(how = How.XPATH, using = "//li[@id='L']/span[@class='tpl-source']/span[2]")
  private static WebElement remarksSource;

  @FindBy(how = How.XPATH, using = "//li[@id='K']/span[@class='tpl-label']/input")
  private static WebElement approvedQuantity;

  @FindBy(how = How.XPATH, using = "//li[@id='K']/span[@class='tpl-source']/span[2]")
  private static WebElement approvedQuantitySource;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Turn OFF')]")
  private static WebElement turnOffButton;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Turn ON')]")
  private static WebElement turnOnButton;

  @FindBy(how = How.XPATH, using = "//div[@ng-show='arithmeticValidationMessageShown']/div/div/strong")
  private static WebElement OffOnIndicator;

  @FindBy(how = How.XPATH, using = "//div[@id='saveErrorMsgDiv']")
  private static WebElement errorMessageDiv;

  private String TEMPLATE_SUCCESS_MESSAGE = "Template saved successfully!";


  public TemplateConfigPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }


  public void verifySourceForTotalConsumedQuantity() {
    Select select = new Select(dropDownTotalConsumedQuantity);
    SeleneseTestNgHelper.assertEquals(select.getFirstSelectedOption().getText(), "User Input");
  }

  public void verifySourceForStockOnHand() {
    Select select = new Select(stockInHandDropDown);
    SeleneseTestNgHelper.assertEquals(select.getFirstSelectedOption().getText(), "User Input");
  }

  public void verifyArithmeticValidations() {
    //verifying If C and E both are user Input then Admin can choose to switch Arithmetic Validation ON/OFF
    testWebDriver.waitForElementToAppear(SaveButton);
    testWebDriver.selectByVisibleText(dropDownTotalConsumedQuantity, "Calculated");
    testWebDriver.sleep(250);
    testWebDriver.selectByVisibleText(dropDownTotalConsumedQuantity, "User Input");
    SeleneseTestNgHelper.assertTrue("Option to choose to switch Airthmetic Validation ON/OFF is not available", turnOffButton.isDisplayed());
    turnOffButton.click();
    testWebDriver.sleep(250);
    SeleneseTestNgHelper.assertTrue("Should show 'Turn ON' on button", turnOnButton.getText().equalsIgnoreCase("Turn ON"));
    SeleneseTestNgHelper.assertEquals("OFF", OffOnIndicator.getText().trim());
    testWebDriver.sleep(250);
    turnOnButton.click();
    testWebDriver.sleep(250);
    SeleneseTestNgHelper.assertTrue("Should show 'Turn OFF' on button", turnOffButton.getText().equalsIgnoreCase("Turn OFF"));
    SeleneseTestNgHelper.assertEquals("ON", OffOnIndicator.getText().trim());
    testWebDriver.selectByVisibleText(stockInHandDropDown, "Calculated");
    SeleneseTestNgHelper.assertFalse("Option to choose to switch Airthmetic Validation ON/OFF should not be visible", turnOffButton.isDisplayed());

    //if "C" is derived then "E" is must and Vice versa
    testWebDriver.selectByVisibleText(dropDownTotalConsumedQuantity, "Calculated");
    stockOnHandCheckBox.click();
    if (!stockOnHandCheckBox.isSelected())
      testWebDriver.click(stockOnHandCheckBox);
    SaveButton.click();
    testWebDriver.sleep(250);
    SeleneseTestNgHelper.assertTrue("Error message not displaying", errorMessageDiv.isDisplayed());
    SeleneseTestNgHelper.assertTrue("Error message not displaying", totalConsumedQtyErrorMessage.isDisplayed());
    SeleneseTestNgHelper.assertTrue("Error message not displaying", stockOnHandQtyErrorMessage.isDisplayed());
  }

  public void verifyColumnLabels() {
    testWebDriver.waitForElementToAppear(SaveButton);
    SeleneseTestNgHelper.assertTrue("product code is not showing up", productCode.isDisplayed());
    SeleneseTestNgHelper.assertTrue("product name is not showing up", productName.isDisplayed());
    SeleneseTestNgHelper.assertTrue("unit of issue is not showing up", unitOfIssue.isDisplayed());
    SeleneseTestNgHelper.assertTrue("beginning balance is not showing up", beginningBalance.isDisplayed());
    SeleneseTestNgHelper.assertTrue("totalReceivedQuantity is not showing up", totalReceivedQuantity.isDisplayed());
    SeleneseTestNgHelper.assertTrue("totalConsumedQuantity is not showing up", totalConsumedQuantity.isDisplayed());
    SeleneseTestNgHelper.assertTrue("lossesAndAdj is not showing up", lossesAndAdj.isDisplayed());
    SeleneseTestNgHelper.assertTrue("stockOnHand is not showing up", stockOnHand.isDisplayed());
    SeleneseTestNgHelper.assertTrue("newPatients is not showing up", newPatients.isDisplayed());
    SeleneseTestNgHelper.assertTrue("stockOutDays is not showing up", stockOutDays.isDisplayed());
    SeleneseTestNgHelper.assertTrue("adjustedTotalConsumption is not showing up", adjustedTotalConsumption.isDisplayed());
    SeleneseTestNgHelper.assertTrue("maxStockQuantity is not showing up", maxStockQuantity.isDisplayed());
    SeleneseTestNgHelper.assertTrue("calculatedOrderQuantity is not showing up", calculatedOrderQuantity.isDisplayed());
    SeleneseTestNgHelper.assertTrue("requestedQuantity is not showing up", requestedQuantity.isDisplayed());
    SeleneseTestNgHelper.assertTrue("requestedQuantityExplanation is not showing up", requestedQuantityExplanation.isDisplayed());
    SeleneseTestNgHelper.assertTrue("packsToShip is not showing up", packsToShip.isDisplayed());
    SeleneseTestNgHelper.assertTrue("pricePerPack is not showing up", pricePerPack.isDisplayed());
    SeleneseTestNgHelper.assertTrue("totalCost is not showing up", totalCost.isDisplayed());
    SeleneseTestNgHelper.assertTrue("remarks is not showing up", remarks.isDisplayed());
    SeleneseTestNgHelper.assertTrue("approvedQuantity is not showing up", approvedQuantity.isDisplayed());
  }

  public void verifyColumnSource() {
    testWebDriver.waitForElementToAppear(SaveButton);
    SeleneseTestNgHelper.assertEquals(productCodeSource.getText().trim(), "Reference Data");
    SeleneseTestNgHelper.assertEquals(productNameSource.getText().trim(), "Reference Data");
    SeleneseTestNgHelper.assertEquals(unitOfIssueSource.getText().trim(), "Reference Data");
    SeleneseTestNgHelper.assertEquals(beginningBalanceSource.getText().trim(), "User Input");
    SeleneseTestNgHelper.assertEquals(totalReceivedQuantitySource.getText().trim(), "User Input");
    SeleneseTestNgHelper.assertEquals(totalConsumedQuantitySource.getText().trim(), "User Input");
    SeleneseTestNgHelper.assertEquals(lossesAndAdjSource.getText().trim(), "User Input");
    SeleneseTestNgHelper.assertEquals(stockOnHandSource.getText().trim(), "User Input");
    SeleneseTestNgHelper.assertEquals(newPatientsSource.getText().trim(), "User Input");
    SeleneseTestNgHelper.assertEquals(stockOutDaysSource.getText().trim(), "User Input");
    SeleneseTestNgHelper.assertEquals(adjustedTotalConsumptionSource.getText().trim(), "Calculated");
    SeleneseTestNgHelper.assertEquals(maxStockQuantitySource.getText().trim(), "Calculated");
    SeleneseTestNgHelper.assertEquals(calculatedOrderQuantitySource.getText().trim(), "Calculated");
    SeleneseTestNgHelper.assertEquals(requestedQuantitySource.getText().trim(), "User Input");
    SeleneseTestNgHelper.assertEquals(requestedQuantityExplanationSource.getText().trim(), "User Input");
    SeleneseTestNgHelper.assertEquals(packsToShipSource.getText().trim(), "Calculated");
    SeleneseTestNgHelper.assertEquals(pricePerPackSource.getText().trim(), "Reference Data");
    SeleneseTestNgHelper.assertEquals(totalCostSource.getText().trim(), "Calculated");
    SeleneseTestNgHelper.assertEquals(remarksSource.getText().trim(), "User Input");
    SeleneseTestNgHelper.assertEquals(approvedQuantitySource.getText().trim(), "User Input");
  }

  public void configureTemplate() {
    String message = null;

    testWebDriver.waitForElementToAppear(SaveButton);
    verifySourceForTotalConsumedQuantity();
    verifySourceForStockOnHand();
    testWebDriver.selectByVisibleText(stockInHandDropDown, "Calculated");
    testWebDriver.sleep(1500);
    SaveButton.click();

    testWebDriver.sleep(2000);
    if (saveSuccessMsg.isDisplayed()) {
      message = testWebDriver.getText(saveSuccessMsg);
    } else {
      message = testWebDriver.getText(saveErrorMsgDiv);
    }

    SeleneseTestNgHelper.assertEquals(message, TEMPLATE_SUCCESS_MESSAGE);

  }
}