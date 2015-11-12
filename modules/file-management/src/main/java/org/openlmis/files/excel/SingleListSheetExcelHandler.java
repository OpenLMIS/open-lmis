package org.openlmis.files.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SingleListSheetExcelHandler extends ExcelHandler {

	public SingleListSheetExcelHandler(String templatePath, String cachePath) {
		super(templatePath, cachePath);
	}

	public void createDataRows(Sheet tempSheet, List<Map<String, String>> dataList) {
		//parse data variable from template
		Map<String, CellMeta> tempDataColumns = parseTemplateVariables(tempSheet);
		//Write data
		for (int l = 0; l < dataList.size(); l++) {
			Map<String, String> oneData = dataList.get(l);
			Row dataRow = tempSheet.createRow((l + 1));
			for (Map.Entry<String, CellMeta> cellMetaEntry : tempDataColumns.entrySet()) {
				createRowCell(cellMetaEntry.getValue(), oneData, dataRow);
			}
		}
	}

	public Map<String, CellMeta> parseTemplateVariables(Sheet tempSheet) {

		Row variablesTemplate = tempSheet.getRow(1);
		Map<String, CellMeta> tempDataColumns = new HashMap<>();
		Iterator<Cell> variables = variablesTemplate.cellIterator();
		while (variables.hasNext()) {
			Cell rowCell = variables.next();
			CellMeta cellMeta = parseCellMeta(rowCell);
			tempDataColumns.put(cellMeta.getName(), cellMeta);
		}
		return tempDataColumns;
	}


	public Cell createRowCell(CellMeta cellMeta, Map<String, String> data, Row dataRow) {
		logger.debug("createRowCell = [" + cellMeta + "]");
		Cell cell = dataRow.createCell(cellMeta.getColumn());
		if (cellMeta.isVariable()) {
			String cellData = data.get(cellMeta.getName());
			cell.setCellValue(cellData);
		} else {
			cell.setCellValue(cellMeta.getDisplay());
		}
		return cell;
	}
	
}

