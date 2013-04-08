/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.openqa.selenium.support.How.ID;

public class RequisitionPage extends Page {
  @FindBy(how = ID, using = "comments")
  private static WebElement commentsButton;
  @FindBy(how = ID, using = "addComment")
  private static WebElement addCommentTextArea;
  @FindBy(how = ID, using = "addButton")
  private static WebElement addCommentButton;
  @FindBy(how = ID, using = "commentClose")
  private static WebElement commentCloseIcon;

  protected RequisitionPage(TestWebDriver driver) {
    super(driver);
  }

  public void addComments(String comments) {
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
    SeleneseTestNgHelper.assertEquals(addCommentTextArea.getAttribute("value").trim(), textToVerify.trim());
  }

  public void verifyCommentBoxNotPresent() {
    commentsButton.click();
    WebElement commentBox = testWebDriver.findElement(By.id("addComment"));
    if (commentBox.isDisplayed())
      SeleneseTestNgHelper.fail("Comment should not be updatable");
  }

}