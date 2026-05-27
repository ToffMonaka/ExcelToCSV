/**
 * @file
 * @brief Utilファイル
 */


package com.toff_monaka.tml;


/**
 * @brief Utilクラス
 */
public class Util
{
	private Util(){}

    private static Boolean _debugFlag = false;

    public static class PROJECT
    {
		private PROJECT(){}

        public static final String NAME = "Toff Monaka Library";
        public static final String VERSION_NAME = "1.0.0";
        public static final String COMPANY_NAME = "Toff Monaka Project";
    }

	/**
     * @brief getDebugFlag関数
     * @return debug_flg (debug_flag)
     */
    public static Boolean getDebugFlag()
    {
        return (Util._debugFlag);
    }
    
    /**
     * @brief setDebugFlag関数
     * @param debug_flg (debug_flag)
     */
    public static void setDebugFlag(Boolean debug_flg)
    {
        System.out.println("Warning: com.toff_monaka.lib.Util.SetDebugFlag: " + debug_flg);

        Util._debugFlag = debug_flg;

        return;
    }
}
