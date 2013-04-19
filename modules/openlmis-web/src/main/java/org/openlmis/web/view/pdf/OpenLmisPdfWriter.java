/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.pdf;

import com.itextpdf.text.pdf.PdfWriter;

public class OpenLmisPdfWriter {
  private PdfWriter writer;

  public OpenLmisPdfWriter(PdfWriter writer) {
    this.writer = writer;
  }

  public void attachPageEvents() {
    writer.setViewerPreferences(getViewerPreferences());
    writer.setPageEvent(new PdfPageEventHandler());
  }

  protected int getViewerPreferences() {
    return PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage;
  }
}
