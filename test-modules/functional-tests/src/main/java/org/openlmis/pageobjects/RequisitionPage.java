/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class RequisitionPage extends Page {
  @FindBy(how = ID, using = "comments")
  private static WebElement commentsButton;
  @FindBy(how = ID, using = "addComment")
  private static WebElement addCommentTextArea;
  @FindBy(how = ID, using = "addButton")
  private static WebElement addCommentButton;
  @FindBy(how = ID, using = "commentClose")
  private static WebElement commentCloseIcon;
  @FindBy(how = ID, using = "button_OK")
  private static WebElement okButton;
  @FindBy(how = ID, using = "button_Cancel")
  private static WebElement cancelButton;
  @FindBy(how = ID, using = "total_0")
  private static WebElement total;
  @FindBy(how = ID, using = "beginningBalance_0")
  private static WebElement beginningBalance;
  @FindBy(how = ID, using = "quantityReceived_0")
  private static WebElement quantityReceived;
  @FindBy(how = ID, using = "nonFullSupplyTab")
  private static WebElement nonFullSupplyTab;
  @FindBy(how = ID, using = "fullSupplyTab")
  private static WebElement fullSupplyTab;
  @FindBy(how = ID, using = "regimenTab")
  private static WebElement regimenTab;
  @FindBy(how = ID, using = "cost_0")
  private static WebElement totalCost;
  @FindBy(how = XPATH, using = "//input[@value='Add']")
  private static WebElement addNonFullSupplyButtonScreen;
  @FindBy(how = ID, using = "regimenTable")
  private static WebElement regimenTable;
  @FindBy(how = XPATH, using = "//input[@value='Save']")
  private static WebElement SaveButton;
  @FindBy(how = XPATH, using = "//input[@value='Submit']")
  private static WebElement SubmitButton;
  @FindBy(how = XPATH, using = "//input[@value='Authorize']")
  private static WebElement AuthorizeButton;
  @FindBy(how = XPATH, using = "//div[@id='saveSuccessMsgDiv' and @openlmis-message='message']")
  private static WebElement saveSuccessMessage;

  @FindBy(how = XPATH, using = "//div[@id='submitSuccessMsgDiv' and @openlmis-message='submitMessage']")
  private static WebElement submitSuccessMessage;

  @FindBy(how = XPATH, using = "//table[@id='regimenTable']/tbody[1]/tr[2]/td[3]/ng-switch/span/input")
  private static WebElement patientsOnTreatmentTextField;
  @FindBy(how = XPATH, using = "//table[@id='regimenTable']/tbody[1]/tr[2]/td[4]/ng-switch/span/input")
  private static WebElement patientsToInitiateTreatmentTextField;
  @FindBy(how = XPATH, using = "//table[@id='regimenTable']/tbody[1]/tr[2]/td[5]/ng-switch/span/input")
  private static WebElement patientsStoppedTreatmentTextField;
  @FindBy(how = XPATH, using = "//table[@id='regimenTable']/tbody[1]/tr[2]/td[6]/ng-switch/span/input")
  private static WebElement remarksTextField;


  protected RequisitionPage(TestWebDriver driver) {
    super(driver);
  }

  public void addComments(String comments) {
    clickCommentsButton();
    typeCommentsInCommentsTextArea(comments);
    clickAddCommentsButton();
    closeCommentPopUp();
  }

  public void verifyComment(String comments, String AddedBy, int commentNumber) {
    boolean isAddedBy;
    boolean isAddedOn;

    commentsButton.click();
    WebElement comment = testWebDriver.getElementByXpath("//ul[@id='comments-list']/li[" + commentNumber + "]/span");
    SeleneseTestNgHelper.assertEquals(comment.getText(), comments);

    WebElement commentAddedBy = testWebDriver.getElementByXpath("//ul[@id='comments-list']/li[" + commentNumber + "]/div");

    isAddedBy = commentAddedBy.getText().contains("By: " + AddedBy);
    SeleneseTestNgHelper.assertTrue(isAddedBy);
    Date date = new Date();
    SimpleDateFormat ft = new SimpleDateFormat("dd/MM/YYYY");
    isAddedOn = commentAddedBy.getText().contains(ft.format(date));
    SeleneseTestNgHelper.assertTrue(isAddedOn);
    commentCloseIcon.click();
  }

  public void closeCommentPopUp() {
    testWebDriver.waitForElementToAppear(commentCloseIcon);
    commentCloseIcon.click();
  }

  public void typeCommentsInCommentsTextArea(String comments) {
    testWebDriver.waitForElementToAppear(addCommentTextArea);
    addCommentTextArea.sendKeys(comments);
  }

  public void clickCommentsButton() {
    testWebDriver.waitForElementToAppear(commentsButton);
    commentsButton.click();
  }

  public void clickAddCommentsButton() {
    testWebDriver.waitForElementToAppear(addCommentButton);
    addCommentButton.click();
  }

  public void verifyValueInCommentsTextArea(String textToVerify) {
    testWebDriver.waitForElementToAppear(addCommentTextArea);
    addCommentTextArea.click();
    SeleneseTestNgHelper.assertEquals(addCommentTextArea.getAttribute("value").trim(), textToVerify.trim());
  }

  public void verifyCommentBoxNotPresent() {
    boolean commentBoxPresent = false;
    testWebDriver.waitForElementToAppear(commentsButton);
    commentsButton.click();
    try {
      addCommentTextArea.click();
      commentBoxPresent = true;
    } catch (ElementNotVisibleException e) {
      commentBoxPresent = false;
    } catch (NoSuchElementException e) {
      commentBoxPresent = false;
    } finally {
      assertFalse(commentBoxPresent);
    }
  }

  public void clickOk() {
    testWebDriver.sleep(250);
    okButton.click();
    testWebDriver.sleep(500);
  }

  public void clickCancel() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public void verifyTotalField() {
    testWebDriver.waitForElementToAppear(total);
    String totalValue = total.getText();
    String beginningBalanceValue = testWebDriver.getAttribute(beginningBalance, "value");
    String quantityReceivedValue = testWebDriver.getAttribute(quantityReceived, "value");
    SeleneseTestNgHelper.assertEquals(totalValue, String.valueOf(Integer.parseInt(beginningBalanceValue) + Integer.parseInt(quantityReceivedValue)));
  }

  public void verifyTotalFieldPostAuthorize() {
    testWebDriver.waitForElementToAppear(total);
    String totalValue = total.getText();
    String beginningBalanceValue = beginningBalance.getText();
    String quantityReceivedValue = quantityReceived.getText();
    SeleneseTestNgHelper.assertEquals(totalValue, String.valueOf(Integer.parseInt(beginningBalanceValue) + Integer.parseInt(quantityReceivedValue)));
  }

  public void clickFullSupplyTab() {
    testWebDriver.waitForElementToAppear(fullSupplyTab);
    fullSupplyTab.click();
    testWebDriver.waitForElementToAppear(totalCost);
  }

  public void clickNonFullSupplyTab() {
    testWebDriver.waitForElementToAppear(nonFullSupplyTab);
    nonFullSupplyTab.click();
    testWebDriver.waitForElementToAppear(addNonFullSupplyButtonScreen);
  }

  public void clickRegimenTab() {
    testWebDriver.waitForElementToAppear(regimenTab);
    regimenTab.click();
    testWebDriver.waitForElementToAppear(regimenTable);
  }

  public boolean existRegimenTab() {
    return regimenTab.isDisplayed();
  }

  public boolean existRegimenCode(String regimentCode, int row) {
    return testWebDriver.getElementByXpath("//table[@id='regimenTable']/tbody/tr[" + row + "]/td[1]/ng-switch/span/span").getText().equals(regimentCode);
  }

  public boolean existRegimenName(String regimentName, int row) {
    return testWebDriver.getElementByXpath("//table[@id='regimenTable']/tbody[1]/tr[" + row + "]/td[2]/ng-switch/span/span").getText().equals(regimentName);
  }

  public boolean existRegimenReportingField(int fieldNumberInTable, int row) {
    return testWebDriver.getElementByXpath("//table[@id='regimenTable']/tbody[1]/tr[" + row + "]/td[" + fieldNumberInTable + "]/ng-switch/span/input").isDisplayed();
  }

  public void enterValuesOnRegimenScreen(int fieldNumberInTable, int row, int value) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("//table[@id='regimenTable']/tbody[1]/tr[" + row + "]/td[" + fieldNumberInTable + "]/ng-switch/span/input"));
    testWebDriver.getElementByXpath("//table[@id='regimenTable']/tbody[1]/tr[" + row + "]/td[" + fieldNumberInTable + "]/ng-switch/span/input").clear();
    testWebDriver.getElementByXpath("//table[@id='regimenTable']/tbody[1]/tr[" + row + "]/td[" + fieldNumberInTable + "]/ng-switch/span/input").sendKeys(String.valueOf(value));
  }

  public String getPatientsOnTreatmentValue() {
    testWebDriver.waitForElementToAppear(patientsOnTreatmentTextField);
    return testWebDriver.getAttribute(patientsOnTreatmentTextField, "value");
  }

  public String getPatientsToInitiateTreatmentValue() {
    testWebDriver.waitForElementToAppear(patientsToInitiateTreatmentTextField);
    return testWebDriver.getAttribute(patientsToInitiateTreatmentTextField, "value");
  }

  public String getPatientsStoppedTreatmentValue() {
    testWebDriver.waitForElementToAppear(patientsStoppedTreatmentTextField);
    return testWebDriver.getAttribute(patientsStoppedTreatmentTextField, "value");
  }

  public String getRemarksValue() {
    testWebDriver.waitForElementToAppear(remarksTextField);
    return testWebDriver.getAttribute(remarksTextField, "value");
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(SaveButton);
    SaveButton.click();
    testWebDriver.waitForElementToAppear(saveSuccessMessage);
  }

  public void clickSubmitButton() {
    testWebDriver.waitForElementToAppear(SubmitButton);
    SubmitButton.click();
  }

  public void clickAuthorizeButton() {
    testWebDriver.waitForElementToAppear(AuthorizeButton);
    AuthorizeButton.click();
  }

  public void verifySubmitSuccessMsg() {
    assertTrue("RnR Submit Success message not displayed", submitSuccessMessage.isDisplayed());
  }

  public void verifySaveSuccessMsg() {
    assertTrue("RnR Submit Success message not displayed", saveSuccessMessage.isDisplayed());
  }

  public void verifyAuthorizeSuccessMsg() {
    assertTrue("RnR Authorize Success message not displayed", submitSuccessMessage.isDisplayed());
  }

  public int getRegimenTableRowCount() {
    WebElement table_element = testWebDriver.findElement(By.id("regimenTable"));
    List<WebElement> tr_collection = table_element.findElements(By.xpath("id('regimenTable')/tbody[1]/tr"));
    return tr_collection.size();
  }

  public int getRegimenTableColumnCount() {
    WebElement table_element = testWebDriver.findElement(By.id("regimenTable"));
    List<WebElement> tr_collection = table_element.findElements(By.xpath("id('regimenTable')/tbody[1]/tr/td"));
    return tr_collection.size();
  }
}