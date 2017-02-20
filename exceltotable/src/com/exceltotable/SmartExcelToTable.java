package com.exceltotable;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.exceltotable.daoimpl.ExcelToTableDao;
import com.exceltotable.smartcomparator.SmartComparator;

public class SmartExcelToTable {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {

		kickStartSmartExcelToTable("AssetData.xlsx",
				"D:/MyWs/Dashboard/exceltotable/AssetData.xlsx");

	}

	public static void kickStartSmartExcelToTable(String filename,
			String filepath) throws Exception {

		List Excelcolumnnames = getExcelColumnNames(getExcelSheet(filepath));
		Map ExplicitMap = ExcelToTableDao
				.getExplicitMappingColumnNames(filename);
		List columnNames = ExcelToTableDao.gettableColumnNames(filename);
		Map finalresult = (new SmartComparator(columnNames, Excelcolumnnames,
				ExplicitMap)).findbestMatchColumn();
		ExcelToTableDao.insertToTable(finalresult, Excelcolumnnames,
				getExcelSheet(filepath));
	}

	public static XSSFSheet getExcelSheet(String filepath) {
		XSSFSheet sheet = null;
		try {
			FileInputStream file = new FileInputStream(new File(filepath));

			XSSFWorkbook workbook = new XSSFWorkbook(file);

			sheet = workbook.getSheetAt(0);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sheet;
	}

	public static List<String> getExcelColumnNames(XSSFSheet sheet) {
		Iterator<Row> rowIterator = sheet.iterator();
		List<String> excelcolumnNames = new ArrayList<String>();
		Row row = rowIterator.next();
		// For each row, iterate through all the columns
		Iterator<Cell> cellIterator = row.cellIterator();

		while (cellIterator.hasNext()) {
			excelcolumnNames.add(cellIterator.next().getStringCellValue());
		}
		return excelcolumnNames;
	}

}
