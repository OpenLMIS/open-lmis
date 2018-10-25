package org.openlmis.report.view.type;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

public interface IExcelCellValueSetter {
    void setCellValue(Map<String, Object> params, Workbook workbook, Cell cell);
}
