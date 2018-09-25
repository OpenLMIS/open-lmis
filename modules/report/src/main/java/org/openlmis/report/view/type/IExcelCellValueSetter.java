package org.openlmis.report.view.type;

import org.apache.poi.ss.usermodel.Cell;

import java.util.Map;

public interface IExcelCellValueSetter {
    void setCellValue(Map<String, Object> params, Cell cell);
}
