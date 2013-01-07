package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UploadPage extends Page {

    String uploadFilePath=null;

    @FindBy(how = How.XPATH, using = "//input[@value='Upload']")
    private static WebElement uploadButton;

    @FindBy(how = How.XPATH, using = "//input[@value='Choose CSV File to upload']")
    private static WebElement setCsvPath;

    @FindBy(how = How.XPATH, using = "//select[@id='model']")
    private static WebElement uploadDropDown;

    @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
    private static WebElement saveSuccessMsgDiv;

    public UploadPage(TestWebDriver driver) throws IOException {
        super(driver);

        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
        SeleneseTestNgHelper.assertTrue(uploadButton.isDisplayed());
        SeleneseTestNgHelper.assertTrue(uploadDropDown.isDisplayed());
    }

    public void selectUploadType(String uploadType){
        testWebDriver.waitForElementToAppear(uploadDropDown);
        testWebDriver.selectByVisibleText(uploadDropDown,uploadType);
    }

    public void uploadFile(String fileName){
        uploadFilePath = System.getProperty("user.dir") + "/src/main/resources/"+fileName;
        setCsvPath.sendKeys(uploadFilePath);
        uploadButton.click();

    }

    public void uploadFacilities() throws FileNotFoundException {

        selectUploadType("Facilities");
        uploadFile("facilities.csv");
        testWebDriver.sleep(250);
        testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
        SeleneseTestNgHelper.assertTrue("File uploaded successfully Message Not Displayed", saveSuccessMsgDiv.isDisplayed());

    }
}
