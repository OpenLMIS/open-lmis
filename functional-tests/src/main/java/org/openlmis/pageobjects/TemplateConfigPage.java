package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.Select;


public class TemplateConfigPage extends Page {

    @FindBy(how = How.LINK_TEXT, using = "Template Configuration")
    private static WebElement TemplateConfigTab;

    @FindBy(how = How.LINK_TEXT, using = "R & R")
    private static WebElement ConfigureTemplateSelectProgramPage;

    @FindBy(how = How.LINK_TEXT, using = "Logout")
    private static WebElement logoutLink;

    @FindBy(how = How.XPATH, using = "//select[@class=\"ng-pristine ng-valid\"]")
    private static WebElement ProgramDropDown;
    Select ProgramDropDownSelect;

    private String BASE_URL = "http://localhost:9090/";

    public TemplateConfigPage(TestWebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
    }


    public void selectProgramToConfigTemplate(String programme) {
        testWebDriver.waitForElementToAppear(TemplateConfigTab);
        testWebDriver.mouseOver(TemplateConfigTab);
        TemplateConfigTab.click();
        testWebDriver.waitForElementToAppear(ConfigureTemplateSelectProgramPage);
        ConfigureTemplateSelectProgramPage.click();
        ProgramDropDownSelect.selectByValue(programme);
      }
}