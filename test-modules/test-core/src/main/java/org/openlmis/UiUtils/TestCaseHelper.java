/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.UiUtils;


import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.CookieManager;
import java.sql.SQLException;
import java.util.*;

import static java.lang.System.getProperty;

public class TestCaseHelper {

  protected DBWrapper dbWrapper;
  protected String baseUrlGlobal, dburlGlobal;

  protected static TestWebDriver testWebDriver;
  protected static boolean isSeleniumStarted = false;
  protected static DriverFactory driverFactory = new DriverFactory();
  public static final String DEFAULT_BROWSER = "firefox";
  public static final String DEFAULT_BASE_URL = "http://localhost:9091/";
  public static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/open_lmis";


  public void setup() throws Exception {
    String browser = getProperty("browser", DEFAULT_BROWSER);
    baseUrlGlobal = getProperty("baseurl", DEFAULT_BASE_URL);
    dburlGlobal = getProperty("dburl", DEFAULT_DB_URL);


    dbWrapper = new DBWrapper(baseUrlGlobal, dburlGlobal);
    dbWrapper.deleteData();

    if (!isSeleniumStarted) {
      loadDriver(browser);
      addTearDownShutDownHook();
      isSeleniumStarted = true;
    }
  }

  public void tearDownSuite() {
    try {
      if (getProperty("os.name").startsWith("Windows") && driverFactory.driverType().contains("IEDriverServer")) {
        Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
        Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
      } else {
        try {
          testWebDriver.quitDriver();
        } catch (UnreachableBrowserException e) {
        }
      }
      driverFactory.deleteExe();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  protected void addTearDownShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        if (testWebDriver != null) {
          tearDownSuite();
        }
      }
    });
  }


  protected void loadDriver(String browser) throws InterruptedException {
    testWebDriver = new TestWebDriver(driverFactory.loadDriver(browser));
  }

  public void setupTestDataToInitiateRnR(boolean configureTemplate, String program, String user, String userId, String vendorName, List<String> rightsList) throws IOException, SQLException {
    setupProductTestData("P10", "P11", program, "Lvl3 Hospital");
    dbWrapper.insertFacilities("F10", "F11");
    if (configureTemplate)
      dbWrapper.configureTemplate(program);

    setupTestUserRoleRightsData(userId, user, vendorName, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment(userId, "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2012-01-16", "2013-01-30", 1, "M");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10");
  }


  public void setupRnRTestDataRnRForCommTrack(boolean configureGenericTemplate, String program, String user, String userId, String vendorName, List<String> rightsList) throws IOException, SQLException {
    setupProductTestData("P10", "P11", program, "Lvl3 Hospital");
    dbWrapper.insertFacilities("F10", "F11");

    setupTestUserRoleRightsData(userId, user, vendorName, rightsList);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment(userId, "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertSupplyLines("N1", program, "F10");
    if (configureGenericTemplate) {
      dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
      dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
      dbWrapper.configureTemplate(program);
    } else {
      dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "M");
      dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");
      dbWrapper.configureTemplateForCommTrack(program);
      dbWrapper.insertPastPeriodRequisitionAndLineItems("F10", "HIV", "Period1", "P10");
    }
  }

  public void setupTestDataToApproveRnR(String user, String userId, String vendorName, List<String> rightsList) throws IOException, SQLException {
    for (String rights : rightsList)
      dbWrapper.assignRight("store in-charge", rights);
    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    dbWrapper.insertUser(userId, user, passwordUsers, "F10", "", vendorName);
    dbWrapper.insertSupervisoryNodeSecond("F10", "N2", "Node 2", "N1");
    dbWrapper.insertRoleAssignmentforSupervisoryNode(userId, "store in-charge", "N2");
  }

  public void setupProductTestData(String product1, String product2, String program, String facilityType) throws IOException, SQLException {
    dbWrapper.insertProducts(product1, product2);
    dbWrapper.insertProgramProducts(product1, product2, program);
    dbWrapper.insertFacilityApprovedProducts(product1, product2, program, facilityType);
  }

  public void setupProgramProductTestData(String product1, String product2, String program) throws IOException, SQLException {
    dbWrapper.insertProducts(product1, product2);
    dbWrapper.insertProgramProducts(product1, product2, program);
  }

  public void setupProgramProductTestDataWithCategories(String product, String productName, String category, String program) throws IOException, SQLException {
    dbWrapper.insertProductWithCategory(product, productName, category);
    dbWrapper.insertProgramProductsWithCategory(product, program);
  }

  public void setupProgramProductISA(String program, String product, String whoratio, String dosesperyear, String wastageFactor, String bufferpercentage, String minimumvalue, String maximumvalue, String adjustmentvalue) throws IOException, SQLException {
    dbWrapper.insertProgramProductISA(program, product, whoratio, dosesperyear, wastageFactor, bufferpercentage, minimumvalue, maximumvalue, adjustmentvalue);
  }

  public void setupRequisitionGroupData(String RGCode1, String RGCode2, String SupervisoryNodeCode1, String SupervisoryNodeCode2, String Facility1, String Facility2) throws IOException, SQLException {
    dbWrapper.insertRequisitionGroups(RGCode1, RGCode2, SupervisoryNodeCode1, SupervisoryNodeCode2);
    dbWrapper.insertRequisitionGroupMembers(Facility1, Facility2);
    dbWrapper.insertRequisitionGroupProgramSchedule();
  }

  public void setupTestUserRoleRightsData(String userId, String userSIC, String vendorName, List<String> rightsList) throws IOException, SQLException {
    dbWrapper.insertRole("store in-charge", "REQUISITION", "");
    dbWrapper.insertRole("district pharmacist", "REQUISITION", "");
    for (String rights : rightsList)
      dbWrapper.assignRight("store in-charge", rights);
    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    dbWrapper.insertUser(userId, userSIC, passwordUsers, "F10", "Fatima_Doe@openlmis.com", vendorName);
  }

  public void setupTestRoleRightsData(String roleName, String roleType, String roleRight) throws IOException, SQLException {
    dbWrapper.insertRole(roleName, roleType, "");
    dbWrapper.assignRight(roleName, roleRight);
    }

  public void setupDataExternalVendor(boolean isPreviousPeriodRnRRequired) throws IOException, SQLException {
    dbWrapper.insertVendor("commTrack");
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    rightsList.add("AUTHORIZE_REQUISITION");
    if (isPreviousPeriodRnRRequired)
      setupRnRTestDataRnRForCommTrack(false, "HIV", "commTrack", "700", "commTrack", rightsList);
    else
      setupRnRTestDataRnRForCommTrack(true, "HIV", "commTrack", "700", "commTrack", rightsList);

  }

  public void setupDataApproverExternalVendor() throws IOException, SQLException {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("APPROVE_REQUISITION");
    rightsList.add("CONVERT_TO_ORDER");
    setupTestDataToApproveRnR("commTrack1", "701", "commTrack", rightsList);
  }

  public void setupDataForDeliveryZone(String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                       String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                       String facilityCodeFirst, String facilityCodeSecond,
                                       String programFirst, String programSecond, String schedule) throws IOException, SQLException {
    dbWrapper.insertDeliveryZone(deliveryZoneCodeFirst, deliveryZoneNameFirst);
    dbWrapper.insertDeliveryZone(deliveryZoneCodeSecond, deliveryZoneNameSecond);
    dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeFirst, facilityCodeFirst);
    dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeSecond, facilityCodeSecond);
    dbWrapper.insertProcessingPeriodForDistribution(14, schedule);
    dbWrapper.insertDeliveryZoneProgramSchedule(deliveryZoneCodeFirst, programFirst, schedule);
    dbWrapper.insertDeliveryZoneProgramSchedule(deliveryZoneCodeSecond, programSecond, schedule);
  }

  public void setupDataForDeliveryZoneForMultipleFacilitiesAttachedWithSingleDeliveryZone(String deliveryZoneCodeFirst,
                                       String deliveryZoneNameFirst,
                                       String facilityCodeFirst, String facilityCodeSecond,
                                       String programFirst, String programSecond, String schedule) throws IOException, SQLException {
    dbWrapper.insertDeliveryZone(deliveryZoneCodeFirst, deliveryZoneNameFirst);
    dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeFirst, facilityCodeFirst);
    dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeFirst, facilityCodeSecond);
    dbWrapper.insertProcessingPeriodForDistribution(14, schedule);
    dbWrapper.insertDeliveryZoneProgramSchedule(deliveryZoneCodeFirst, programFirst, schedule);
    dbWrapper.insertDeliveryZoneProgramSchedule(deliveryZoneCodeFirst, programSecond, schedule);
  }

  public void addOnDataSetupForDeliveryZoneForMultipleFacilitiesAttachedWithSingleDeliveryZone(String deliveryZoneCodeFirst,
                                                                                               String facilityCodeThird,
                                                                                               String facilityCodeFourth, String geoZone1, String geoZone2, String parentGeoZone) throws IOException, SQLException {
    dbWrapper.insertGeographicZone(geoZone1, geoZone2, parentGeoZone);
    dbWrapper.insertFacilitiesWithDifferentGeoZones(facilityCodeThird, facilityCodeFourth, geoZone1, geoZone2);
    dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeFirst, facilityCodeThird);
    dbWrapper.insertDeliveryZoneMembers(deliveryZoneCodeFirst, facilityCodeFourth);
  }

  public void setupTestDataToInitiateRnRForDistribution(String facilityCode1, String facilityCode2,boolean configureTemplate, String program, String user, String userId,
                                                        String vendorName, List<String> rightsList, String programCode,
  String geoLevel1, String geoLevel2, String parentGeoLevel) throws IOException, SQLException {
    setupProductTestData("P10", "P11", program, "Lvl3 Hospital");
    dbWrapper.insertGeographicZone(geoLevel1,geoLevel1,parentGeoLevel);
    dbWrapper.insertFacilitiesWithDifferentGeoZones(facilityCode1, facilityCode2,geoLevel2,geoLevel1);
    if (configureTemplate)
      dbWrapper.configureTemplate(program);

    setupTestUserRoleRightsData(userId, user, vendorName, rightsList);
    dbWrapper.insertSupervisoryNode(facilityCode1, "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment(userId, "store in-charge");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", facilityCode1, facilityCode2);
    dbWrapper.insertSupplyLines("N1", program, facilityCode1);
    dbWrapper.updateActiveStatusOfProgram(programCode);
  }


    public void sendKeys(String locator, String value) {
        int length = testWebDriver.getAttribute(testWebDriver.getElementByXpath(locator), "value").length();
        for (int i = 0; i < length; i++)
            testWebDriver.getElementByXpath(locator).sendKeys("\u0008");
        testWebDriver.getElementByXpath(locator).sendKeys(value);
    }

    public void sendKeys(WebElement locator, String value) {
        int length = testWebDriver.getAttribute(locator, "value").length();
        for (int i = 0; i < length; i++)
            locator.sendKeys("\u0008");
        locator.sendKeys(value);
    }

    public String IsaProgramProduct(String program, String product, String population) throws IOException, SQLException{
        String[] isaParams = dbWrapper.getProgramProductISA(program,product);
        return calculateISA(isaParams[0],isaParams[1],isaParams[2],isaParams[3],isaParams[4],isaParams[5],isaParams[6],population);
    }
  public String calculateISA(String ratioValue, String dosesPerYearValue, String wastageValue, String bufferPercentageValue, String adjustmentValue,
                               String minimumValue, String maximumValue, String populationValue) {
        Float calculatedISA;
        Float minimum=0.0F;
        Float maximum=0.0F;

        Integer population = Integer.parseInt(populationValue);
        Float ratio = Float.parseFloat(ratioValue) / 100;
        Integer dossesPerYear = Integer.parseInt(dosesPerYearValue);
        Float wastage = (Float.parseFloat(wastageValue) / 100) + 1;
        Float bufferPercentage = (Float.parseFloat(bufferPercentageValue) / 100) + 1;

        if (minimumValue!=null){
            minimum = Float.parseFloat(minimumValue);}
        if (maximumValue!=null){
            maximum = Float.parseFloat(maximumValue);}

        Integer adjustment = Integer.parseInt(adjustmentValue);

        calculatedISA = (((population * ratio * dossesPerYear * wastage) / 12) * bufferPercentage) + adjustment;

        if (calculatedISA <= minimum && minimum!=0.0)
                return (minimumValue);
        else if (calculatedISA >= maximum && maximum!=0.0)
            return (maximumValue);
        return (new BigDecimal(calculatedISA).setScale(0,BigDecimal.ROUND_CEILING)).toString();
   }

    public void SetupDeliveryZoneRolesAndRights(String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                  String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                  String facilityCodeFirst, String facilityCodeSecond,
                                                  String programFirst, String programSecond, String schedule, String roleNmae) throws IOException, SQLException {
        dbWrapper.insertFacilities(facilityCodeFirst, facilityCodeSecond);
        dbWrapper.insertSchedule(schedule, "Monthly", "Month");
        setupTestRoleRightsData(roleNmae,"ALLOCATION","MANAGE_DISTRIBUTION");
        setupDataForDeliveryZone(deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond,facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule);
    }

    public void OpenIndexedDB(String dbName)
    {
        WebDriver driver;
        String Separator = getProperty("file.separator");
        //String script = "var z= x();function x() {return document.title;};return z;";
        String script= "var x;window.indexedDB = window.indexedDB || window.webkitIndexedDB || window.mozIndexedDB;" +
                       "var dbreq = window.indexedDB.open(\"" + dbName + "\");" +
                       "dbreq.onsuccess = function (event){var db = dbreq.result; " +
                       //"db.createObjectStore(\"objects\", \"keyPath\": \"id\");" +
                       "var dTableNames = db.objectStoreNames;document.cookie=dTableNames[0]};" +
                       "dbreq.onerror = function (event) {return \"test.open Error: \" + event.message;};" ;
                        /*"var dTableNames = db.objectStoreNames;" +
                        "var strNames;" +
                        "for (var i = 0; i < dTableNames.length; i++) {strNames = strNames + dTableNames[i];};"+
                        "return strNames;";*/

        driver= TestWebDriver.getDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object x= js.executeScript(script);
        CookieManager cm=new CookieManager();
        cm.getCookieStore();
        //cm.


        Object y = js.executeScript(x.toString());
        System.out.println(x.getClass());

    }
}
