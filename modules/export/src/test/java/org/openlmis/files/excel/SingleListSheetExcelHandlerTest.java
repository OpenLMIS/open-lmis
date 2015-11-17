package org.openlmis.files.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SingleListSheetExcelHandlerTest {

	private SingleListSheetExcelHandler util;
	Workbook tempWb;
	Sheet tempSheet;

	@Before
	public void setUp() throws Exception {
		util = new SingleListSheetExcelHandler();
		tempWb = util.readXssTemplateFile("template_test.xlsx", ExcelHandler.PathType.CLASSPATH);
		tempSheet = tempWb.getSheetAt(0);
	}

	@Test
	public void shouldReturnCellRowGivenData(){
		CellMeta cellMeta = new CellMeta(0, "facilityName", "haha", true);
		Map<String, String> data = new HashMap<>();
		data.put("facilityName",  "facility1");

		Row dataRow = tempSheet.createRow(1);
		Cell rowCell = util.createRowCell(cellMeta, data, dataRow);

		assertThat(rowCell.getStringCellValue(), is("facility1"));
	}
	@Test
	public void shouldReturnCellDisplayGivenData(){
		CellMeta cellMeta = new CellMeta(0, "facilityName", "facility2", false);
		Map<String, String> data = new HashMap<>();
		data.put("facilityName",  "facility1");

		Row dataRow = tempSheet.createRow(1);
		Cell rowCell = util.createRowCell(cellMeta, data, dataRow);

		assertThat(rowCell.getStringCellValue(), is("facility2"));
	}

	@Test
	public void shouldReturnKeyCellMetaMapGivenTempSheet(){
		Map<String, CellMeta> cells = util.parseTemplateVariables(tempSheet);
		assertThat(cells.get("facilityName").getName(), is("facilityName"));
		assertThat(cells.get("facilityName").getColumn(), is(0));
		assertThat(cells.get("facilityName").isVariable(), is(true));
		assertThat(cells.get("TEXT").getDisplay(), is("TEXT"));
		assertThat(cells.get("TEXT").isVariable(), is(false));
	}

	@Test
	public void shouldSetCellsGivenDataList(){
		Map<String, String> row1 = new HashMap<>();
		row1.put("beginningBalance", "balanceX");
		row1.put("quantityDispensed", "321");
		Map<String, String> row2 = new HashMap<>();
		row2.put("beginningBalance", "balanceY");
		row2.put("quantityDispensed", "123");

		List<Map<String, String>> dataList = new ArrayList<>();
		dataList.add(row1);
		dataList.add(row2);

		util.createDataRows(tempSheet, dataList);

		assertThat(tempSheet.getRow(0).getCell(3).getStringCellValue(), is("BOP"));
		assertThat(tempSheet.getRow(1).getCell(0).getStringCellValue(), is(""));
		assertThat(tempSheet.getRow(1).getCell(3).getStringCellValue(), is("balanceX"));
		assertThat(tempSheet.getRow(1).getCell(4).getStringCellValue(), is("321"));
		assertThat(tempSheet.getRow(1).getCell(2).getStringCellValue(), is("TEXT"));
		assertThat(tempSheet.getRow(2).getCell(3).getStringCellValue(), is("balanceY"));
		assertThat(tempSheet.getRow(2).getCell(4).getStringCellValue(), is("123"));
		assertThat(tempSheet.getRow(2).getCell(2).getStringCellValue(), is("TEXT"));
	}
}