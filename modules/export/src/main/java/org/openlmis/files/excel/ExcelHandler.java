package org.openlmis.files.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;
import java.util.Map;

public abstract class ExcelHandler {

	public enum PathType{
		FILE,
		CLASSPATH
	}

	protected static Logger logger = LoggerFactory.getLogger(ExcelHandler.class);
	public static final String VARIABLE_PREFIX = "$";

	public static final String FOLDER_SUFFIX = "/";

	@Value("${email.attachment.template.path}")
	protected String templatePath;

	@Value("${email.attachment.cache.path}")
	protected String cachePath;

	@PostConstruct
	public void init() {

		if (!this.templatePath.endsWith(FOLDER_SUFFIX)) {
			this.templatePath = this.templatePath.concat(FOLDER_SUFFIX);
		}

		if (!this.cachePath.endsWith(FOLDER_SUFFIX)) {
			this.cachePath = this.cachePath.concat(FOLDER_SUFFIX);
		}
	}

	public Workbook readXssTemplateFile(String templateFileName, PathType type ) {

		Workbook wb = null;
		try {
			InputStream templateIn = getClasspathFileInputStream(templateFileName, type);
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

	public InputStream getClasspathFileInputStream(String templateFileName, PathType type) throws FileNotFoundException {
		switch(type){
			case FILE:
				return new FileInputStream(new File(this.templatePath+templateFileName));
			case CLASSPATH:
				return ExcelHandler.class.getClassLoader().getResourceAsStream(templateFileName);
			default:
				throw new IllegalArgumentException("Path type not supported!");
		}
	}

	public abstract void createDataRows(Sheet tempSheet, List<Map<String, String>> dataList);

	public String createXssFile(Workbook wb, String fileName) {
		FileOutputStream fileOut = null;
		String filePath = this.cachePath + fileName;
		try {
			fileOut = new FileOutputStream(filePath);
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
		return filePath;
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

