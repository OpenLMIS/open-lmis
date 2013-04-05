/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.text.SimpleDateFormat;
import java.util.Date;

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
  @FindBy(how = XPATH, using = "//ul[@id='comments-list']/li[1]/span")
  private static WebElement lastComment;
  @FindBy(how = XPATH, using = "//ul[@id='comments-list']/li[1]/div")
  private static WebElement lastCommentAddedBy;

  protected RequisitionPage(TestWebDriver driver) {
    super(driver);
  }

  public void addComments(String comments) {
    testWebDriver.waitForElementToAppear(commentsButton);
    commentsButton.click();
    addCommentTextArea.sendKeys(comments);
    addCommentButton.click();
    commentCloseIcon.click();
  }

  public void verifyLastComment(String comments, String AddedBy) {
    boolean isAddedBy;
    boolean isAddedOn;
    commentsButton.click();
    SeleneseTestNgHelper.assertEquals(lastComment.getText(), comments);

    isAddedBy = lastCommentAddedBy.getText().contains("By: " + AddedBy);
    SeleneseTestNgHelper.assertTrue(isAddedBy);
    Date date = new Date();
    SimpleDateFormat ft = new SimpleDateFormat("dd/MM/YYYY");
    isAddedOn = lastCommentAddedBy.getText().contains(ft.format(date));
    SeleneseTestNgHelper.assertTrue(isAddedOn);
    commentCloseIcon.click();
  }
}