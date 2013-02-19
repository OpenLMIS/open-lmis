package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.sql.SQLException;


public class UserPage extends Page {

  @FindBy(how = How.ID, using = "userName")
  private static WebElement userNameField;

  @FindBy(how = How.ID, using = "email")
  private static WebElement emailField;

  @FindBy(how = How.ID, using = "firstName")
  private static WebElement firstNameField;

  @FindBy(how = How.ID, using = "lastName")
  private static WebElement lastNameField;

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton;

  @FindBy(how = How.LINK_TEXT, using = "View Here")
  private static WebElement viewHereLink;


  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Add new')]")
  private static WebElement addNewButton;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']/span")
  private static WebElement successMessage;

  @FindBy(how = How.ID, using = "searchFacility")
  private static WebElement searchFacility;

  @FindBy(how = How.XPATH, using = "//a[@ng-click='setSelectedFacility(facility)']")
  private static WebElement selectFacility;

  @FindBy(how = How.XPATH, using = "//select[@ng-model='selectedProgramIdToSupervise']")
  private static WebElement programsToSupervise;

  @FindBy(how = How.XPATH, using = "//select[@ng-model='selectedSupervisoryNodeIdToSupervise']")
  private static WebElement supervisoryNodeToSupervise;

  @FindBy(how = How.XPATH, using = "//select[@ng-model='programSelected']")
  private static WebElement programsMyFacility;


  @FindBy(how = How.XPATH, using = "//div[@class='select2-result-label']/span")
  private static WebElement rolesSelectFieldMyFacility;

  @FindBy(how = How.XPATH, using = "//div[@class='select2-result-label']/span")
  private static WebElement rolesSelectField;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[12]")
  private static WebElement rolesInputFieldMyFacility;


  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[13]")
  private static WebElement rolesInputFieldSecondMyFacility;

  @FindBy(how = How.XPATH, using = "(//input[@type='text'])[15]")
  private static WebElement rolesInputField;


  @FindBy(how = How.XPATH, using = "//div[contains(text(),'Store In-Charge')]")
  private static WebElement storeInChargeOption;

  @FindBy(how = How.XPATH, using = "//div[contains(text(),'Medical-Officer')]")
  private static WebElement medicalOfficerOption;

  @FindBy(how = How.XPATH, using = "//a[@ng-click='addSupervisoryRole()']")
  private static WebElement addButton;

  @FindBy(how = How.XPATH, using = "//a[@ng-click='addHomeFacilityRole()']")
  private static WebElement addButtonMyFacility;


  public UserPage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(25);

  }

  public String enterAndverifyUserDetails(String userName, String email, String firstName, String lastName) throws IOException, SQLException {
    testWebDriver.waitForElementToAppear(addNewButton);
    addNewButton.click();
    testWebDriver.waitForElementToAppear(userNameField);
    userNameField.clear();
    userNameField.sendKeys(userName);
    emailField.clear();
    emailField.sendKeys(email);
    firstNameField.clear();
    firstNameField.sendKeys(firstName);
    lastNameField.clear();
    lastNameField.sendKeys(lastName);

    saveButton.click();
    testWebDriver.waitForElementToAppear(viewHereLink);

    SeleneseTestNgHelper.assertTrue("User '" + firstName + " " + lastName + "' has been successfully created, password link sent on registered Email address message is not getting displayed", successMessage.isDisplayed());
    viewHereLink.click();

    DBWrapper dbWrapper = new DBWrapper();
    String userID = dbWrapper.getUserID(userName);

    return userID;

  }

  public void enterMyFacilityAndMySupervisedFacilityData(String firstName, String lastName, String facilityCode, String program1, String program2, String node, String role) {
    testWebDriver.waitForElementToAppear(searchFacility);
    searchFacility.clear();
    searchFacility.sendKeys(facilityCode);
    selectFacility.click();
    testWebDriver.selectByVisibleText(programsMyFacility, program1);
    rolesInputFieldMyFacility.click();
    rolesInputFieldMyFacility.clear();
    rolesInputFieldMyFacility.sendKeys(role);
    testWebDriver.waitForElementToAppear(rolesSelectFieldMyFacility);
    rolesSelectFieldMyFacility.click();
    addButtonMyFacility.click();
    testWebDriver.sleep(1000);

    testWebDriver.selectByVisibleText(programsMyFacility, program2);
    rolesInputFieldSecondMyFacility.click();
    rolesInputFieldSecondMyFacility.clear();
    rolesInputFieldSecondMyFacility.sendKeys(role);
    testWebDriver.waitForElementToAppear(rolesSelectFieldMyFacility);
    rolesSelectFieldMyFacility.click();
    addButtonMyFacility.click();
    testWebDriver.sleep(1000);


    testWebDriver.selectByVisibleText(programsToSupervise, program1);
    testWebDriver.selectByVisibleText(supervisoryNodeToSupervise, node);
    rolesInputField.click();
    rolesInputField.clear();
    rolesInputField.sendKeys(role);
    testWebDriver.waitForElementToAppear(rolesSelectField);
    rolesSelectField.click();
    addButton.click();
    testWebDriver.sleep(1000);
    saveButton.click();
    testWebDriver.sleep(1000);

    SeleneseTestNgHelper.assertTrue("User '" + firstName + " " + lastName + "' has been successfully updated message is not getting displayed", successMessage.isDisplayed());

  }


}