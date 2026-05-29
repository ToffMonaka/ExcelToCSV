/**
 * @file
 * @brief DataUtilファイル
 */

package com.toff_monaka.tml.data;

import java.io.File;

/**
 * @brief DataUtilクラス
 */
public class DataUtil
{
	private DataUtil(){}

	/**
	 * getFileNameArray関数
	 * @param dir_path (directory_path)
	 * @param file_ext_ary (file_extension_array)
	 * @return file_name_ary (file_name_array)<br>
     * null=失敗
	 */
    public static String[] getFileNameArray(String dir_path, String[] file_ext_ary)
    {
        var dir = new File(dir_path);
        
        if (!dir.isDirectory()) {
            return (null);
        }

        var file_name_ary = new String[0];
    
        var file_ary = dir.listFiles();

        for (var file : file_ary) {
            if (file.isDirectory()) {
                continue;
            }

            var file_name = file.getName();
            var file_name_dot_str_index = file_name.lastIndexOf('.');

            if (file_name_dot_str_index < 0) {
                continue;
            }

            var file_name_ext = file_name.substring(file_name_dot_str_index + 1);
            var file_name_ext_equal_flg = false;

            for (var file_ext : file_ext_ary) {
                if (file_ext.equals(file_name_ext)) {
                    file_name_ext_equal_flg = true;

                    break;
                }
            }

            if (!file_name_ext_equal_flg) {
                continue;
            }

            var tmp_file_name_ary = file_name_ary;
            
            file_name_ary = new String[tmp_file_name_ary.length + 1];

            for (int tmp_file_name_i = 0; tmp_file_name_i < tmp_file_name_ary.length; ++tmp_file_name_i) {
                file_name_ary[tmp_file_name_i] = tmp_file_name_ary[tmp_file_name_i];
            }
            
            file_name_ary[tmp_file_name_ary.length] = file_name;
        }

        return (file_name_ary);
    }
}
