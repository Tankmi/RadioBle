package com.example.radio.radioble.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
* @Title: StringUtils.java
* @Package com.lingjiedian.modou.util
* @Description: TODO(字符串处理，特殊字符、电话号码)
* @author ZhuTao
* @date 2015年8月27日 下午4:10:10
* @version V1.0
*/
public class StringUtils {

   private static int IMG_MINHEIGHT;
   private static String[] GROUP_LETTERS = { "@", "A", "B", "C", "D", "E",
           "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
           "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };

   /**
    * 电话号码判断
    * @param mobiles
    * @return
    */
   public static boolean isMobileNO(String mobiles) {

       return isChinaPhoneLegal(mobiles) || isHKPhoneLegal(mobiles);
   }

   /**
    * 香港手机号码8位数，5|6|8|9开头+7位任意数
    */
   public static boolean isHKPhoneLegal(String str)throws PatternSyntaxException {
       String regExp = "^(5|6|8|9)\\d{7}$";
       Pattern p = Pattern.compile(regExp);
       Matcher m = p.matcher(str);
       return m.matches();
   }

   /**
    * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
    * 此方法中前三位格式有：
    * 13+任意数
    * 15+除4的任意数
    * 18+除1和4的任意数
    * 17+除9的任意数
    * 147
    */
   public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
       String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
       Pattern p = Pattern.compile(regExp);
       Matcher m = p.matcher(str);
       return m.matches();
   }

   //特殊字符替换
   public static String replaceBlanktihuan(String str) {
       String dest = "";
       if (str != null) {
           Pattern p = Pattern.compile("\\s*|\t|\r|\n");
           Matcher m = p.matcher(str);
           dest = m.replaceAll("");
       }
       return dest;
   }

   /**
    * json转换
    * @param str
    * @return
    */
   public static String replaceJson(String str) {
   String dest = "";
   if (str != null) {

       Pattern p1 = Pattern.compile("\\\\n");
       Matcher m1 = p1.matcher(str);
       dest = m1.replaceAll("n1b2n3b4n5cqd");
       System.out.println("第一遍转换：" + dest);

       Pattern p = Pattern.compile("\\\\");
       Matcher m = p.matcher(dest);
       dest = m.replaceAll("");
       System.out.println("第2遍转换：" + dest);

       Pattern p2 = Pattern.compile("n1b2n3b4n5cqd");
       Matcher m2 = p2.matcher(dest);
       dest = m2.replaceAll("\n");
       System.out.println("第3遍转换：" + dest);
   }
   return dest.substring(1, dest.length()-1);}

   /**
    * 半角转换为全角
    *
    * @param input
    * @return
*/
   public static String ToDBC(String input) {
       char[] c = input.toCharArray();
       for (int i = 0; i < c.length; i++) {
           if (c[i] == 12288) {
               c[i] = (char) 32;
               continue;
           }
           if (c[i] > 65280 && c[i] < 65375)
               c[i] = (char) (c[i] - 65248);
       }
       return new String(c);
   }
   /**
    * 去除特殊字符或将所有中文标号替换为英文标号
    *
    * @param str
    * @return
*/
   public static String stringFilter(String str) {
       str = str.replaceAll("【", "[").replaceAll("】", "]")
               .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
       String regEx = "[『』]"; // 清除掉特殊字符
       Pattern p = Pattern.compile(regEx);
       Matcher m = p.matcher(str);
       return m.replaceAll("").trim();
   }

   /**
    * 过滤指定字符串
    *
    * @param str 原字符串
    * @param strs 需要过滤的内容
    * @return
    */
   public static String stringFilterstr(String str,String strs) {
       String regEx = strs; // 清除掉特殊字符
       Pattern p = Pattern.compile(regEx);
       Matcher m = p.matcher(str);
       return m.replaceAll("");
   }

   /**
    * html数据去除多余符号
    *
    * @param str
    * @return
    */
   public static String stringHtml(String str) {
       str = str.replace("\"", "");
          boolean flag = true;
          String result = "";
       char[] a = str.toCharArray();
    int length = a.length;
    for (int i = 0; i < length; i++) {
     if (a[i] == '<') {
      flag = false;
      continue;
     }
     if (a[i] == '>') {
      flag = true;
      continue;
     }
     if (a[i] == '&') {
         flag = false;
         continue;
     }
     if (a[i] == ';') {
         flag = true;
         continue;
     }
     if (flag == true) {
      result += a[i];
     }
    }
    return result.toString();
   }


   public static boolean isEmpty(String str) {
       return (str == null || str.length() == 0);
   }

   /**
    * 判断字符串是否有内容
    * @param str
    * @return 没有内容的话返回true
    */
   public static boolean isBlank(String str) {
       return (str == null || str.trim().length() == 0);
   }


}
