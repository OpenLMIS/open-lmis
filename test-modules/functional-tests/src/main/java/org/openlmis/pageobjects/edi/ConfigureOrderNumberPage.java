package org.openlmis.pageobjects.edi;

import org.openlmis.pageobjects.Page;

  import org.openlmis.UiUtils.TestWebDriver;
  import org.openlmis.pageobjects.Page;
  import org.openqa.selenium.WebElement;
  import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

  import static com.thoughtworks.selenium.SeleneseTestBase.*;
  import static org.openqa.selenium.support.How.ID;
  import static org.openqa.selenium.support.How.XPATH;
  import static org.openqa.selenium.support.PageFactory.initElements;


public class ConfigureOrderNumberPage extends Page {

  @FindBy(how = ID, using = "includeHeadersCheckbox")
  private static WebElement includeHeaders = null;

  @FindBy(how = ID, using = "saveOrderNumberConfig")
  private WebElement saveButton = null;

  @FindBy(how = ID, using = "orderPrefix")
  private WebElement setOrderNumberPrefix = null;

  public ConfigureOrderNumberPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(includeHeaders);
  }

  public void checkIncludeHeader() {
    testWebDriver.waitForElementToAppear(includeHeaders);
    if (!includeHeaders.isSelected())
      includeHeaders.click();
  }

  public void setOrderNumberPrefix(String value) {
    testWebDriver.waitForElementToAppear(setOrderNumberPrefix);
    sendKeys(setOrderNumberPrefix, value);
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }
}
