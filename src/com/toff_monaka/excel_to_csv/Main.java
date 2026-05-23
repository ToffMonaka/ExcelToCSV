/**
 * @file
 * @brief Mainファイル
 */


package com.toff_monaka.excel_to_csv;


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

		if (app_param_ary.length != 3) {
			System.out.println("Error: アプリケーションパラメータ数が異常です。: excel_dir_path csv_dir_path charset");
			
			System.exit(0);
        }
        
        var excel_dir_path = app_param_ary[0];
        
        if (excel_dir_path.length() <= 0) {
			System.out.println("Error: excel_dir_pathが空です。");
			
			System.exit(0);
        }
        
        var csv_dir_path = app_param_ary[1];

        if (csv_dir_path.length() <= 0) {
			System.out.println("Error: csv_dir_pathが空です。");
			
			System.exit(0);
        }

        var charset = app_param_ary[2];

        if (charset.length() <= 0) {
			System.out.println("Error: charsetが空です。");
			
			System.exit(0);
        }

		System.out.println("excel_dir_path=" + excel_dir_path);
		System.out.println("csv_dir_path=" + csv_dir_path);
		System.out.println("charset=" + charset);
		System.out.println();

		var excel_file_name_ary = com.toff_monaka.lib.data.Util.getFileNameArray(excel_dir_path, new String[] {"xls", "xlsx"});

		System.out.println("excel_file_name_cnt=" + excel_file_name_ary.length);

		for (var excel_file_name : excel_file_name_ary) {
			System.out.println("excel_file_name=" + excel_file_name);
		}

		return;
	}
}
