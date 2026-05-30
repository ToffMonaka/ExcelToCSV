/**
 * @file
 * @brief Mainファイル
 */

package com.toff_monaka.excel_to_csv;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @brief Mainクラス
 */
public class Main
{
	private Main(){}

	public static final String[] EXCEL_FILE_EXTENSION_ARRAY = new String[] {"xls", "xlsx"};
	public static final String CSV_FILE_EXTENSION = "csv";
	public static final String COMMENT_PREFIX = "_";
	public static final String SHEET_NAME = "テーブル";
	public static final String INVALID_COLUMN_NAME = "invalid_flg";
	public static final String[] INVALID_COLUMN_VALUE_ARRAY = new String[] {"1", "1.0", ""};
	public static final String LAST_COLUMN_NAME = "invalid_flg";
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Tokyo"));

	/**
	 * @brief main関数
	 * @param app_param_ary (application_parameter_array)
	 */
	public static void main(String[] app_param_ary)
	{
		System.out.println(Util.PROJECT.NAME);
		System.out.println(Util.PROJECT.VERSION_NAME);
		System.out.println(Util.PROJECT.COMPANY_NAME);
		System.out.println();

		if (app_param_ary.length != 4) {
			System.out.println("Error: アプリケーションパラメーター数が異常です。: excel_dir_path csv_dir_path charset_name newline_name");
			
			return;
		}
		
		var excel_dir_path = app_param_ary[0];
		
		if (excel_dir_path.isEmpty()) {
			System.out.println("Error: excel_dir_pathが空です。");

			return;
		} else {
			var excel_dir = new File(excel_dir_path);

			if ((!excel_dir.exists())
			|| (!excel_dir.isDirectory())) {
				System.out.println("Error: excel_dir_pathが存在しないディレクトリです。: " + excel_dir_path);

				return;
			}
		}
		
		var csv_dir_path = app_param_ary[1];

		if (csv_dir_path.isEmpty()) {
			System.out.println("Error: csv_dir_pathが空です。");
			
			return;
		} else {
			var csv_dir = new File(csv_dir_path);

			if ((!csv_dir.exists())
			|| (!csv_dir.isDirectory())) {
				System.out.println("Error: csv_dir_pathが存在しないディレクトリです。: " + csv_dir_path);

				return;
			}
		}

		var charset_name = app_param_ary[2];

		if (charset_name.isEmpty()) {
			System.out.println("Error: charset_nameが空です。");
			
			return;
		}

		var newline_name = app_param_ary[3];
		var newline_code = "";

		if (newline_name.isEmpty()) {
			System.out.println("Error: newline_nameが空です。");
			
			return;
		} else {
			if (newline_name.equals("CRLF")) {
				newline_code = "\r\n";
			} else if (newline_name.equals("LF")) {
				newline_code = "\n";
			} else {
				System.out.println("Error: newline_nameが存在しない改行です。: " + newline_name);

				return;
			}
		}

		System.out.println("excel_dir_path=" + excel_dir_path);
		System.out.println("csv_dir_path=" + csv_dir_path);
		System.out.println("charset_name=" + charset_name);
		System.out.println("newline_name=" + newline_name);
		System.out.println();

		var excel_file_name_ary = com.toff_monaka.tml.data.DataUtil.getFileNameArray(excel_dir_path, Main.EXCEL_FILE_EXTENSION_ARRAY);

		if (excel_file_name_ary == null) {
			System.out.println("Error: Excelファイル名配列の作成に失敗しました。");

			return;
		}

		for (var excel_file_name : excel_file_name_ary) {
			System.out.println("変換開始: " + excel_file_name);

			{// メモリリフレッシュ
				// 待機
				try {
					Thread.sleep(40L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				System.gc();
				
				// 待機
				try {
					Thread.sleep(40L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			var convert_res = 0;

			do {
				var tbl_name = excel_file_name.substring(0, excel_file_name.lastIndexOf('.'));

				if (tbl_name.isEmpty()) {
					System.out.println("Error: テーブル名が空です。");

					convert_res = -1;
					
					break;
				}

				System.out.println("テーブル名=" + tbl_name);

				if (!Main.COMMENT_PREFIX.isEmpty()) {
					if (tbl_name.indexOf(Main.COMMENT_PREFIX) == 0) {
						System.out.println("コメントテーブルです。");
						
						break;
					}
				}

				var file_dat = new StringBuilder();

				// 読み込み
				try (var file_is = new FileInputStream(excel_dir_path + "/" + excel_file_name);
					var workbook = WorkbookFactory.create(file_is)) {
					var sheet = workbook.getSheet(Main.SHEET_NAME);

					if (sheet == null) {
						System.out.println("Error: シートが存在しません。: " + Main.SHEET_NAME);

						convert_res = -1;

						break;
					}

					var row_cnt = sheet.getLastRowNum() - sheet.getFirstRowNum() + 1;

					if (row_cnt <= 0) {
						System.out.println("Error: 行数が異常です。: " + row_cnt);

						convert_res = -1;

						break;
					}

					var column_name_row = sheet.getRow(0);

					if (column_name_row == null) {
						System.out.println("Error: 列名の行がありません。");

						convert_res = -1;

						break;
					}

					var column_cnt = column_name_row.getLastCellNum() - column_name_row.getFirstCellNum();
					
					if (column_cnt <= 0) {
						System.out.println("Error: 列数が異常です。: " + column_cnt);
						
						convert_res = -1;
						
						break;
					}

					System.out.println("行数=" + row_cnt);
					System.out.println("列数=" + column_cnt);

					var my_sheet = Main._getMySheet(row_cnt, column_cnt, sheet);

					for (int column_i = 0; column_i < column_cnt; ++column_i) {
						var column_name = my_sheet[0][column_i];

						if (column_name.isEmpty()) {
							System.out.println("Error: 列名が空です。");
							
							convert_res = -1;
							
							break;
						}
					}
					
					if (convert_res < 0) {
						break;
					}

					var invalid_column_index = -1;

					if (!Main.INVALID_COLUMN_NAME.isEmpty()) {
						for (int column_i = 0; column_i < column_cnt; ++column_i) {
							var column_name = my_sheet[0][column_i];

							if (column_name.equals(Main.INVALID_COLUMN_NAME)) {
								invalid_column_index = column_i;
								
								break;
							}
						}

						if (invalid_column_index < 0) {
							System.out.println("Error: 無効列名がありません。: " + Main.INVALID_COLUMN_NAME);
							
							convert_res = -1;
							
							break;
						}
					}

					if (!Main.LAST_COLUMN_NAME.isEmpty()) {
						var column_name = my_sheet[0][column_cnt - 1];
						
						if (!column_name.equals(Main.LAST_COLUMN_NAME)) {
							System.out.println("Error: 最後の列名がありません。: " + Main.LAST_COLUMN_NAME);
							
							convert_res = -1;
							
							break;
						}
					}

					for (int row_i = 1; row_i < row_cnt; ++row_i) {
						System.out.print("\r[" + row_i + "/" + (row_cnt - 1) + "]                    ");
						
						// 待機
						if ((row_i % 200) == 0) {
							try {
								Thread.sleep(20L);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						if (invalid_column_index >= 0) {
							var column_val = my_sheet[row_i][invalid_column_index];
							
							var invalid_column_val_flg = false;
							
							for (int invalid_column_val_i = 0; invalid_column_val_i < Main.INVALID_COLUMN_VALUE_ARRAY.length; ++invalid_column_val_i) {
								if (column_val.equals(Main.INVALID_COLUMN_VALUE_ARRAY[invalid_column_val_i])) {
									invalid_column_val_flg = true;
								
									break;
								}
							}
							
							if (invalid_column_val_flg) {
								continue;
							}
						}

						var column_add_flg = false;

						for (int column_i = 0; column_i < column_cnt; ++column_i) {
							var column_name = my_sheet[0][column_i];

							if (!Main.COMMENT_PREFIX.isEmpty()) {
								if (column_name.indexOf(Main.COMMENT_PREFIX) == 0) {
									continue;
								}
							}
							
							var column_val = my_sheet[row_i][column_i];

							if (column_add_flg) {
								file_dat.append(",");
							}
							
							column_val = column_val.replaceAll("\"", "\"\"");
							
							file_dat.append("\"" + column_val + "\"");
							
							column_add_flg = true;
						}

						if (column_add_flg) {
							file_dat.append(newline_code);
						}
					}

					if (row_cnt > 1) {
						System.out.println();
					}
				} catch (IOException e) {
					e.printStackTrace();

					System.out.println("Error: 読み込みに失敗しました。");
					
					convert_res = -1;
					
					break;
				}

				// 書き込み
				try (var file_os = new FileOutputStream(csv_dir_path + "/" + tbl_name + "." + Main.CSV_FILE_EXTENSION);
					var osw = new OutputStreamWriter(file_os, charset_name);
					var bw = new BufferedWriter(osw)) {

					bw.write(file_dat.toString());
				} catch (IOException e) {
					e.printStackTrace();

					System.out.println("Error: 書き込みに失敗しました。");
					
					convert_res = -1;
					
					break;
				}
			} while (false);

			if (convert_res < 0) {
				System.out.println("Error: 変換に失敗しました。");

				break;
			}

			System.out.println("変換終了: " + excel_file_name);
			System.out.println();
		}

		return;
	}

	/**
	 * @brief _getMySheet関数
	 * @param row_cnt (row_count)
	 * @param column_cnt (column_count)
	 * @param sheet (sheet)
     * @return my_sheet (my_sheet)
	 */
	private static String[][] _getMySheet(int row_cnt, int column_cnt, Sheet sheet)
	{
		var my_sheet =  new String[row_cnt][column_cnt];

		for (int row_i = 0; row_i < row_cnt; ++row_i) {
			var row = sheet.getRow(row_i);

			for (int cell_i = 0; cell_i < column_cnt; ++cell_i) {
				var val = "";
				
				if (row != null) {
					var cell = row.getCell(cell_i);
					
					if (cell != null) {
						switch (cell.getCellType()) {
						case STRING: {
							val = cell.getStringCellValue();

							break;
						}
						case NUMERIC: {
							if (DateUtil.isCellDateFormatted(cell)) {
								val = Main.DATE_TIME_FORMATTER.format(cell.getDateCellValue().toInstant());
							} else {
								val = String.valueOf(cell.getNumericCellValue());

								{// 末尾の｢.0｣を削除
									int str_index = val.lastIndexOf(".0");
									
									if ((str_index >= 0) && (str_index == (val.length() - 2))) {
										val = val.substring(0, str_index);
									}
								}
							}

							break;
						}
						case BOOLEAN: {
							val = (cell.getBooleanCellValue()) ? "1" : "0";

							break;
						}
						case FORMULA: {
							val = String.valueOf(cell.getNumericCellValue());

							{// 末尾の｢.0｣を削除
								int str_index = val.lastIndexOf(".0");
								
								if ((str_index >= 0) && (str_index == (val.length() - 2))) {
									val = val.substring(0, str_index);
								}
							}

							break;
						}
						default: {
							break;
						}
						}
					}
				}
				
				my_sheet[row_i][cell_i] = val;
			}
		}

		return (my_sheet);
	}
}
