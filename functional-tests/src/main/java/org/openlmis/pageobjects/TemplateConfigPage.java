package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;


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




    private String TEMPLATE_SUCCESS_MESSAGE = "Template saved successfully!";



    public TemplateConfigPage(TestWebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
    }

    public void editProductCode(String productCode){

    }

    public void excludeRemarks(){

    }

    public void configureTemplate(){
        String message=null;

        testWebDriver.waitForElementToAppear(SaveButton);
        testWebDriver.selectByVisibleText(stockInHandDropDown,"Calculated");
        testWebDriver.sleep(1500);
        SaveButton.click();


        testWebDriver.sleep(2000);
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