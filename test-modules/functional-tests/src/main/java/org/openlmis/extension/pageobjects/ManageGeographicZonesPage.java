/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.extension.pageobjects;

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.Page;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import java.io.IOException;

public class ManageGeographicZonesPage extends Page {

    @FindBy(how=How.XPATH,using="//input[@value='Save']")
    private static WebElement saveButton;

    @FindBy(how=How.ID, using="code")
    private static WebElement geographicZoneCodeField;

    @FindBy(how=How.ID, using="name")
    private static WebElement geographicZoneNameField;

    @FindBy(how=How.XPATH, using="//select[@ng-model='geographizZone.level.id']")
    private static WebElement geographicZoneLevelField;

    @FindBy(how=How.XPATH, using="//select[@ng-model='geographicZone.parent.id']")
    private static WebElement geographicZoneParentField;


    @FindBy(how= How.ID, using="geographic-zone-add-new")
    private static WebElement addGeographicZoneButton;

    @FindBy(how=How.XPATH, using = "//div[@id='saveSuccessMessageDiv']/span")
    private static WebElement saveSuccessMessage;

    @FindBy(how=How.LINK_TEXT, using ="View Here")
    private static WebElement viewHereLink;


    public ManageGeographicZonesPage(TestWebDriver driver) throws IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(),10),this);
        testWebDriver.setImplicitWait(10);
    }

    public void EnterAndVerifyNewGeographicZone(String code, String name, String levelID, String parentID){
        testWebDriver.waitForElementToAppear(addGeographicZoneButton);
        addGeographicZoneButton.click();
        testWebDriver.waitForElementToAppear(geographicZoneCodeField);

        geographicZoneCodeField.clear();
        geographicZoneCodeField.sendKeys(code);
        geographicZoneNameField.clear();
        geographicZoneNameField.sendKeys(name);
        testWebDriver.selectByValue(geographicZoneLevelField, levelID);
        testWebDriver.selectByValue(geographicZoneParentField, parentID);

        testWebDriver.waitForElementToAppear(saveButton);
        saveButton.click();

        testWebDriver.sleep(1500);
        testWebDriver.waitForElementToAppear(viewHereLink);

        SeleneseTestNgHelper.assertTrue("Geographic zone '" + name + "' has been successfully created message is not showing up.",saveSuccessMessage.isDisplayed());
    }

    public void EnterAGeographicZoneAndConfirmEditWorks(String code, String name, String name_Changed, String levelID, String parentID){
        testWebDriver.waitForElementToAppear(addGeographicZoneButton);
        addGeographicZoneButton.click();
        testWebDriver.waitForElementToAppear(geographicZoneCodeField);

        geographicZoneCodeField.clear();
        geographicZoneCodeField.sendKeys(code);
        geographicZoneNameField.clear();
        geographicZoneNameField.sendKeys(name);
        testWebDriver.selectByValue(geographicZoneLevelField, levelID);
        testWebDriver.selectByValue(geographicZoneParentField, parentID);

        testWebDriver.waitForElementToAppear(saveButton);
        saveButton.click();

        testWebDriver.sleep(1500);
        testWebDriver.waitForElementToAppear(viewHereLink);

        viewHereLink.click();


        testWebDriver.waitForElementToAppear(geographicZoneNameField);
        geographicZoneNameField.clear();
        geographicZoneNameField.sendKeys(name_Changed);
        testWebDriver.waitForElementToAppear(saveButton);
        saveButton.click();

        testWebDriver.sleep(1500);
        testWebDriver.waitForElementToAppear(viewHereLink);

        viewHereLink.click();

        testWebDriver.waitForElementToAppear(geographicZoneNameField);
        SeleneseTestNgHelper.assertTrue("Editing the geographic zone isn't working.", geographicZoneNameField.getText().equals(name_Changed));

    }



}
