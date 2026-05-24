/**
 * @file
 * @brief Mainファイル
 */


package com.toff_monaka.excel_to_csv;


import java.io.IOException;
import java.io.FileInputStream;
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
			
			System.exit(0);
        }
        
        var excel_dir_path = app_param_ary[0];
        
        if (excel_dir_path.isEmpty()) {
			System.out.println("Error: excel_dir_pathが空です。");
			
			System.exit(0);
        }
        
        var csv_dir_path = app_param_ary[1];

        if (csv_dir_path.isEmpty()) {
			System.out.println("Error: csv_dir_pathが空です。");
			
			System.exit(0);
        }

        var charset = app_param_ary[2];

        if (charset.isEmpty()) {
			System.out.println("Error: charsetが空です。");
			
			System.exit(0);
        }

        var newline = app_param_ary[3];

        if (newline.isEmpty()) {
			System.out.println("Error: newlineが空です。");
			
			System.exit(0);
        }

		System.out.println("excel_dir_path=" + excel_dir_path);
		System.out.println("csv_dir_path=" + csv_dir_path);
		System.out.println("charset=" + charset);
		System.out.println("newline=" + newline);
		System.out.println();

		var excel_file_name_ary = com.toff_monaka.lib.data.Util.getFileNameArray(excel_dir_path, new String[] {"xls", "xlsx"});
		var comment_prefix = "_";

		for (var excel_file_name : excel_file_name_ary) {
			var convert_res = 0;

			System.out.println("変換開始: " + excel_file_name);

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

				try (var file_is = new FileInputStream(excel_dir_path + "/" + excel_file_name); var workbook = WorkbookFactory.create(file_is)) {
					var sheet = workbook.getSheetAt(0);

					var row_cnt = sheet.getLastRowNum() - sheet.getFirstRowNum() + 1;

					if (row_cnt <= 0) {
						System.out.println("Error: ロウ数が異常です。: " + row_cnt);

						convert_res = -1;

						break;
					}

					var column_cnt = sheet.getRow(0).getLastCellNum() - sheet.getRow(0).getFirstCellNum();
					
					if (column_cnt <= 0) {
						System.out.println("Error: カラム数が異常です。: " + column_cnt);
						
						convert_res = -1;
						
						break;
					}

					System.out.println("ロウ数=" + (row_cnt - 1));
					System.out.println("カラム数=" + column_cnt);
				} catch (IOException e) {
					e.printStackTrace();
					
					convert_res = -1;
					
					break;
				}
			} while (false);

			System.out.println("変換終了: " + excel_file_name);
			System.out.println();

			if (convert_res < 0) {
				System.out.println("Error: 変換に失敗しました。");
			}

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
