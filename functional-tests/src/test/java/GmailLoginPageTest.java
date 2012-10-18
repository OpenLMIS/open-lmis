import org.openlmis.pageobjects.GmailLoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class GmailLoginPageTest {
    private WebDriver driver;

    @BeforeClass
    public void FirefoxWebDriver() {
        driver = new FirefoxDriver();
    }

    @AfterClass
    public void quitDriver() {
        driver.quit();
    }

    @Test
    public void testLogin() {
        driver.get("http:localhost:8080/openlmis");

        GmailLoginPage page = new GmailLoginPage(driver);
        page.login("user@gmail.com", "password");
    }

}