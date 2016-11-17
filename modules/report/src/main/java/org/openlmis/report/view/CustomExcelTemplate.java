package org.openlmis.report.view;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openlmis.report.model.TracerDrugRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

public class CustomExcelTemplate  extends AbstractXlsxView {

    private static final String KEY_EXCEL_DATA = "KEY_EXCEL_DATA";

    public static ModelAndView newModelAndView(Object data) {
        ModelAndView modelAndView = new ModelAndView(INSTANCE);
        modelAndView.addObject(KEY_EXCEL_DATA, data);
        return modelAndView;
    }

    private static CustomExcelTemplate INSTANCE = new CustomExcelTemplate();

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

        @SuppressWarnings("unchecked")
        Object data = model.get(KEY_EXCEL_DATA);

        if (!(data instanceof Collection)) {
            throw new IllegalArgumentException("Type is not correct");
        }
        Collection<?> list = (Collection<?>) data;

        int rowIndex = 0;

        Sheet sheet = workbook.createSheet();
        Row header = sheet.createRow(rowIndex++);

        header.createCell(0).setCellValue("Drug Code");
        header.createCell(1).setCellValue("Drug Name");
        header.createCell(2).setCellValue("Province");
        header.createCell(3).setCellValue("District");
        header.createCell(4).setCellValue("Facility");
        header.createCell(5).setCellValue("Drug Quantity");
        header.createCell(6).setCellValue("Date");


        for (Object o : list) {
            TracerDrugRequest item = (TracerDrugRequest) o;
            Row itemRow = sheet.createRow(rowIndex++);

            itemRow.createCell(0).setCellValue(item.getDrugCode());
            itemRow.createCell(1).setCellValue(item.getDrugName());
            itemRow.createCell(2).setCellValue(item.getProvince());
            itemRow.createCell(3).setCellValue(item.getDistrict());
            itemRow.createCell(4).setCellValue(item.getFacility());
            itemRow.createCell(5).setCellValue(item.getQuantity());
            itemRow.createCell(6).setCellValue(item.getDate());
        }
    }
}
