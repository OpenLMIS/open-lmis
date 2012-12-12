package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class TemplateConfigPage extends Page {

    @FindBy(how = How.LINK_TEXT, using = "Template Configuration")
    private static WebElement TemplateConfigTab;

    @FindBy(how = How.LINK_TEXT, using = "R & R")
    private static WebElement ConfigureTemplateSelectProgramPage;

    @FindBy(how = How.LINK_TEXT, using = "Logout")
    private static WebElement logoutLink;

    @FindBy(how = How.ID, using = "selectProgram")
    private static WebElement ProgramDropDown;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'Next')]")
    private static WebElement NextButton;

    @FindBy(how = How.XPATH, using = "//input[@value='Save']")
    private static WebElement SaveButton;

    @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-show='message']")
    private static WebElement saveSuccessMsg;

    @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv' and @ng-show='error']")
    private static WebElement saveErrorMsgDiv;



    private String TEMPLATE_SUCCESS_MESSAGE = "Template saved successfully!";



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
        testWebDriver.selectByVisibleText(ProgramDropDown, programme);
        NextButton.click();
    }

    public void editProductCode(String productCode){

    }

    public void excludeRemarks(){

    }

    public void configureTemplate(){
        String message=null;
    testWebDriver.waitForElementToAppear(SaveButton);
    SaveButton.click();


        testWebDriver.sleep(1500);
        if(saveSuccessMsg.isDisplayed())
        {
            message= testWebDriver.getText(saveSuccessMsg);
        }
        else
        {
            message= testWebDriver.getText(saveErrorMsgDiv);
        }

    SeleneseTestNgHelper.assertEquals(message, TEMPLATE_SUCCESS_MESSAGE);

    }
}