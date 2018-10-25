package org.openlmis.report.view.type.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.openlmis.report.view.type.IExcelCellValueSetter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("integer")
public class IntegerCellValueSetter implements IExcelCellValueSetter {
    @Override
    public void setCellValue(Map<String, Object> params, Cell cell) {
        try {
            cell.setCellValue((double)Integer.parseInt(params.get("value").toString()));
        } catch (RuntimeException ex) {

        } finally {
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        }
    }
}
