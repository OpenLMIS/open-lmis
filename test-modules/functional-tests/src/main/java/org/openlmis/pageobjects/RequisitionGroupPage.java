package org.openlmis.pageobjects;

import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.NoSuchElementException;

import static org.openqa.selenium.support.How.ID;

public class RequisitionGroupPage extends Page {

  @FindBy(how = ID, using = "searchOptionButton")
  private static WebElement searchOptionButton = null;

  @FindBy(how = ID, using = "searchRequisitionGroup")
  private static WebElement searchRequisitionGroupParameter = null;

  @FindBy(how = ID, using = "requisitionGroupAddNew")
  private static WebElement requisitionGroupAddNew = null;

  @FindBy(how = ID, using = "searchOption0")
  private static WebElement searchOption1 = null;

  @FindBy(how = ID, using = "searchOption1")
  private static WebElement searchOption2 = null;

  @FindBy(how = ID, using = "searchRequisitionGroupLabel")
  private static WebElement searchRequisitionGroupLabel = null;

  @FindBy(how = ID, using = "noResultMessage")
  private static WebElement noResultMessage = null;

  @FindBy(how = ID, using = "oneResultMessage")
  private static WebElement oneResultMessage = null;

  @FindBy(how = ID, using = "nResultsMessage")
  private static WebElement nResultsMessage = null;

  @FindBy(how = ID, using = "requisitionGroupHeader")
  private static WebElement requisitionGroupHeader = null;

  @FindBy(how = ID, using = "codeHeader")
  private static WebElement codeHeader = null;

  @FindBy(how = ID, using = "supervisoryNodeHeader")
  private static WebElement supervisoryNodeHeader = null;

  @FindBy(how = ID, using = "facilityCount")
  private static WebElement facilityCount = null;

  @FindBy(how = ID, using = "closeButton")
  private static WebElement closeSearchResultsButton = null;

  @FindBy(how = ID, using = "searchIcon")
  private static WebElement searchIcon = null;

  @FindBy(how = ID, using = "requisitionGroupSearchResults")
  private static WebElement requisitionGroupSearchResult = null;



  public RequisitionGroupPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public String getSearchRequisitionGroupLabel() {
    testWebDriver.waitForElementToAppear(searchRequisitionGroupLabel);
    return searchRequisitionGroupLabel.getText();
  }

  public boolean isAddNewButtonDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(requisitionGroupAddNew);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    testWebDriver.waitForElementToAppear(requisitionGroupAddNew);
    return requisitionGroupAddNew.isDisplayed();
  }

  public void clickAddNewButton() {
    testWebDriver.waitForElementToAppear(requisitionGroupAddNew);
    requisitionGroupAddNew.click();
  }

  public void clickSearchOptionButton() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    searchOptionButton.click();
  }

  public String getSelectedSearchOption() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    return searchOptionButton.getText();
  }

  public void selectRequisitionGroupAsSearchOption() {
    testWebDriver.waitForElementToAppear(searchOption1);
    searchOption1.click();
  }

  public void selectSupervisoryNodeAsSearchOption() {
    testWebDriver.waitForElementToAppear(searchOption2);
    searchOption2.click();
  }

  public void enterSearchParameter(String searchParameter) {
    testWebDriver.waitForElementToAppear(searchRequisitionGroupParameter);
    sendKeys(searchRequisitionGroupParameter, searchParameter);
  }

  public boolean isNoResultMessageDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(noResultMessage);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return noResultMessage.isDisplayed();
  }

  public boolean isOneResultMessageDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(oneResultMessage);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return oneResultMessage.isDisplayed();
  }

  public boolean isResultDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(requisitionGroupSearchResult);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return requisitionGroupSearchResult.isDisplayed();
  }

  public String getNResultsMessage() {
    testWebDriver.waitForElementToAppear(nResultsMessage);
    return nResultsMessage.getText();
  }

  public void closeSearchResults() {
    testWebDriver.waitForElementToAppear(closeSearchResultsButton);
    closeSearchResultsButton.click();
  }

  public boolean isRequisitionGroupHeaderDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(requisitionGroupHeader);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return requisitionGroupHeader.isDisplayed();
  }

  public String getRequisitionGroupHeader() {
    testWebDriver.waitForElementToAppear(requisitionGroupHeader);
    return requisitionGroupHeader.getText();
  }

  public String getCodeHeader() {
    testWebDriver.waitForElementToAppear(codeHeader);
    return codeHeader.getText();
  }

  public String getFacilityCountHeader() {
    testWebDriver.waitForElementToAppear(facilityCount);
    return facilityCount.getText();
  }

  public String getSupervisoryNodeHeader() {
    testWebDriver.waitForElementToAppear(supervisoryNodeHeader);
    return supervisoryNodeHeader.getText();
  }

  public String getRequisitionGroupName(int rowNumber) {
    WebElement name = testWebDriver.getElementById("name" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(name);
    return name.getText();
  }

  public String getRequisitionGroupCode(int rowNumber) {
    WebElement code = testWebDriver.getElementById("code" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(code);
    return code.getText();
  }

  public String getSupervisoryNodeName(int rowNumber) {
    WebElement facility = testWebDriver.getElementById("supervisoryName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(facility);
    return facility.getText();
  }

  public String getFacilityCount(int rowNumber) {
    WebElement parent = testWebDriver.getElementById("facilityCount" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(parent);
    return parent.getText();
  }

  public void clickSearchIcon() {
    testWebDriver.waitForElementToAppear(searchIcon);
    searchIcon.click();
  }

  public boolean isSearchIconDisplayed() {
    testWebDriver.waitForElementToAppear(searchIcon);
    return searchIcon.isDisplayed();
  }
}
