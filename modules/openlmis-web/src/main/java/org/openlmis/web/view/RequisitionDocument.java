package org.openlmis.web.view;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.web.controller.RequisitionController;

import java.util.Map;

public class RequisitionDocument {
  private Document document;
  public static final BaseColor HEADER_BACKGROUND = new BaseColor(210, 210, 210);
//  public static final BaseColor ROW_GREY_BACKGROUND = new BaseColor(235, 235, 235);
  public static final Font H2_FONT = FontFactory.getFont(FontFactory.TIMES, 20f, Font.BOLD, BaseColor.BLACK);
  public static final int CELL_PADDING = 5;
  public static final int WIDTH_PERCENTAGE = 100;
  public static final int TABLE_SPACING = 25;

  public RequisitionDocument(Document document) {
    this.document = document;
    document.open();
  }

  public void build(Map<String, Object> model) throws DocumentException, NoSuchFieldException, IllegalAccessException {

    RequisitionTable requisitionTable = new RequisitionTable(model);
    Rnr requisition = (Rnr) model.get(RequisitionController.RNR);

    document.add(requisitionTable.getFullSupplyHeader());
    document.add(requisitionTable.getFullSupplyTable());
    PdfPTable nonFullSupplyTable = requisitionTable.getNonFullSupplyTable();
    if (nonFullSupplyTable != null) {
      document.newPage();
      document.add(requisitionTable.getNonFullSupplyHeader());
      document.add(nonFullSupplyTable);
    }

    document.newPage();

    RequisitionSummary requisitionSummary = new RequisitionSummary(requisition);
    document.add(requisitionSummary.get());

    document.close();
  }

}
