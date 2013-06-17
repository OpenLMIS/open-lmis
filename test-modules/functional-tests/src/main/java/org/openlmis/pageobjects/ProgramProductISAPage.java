/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class ProgramProductISAPage extends RequisitionPage {

    @FindBy(how = ID, using = "program")
    private static WebElement selectProgramSelectBox;

    @FindBy(how = ID, using = "searchProduct")
    private static WebElement searchProductTextBox;

    @FindBy(how = XPATH, using = "//div[@id='wrap']/div[@class='content']/div/div[@class='ng-scope']/div[2]/div[5][@class='row-fluid list-row ng-scope']/div[3][@class='span2 offset2']/input[@class='btn btn-small btn-primary']")
    private static WebElement editFormulaButton;

    @FindBy(how = ID, using = "who-ratio")
    private static WebElement ratioTextBox;

    @FindBy(how = ID, using = "doses-per-year")
    private static WebElement dosesPerYearTextBox;

    @FindBy(how = ID, using = "wastage-rate")
    private static WebElement wastageRateTextBox;

    @FindBy(how = ID, using = "buffer-percentage")
    private static WebElement bufferPercentageTextBox;

    @FindBy(how = ID, using = "adjustment-value")
    private static WebElement adjustmentValueTextBox;

    @FindBy(how = ID, using = "minimum-value")
    private static WebElement minimumValueTextBox;

    @FindBy(how = XPATH, using = "//div[@id='ISA-population' and @class='calculatedAmount']/span[3][@class='ng-binding']")
    private static WebElement isaValueLabel;

    @FindBy(how = XPATH, using = "//div[@id='ISA-population' and @class='calculatedAmount']/input[@class='ng-pristine ng-valid']")
    private static WebElement populationTextBox;

    @FindBy(how = XPATH, using = "//div[@id='programProductISA' and @class='modal in']/div[3][@class='modal-footer']/input[1][@class='btn btn-primary save-button']")
    private static WebElement programProductISASaveButton;

    @FindBy(how = XPATH, using = "//div[@id='programProductISA' and @class='modal']/div[3][@class='modal-footer']/input[2][@class='btn btn-cancel']")
    private static WebElement programProductISACancelButton;


    @FindBy(how = ID, using = "saveSuccessMsgDiv")
    private static WebElement saveSuccessMsgDiv;


    public ProgramProductISAPage(TestWebDriver driver) throws IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(10);

    }

    public void fillProgramProductISA(String ratio, String dosesPerYear, String wastage, String bufferPercentage, String adjustmentValue, String minimumValue) {
        testWebDriver.waitForElementToAppear(ratioTextBox);
        ratioTextBox.sendKeys(ratio);
        dosesPerYearTextBox.sendKeys(dosesPerYear);
        wastageRateTextBox.sendKeys(wastage);
        bufferPercentageTextBox.sendKeys(bufferPercentage);
        adjustmentValueTextBox.sendKeys(adjustmentValue);
        minimumValueTextBox.sendKeys(minimumValue);
    }

    public String fillPopulation(String population) {
        populationTextBox.sendKeys(population);
        return testWebDriver.getText(isaValueLabel);
    }

    public void verifySuccessMessageDiv() {
        SeleneseTestNgHelper.assertTrue("Save success message should show up", saveSuccessMsgDiv.isDisplayed());
    }

    public void selectProgram(String program) {
        testWebDriver.waitForElementToAppear(selectProgramSelectBox);
        testWebDriver.selectByVisibleText(selectProgramSelectBox, program);
    }

    public void editFormula() {
        editFormulaButton.click();
    }

    public void saveISA() {
        programProductISASaveButton.click();
    }

    public void cancelISA() {
        programProductISACancelButton.click();
    }

    public void searchProduct(String product) {
        searchProductTextBox.sendKeys(product);
    }
}