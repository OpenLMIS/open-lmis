package org.openlmis.rnr.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Signature;
import org.openlmis.rnr.domain.PatientQuantificationLineItem;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
@NoArgsConstructor
public class MMIAPDFGenerator {

  @Value("${email.attachment.cache.path}")
  protected String cachePath;

  private Font fontBody = new Font(Font.FontFamily.HELVETICA, 9.0f, Font.NORMAL, BaseColor.BLACK);;

  private BaseColor colorGray = new BaseColor(208, 208, 208);
  private BaseColor colorLightGray = new BaseColor(240, 240, 240);
  private BaseColor colorDarkGray = new BaseColor(179, 177, 177);
  private BaseColor colorYellow = new BaseColor(246, 243, 145);
  private BaseColor colorLightYellow = new BaseColor(250, 248, 199);
  private BaseColor colorBlue = new BaseColor(211, 233, 240);
  private BaseColor colorGreen = new BaseColor(199, 221, 187);

  private final Font font8Normal = new Font(Font.FontFamily.HELVETICA, 8.0f, Font.NORMAL, BaseColor.BLACK);
  private final Font font8Bold = new Font(Font.FontFamily.HELVETICA, 8.0f, Font.BOLD, BaseColor.BLACK);

  public String generateMMIAPdf(Rnr requisition, String fileNameForMMIAPdf) {
    Document document = new Document();
    String pathname = "";
    try {
      pathname = cachePath + "/" + fileNameForMMIAPdf;
      PdfWriter.getInstance(document, new FileOutputStream(pathname));

      document.open();

      document.add(createHeaderTable(requisition.getFacility(), requisition.getPeriod()));
      document.add(createMMIATable(requisition.getFullSupplyLineItems()));
      document.add(createRegimeAndPatientTable(requisition));
      document.add(createFooterSection(requisition.getRnrSignatures()));

      document.close();

    } catch (DocumentException | ParseException | IOException e) {
      e.printStackTrace();
    }
    return pathname;
  }

  /*
   * Add Header Table!!!!
   */

  private PdfPTable createHeaderTable(Facility facility, ProcessingPeriod period) throws DocumentException, ParseException {
    PdfPTable table = new PdfPTable(4);

    table.setWidths(new float[]{1f, 4f, 5f, 2f});
    table.setWidthPercentage(100f);

    String stringMonth = convertDateToPortuguese(period.getStringEndDate(), "MM/dd/yyyy", "MMMM");
    addHeaderTableFirstLine(table, stringMonth);
    addHeaderTableSecondLine(table, facility, period);

    return table;
  }

  private String convertDateToPortuguese(String dateString, String originFormat, String DestFormat) throws ParseException {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(originFormat);
    LocalDate localDate = formatter.parseLocalDate(dateString);
    return localDate.toString(DestFormat, new Locale("pt", "PT"));
  }

  private void addHeaderTableFirstLine(PdfPTable table, String stringMonth) {
    Image image = null;
    try {
      image = Image.getInstance(MMIAPDFGenerator.class.getClassLoader().getResource("images/table-logo-moz.png"));
      image.scaleToFit(40, 40);
    } catch (BadElementException | IOException e) {
      e.printStackTrace();
    }

    PdfPCell logoCell = new PdfPCell(image);
    logoCell.setPadding(1f);
    logoCell.setFixedHeight(20f);
    table.addCell(logoCell);

    Font fontHeaderLeft = new Font(Font.FontFamily.HELVETICA, 6.0f, Font.BOLD, BaseColor.BLACK);
    Paragraph republicaCell = new Paragraph("REPUBLICA DE MOCAMBIQUE\n\n" +
            "MINISTERIO DA SAUDE\n\n" +
            "CENTRAL DE MEDICAMENTOS E ARTIGOS MEDICOS", fontHeaderLeft);
    PdfPCell headerLeft = new PdfPCell(republicaCell);
    positioningCenter(headerLeft);

    Font fontHeaderRight = new Font(Font.FontFamily.HELVETICA, 9.0f, Font.BOLD, BaseColor.BLACK);
    PdfPCell headerRight = new PdfPCell(new Paragraph("MMIA\n\n" +
            "MAPA MENSAL DE INFORMACAO ARV", fontHeaderRight));
    positioningCenter(headerRight);

    table.addCell(headerLeft);
    table.addCell(headerRight);

    Paragraph phraseMonth = new Paragraph("mês : " + stringMonth, fontBody);
    PdfPCell monthCell =  new PdfPCell(phraseMonth);
    monthCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    table.addCell(monthCell);
  }
  private void addHeaderTableSecondLine(PdfPTable table, Facility facility, ProcessingPeriod period) {
    PdfPCell facilityCell = new PdfPCell(new Paragraph("Unidade Sanitaria : " + facility.getName(), fontBody));
    facilityCell.setColspan(3);

    PdfPCell districtCell = new PdfPCell(new Paragraph("Distrital : " + facility.getGeographicZone().getName(), fontBody));
    districtCell.setColspan(2);

    PdfPCell provinceCell = new PdfPCell(new Paragraph("Provincial : " + facility.getGeographicZone().getParent().getName(), fontBody));

    PdfPCell yearCell = new PdfPCell(new Paragraph("Ano : " + period.getStringYear(), fontBody));
    yearCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    yearCell.setRowspan(2);

    table.addCell(facilityCell);
    table.addCell(yearCell);
    table.addCell(districtCell);
    table.addCell(provinceCell);
  }

   /*
   * Add MMIA Table
   */

  private PdfPTable createMMIATable(List<RnrLineItem> fullSupplyLineItems) throws DocumentException, ParseException {
    PdfPTable table = new PdfPTable(9);

    table.setWidthPercentage(100f);
    table.setSpacingBefore(2);

    table.setWidths(new float[]{1f, 4f, 3f, 1f, 1f, 1f, 1f, 1f, 1f});

    List<RnrLineItem> adultRnrLineItmes = fullSupplyLineItems.subList(0,12);
    List<RnrLineItem> childrenRnrLineItmes = fullSupplyLineItems.subList(12,22);
    List<RnrLineItem> otherRnrLineItmes = fullSupplyLineItems.subList(22,24);

    List<String> headerList = Arrays.asList("FNM", "Medicamento", "UNIDATA de Saida", "Saldo Inicial", "Entradas", "Saidas", "Perda e ajustes", "Inventario", "Validade");

    for (String header: headerList){
      getMMIATableHeaderCell(table, header);
    }

    for (RnrLineItem adultRnrLineItem : adultRnrLineItmes) {
      addMMIATableLineItem(table,adultRnrLineItem,colorGreen);
    }

    addMMIATableLineItem(table, null,colorGreen);
    addMMIATableLineItem(table, null,colorGreen);

    for (RnrLineItem childrenRnrLineItme : childrenRnrLineItmes) {
      addMMIATableLineItem(table, childrenRnrLineItme, colorLightYellow);
    }
    addMMIATableLineItem(table, null,colorLightYellow);

    for (RnrLineItem otherRnrLineItem : otherRnrLineItmes) {
      addMMIATableLineItem(table, otherRnrLineItem, colorBlue);
    }
    addMMIATableLineItem(table, null,colorBlue);
    return table;
  }
  private void getMMIATableHeaderCell(PdfPTable table, String name) {
    PdfPCell fnm = new PdfPCell(new Phrase(name, new Font(Font.FontFamily.HELVETICA, 7.0f, Font.NORMAL, BaseColor.BLACK)));
    fnm.setHorizontalAlignment(Element.ALIGN_CENTER);
    fnm.setVerticalAlignment(Element.ALIGN_MIDDLE);
    fnm.setBackgroundColor(colorGray);
    table.addCell(fnm);
  }
  private void addMMIATableLineItem(PdfPTable table, RnrLineItem rnrLineItem, BaseColor medicineColor) throws ParseException {
    addMMIATableLineCell(table, rnrLineItem == null ? " " : rnrLineItem.getProductCode(), Element.ALIGN_CENTER, medicineColor);
    addMMIATableLineCell(table, rnrLineItem == null ? " " : rnrLineItem.getProductPrimaryName(), Element.ALIGN_LEFT, medicineColor);
    addMMIATableLineCell(table, rnrLineItem == null ? " " : rnrLineItem.getProductStrength(), Element.ALIGN_CENTER, colorLightGray);
    addMMIATableLineCell(table, rnrLineItem == null ? " " : rnrLineItem.getBeginningBalance().toString(), Element.ALIGN_CENTER, BaseColor.WHITE);
    addMMIATableLineCell(table, rnrLineItem == null ? " " : rnrLineItem.getQuantityReceived().toString(), Element.ALIGN_CENTER, BaseColor.WHITE);
    addMMIATableLineCell(table, rnrLineItem == null ? " " : rnrLineItem.getQuantityDispensed().toString(), Element.ALIGN_CENTER, BaseColor.WHITE);
    addMMIATableLineCell(table, rnrLineItem == null ? " " : rnrLineItem.getTotalLossesAndAdjustments().toString(), Element.ALIGN_CENTER, BaseColor.WHITE);
    addMMIATableLineCell(table, rnrLineItem == null ? " " : rnrLineItem.getStockInHand().toString(), Element.ALIGN_CENTER, BaseColor.WHITE);
    addMMIATableLineCell(table, rnrLineItem == null ? " " : convertDateToPortuguese(rnrLineItem.getExpirationDate(), "dd/MM/yyyy", "MMM yyyy"), Element.ALIGN_CENTER, BaseColor.WHITE);
  }
  private void addMMIATableLineCell(PdfPTable table, String name, int alignment, BaseColor bgcolor) {
    PdfPCell fnm = new PdfPCell(new Phrase(name, new Font(Font.FontFamily.HELVETICA, 7.0f, Font.NORMAL, BaseColor.BLACK)));
    fnm.setHorizontalAlignment(alignment);
    fnm.setVerticalAlignment(Element.ALIGN_MIDDLE);
    fnm.setBackgroundColor(bgcolor);
    table.addCell(fnm);
  }

  /*
  * Regime and Patient Table
   */

  private PdfPTable createRegimeAndPatientTable(Rnr requisition) throws DocumentException {
    PdfPTable section = new PdfPTable(3);
    section.setWidths(new float[]{6f, 1f, 5f});

    section.setSpacingBefore(5f);
    section.setWidthPercentage(100f);

    PdfPCell regimeCell = new PdfPCell(createRegimeTable(requisition.getRegimenLineItems(), requisition.calculateRegimeTotal()));
    noBorder(regimeCell);
    regimeCell.setRowspan(2);

    PdfPCell emptyCell = new PdfPCell();
    noBorder(emptyCell);
    emptyCell.setRowspan(2);

    PdfPCell PatientCell = new PdfPCell(createPatientTable(requisition.getPatientQuantifications()));
    noBorder(regimeCell);

    Chunk chunk = new Chunk("Observações: " + requisition.getClientSubmittedNotes(), fontBody);
    chunk.setUnderline(1f, -2f);
    Phrase comments = new Phrase(chunk);
    PdfPCell commentCell = new PdfPCell(comments);
    noBorder(commentCell);

    section.addCell(regimeCell);
    section.addCell(emptyCell);
    section.addCell(PatientCell);
    section.addCell(commentCell);
    return section;
  }

  /*
  * Create Regime Table
   */
  private PdfPTable createRegimeTable(List<RegimenLineItem> regimenLineItems, int total) throws DocumentException {
    PdfPTable regimeTable = new PdfPTable(3);

    regimeTable.setWidthPercentage(50f);
    regimeTable.setSpacingBefore(2);
    regimeTable.setWidths(new float[]{2f, 6f, 2f});

    addRegimeTableHeaders(regimeTable);
    addRegimeTableContents(regimeTable,regimenLineItems);
    addRegimeTableTotalCell(regimeTable, total);

    return regimeTable;
  }
  private void addRegimeTableHeaders(PdfPTable regimeTable) {
    PdfPCell code = new PdfPCell(new Paragraph("Código", font8Bold));
    positioningCenter(code);
    code.setBackgroundColor(colorLightGray);

    PdfPCell regime = new PdfPCell(new Paragraph("REGIME TERAPÊUTICO", font8Bold));
    positioningCenter(regime);
    regime.setBackgroundColor(colorLightGray);

    PdfPCell total = new PdfPCell(new Paragraph("Total doentes", font8Bold));
    positioningCenter(total);
    total.setBackgroundColor(colorLightGray);

    regimeTable.addCell(code);
    regimeTable.addCell(regime);
    regimeTable.addCell(total);
  }
  private void addRegimeTableContents(PdfPTable regimeTable, List<RegimenLineItem> regimenLineItems) {
    List<RegimenLineItem> adultRegimenLineItems = regimenLineItems.subList(0, 8);
    List<RegimenLineItem> childrenRegimenLineItems = regimenLineItems.subList(8, 18);

    // First section of regime table
    for (RegimenLineItem adultRegimenLineItem : adultRegimenLineItems){
      addRegimeItem(regimeTable,adultRegimenLineItem,colorGreen);
    }
    // Empty section between
    addRegimeItem(regimeTable, null, colorGreen);
    addRegimeItem(regimeTable, null, colorGreen);

    // Second section of regime table
    for (RegimenLineItem childrenRegimenLineItem : childrenRegimenLineItems){
      addRegimeItem(regimeTable,childrenRegimenLineItem,colorYellow);
    }

    // Empty section between
    addRegimeItem(regimeTable, null, colorYellow);
    addRegimeItem(regimeTable, null, colorYellow);

  }

  private void addRegimeItem(PdfPTable regimeTable, RegimenLineItem regimenLineItem, BaseColor baseColor) {
    PdfPCell codeCell = new PdfPCell(new Phrase(" ", fontBody));
    codeCell.setBackgroundColor(baseColor);
    regimeTable.addCell(codeCell);

    PdfPCell therapeuticCell = new PdfPCell(new Phrase(regimenLineItem == null ? " " : regimenLineItem.getName(), fontBody));
    therapeuticCell.setBackgroundColor(baseColor);
    regimeTable.addCell(therapeuticCell);

    PdfPCell totalCell = new PdfPCell(new Phrase(regimenLineItem == null ? " " : regimenLineItem.getPatientsOnTreatment().toString(), fontBody));
    totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    regimeTable.addCell(totalCell);
  }
  private void addRegimeTableTotalCell(PdfPTable regimeTable, int total) throws DocumentException {
    PdfPTable totalTable = new PdfPTable(4);
    totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
    totalTable.setWidthPercentage(25f);
    totalTable.setWidths(new float[]{2f, 4f, 2f, 2f});

    PdfPCell cell = new PdfPCell();
    noBorder(cell);

    totalTable.addCell(cell);
    totalTable.addCell(cell);

    PdfPCell totalCell = new PdfPCell(new Phrase("Total : ", fontBody));
    totalCell.setBackgroundColor(colorDarkGray);
    totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    totalTable.addCell(totalCell);

    PdfPCell totalValueCell = new PdfPCell(new Phrase(String.valueOf(total), fontBody));
    positioningCenter(totalValueCell);
    totalTable.addCell(totalValueCell);

    PdfPCell regimeTotalCell = new PdfPCell(totalTable);
    regimeTotalCell.setColspan(3);
    noBorder(regimeTotalCell);
    regimeTable.addCell(regimeTotalCell);
  }

  /*
* Create Patient Table
* */
  private PdfPTable createPatientTable(List<PatientQuantificationLineItem> patientQuantifications) {
    PdfPTable table = new PdfPTable(2);

    Font fontHeaderWhite = new Font(Font.FontFamily.HELVETICA, 5.0f, Font.BOLD, BaseColor.WHITE);
    PdfPCell tableHeader = new PdfPCell(new Phrase("Quantificaçāo dos doentes em TARV",
            fontHeaderWhite));
    tableHeader.setBackgroundColor(BaseColor.BLACK);
    positioningCenter(tableHeader);
    tableHeader.setColspan(2);

    Font fontHeaderBlack = new Font(Font.FontFamily.HELVETICA, 5.0f, Font.BOLD, BaseColor.BLACK);
    PdfPCell typeHeader = new PdfPCell(new Phrase("TIPO", fontHeaderBlack));
    typeHeader.setBackgroundColor(colorGray);
    positioningCenter(typeHeader);

    PdfPCell totalHeader = new PdfPCell(new Phrase("Total", fontHeaderBlack));
    positioningCenter(totalHeader);
    totalHeader.setBackgroundColor(colorGray);

    table.addCell(tableHeader);
    table.addCell(typeHeader);
    table.addCell(totalHeader);

    for (PatientQuantificationLineItem patientLineItem : patientQuantifications) {
      addPatientTableItem(table, patientLineItem);
    }

    return table;
  }

  private void addPatientTableItem(PdfPTable table, PatientQuantificationLineItem patientLineItem) {
    PdfPCell typeCell = new PdfPCell(new Phrase(patientLineItem.getCategory(), fontBody));
    typeCell.setBackgroundColor(colorGray);
    typeCell.setFixedHeight(15f);

    PdfPCell totalCell = new PdfPCell(new Phrase(patientLineItem.getTotal().toString(), fontBody));

    positioningCenter(typeCell);
    positioningCenter(totalCell);

    table.addCell(typeCell);
    table.addCell(totalCell);
  }

  /*
  * Create Footer Section
  * */
  private PdfPTable createFooterSection(List<Signature> rnrSignatures) {
    PdfPTable table = new PdfPTable(3);
    table.setWidthPercentage(100f);
    table.setSpacingBefore(5);

    String submitter = "";
    String approver = "";
    for(Signature signature : rnrSignatures){
      if(signature.getType() == Signature.Type.SUBMITTER){
        submitter = signature.getText();
      }else if (signature.getType() == Signature.Type.APPROVER){
         approver = signature.getText();
      }
    }

    table.addCell(new PdfPCell(new Phrase("Elaborado por : " + submitter, font8Normal)));
    table.addCell(new PdfPCell(new Phrase("Visto : " + approver, font8Normal)));
    table.addCell(new PdfPCell(new Phrase("Data de elaboração :", font8Normal)));

    PdfPCell noteCell = new PdfPCell(
            new Phrase("Nota: Mapa de Preenchimento Obrigatório Mensal HdD em Conjunto com a Farmácia da Unidade Sanitária/ ou DPM\n " +
                    "Versão no 5 14Nov 2012", font8Normal));

    noteCell.setColspan(3);
    noBorder(noteCell);
    table.addCell(noteCell);

    return table;
  }


  /*
  * Public Stying Methods
  * */

  private void positioningCenter(PdfPCell tableHeader) {
    tableHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
    tableHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
  }

  private void noBorder(PdfPCell regimeCell) {
    regimeCell.setBorder(0);
  }


}
