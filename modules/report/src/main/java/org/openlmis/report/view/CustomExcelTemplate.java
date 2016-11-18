package org.openlmis.report.view;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openlmis.report.model.TracerDrugRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomExcelTemplate  extends AbstractXlsxView {

    private static final String KEY_EXCEL_CONTENT = "KEY_EXCEL_CONTENT";
    private static final String KEY_EXCEL_HEADERS = "KEY_EXCEL_HEADERS";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static ModelAndView newModelAndView(Object reportContent, List<String> reportHeaders) {
        ModelAndView modelAndView = new ModelAndView(INSTANCE);
        modelAndView.addObject(KEY_EXCEL_CONTENT, reportContent);
        modelAndView.addObject(KEY_EXCEL_HEADERS, reportHeaders);
        return modelAndView;
    }

    private static CustomExcelTemplate INSTANCE = new CustomExcelTemplate();

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Object reportContent = model.get(KEY_EXCEL_CONTENT);
        Object reportHeaders = model.get(KEY_EXCEL_HEADERS);

        if (!(reportContent instanceof Collection)) {
            throw new IllegalArgumentException("Type is not correct");
        }

        if (!(reportHeaders instanceof Collection)) {
            throw new IllegalArgumentException("Type is not correct");
        }

        Collection<?> reportContentList = (Collection<?>) reportContent;
        Collection<String> reportHeaderList = (Collection<String>) reportHeaders;

        int rowIndex = 0;
        int headerCellIndex = 0;

        Sheet sheet = workbook.createSheet();
        Row header = sheet.createRow(rowIndex++);

        for (String reportHeader : reportHeaderList) {
            header.createCell(headerCellIndex).setCellValue(reportHeader);
            headerCellIndex++;
        }

        for (Object o : reportContentList) {
            TracerDrugRequest item = (TracerDrugRequest) o;
            Row itemRow = sheet.createRow(rowIndex++);

            itemRow.createCell(0).setCellValue(item.getDrugCode());
            itemRow.createCell(1).setCellValue(item.getDrugName());
            itemRow.createCell(2).setCellValue(item.getProvince());
            itemRow.createCell(3).setCellValue(item.getDistrict());
            itemRow.createCell(4).setCellValue(item.getFacility());
            itemRow.createCell(5).setCellValue(item.getQuantity());
            itemRow.createCell(6).setCellValue(DATE_FORMAT.format(item.getDate()));
        }
    }
}
