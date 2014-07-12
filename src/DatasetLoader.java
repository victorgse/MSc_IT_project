import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class DatasetLoader {
	
	// instance variables
	File excel;
	FileInputStream fis;
	HSSFWorkbook wb;
	HSSFSheet ws;
	int rowNum;
	int colNum;
	String[][] data;
	String[] dataTypes;
	
	// constructor
	public DatasetLoader(File excel) throws Exception {
		this.excel = excel;
		fis = new FileInputStream(excel);
		wb  = new HSSFWorkbook(fis);
		ws = wb.getSheetAt(0);
		rowNum = ws.getLastRowNum();
		colNum = ws.getRow(0).getLastCellNum();
		data = new String[rowNum][colNum];
		dataTypes = new String[colNum];
		getDataFromFile();
		loadDataIntoDatabase();
	}
	
	public void getDataFromFile() {
		for (int i = 0; i < rowNum; i++) {
			HSSFRow row = ws.getRow(i);
			for (int j = 0; j < colNum; j++) {
				HSSFCell cell = row.getCell(j);
				int type = cell.getCellType();
				switch (type) {
					case 0:
						dataTypes[j] = "numeric(10, 2)";
						break;
					case 1:
						dataTypes[j] = "varchar(30)";
						break;
					default:
						dataTypes[j] = "varchar(30)";
				}
				String value = cellToString(cell);
				data[i][j] = value;
			}
		}
	}
	
	public void loadDataIntoDatabase() throws SQLException {
		// establish connection with database
		Connection con = DriverManager.getConnection("jdbc:derby:datasetsDB;create=true");
		Statement stmt = con.createStatement();
		
		// load schema
		String schema = "create table " + wb.getSheetName(0).replace(" ", "_") + "(";
		for (int i = 0; i < colNum - 1; i++) {
			schema += data[0][i].replace(' ', '_').replace('-', '_') + " " + dataTypes[i] + ", ";
		}
		schema += data[0][colNum-1].replace(' ', '_').replace('-', '_') + " " + dataTypes[colNum-1] + ")";
		stmt.executeUpdate(schema);
		
		// load data items
		for (int i = 1; i < rowNum; i++) {
			String rowValues = "insert into " + wb.getSheetName(0).replace(" ", "_") + " values(";
			for (int j = 0; j < colNum - 1; j++) {
				if (dataTypes[j].equals("varchar(30)")) {
					rowValues += "'" + data[i][j].replace("'", "''") + "', ";
				} else {
					rowValues += data[i][j].replace("'", "''") + ", ";
				}
				
			}
			if (dataTypes[colNum-1].equals("varchar(30)")) {
				rowValues += "'" + data[i][colNum-1].replace("'", "''") + "', ";
			} else {
				rowValues += data[i][colNum-1].replace("'", "''") + ")";
			}
			stmt.executeUpdate(rowValues);
		}
	}
	
	public String cellToString(HSSFCell cell) {
		int type;
		Object result;
		type = cell.getCellType();
		
		switch (type) {
			case 0:
				result = cell.getNumericCellValue();
				break;
			case 1:
				result = cell.getStringCellValue();
				break;
			default:
				result = "";
				//throw new RuntimeException("Cell type not supported!");
		}
		return result.toString();
	}
	
}
