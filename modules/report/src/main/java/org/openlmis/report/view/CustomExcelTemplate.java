package org.openlmis.report.view;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CustomExcelTemplate  extends AbstractXlsxView {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCsvTemplate.class);
    @Getter
    private static final String KEY_EXCEL_CONTENT = "KEY_EXCEL_CONTENT";
    @Getter
    private static final String KEY_EXCEL_HEADERS = "KEY_EXCEL_HEADERS";
    private static final DateFormat DATE_FORMAT_DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat DATE_FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    private static final String REPORT_HEADER_KEY_DATE = "date";


    public static ModelAndView newModelAndView(Object reportContent, Object reportHeaders) {
        ModelAndView modelAndView = new ModelAndView(INSTANCE);
        modelAndView.addObject(KEY_EXCEL_CONTENT, reportContent);
        modelAndView.addObject(KEY_EXCEL_HEADERS, reportHeaders);
        return modelAndView;
    }

    public static ModelAndView newModelAndView(Map<String, Object> model) {
        return newModelAndView(model.get(KEY_EXCEL_CONTENT), model.get(KEY_EXCEL_HEADERS));
    }

    private static CustomExcelTemplate INSTANCE = new CustomExcelTemplate();

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Object reportContent = model.get(KEY_EXCEL_CONTENT);
        Object reportHeaders = model.get(KEY_EXCEL_HEADERS);

        if (!(reportContent instanceof Collection)) {
            throw new IllegalArgumentException("Type is not correct");
        }

        if (!(reportHeaders instanceof Map)) {
            throw new IllegalArgumentException("Type is not correct");
        }

        Collection<?> reportContentList = (Collection<?>) reportContent;

        int rowIndex = 0;
        int headerCellIndex = 0;

        Sheet sheet = workbook.createSheet();
        Row header = sheet.createRow(rowIndex++);

        Collection reportHeaderValues = ((LinkedHashMap) reportHeaders).values();
        Set reportHeaderKeySet = ((LinkedHashMap) reportHeaders).keySet();

        String[] headerValues = (String[]) reportHeaderValues.toArray(new String[0]);
        String[] headerKeys = (String[]) reportHeaderKeySet.toArray(new String[0]);

        for (String reportHeader : headerValues) {
            header.createCell(headerCellIndex).setCellValue(reportHeader);
            headerCellIndex++;
        }

        for (Object reportContentMap : reportContentList) {
            Row itemRow = sheet.createRow(rowIndex++);

            for (int cellIndex = 0; cellIndex < headerKeys.length; cellIndex++) {
                Object cellWrapper = ((HashMap) reportContentMap).get(headerKeys[cellIndex]);
                String cellValue = null;
                Map<String, Object> styleMap = null;
                if (cellWrapper instanceof Map) {
                    Map<String, Object> tmp = (Map<String, Object>)cellWrapper;
                    cellValue = (String)tmp.get("value");
                    styleMap = (Map<String, Object>)tmp.get("style");
                } else {
                    cellValue = (String)cellWrapper;
                }
                if (headerKeys[cellIndex].equals(REPORT_HEADER_KEY_DATE)) {
                    String formattedDateString = "";
                    try {
                        formattedDateString = DATE_FORMAT_DD_MM_YYYY.format(DATE_FORMAT_YYYY_MM_DD.parse(cellValue));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        LOGGER.error("Date parse error: ", e);
                    }
                    itemRow.createCell(cellIndex).setCellValue(formattedDateString);
                } else {
                    itemRow.createCell(cellIndex).setCellValue(cellValue);
                }

                if (null != styleMap) {
                    Cell cell = itemRow.getCell(itemRow.getLastCellNum() - 1);
                    cell.setCellStyle(fillCellStyle(styleMap, workbook.createCellStyle()));
                }
            }
        }
    }

    private CellStyle fillCellStyle(Map<String, Object> styleMap, CellStyle cellStyle) {
        if (styleMap.containsKey("color")) {
            cellStyle.setFillForegroundColor((short) styleMap.get("color"));
            cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }
        return cellStyle;
    }
}
