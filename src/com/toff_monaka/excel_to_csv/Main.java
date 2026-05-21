/**
 * @file
 * @brief Mainファイル
 */


package com.toff_monaka.excel_to_csv;


import java.io.*;


/**
 * @brief Mainクラス
 */
public class Main
{
	/**
	 * @brief main関数
	 * @param app_param_ary(application_parameter_array)
	 */
	public static void main(String[] app_param_ary)
	{
		if (app_param_ary.length != 3) {
			System.out.println("Error: アプリケーションパラメータ数が異常です。: excel_path csv_path charset");
			
			System.exit(0);
        }
        
        var excel_path = app_param_ary[0];
        
        if (excel_path.length() <= 0) {
			System.out.println("Error: excel_pathが空です。");
			
			System.exit(0);
        }
        
		System.out.println("excel_path=" + excel_path);

        var csv_path = app_param_ary[1];

        if (csv_path.length() <= 0) {
			System.out.println("Error: csv_pathが空です。");
			
			System.exit(0);
        }

		System.out.println("csv_path=" + csv_path);

        var charset = app_param_ary[2];

        if (charset.length() <= 0) {
			System.out.println("Error: charsetが空です。");
			
			System.exit(0);
        }

		System.out.println("charset=" + charset);

		return;
	}
}
