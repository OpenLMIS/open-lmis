package org.openlmis.UiUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.session.SessionOutputReader;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;


public class TestWebDriver {

    private WebDriver driver;
    private String BASE_URL;
    private String ERROR_MESSAGE_LOGIN;
    private int DEFAULT_WAIT_TIME = 30;


    public TestWebDriver(WebDriver driver)  {

        this.driver=driver;
    }

    public void setBaseURL(String BASE_URL)
    {
        this.BASE_URL=BASE_URL;
        get();
    }

    public void setErrorMessage(String ERROR_MESSAGE_LOGIN)
    {
        this.ERROR_MESSAGE_LOGIN=ERROR_MESSAGE_LOGIN;
    }

    public void verifyUrl(String identifier) {
        sleep(2000);
        String url = getCurrentUrl();
        if (identifier.equalsIgnoreCase("Admin"))
            assertTrue(url.contains(BASE_URL + "public/pages/admin/index.html"));
        else
            assertTrue(url.contains(BASE_URL + "public/pages/logistics/rnr/create.html#/init-rnr"));

    }



    public void verifyUrlInvalid() {
        String url = getCurrentUrl();
        assertTrue(url.contains(BASE_URL + "public/pages/login.html?error=true"));
    }


    public void get() {
        driver.get(BASE_URL);
    }

    public WebDriver getDriver()
    {
        return driver;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }


    public String getPageSource() {
        return driver.getPageSource();
    }

    public void setImplicitWait(int defaultTimeToWait) {
        driver.manage().timeouts().implicitlyWait(defaultTimeToWait, TimeUnit.SECONDS);
    }


    public void quitDriver() {
        driver.quit();
    }


    public void close() {
        driver.close();
    }

    public void waitForElementToAppear(final WebElement element) {
        (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (element.isDisplayed());
            }
        });
    }

    public void waitForTextToAppear(final String textToWaitFor) {
        (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (getPageSource().contains(textToWaitFor));
            }
        });
    }

    public boolean verifyErrorMessage() {
        waitForTextToAppear(ERROR_MESSAGE_LOGIN);
        return getPageSource().contains(ERROR_MESSAGE_LOGIN);
    }

    public void selectByVisibleText(WebElement element, String visibleText) {
        new Select(element).selectByVisibleText(visibleText);
    }

    public void selectByValue(WebElement element, String value) {
        new Select(element).selectByValue(value);
    }

    public void selectByIndex(WebElement element, int index) {
        new Select(element).selectByIndex(index);
    }



    public String getText(WebElement element) {
       return element.getText();
    }

    public String getValue(WebElement element, String value) {
        return element.getAttribute(value);
    }

    public void sleep(long timeToSleep) {
        try{
            Thread.sleep(timeToSleep);
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void click(final WebElement element)
    {
        Actions action = new Actions(driver);
        action.click(element).perform();
    }

    public boolean mouseOver(final WebElement element) {
        boolean flag= false;
        waitForElementToAppear(element);
        sleep(1500);
        if (element!=null) {
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
            flag = true;
            return flag;
        } else
            flag = false;
            return flag;
    }

    public void connectSSH()
    {
        boolean flag = false;
        try {

            SshClient ssh = new SshClient();

            System.out.println("Host to connect: ");

            ssh.connect("192.168.34.2", new IgnoreHostKeyVerification());

            PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();

            System.out.println("Username: ");

            pwd.setUsername("root");

            System.out.println("Password: ");
            String password="!abcd4321";
            pwd.setPassword(password);

            int result = ssh.authenticate(pwd);

            if (result == AuthenticationProtocolState.FAILED)
                System.out.println("The authentication failed");

            if (result == AuthenticationProtocolState.PARTIAL)
                System.out.println("The authentication succeeded but another"
                        + "authentication is required");

            if (result == AuthenticationProtocolState.COMPLETE)
                System.out.println("The authentication is complete");

            SessionChannelClient session = ssh.openSessionChannel();
            SessionOutputReader sor = new SessionOutputReader(session);
            SftpClient sftpclient = ssh.openSftpClient();

            if (session.requestPseudoTerminal("gogrid", 80, 24, 0, 0, "")) {
                if (session.startShell()) {

                    Thread.currentThread().sleep(1000 * 2);

                    session.getOutputStream().write(
                            "su openlmis\n".getBytes());

                    Thread.currentThread().sleep(1000 * 2);


                    session.getOutputStream().write(
                            "psql -U postgres -d open_lmis\n".getBytes());

                    Thread.currentThread().sleep(1000 * 2);

                    String answer = null;
                    String aux = null;
                    aux = sor.getOutput();
                    answer = aux.substring(0);

                    System.out.println(answer);
                    if (answer.contains("Password for user")) {
                        session.getOutputStream().write(
                                ("p@ssw0rd" + "\n").getBytes());
                    }

                    Thread.currentThread().sleep(1000 * 2);

                    String answer1 = null;
                    String aux1 = null;
                    aux1 = sor.getOutput();
                    answer1 = aux1.substring(0);

                    System.out.println(answer1);
                    if (answer1.contains("open_lmis=#")) {
                        session.getOutputStream().write(
                                ("select * from users;" + "\n").getBytes());
                    }

                    String answer2 = null;
                    String aux2 = null;
                    aux2 = sor.getOutput();
                    answer2 = aux2.substring(0);
                    System.out.println("Hi"+answer2);

                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        } finally {

        }
    }
    public void dbConnection()
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;


        try{
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException cnfe){
            System.out.println("Could not find the JDBC driver!");
            System.exit(1);
        }

        String url = "jdbc:postgresql://192.168.34.2:5432/open_lmis";
        String user = "postgres";
        String password = "p@ssw0rd";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT user_name from users;");

            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}
