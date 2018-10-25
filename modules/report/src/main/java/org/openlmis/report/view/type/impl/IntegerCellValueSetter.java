package org.openlmis.report.view.type.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.openlmis.report.view.type.IExcelCellValueSetter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("integer")
public class IntegerCellValueSetter implements IExcelCellValueSetter {
    @Override
    public void setCellValue(Map<String, Object> params, Workbook workbook, Cell cell) {
        try {
            cell.setCellValue((double)Integer.parseInt(params.get("value").toString()));
        } catch (RuntimeException ex) {

        }
        XSSFDataFormat format = (XSSFDataFormat)workbook.createDataFormat();
        XSSFCellStyle style = (XSSFCellStyle)workbook.createCellStyle();
        style.setDataFormat(format.getFormat("0.0"));
        cell.setCellStyle(style);
    }
}
