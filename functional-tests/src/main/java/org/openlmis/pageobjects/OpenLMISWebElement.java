package org.openlmis.pageobjects;

import org.openqa.selenium.*;


public class OpenLMISWebElement {

    protected WebElement webElement;

    public OpenLMISWebElement(WebElement webElement) {
        this.webElement = webElement;
    }


    public void click() {
        try {
            webElement.click();
        } catch (Exception e) {
            webElement.click();
        }
    }


    public void submit() {
        webElement.sendKeys(Keys.RETURN);
    }


    public void sendKeys(CharSequence... charSequences) {
        click();
        clear();
        webElement.sendKeys(charSequences);
    }


    public void clear() {
        webElement.clear();
    }



}
