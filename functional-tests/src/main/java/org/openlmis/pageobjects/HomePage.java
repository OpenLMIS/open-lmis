package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;


public class HomePage extends Page {

    @FindBy(how = How.XPATH, using = "//a[@id=\"username\"]")
    private static WebElement usernameDisplay;

    @FindBy(how = How.LINK_TEXT, using = "Logout")
    private static WebElement logoutLink;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'Manage Facilities')]")
    private static WebElement manageFacilityMenuItem;

    @FindBy(how = How.LINK_TEXT, using = "Create")
    private static WebElement createFacility;

    @FindBy(how = How.XPATH, using = "//div[@id='wrap']/div/div/div/h2")
    private static WebElement addNewFacilityHeader;

    @FindBy(how = How.LINK_TEXT, using = "Template Configuration")
    private static WebElement TemplateConfigTab;

    @FindBy(how = How.LINK_TEXT, using = "R & R")
    private static WebElement ConfigureTemplateSelectProgramPage;

    @FindBy(how = How.ID, using = "selectProgram")
    private static WebElement ProgramDropDown;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'Next')]")
    private static WebElement NextButton;

    @FindBy(how = How.LINK_TEXT, using = "Requisitions")
    private static WebElement requisitionsLink;

    @FindBy(how = How.XPATH, using = "//a[contains(@href,'/public/pages/logistics/rnr/create.html')]")
    private static WebElement createRnRLink;

    @FindBy(how = How.XPATH, using = "//select[@ng-change='loadPrograms()']")
    private static WebElement facilityDropDown;

    @FindBy(how = How.XPATH, using = "//option[@value='0']")
    private static WebElement programDropDown;

    @FindBy(how = How.XPATH, using = "//select[2]")
    private static WebElement programDropDownSelect;


    @FindBy(how = How.XPATH, using = "//input[@value='Next']")
    private static WebElement nextButton;

    @FindBy(how = How.LINK_TEXT, using = "Manage Facilities")
    private static WebElement manageFacilityLink;

    @FindBy(how = How.LINK_TEXT, using = "Search")
    private static WebElement searchLink;

    public HomePage(TestWebDriver driver) throws  IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
        SeleneseTestNgHelper.assertTrue(usernameDisplay.isDisplayed());
    }

    public LoginPage logout() throws IOException{
        testWebDriver.waitForElementToAppear(usernameDisplay);
        testWebDriver.mouseOver(usernameDisplay);
        usernameDisplay.click();
        testWebDriver.waitForElementToAppear(logoutLink);
        logoutLink.click();
        return new LoginPage(testWebDriver);
    }

    public boolean verifyWelcomeMessage(String user) {
        testWebDriver.waitForTextToAppear("Welcome " + user);
        return testWebDriver.getPageSource().contains("Welcome " + user);
    }

    public CreateFacilityPage navigateCreateFacility() throws IOException {
        SeleneseTestNgHelper.assertTrue(manageFacilityMenuItem.isDisplayed());
        testWebDriver.waitForElementToAppear(manageFacilityMenuItem);
        testWebDriver.click(manageFacilityMenuItem);
        manageFacilityMenuItem.click();
        testWebDriver.waitForElementToAppear(createFacility);
        createFacility.click();
        testWebDriver.waitForElementToAppear(addNewFacilityHeader);
        return new CreateFacilityPage(testWebDriver);
    }

    public TemplateConfigPage selectProgramToConfigTemplate(String programme) {
        testWebDriver.waitForElementToAppear(TemplateConfigTab);
        testWebDriver.mouseOver(TemplateConfigTab);
        TemplateConfigTab.click();
        testWebDriver.waitForElementToAppear(ConfigureTemplateSelectProgramPage);
        ConfigureTemplateSelectProgramPage.click();
        testWebDriver.selectByVisibleText(ProgramDropDown, programme);
        NextButton.click();
        return new TemplateConfigPage(testWebDriver);
    }

    public InitiateRnRPage navigateAndInitiateRnr(String FCstring, String program) throws IOException {
        testWebDriver.waitForElementToAppear(requisitionsLink);
        requisitionsLink.click();
        testWebDriver.waitForElementToAppear(createRnRLink);
        createRnRLink.click();
        testWebDriver.waitForElementToAppear(facilityDropDown);
        testWebDriver.selectByVisibleText(facilityDropDown, "FCcode"+FCstring+"-FCname"+FCstring);
        testWebDriver.waitForElementToAppear(programDropDown);
        programDropDown.click();
        testWebDriver.selectByVisibleText(programDropDownSelect,program);
        nextButton.click();
        return new InitiateRnRPage(testWebDriver);
    }

    public DeleteFacilityPage navigateSearchFacility() throws IOException {
        testWebDriver.waitForElementToAppear(manageFacilityLink);
        manageFacilityLink.click();
        testWebDriver.waitForElementToAppear(searchLink);
        searchLink.click();
        return new DeleteFacilityPage(testWebDriver);
    }

}