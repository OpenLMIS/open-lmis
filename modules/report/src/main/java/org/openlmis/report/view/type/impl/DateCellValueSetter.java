package org.openlmis.report.view.type.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.report.view.type.IExcelCellValueSetter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("date")
public class DateCellValueSetter implements IExcelCellValueSetter {

    @Override
    public void setCellValue(Map<String, Object> params, Workbook workbook, Cell cell) {
        String pattern = "yyyy-MM-dd";
        if (params.containsKey("style")) {
            Map<String, Object> styleMap =  (Map<String,Object>)params.get("style");
            if (styleMap.containsKey("dataPattern")) {
                pattern = styleMap.get("dataPattern").toString();
            }

        }

        if (null != params.get("value")) {
            try {
                cell.setCellValue(DateUtil.parseDate(params.get("value").toString(), pattern));
            } catch (RuntimeException ex) {
                cell.setCellValue("");
            }
            return;
        }
        cell.setCellValue("");
    }
}
