package org.openlmis.pageobjects;

import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebDriver;

public abstract class Page {

    public TestWebDriver testWebDriver;

    protected Page(TestWebDriver driver)
    {
        this.testWebDriver=driver;
    }

}
