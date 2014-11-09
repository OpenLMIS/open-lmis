package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.report.ReportManager;
import org.openlmis.report.ReportOutputOption;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class ReportExportController {


  public static final String USER_ID = "USER_ID";
  @Autowired
  public ReportManager reportManager;
  @Autowired
  public ReportLookupService reportService;


  @RequestMapping(value = "/download/{reportKey}/{outputOption}")
  public void showReport(
    @PathVariable(value = "reportKey") String reportKey
    , @PathVariable(value = "outputOption") String outputOption
    , HttpServletRequest request
    , HttpServletResponse response
  ) {
    //TODO: change the methods to have a long user id parameter instead of integer
    Integer userId = Integer.parseInt(request.getSession().getAttribute(USER_ID).toString());

    switch (outputOption.toUpperCase()) {
      case "PDF":
        reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.CSV.PDF, response);
        break;
      case "XLS":
        reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.XLS, response);
        break;
      case "HTML":
        reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.HTML, response);
    }
  }

    @RequestMapping(value = "/exportfile/{reportKey}/{outputOption}")
    public void exportReportBytesStream(
            @PathVariable(value = "reportKey") String reportKey
            , @PathVariable(value = "outputOption") String outputOption
            , HttpServletRequest request
    ) {
        //TODO: change the methods to have a long user id parameter instead of integer
        Integer userId = Integer.parseInt(request.getSession().getAttribute(USER_ID).toString());

        ByteArrayOutputStream byteArrayOutputStream = reportManager.exportReportBytesStream(userId, reportKey, request.getParameterMap(), outputOption);

        OutputStream outStream = null;
        ByteArrayOutputStream byteOutStream = null;
        try {
            outStream = new FileOutputStream("C:\\companies\\doop.pdf");
            // writing bytes in to byte output stream
            byteArrayOutputStream.writeTo(outStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

  @RequestMapping(value = "/download/mailinglabels/list/{outputOption}")
  public void showMailingListReport(
    @PathVariable(value = "outputOption") String outputOption
    , HttpServletRequest request
    , HttpServletResponse response
  ) {
    showReport("facility_mailing_list", outputOption, request, response);
  }
}