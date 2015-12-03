/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pageobjects;

import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;

public class RequisitionPage extends Page {
  @FindBy(how = ID, using = "comments")
  private static WebElement commentsButton = null;

  @FindBy(how = ID, using = "addComment")
  private static WebElement addCommentTextArea = null;

  @FindBy(how = ID, using = "addButton")
  private static WebElement addCommentButton = null;

  @FindBy(how = ID, using = "commentClose")
  private static WebElement commentCloseIcon = null;

  @FindBy(how = ID, using = "button_OK")
  private static WebElement okButton = null;

  @FindBy(how = ID, using = "total_0")
  private static WebElement total = null;

  @FindBy(how = ID, using = "beginningBalance_0")
  private static WebElement beginningBalance = null;

  @FindBy(how = ID, using = "quantityReceived_0")
  private static WebElement quantityReceived = null;

  @FindBy(how = ID, using = "nonFullSupplyTab")
  private static WebElement nonFullSupplyTab = null;

  @FindBy(how = ID, using = "fullSupplyTab")
  private static WebElement fullSupplyTab = null;

  @FindBy(how = ID, using = "regimenTab")
  private static WebElement regimenTab = null;

  @FindBy(how = ID, using = "cost_0")
  private static WebElement totalCost = null;

  @FindBy(how = XPATH, using = "//input[@value='Add']")
  private static WebElement addNonFullSupplyButtonScreen = null;

  @FindBy(how = ID, using = "regimenTable")
  private static WebElement regimenTable = null;

  @FindBy(how = XPATH, using = "//input[@value='Save']")
  private static WebElement SaveButton = null;

  @FindBy(how = XPATH, using = "//input[@value='Submit']")
  private static WebElement SubmitButton = null;

  @FindBy(how = XPATH, using = "//input[@value='Authorize']")
  private static WebElement AuthorizeButton = null;

  @FindBy(how = XPATH, using = "//div[@id='saveSuccessMsgDiv' and @openlmis-message='message']")
  private static WebElement saveSuccessMessage = null;

  @FindBy(how = ID, using = "saveFailMessage")
  private static WebElement errorMessage = null;

  @FindBy(how = XPATH, using = "//div[@id='submitSuccessMsgDiv' and @openlmis-message='submitMessage']")
  private static WebElement submitSuccessMessage = null;

  @FindBy(how = XPATH, using = "//table[@id='regimenTable']/tbody[1]/tr[2]/td[3]/ng-switch/span/span")
  private static WebElement patientsOnTreatmentTextField = null;

  @FindBy(how = XPATH, using = "//table[@id='regimenTable']/tbody[1]/tr[2]/td[4]/ng-switch/span/span")
  private static WebElement patientsToInitiateTreatmentTextField = null;

  @FindBy(how = XPATH, using = "//table[@id='regimenTable']/tbody[1]/tr[2]/td[5]/ng-switch/span/span")
  private static WebElement patientsStoppedTreatmentTextField = null;

  @FindBy(how = XPATH, using = "//table[@id='regimenTable']/tbody[1]/tr[2]/td[6]/ng-switch/span/span")
  private static WebElement remarksTextField = null;

  @FindBy(how = XPATH, using = "//table[@id='regimenTable']/tbody[1]/tr[2]/td[3]/ng-switch/span/input")
  private static WebElement patientsOnTreatmentInputField = null;

  @FindBy(how = XPATH, using = "//table[@id='regimenTable']/tbody[1]/tr[2]/td[4]/ng-switch/span/input")
  private static WebElement patientsToInitiateTreatmentInputField = null;

  @FindBy(how = XPATH, using = "//table[@id='regimenTable']/tbody[1]/tr[2]/td[5]/ng-switch/span/input")
  private static WebElement patientsStoppedTreatmentInputField = null;

  @FindBy(how = XPATH, using = "//table[@id='regimenTable']/tbody[1]/tr[2]/td[6]/ng-switch/span/input")
  private static WebElement remarksInputField = null;

  @FindBy(how = ID, using = "printButton")
  private static WebElement printButtonFullView = null;

  @FindBy(how = ID, using = "resizeViewButton")
  private static WebElement resizeViewButton = null;

  @FindBy(how = ID, using = "printButtonOnHeader")
  private static WebElement printButtonOnHeader = null;

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
    assertEquals(comment.getText(), comments);

    WebElement commentAddedBy = testWebDriver.getElementByXpath("//ul[@id='comments-list']/li[" + commentNumber + "]/div");

    isAddedBy = commentAddedBy.getText().contains("By: " + AddedBy);
    assertTrue(isAddedBy);
    Date date = new Date();
    SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
    isAddedOn = commentAddedBy.getText().contains(ft.format(date));
    assertTrue(isAddedOn);
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
    assertEquals(addCommentTextArea.getAttribute("value").trim(), textToVerify.trim());
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
    testWebDriver.waitForElementToAppear(okButton);
    okButton.click();
    testWebDriver.sleep(500);
  }

  public void verifyTotalField() {
    testWebDriver.waitForElementToAppear(total);
    String totalValue = total.getText();
    String beginningBalanceValue = testWebDriver.getAttribute(beginningBalance, "value");
    String quantityReceivedValue = testWebDriver.getAttribute(quantityReceived, "value");
    assertEquals(totalValue,
      String.valueOf(Integer.parseInt(beginningBalanceValue) + Integer.parseInt(quantityReceivedValue)));
  }

  public void verifyTotalFieldPostAuthorize() {
    testWebDriver.waitForElementToAppear(total);
    String totalValue = total.getText();
    String beginningBalanceValue = beginningBalance.getText();
    String quantityReceivedValue = quantityReceived.getText();
    assertEquals(totalValue,
      String.valueOf(Integer.parseInt(beginningBalanceValue) + Integer.parseInt(quantityReceivedValue)));
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

  public void enterValuesOnRegimenScreen(int columnNumber, int row, String value) {
    Map<Integer, String> tableIdColumnMapper = new HashMap<>();
    tableIdColumnMapper.put(3, "patientsOnTreatment");
    tableIdColumnMapper.put(4, "patientsToInitiateTreatment");
    tableIdColumnMapper.put(5, "patientsStoppedTreatment");
    tableIdColumnMapper.put(6, "remarks");

    WebElement element = testWebDriver.getElementById(tableIdColumnMapper.get(columnNumber) + "_" + (row - 1));
    testWebDriver.waitForElementToAppear(element);
    sendKeys(element, value);
    element.sendKeys(Keys.TAB);
  }

  public String getPatientsOnTreatmentValue() {
    testWebDriver.waitForElementToAppear(patientsOnTreatmentTextField);
    return testWebDriver.getText(patientsOnTreatmentTextField);
  }

  public String getPatientsToInitiateTreatmentValue() {
    testWebDriver.waitForElementToAppear(patientsToInitiateTreatmentTextField);
    return testWebDriver.getText(patientsToInitiateTreatmentTextField);
  }

  public String getPatientsStoppedTreatmentValue() {
    testWebDriver.waitForElementToAppear(patientsStoppedTreatmentTextField);
    return testWebDriver.getText(patientsStoppedTreatmentTextField);
  }

  public String getRemarksValue() {
    testWebDriver.waitForElementToAppear(remarksTextField);
    return testWebDriver.getText(remarksTextField);
  }

  public String getPatientsOnTreatmentInputValue() {
    testWebDriver.waitForElementToAppear(patientsOnTreatmentInputField);
    return testWebDriver.getAttribute(patientsOnTreatmentInputField, "value");
  }

  public String getPatientsToInitiateTreatmentInputValue() {
    testWebDriver.waitForElementToAppear(patientsToInitiateTreatmentInputField);
    return testWebDriver.getAttribute(patientsToInitiateTreatmentInputField, "value");
  }

  public String getPatientsStoppedTreatmentInputValue() {
    testWebDriver.waitForElementToAppear(patientsStoppedTreatmentInputField);
    return testWebDriver.getAttribute(patientsStoppedTreatmentInputField, "value");
  }

  public String getRemarksInputValue() {
    testWebDriver.waitForElementToAppear(remarksInputField);
    return testWebDriver.getAttribute(remarksInputField, "value");
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
    testWebDriver.waitForElementToAppear(submitSuccessMessage);
    assertTrue("RnR Submit Success message not displayed", submitSuccessMessage.isDisplayed());
  }

  public void verifySaveSuccessMsg() {
    assertTrue("RnR Submit Success message not displayed", saveSuccessMessage.isDisplayed());
  }

  public void verifyApproveErrorDiv() {
    testWebDriver.waitForElementToAppear(errorMessage);
    assertTrue("RnR Approved error message not displayed", errorMessage.isDisplayed());
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

  public void verifySkippedProductsOnRnRScreen(int rowNumber) {
    testWebDriver.waitForAjax();
    WebElement skipCheckBox = testWebDriver.getElementById("skip_" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(skipCheckBox);
    assertTrue(skipCheckBox.isSelected());
    skipCheckBox = testWebDriver.getElementById("skip_" + (rowNumber));
    assertFalse(skipCheckBox.isSelected());
  }

  public void clickPrintButton() {
    testWebDriver.waitForElementToAppear(printButtonOnHeader);
    printButtonOnHeader.click();
    testWebDriver.sleep(1000);
  }

  public void clickFullViewPrintButton() {
    testWebDriver.waitForElementToAppear(printButtonFullView);
    printButtonFullView.click();
    testWebDriver.sleep(1000);
  }

  public void clickResizeViewButton() {
    testWebDriver.waitForElementToAppear(resizeViewButton);
    resizeViewButton.click();
  }
}