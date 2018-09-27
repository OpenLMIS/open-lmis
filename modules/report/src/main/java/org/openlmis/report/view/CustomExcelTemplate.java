package org.openlmis.report.view;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openlmis.report.view.type.ExcelCellValueSetterService;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomExcelTemplate  extends AbstractXlsxView {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCsvTemplate.class);
    @Getter
    private static final String KEY_EXCEL_CONTENT = "KEY_EXCEL_CONTENT";
    @Getter
    private static final String KEY_EXCEL_HEADERS = "KEY_EXCEL_HEADERS";
    @Getter
    private static final String KEY_EXCEL_TITLES = "KEY_EXCEL_TITLES";
    @Getter
    private static final String KEY_EXCEL_LEGENDA = "KEY_EXCEL_LEGENDA";
    private static final String KEY_EXCEL_VALUE_SETTER_SERVICE = "KEY_EXCEL_VALUE_SETTER_SERVICE";
    private static final DateFormat DATE_FORMAT_DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat DATE_FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    private static final String REPORT_HEADER_KEY_DATE = "date";



    public static ModelAndView newModelAndView(Object reportContent, Object reportHeaders, Object reportTitles, Object legenda,
                                               ExcelCellValueSetterService excelCellValueSetterService) {
        ModelAndView modelAndView = new ModelAndView(INSTANCE);
        modelAndView.addObject(KEY_EXCEL_CONTENT, reportContent);
        modelAndView.addObject(KEY_EXCEL_HEADERS, reportHeaders);
        modelAndView.addObject(KEY_EXCEL_TITLES, reportTitles);
        modelAndView.addObject(KEY_EXCEL_VALUE_SETTER_SERVICE, excelCellValueSetterService);
        modelAndView.addObject(KEY_EXCEL_LEGENDA, legenda);
        return modelAndView;
    }

    public static ModelAndView newModelAndView(Map<String, Object> model, ExcelCellValueSetterService excelCellValueSetterService) {
        return newModelAndView(model.get(KEY_EXCEL_CONTENT), model.get(KEY_EXCEL_HEADERS),
                model.get(KEY_EXCEL_TITLES), model.get(KEY_EXCEL_LEGENDA),excelCellValueSetterService);
    }

    private static CustomExcelTemplate INSTANCE = new CustomExcelTemplate();

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Object reportContent = model.get(KEY_EXCEL_CONTENT);
        Object reportHeaders = model.get(KEY_EXCEL_HEADERS);
        Object reportTitles = model.get(KEY_EXCEL_TITLES);
        Object reportLegenda = model.get(KEY_EXCEL_LEGENDA);
        ExcelCellValueSetterService excelCellValueSetterService = (ExcelCellValueSetterService)model.get(KEY_EXCEL_VALUE_SETTER_SERVICE);

        if (!(reportContent instanceof Collection)) {
            throw new IllegalArgumentException("Type is not correct");
        }

        if (!(reportHeaders instanceof Map)) {
            throw new IllegalArgumentException("Type is not correct");
        }

        if (null != reportTitles) {
            if (!(reportTitles instanceof List)) {
                throw new IllegalArgumentException("Type is not correct");
            }
        }

        if (null != reportLegenda) {
            if (!(reportLegenda instanceof Collection)) {
                throw new IllegalArgumentException("Type is not correct");
            }
        }

        Collection<?> reportContentList = (Collection<?>) reportContent;

        int rowIndex = 0;
        int headerCellIndex = 0;

        Sheet sheet = workbook.createSheet();

        if (null != reportTitles) {
            List<Object> reportTitleList = (List<Object>)reportTitles;
            for (Object obj : reportTitleList) {
                int titleIndex = 0;
                Row row = sheet.createRow(rowIndex++);
                List<String> list = (List<String>)obj;
                for (String title : list) {
                    row.createCell(titleIndex++).setCellValue(title);
                }
            }
        }


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
                if (cellWrapper instanceof String && headerKeys[cellIndex].equals(REPORT_HEADER_KEY_DATE)) {
                    String formattedDateString = "";
                    try {
                        formattedDateString = DATE_FORMAT_DD_MM_YYYY.format(DATE_FORMAT_YYYY_MM_DD.parse((String)cellWrapper));
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

        if (null != reportLegenda) {
            List<List<Map<String, Object>>> list = (List<List<Map<String, Object>>>)reportLegenda;
            for (List<Map<String, Object>> listMap : list) {
                Row row = sheet.createRow(rowIndex++);
                for (Map<String, Object> map : listMap) {
                    excelCellValueSetterService.setLegendaCellValue(map, workbook, row);
                }
            }
        }
    }
}
