/**
 * @file
 * @brief Mainファイル
 */


package com.toff_monaka.excel_to_csv;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.WorkbookFactory;


/**
 * @brief Mainクラス
 */
public class Main
{
	private Main(){}

	/**
	 * @brief main関数
	 * @param app_param_ary(application_parameter_array)
	 */
	public static void main(String[] app_param_ary)
	{
		System.out.println(com.toff_monaka.excel_to_csv.Util.PROJECT.NAME);
		System.out.println(com.toff_monaka.excel_to_csv.Util.PROJECT.VERSION_NAME);
		System.out.println(com.toff_monaka.excel_to_csv.Util.PROJECT.COMPANY_NAME);
		System.out.println();

		if (app_param_ary.length != 4) {
			System.out.println("Error: アプリケーションパラメーター数が異常です。: excel_dir_path csv_dir_path charset newline");
			
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

		var charset = app_param_ary[2];

		if (charset.isEmpty()) {
			System.out.println("Error: charsetが空です。");
			
			return;
		}

		var newline = app_param_ary[3];
		var newline_code = "";

		if (newline.isEmpty()) {
			System.out.println("Error: newlineが空です。");
			
			return;
		} else {
			if (newline.equals("CRLF")) {
				newline_code = "\r\n";
			} else if (newline.equals("LF")) {
				newline_code = "\n";
			} else {
				System.out.println("Error: newlineが存在しない改行です。: " + newline);

				return;
			}
		}

		System.out.println("excel_dir_path=" + excel_dir_path);
		System.out.println("csv_dir_path=" + csv_dir_path);
		System.out.println("charset=" + charset);
		System.out.println("newline=" + newline);
		System.out.println();

		var excel_file_name_ary = com.toff_monaka.lib.data.Util.getFileNameArray(excel_dir_path, new String[] {"xls", "xlsx"});

		if (excel_file_name_ary == null) {
			System.out.println("Error: Excelファイル名配列の作成に失敗しました。");

			return;
		}

		var comment_prefix = "_";
		var sheet_name = "テーブル";
		var invalid_column_name = "invalid_flg";
		var invalid_column_val_ary = new String[] {"1", "1.0", ""};
		var last_column_name = invalid_column_name;
		var date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Tokyo"));

		for (var excel_file_name : excel_file_name_ary) {
			System.out.println("変換開始: " + excel_file_name);

			var convert_res = 0;

			do {
				var tbl_name = excel_file_name.substring(0, excel_file_name.lastIndexOf('.'));

				if (tbl_name.isEmpty()) {
					System.out.println("Error: テーブル名が空です。");

					convert_res = -1;
					
					break;
				}

				System.out.println("テーブル名=" + tbl_name);

				if (!comment_prefix.isEmpty()) {
					if (tbl_name.indexOf(comment_prefix) == 0) {
						System.out.println("コメントテーブルです。");
						
						break;
					}
				}

				var file_dat = new StringBuilder();

				// 読み込み
				try (var file_is = new FileInputStream(excel_dir_path + "/" + excel_file_name);
					var workbook = WorkbookFactory.create(file_is)) {
					var sheet = workbook.getSheet(sheet_name);

					if (sheet == null) {
						System.out.println("Error: シートが存在しません。: " + sheet_name);

						convert_res = -1;

						break;
					}

					var row_cnt = sheet.getLastRowNum() - sheet.getFirstRowNum() + 1;

					if (row_cnt <= 1) {
						System.out.println("Error: 行数が異常です。: " + row_cnt);

						convert_res = -1;

						break;
					}

					var column_cnt = sheet.getRow(0).getLastCellNum() - sheet.getRow(0).getFirstCellNum();
					
					if (column_cnt <= 0) {
						System.out.println("Error: 列数が異常です。: " + column_cnt);
						
						convert_res = -1;
						
						break;
					}

					System.out.println("行数=" + row_cnt);
					System.out.println("列数=" + column_cnt);

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
											val = date_formatter.format(cell.getDateCellValue().toInstant());
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
				} catch (IOException e) {
					e.printStackTrace();

					System.out.println("Error: 読み込みに失敗しました。");
					
					convert_res = -1;
					
					break;
				}

				// 書き込み
				try (var file_os = new FileOutputStream(csv_dir_path + "/" + tbl_name + ".csv");
					var osw = new OutputStreamWriter(file_os, charset);
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
		}

		return;
	}
}
