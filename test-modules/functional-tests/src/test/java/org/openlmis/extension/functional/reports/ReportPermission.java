package org.openlmis.extension.functional.reports;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ReportPermission extends ReportTestHelper {


  @BeforeMethod(groups = {"report"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
  public void createTestRole(){

  }


}
