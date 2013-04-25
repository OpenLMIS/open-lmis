/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.jasperreports.*;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext;

@Resource
@Service
public class JasperReportsViewFactory {

  @Autowired
  DataSource replicationDataSource;

  public AbstractJasperReportsSingleFormatView getJasperReportsView(
    String url, String format) {
    String viewFormat = format == null ? "pdf" : format;

    // get jasperView class based on the format supplied
    // defaults to pdf
    AbstractJasperReportsSingleFormatView jasperView = null;
    if (viewFormat.equals("csv")) {
      jasperView = new JasperReportsCsvView();
    } else if (viewFormat.equals("html")) {
      jasperView = new JasperReportsHtmlView();
    } else if (viewFormat.equals("xls")) {
      jasperView = new JasperReportsXlsView();
    } else {
      jasperView = new JasperReportsPdfView();
    }

    // get appContext. required by the view
    WebApplicationContext ctx = getCurrentWebApplicationContext();

    jasperView.setJdbcDataSource(replicationDataSource);
    jasperView.setUrl(url);
    if (ctx != null)
      jasperView.setApplicationContext(ctx);

    return jasperView;
  }

}