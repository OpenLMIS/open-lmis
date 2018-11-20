package org.openlmis.report.view;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openlmis.report.view.type.ExcelCellValueSetterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorkbookCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbookCreator.class);

    @Getter
    private static final String KEY_EXCEL_CONTENT = "KEY_EXCEL_CONTENT";
    @Getter
    private static final String KEY_EXCEL_HEADERS = "KEY_EXCEL_HEADERS";
    @Getter
    private static final String KEY_EXCEL_TITLES = "KEY_EXCEL_TITLES";
    @Getter
    private static final String KEY_EXCEL_LEGENDA = "KEY_EXCEL_LEGENDA";
    @Getter
    private static final String KEY_EXCEL_MERGE = "KEY_EXCEL_MERGE";
    @Getter
    private static final String KEY_EXCEL_VALUE_SETTER_SERVICE = "KEY_EXCEL_VALUE_SETTER_SERVICE";
    private static final DateFormat DATE_FORMAT_DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat DATE_FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    private static final String REPORT_HEADER_KEY_DATE = "date";
    private ExcelCellValueSetterService excelCellValueSetterService;
    private Workbook workbook;
    private Map<String, Object> model;
    private Sheet sheet;

    public WorkbookCreator(Map<String, Object> model, Workbook workbook) {
        this.excelCellValueSetterService = (ExcelCellValueSetterService)model.get(KEY_EXCEL_VALUE_SETTER_SERVICE);
        this.workbook = workbook;
        sheet = this.workbook.createSheet();
        this.model = model;
    }

    private int createReportTitles(int rowIndex) {
        Object reportTitles = model.get(KEY_EXCEL_TITLES);
        if (null == reportTitles) {
            return rowIndex;
        }
        if (!(reportTitles instanceof List)) {
            throw new IllegalArgumentException("Type is not correct");
        }
        Sheet sheet = getSheet();
        List<Object> reportTitleList = (List<Object>) reportTitles;
        for (Object obj : reportTitleList) {
            int titleIndex = 0;
            Row row = sheet.createRow(rowIndex++);
            List<String> list = (List<String>) obj;
            for (String title : list) {
                row.createCell(titleIndex++).setCellValue(title);
            }
        }

        return rowIndex;
    }

    private int createReportContent(int rowIndex) {
        Object reportContent = model.get(KEY_EXCEL_CONTENT);
        Object reportHeaders = model.get(KEY_EXCEL_HEADERS);
        if (!(reportContent instanceof Collection)) {
            throw new IllegalArgumentException("Type is not correct");
        }
        if (!(reportHeaders instanceof Map)) {
            throw new IllegalArgumentException("Type is not correct");
        }

        Collection<?> reportContentList = (Collection<?>) reportContent;
        Sheet sheet = getSheet();
        Row header = sheet.createRow(rowIndex++);

        Collection reportHeaderValues = ((LinkedHashMap) reportHeaders).values();
        Set reportHeaderKeySet = ((LinkedHashMap) reportHeaders).keySet();
        String[] headerValues = (String[]) reportHeaderValues.toArray(new String[0]);
        String[] headerKeys = (String[]) reportHeaderKeySet.toArray(new String[0]);
        int headerCellIndex = 0;
        for (String reportHeader : headerValues) {
            header.createCell(headerCellIndex).setCellValue(reportHeader);
            headerCellIndex++;
        }
        for (Object reportLineContent : reportContentList) {
            Row itemRow = sheet.createRow(rowIndex++);

            for (int cellIndex = 0; cellIndex < headerKeys.length; cellIndex++) {
                Object cellWrapper;
                if (reportLineContent instanceof List) {
                    cellWrapper = ((List)reportLineContent).get(cellIndex);
                } else {
                    cellWrapper = ((HashMap)reportLineContent).get(headerKeys[cellIndex]);
                }
                if (cellWrapper instanceof String && headerKeys[cellIndex].equals(REPORT_HEADER_KEY_DATE)) {
                    String formattedDateString = "";
                    try {
                        formattedDateString = DATE_FORMAT_DD_MM_YYYY.format(DATE_FORMAT_YYYY_MM_DD.parse((String) cellWrapper));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        LOGGER.error("Date parse error: ", e);
                    }
                    itemRow.createCell(cellIndex).setCellValue(formattedDateString);
                } else {
                    excelCellValueSetterService.setCellValue(cellWrapper, workbook, itemRow.createCell(cellIndex));
                }
            }
        }

        return rowIndex;
    }

    private int createLegend(int rowIndex) {
        Object reportLegenda = model.get(KEY_EXCEL_LEGENDA);
        if (null != reportLegenda) {
            if (!(reportLegenda instanceof Collection)) {
                throw new IllegalArgumentException("Type is not correct");
            }
        }
        if (null != reportLegenda) {
            Sheet sheet = getSheet();
            List<List<Map<String, Object>>> list = (List<List<Map<String, Object>>>) reportLegenda;
            for (List<Map<String, Object>> listMap : list) {
                Row row = sheet.createRow(rowIndex++);
                for (Map<String, Object> map : listMap) {
                    excelCellValueSetterService.setLegendaCellValue(map, workbook, row);
                }
            }
        }
        return rowIndex;
    }

    private void mergeCells() {
        if (null != model.get(KEY_EXCEL_MERGE)) {
            Sheet sheet = getSheet();
            List<Map<String, String>> list = (List<Map<String, String>>)model.get(KEY_EXCEL_MERGE);
            for (Map<String, String> map : list) {
                sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("firstRow")), Integer.parseInt(map.get("lastRow")),
                        Integer.parseInt(map.get("firstCol")), Integer.parseInt(map.get("lastCol"))));
            }
        }
    }

    public void createWorkbook() {
        createLegend(createReportContent(createReportTitles(0)));
        mergeCells();
    }

    private Sheet getSheet() {
        return sheet;
    }

}
