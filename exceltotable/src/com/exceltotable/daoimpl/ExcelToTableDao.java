package com.exceltotable.daoimpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ExcelToTableDao {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://192.168.20.42:3306/dashboard_tools?allowMultiQueries=true";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "opprod";
	static Map<String, String> mapcolumnToDatatype = new HashMap();
	public static String tableName = "";

	static public Connection getConnection() throws ClassNotFoundException,
			SQLException {
		// STEP 2: Register JDBC driver

		Class.forName(JDBC_DRIVER);

		// STEP 3: Open a connection
		System.out.println("Connecting to database...");
		return DriverManager.getConnection(DB_URL, USER, PASS);

	}

	public static Map getExplicitMappingColumnNames(final String filename)
			throws ClassNotFoundException {
		Connection conn = null;
		Map<String, String> explicitMappedcolumns = new HashMap<String, String>();
		try {

			// STEP 4: Execute a query
			System.out.println("Creating statement...");
			String sql = "select tablename from excelfilename where ExcelFileName='"
					+ filename + "')";
			conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			tableName = rs.getObject("tablename").toString();

			sql = "select excelColumn,tableColumn from exceltablemapper where idExcelfileName="
					+ "(select id from excelfilename where ExcelFileName='"
					+ filename + "')";
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery(sql);

			while (rs.next()) {

				explicitMappedcolumns.put(rs.getObject("excelColumn")
						.toString(), rs.getObject("tableColumn").toString());

			}

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		}
		return explicitMappedcolumns;

	}

	public static List gettableColumnNames(final String filename)
			throws ClassNotFoundException {
		Connection conn = null;
		List<String> tablecolumnnames = new ArrayList<String>();
		try {

			// STEP 4: Execute a query
			System.out.println("Creating statement...");
			String sql = "SELECT COLUMN_NAME,DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = (select tablename from excelfilename where ExcelFileName='"
					+ filename + "');";
			conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {

				tablecolumnnames.add(rs.getObject("COLUMN_NAME").toString());
				mapcolumnToDatatype.put(rs.getObject("COLUMN_NAME").toString(),
						rs.getObject("DATA_TYPE").toString());
			}

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		}
		return tablecolumnnames;

	}

	public static void insertToTable(Map columnmapper, List ExcelColumns,
			XSSFSheet sheet) throws SQLException, ClassNotFoundException {
		Iterator<Row> rowIterator = sheet.iterator();
		String forvalues = "values";
		int ignoureFirst = 1;
		int temp = 0;
		while (rowIterator.hasNext()) {
			if (ignoureFirst == 1) {
				rowIterator.next();
				ignoureFirst++;
				continue;
			}
			Row row = rowIterator.next();
			if (temp != 0 && temp != row.getLastCellNum())
				continue;
			temp = row.getLastCellNum();
			List<Object> arr = new ArrayList<Object>();
			// For each row, iterate through all the columns
			Iterator<Cell> cellIterator = row.cellIterator();
			forvalues = forvalues + "(";
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_NUMERIC:
					arr.add(convertExcelToMysqlDate(cell));
					break;
				case Cell.CELL_TYPE_STRING:
					arr.add(cell.getStringCellValue());
					break;
				case Cell.CELL_TYPE_BLANK:
					arr.add("");
					break;
				default:
					arr.add(new DataFormatter().formatCellValue(cell));
				}
			}
			System.out.println(arr);
			forvalues = forvalues + joinbasedOndatatype(columnmapper, arr)
					+ "),";
		}
		forvalues = forvalues.substring(0, forvalues.length() - 1);
		forvalues = "Insert into " + tableName + " ("
				+ StringUtils.join(new ArrayList(columnmapper.values()), ',')
				+ ")" + forvalues;
		System.out.println(forvalues);
		
		 Connection conn = getConnection(); PreparedStatement stmt =
		 conn.prepareStatement(forvalues); stmt.execute(forvalues);
		 

	}

	public static String joinbasedOndatatype(Map<String, String> columnmapper,
			List listOfvalues) {
		String buildstr = "";
		int walk = 0;
		for (Entry<String, String> column : columnmapper.entrySet()) {
			if (mapcolumnToDatatype.get(column.getValue()).equals("int"))
				buildstr = buildstr + listOfvalues.get(walk);
			else
				buildstr = buildstr + "'" + listOfvalues.get(walk) + "'";
			buildstr = buildstr + ",";
			walk++;
		}
		return buildstr.substring(0, buildstr.length() - 1);
	}

	public static String convertExcelToMysqlDate(Cell cell) {
		if (DateUtil.isCellDateFormatted(cell)) {
			// System.out.println("date value: "+cell.getDateCellValue());
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			return sdf.format(cell.getDateCellValue());
		} else
			return Double.valueOf(cell.getNumericCellValue()).toString();

	}

}