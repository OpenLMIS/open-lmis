package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openqa.selenium.support.How.*;
import static org.testng.AssertJUnit.assertEquals;


public class ManageGeographicZonesPage extends Page {

  @FindBy(how = ID, using = "geographicZonesMenu")
  private static WebElement geographicZoneMenu = null;

  @FindBy(how = ID, using = "searchGeoZone")
  private static WebElement searchGeoZoneTextField = null;

  @FindBy(how = ID, using = "geoZoneTab")
  private static WebElement geoZoneTab = null;

  @FindBy(how = ID, using = "searchOptionButton")
  private static WebElement searchOptionButton = null;

  @FindBy(how = ID, using = "add-new-geo-zone")
  private static WebElement addNewButton = null;

  @FindBy(how = ID, using = "addNewGeoZoneHeader")
  private static WebElement addNewGeoZoneHeader = null;

  @FindBy(how = ID, using = "code")
  private static WebElement codeTextField = null;

  @FindBy(how = ID, using = "name")
  private static WebElement nameTextField = null;

  @FindBy(how = ID, using = "levelCode")
  private static WebElement levelCodeDropDown = null;

  @FindBy(how = ID, using = "catchment-population")
  private static WebElement catchmentPopulationTextField = null;

  @FindBy(how = ID, using = "latitude")
  private static WebElement latitudeTextField = null;

  @FindBy(how = ID, using = "longitude")
  private static WebElement longitudeTextField = null;

  @FindBy(how = ID, using = "saveButton")
  private static WebElement saveButton = null;

  @FindBy(how = ID, using = "cancelButton")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement successMsgDiv = null;

  @FindBy(how = XPATH, using = "//*[@id='geoZoneName']/a")
  private static WebElement firstElement = null;

  @FindBy(how = ID, using = "selectParentGeoZone")
  private static WebElement parentDropDown = null;

  @FindBy(how = ID, using = "closeButton")
  private static WebElement closeButton = null;

  @FindBy(how = ID, using = "searchIcon")
  private static WebElement searchIcon = null;

  private String searchResultTable = "searchResultTable";

  private String geoZoneList = "geoZoneList";

  private static ManageGeographicZonesPage instance;

  public static ManageGeographicZonesPage getInstance(TestWebDriver testWebDriver) {
    if (instance == null) {
      instance = PageObjectFactory.getManageGeographicZonesPage(testWebDriver);
    }
    return instance;
  }

  public ManageGeographicZonesPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public void searchGeoZoneUsingGeoZoneName(String geoZoneName) {
    testWebDriver.waitForElementToAppear(searchGeoZoneTextField);
    sendKeys(searchGeoZoneTextField, geoZoneName);
    testWebDriver.click(searchIcon);
  }

  public void goToGeoZoneTab() {
    testWebDriver.waitForElementToAppear(geoZoneTab);
    testWebDriver.click(geoZoneTab);
  }

  public void changeSearchOption() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    testWebDriver.click(searchOptionButton);
    testWebDriver.findElement(By.linkText("Geographic zone parent")).click();
  }

  public void addNewGeoZone(String name, String code, String catchment_population, String latitude, String longitude, String level, String parent) {
    testWebDriver.click(addNewButton);
    nameTextField.sendKeys(name);
    codeTextField.sendKeys(code);
    catchmentPopulationTextField.sendKeys(catchment_population);
    latitudeTextField.sendKeys(latitude);
    longitudeTextField.sendKeys(longitude);
    testWebDriver.selectByVisibleText(levelCodeDropDown, level);
    testWebDriver.sleep(1000);
    testWebDriver.selectByVisibleText(parentDropDown, parent);
  }

  public void searchGeoZoneUsingGeoZoneParentName(String geoZoneParentName) {
    testWebDriver.waitForElementToAppear((searchGeoZoneTextField));
    sendKeys(searchGeoZoneTextField, geoZoneParentName);
    testWebDriver.click(searchIcon);
  }

  public void verifyNumberOfItemsPerPage(int numberOfItemsPerPage){
    assertEquals(numberOfItemsPerPage, testWebDriver.getElementsSizeByXpath("//*[@id='searchResult']"));
  }

  public void verifySuccessMessage() {
    testWebDriver.waitForElementToAppear(successMsgDiv);
    assertTrue(successMsgDiv.isDisplayed());
  }

  public void clickOnSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void clickOnCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public void verifySearchResultTable(){
    testWebDriver.getElementByName(searchResultTable);
  }

  public void verifySearchResultCounter(String message){
    testWebDriver.getElementByName(searchResultTable).getText().equalsIgnoreCase(message);
  }

  public void clickOnCrossButton(){
  testWebDriver.click(closeButton);
  }

  public void verifySearchResultBody(){
    testWebDriver.getElementById(geoZoneList);
  }


  public void clickOnFirstElement() {
    testWebDriver.waitForElementToAppear(firstElement);
    testWebDriver.waitForPageToLoad();
    firstElement.click();
  }

  public String getGeoZoneName(){
    WebElement geoZoneName =  testWebDriver.getElementById("geoZoneName");
    testWebDriver.waitForElementToAppear(geoZoneName);
    return geoZoneName.getText();
  }

  public String getLevelName(int rowNumber) {
    WebElement levelName = testWebDriver.getElementById("levelName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(levelName);
    return levelName.getText();
  }

  public String getParentName(int rowNumber) {
    WebElement parentName = testWebDriver.getElementById("parentName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(parentName);
    return parentName.getText();
  }

  public void editAlreadyExistingGeoZone(String name, String code, String catchmentPopulation, String latitude, String longitude) {
    sendKeys(nameTextField, name);
    sendKeys(codeTextField, code);
    sendKeys(catchmentPopulationTextField, catchmentPopulation);
    sendKeys(latitudeTextField, latitude);
    sendKeys(longitudeTextField, longitude);
  }

  public void clickOnViewHereLink()
  {
    testWebDriver.getElementById("viewHere").click();
  }
}
