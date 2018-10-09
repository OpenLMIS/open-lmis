package org.openlmis.report.view;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.openlmis.report.view.type.ExcelCellValueSetterService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class CustomExcelTemplate  extends AbstractXlsxView {

    public static ModelAndView newModelAndView(Object reportContent, Object reportHeaders, Object reportTitles, Object legenda,
                                               ExcelCellValueSetterService excelCellValueSetterService) {
        ModelAndView modelAndView = new ModelAndView(INSTANCE);
        modelAndView.addObject(WorkbookCreator.getKEY_EXCEL_CONTENT(), reportContent);
        modelAndView.addObject(WorkbookCreator.getKEY_EXCEL_HEADERS(), reportHeaders);
        modelAndView.addObject(WorkbookCreator.getKEY_EXCEL_TITLES(), reportTitles);
        modelAndView.addObject(WorkbookCreator.getKEY_EXCEL_LEGENDA(), legenda);
        modelAndView.addObject(WorkbookCreator.getKEY_EXCEL_VALUE_SETTER_SERVICE(), excelCellValueSetterService);
        return modelAndView;
    }

    public static ModelAndView newModelAndView(Map<String, Object> model, ExcelCellValueSetterService excelCellValueSetterService) {
        ModelAndView modelAndView = new ModelAndView(INSTANCE);
        modelAndView.addAllObjects(model);
        modelAndView.addObject(WorkbookCreator.getKEY_EXCEL_VALUE_SETTER_SERVICE(), excelCellValueSetterService);
        return modelAndView;
    }

    private static CustomExcelTemplate INSTANCE = new CustomExcelTemplate();

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        WorkbookCreator workbookCreator = new WorkbookCreator(model, workbook);
        workbookCreator.createWorkbook();
    }
}
