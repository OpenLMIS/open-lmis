package org.openlmis.files.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class ExcelHandler {
	protected static Logger logger = LoggerFactory.getLogger(ExcelHandler.class);

	public static final String VARIABLE_PREFIX = "$";
	public static final String FOLDER_SUFFIX = "./";
	private String templatePath = "";
	private String cachePath = "";

	public ExcelHandler(String templatePath, String cachePath) {

		if (templatePath != null) {
			this.templatePath = templatePath;
		}
		if (!this.templatePath.endsWith(FOLDER_SUFFIX)) {
			this.templatePath = this.templatePath.concat(FOLDER_SUFFIX);
		}
		if (cachePath != null) {
			this.cachePath = cachePath;
		}

		if (!this.cachePath.endsWith(FOLDER_SUFFIX)) {
			this.cachePath = this.cachePath.concat(FOLDER_SUFFIX);
		}
	}

	public Workbook readXssTemplateFile(String templateFileName) {
		InputStream templateIn = getClasspathFileInputStream(templateFileName);

		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(templateIn);
		} catch (FileNotFoundException e) {
			logger.error("Not found file with error:" + e.getMessage());
		} catch (InvalidFormatException e) {
			logger.error("Read file with format error:" + e.getMessage());
		} catch (IOException e) {
			logger.error("Read file with io error:" + e.getMessage());
		}
		return wb;
	}

	private InputStream getClasspathFileInputStream(String templateFileName) {
		return ExcelHandler.class.getClassLoader().getResourceAsStream(templatePath + templateFileName);
	}

	public abstract void createDataRows(Sheet tempSheet, List<Map<String, String>> dataList);

	public Workbook createXssFile(Workbook wb, String fileName) {
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(this.cachePath + fileName);
			wb.write(fileOut);
		} catch (FileNotFoundException e) {
			logger.error("Not found file:" + fileName + " with error:" + e.getMessage());
		} catch (IOException e) {
			logger.error("Create file:" + fileName + " with error:" + e.getMessage());
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e) {
					logger.error("Close file:" + fileName + " stream with error:" + e.getMessage());
				}
			}
		}
		return wb;
	}

	public CellMeta parseCellMeta(Cell rowCell) {
		CellMeta cellMeta = null;
		String var = rowCell.getRichStringCellValue().getString();

		if (var != null && var.startsWith(VARIABLE_PREFIX)) {
			var = var.substring(2, var.length() - 1);
			logger.debug("Variables " + rowCell.getColumnIndex() + " = [" + var + "]");
			cellMeta = new CellMeta(rowCell.getColumnIndex(), var, null, true);
		} else {
			cellMeta = new CellMeta(rowCell.getColumnIndex(), var, var, false);
		}
		return cellMeta;

	}
}

