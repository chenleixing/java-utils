package com.inspur.mng.core.utils;

import com.inspur.mng.web.IdcardUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public final class CTools
{
  private static int Object = 1000;
  private static final int o00000 = 16;
  private static final String Ò00000 = "0933910847463829232312312";

  public static String bytesToString(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length < 1))
      return null;
    try
    {
      return new String(paramArrayOfByte, "UTF-8");
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return "";
  }

  public static byte[] InputStreamToByte(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    while ((i = paramInputStream.read()) != -1)
    {
      int i;
      localByteArrayOutputStream.write(i);
    }
    byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
    localByteArrayOutputStream.close();
    return arrayOfByte;
  }

  public static InputStream ByteToInputStream(byte[] paramArrayOfByte)
  {
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    return localByteArrayInputStream;
  }

  public static String getStatus(Object paramObject)
  {
    if ((paramObject != null) && (paramObject.equals("on")))
      return "1";
    return "0";
  }

  public static String getIsPublis(Object paramObject)
  {
    if ((paramObject != null) && (paramObject.equals("on")))
      return "1";
    return "0";
  }

  public static String getSortOrder(Object paramObject)
  {
    String str1 = "00001";
    if (paramObject != null)
    {
      str1 = "";
      String str2 = String.valueOf(paramObject);
      if (str2.length() < 5)
        for (int i = 0; i < 5 - str2.length(); ++i)
          str1 = str1 + "0";
      return str1 + str2;
    }
    return str1;
  }

  public static String getMD5(String paramString)
  {
    if (paramString == null)
      return "";
    return getMD5(paramString.getBytes());
  }

  public static String getMD5(byte[] paramArrayOfByte)
  {
    String str = null;
    char[] arrayOfChar1 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      localMessageDigest.update(paramArrayOfByte);
      byte[] arrayOfByte = localMessageDigest.digest();
      char[] arrayOfChar2 = new char[32];
      int i = 0;
      for (int j = 0; j < 16; ++j)
      {
        int k = arrayOfByte[j];
        arrayOfChar2[(i++)] = arrayOfChar1[(k >>> 4 & 0xF)];
        arrayOfChar2[(i++)] = arrayOfChar1[(k & 0xF)];
      }
      str = new String(arrayOfChar2);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return str;
  }

  public static final String encrypt(String paramString)
  {
    if (paramString == null)
      return "";
    if (paramString.length() == 0)
      return "";
    BigInteger localBigInteger1 = new BigInteger(paramString.getBytes());
    BigInteger localBigInteger2 = new BigInteger("0933910847463829232312312");
    BigInteger localBigInteger3 = localBigInteger2.xor(localBigInteger1);
    return localBigInteger3.toString(16);
  }

  public static final String decrypt(String paramString)
  {
    if (paramString == null)
      return "";
    if (paramString.length() == 0)
      return "";
    BigInteger localBigInteger1 = new BigInteger("0933910847463829232312312");
    try
    {
      BigInteger localBigInteger2 = new BigInteger(paramString, 16);
      BigInteger localBigInteger3 = localBigInteger2.xor(localBigInteger1);
      return new String(localBigInteger3.toByteArray());
    }
    catch (Exception localException)
    {
    }
    return "";
  }

  public static final String getLoginSQL(String paramString)
  {
    String str = "select t.id,t.code,t.account,t.password,t.gender,t.birthday,t.name,t.identity_num,t.phone,t.mobile,t.email,t.photo,t.is_lawer,t.region_code,t.region_name,t.org_code,t.org_name,t.agent_code,t.agent_name,t.power_code,t.role_code,t.position,t.style,t.tip_way from pub_user t where 1=1";
    if (isEmail(paramString))
    {
      str = str + " and t.email=?";
      paramString = encrypt(paramString);
    }
    else if (isMobile(paramString))
    {
      str = str + " and t.mobile=?";
      paramString = encrypt(paramString);
    }
    else
    {
      str = str + " and t.account=?";
    }
    return str;
  }

  public static boolean isMobile(String paramString)
  {
    Pattern localPattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    Matcher localMatcher = localPattern.matcher(paramString);
    return localMatcher.matches();
  }

  public static boolean isEmail(String paramString)
  {
    return ((StringUtils.isNotBlank(paramString)) && (paramString.indexOf("@") != -1));
  }

  public static boolean isDate(String paramString1, String paramString2)
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString2);
    Date localDate = null;
    try
    {
      localDate = localSimpleDateFormat.parse(paramString1);
    }
    catch (Exception localException)
    {
      return false;
    }
    String str = localSimpleDateFormat.format(localDate);
    return paramString1.equals(str);
  }

  public static boolean isDate(String paramString)
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    try
    {
      localSimpleDateFormat.parse(paramString);
    }
    catch (Exception localException)
    {
      return false;
    }
    return true;
  }

  public boolean isNumber(String paramString)
  {
    Pattern localPattern = Pattern.compile("[0-9]*");
    Matcher localMatcher = localPattern.matcher(paramString);
    return (localMatcher.matches());
  }

  public static String getSexFromIdCard(String paramString)
  {
    String str = "";
    if (paramString.length() == 15)
      str = paramString.substring(paramString.length() - 3, paramString.length());
    if (paramString.length() == 18)
      str = paramString.substring(paramString.length() - 4, paramString.length() - 1);
    int i = Integer.parseInt(str) % 2;
    if (i == 0)
      return "女";
    return "男";
  }

  public String getBirthdayFromIdCard(String paramString)
  {
    String str1 = "";
    if (paramString.length() == 18)
      str1 = paramString.substring(0, 17);
    else if (paramString.length() == 15)
      str1 = paramString.substring(0, 6) + "19" + paramString.substring(6, 15);
    String str2 = str1.substring(6, 10);
    String str3 = str1.substring(10, 12);
    String str4 = str1.substring(12, 14);
    return str2 + "-" + str3 + "-" + str4;
  }

  public static Hashtable<String, String> getAreaCode()
  {
    Hashtable localHashtable = new Hashtable();
    localHashtable.put("11", "北京");
    localHashtable.put("12", "天津");
    localHashtable.put("13", "河北");
    localHashtable.put("14", "山西");
    localHashtable.put("15", "内蒙古");
    localHashtable.put("21", "辽宁");
    localHashtable.put("22", "吉林");
    localHashtable.put("23", "黑龙江");
    localHashtable.put("31", "上海");
    localHashtable.put("32", "江苏");
    localHashtable.put("33", "浙江");
    localHashtable.put("34", "安徽");
    localHashtable.put("35", "福建");
    localHashtable.put("36", "江西");
    localHashtable.put("37", "山东");
    localHashtable.put("41", "河南");
    localHashtable.put("42", "湖北");
    localHashtable.put("43", "湖南");
    localHashtable.put("44", "广东");
    localHashtable.put("45", "广西");
    localHashtable.put("46", "海南");
    localHashtable.put("50", "重庆");
    localHashtable.put("51", "四川");
    localHashtable.put("52", "贵州");
    localHashtable.put("53", "云南");
    localHashtable.put("54", "西藏");
    localHashtable.put("61", "陕西");
    localHashtable.put("62", "甘肃");
    localHashtable.put("63", "青海");
    localHashtable.put("64", "宁夏");
    localHashtable.put("65", "新疆");
    localHashtable.put("71", "台湾");
    localHashtable.put("81", "香港");
    localHashtable.put("82", "澳门");
    localHashtable.put("91", "国外");
    return localHashtable;
  }

  public boolean IDCardValidate(String paramString)
    throws ParseException
  {
    String str1 = "";
    String[] arrayOfString1 = { "1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2" };
    String[] arrayOfString2 = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
    String str2 = "";
    if ((paramString.length() != 15) && (paramString.length() != 18))
    {
      str1 = "号码长度应该为15位或18位。";
      return false;
    }
    if (paramString.length() == 18)
      str2 = paramString.substring(0, 17);
    else if (paramString.length() == 15)
      str2 = paramString.substring(0, 6) + "19" + paramString.substring(6, 15);
    if (!(isNumber(str2)))
    {
      str1 = "15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
      return false;
    }
    String str3 = str2.substring(6, 10);
    String str4 = str2.substring(10, 12);
    String str5 = str2.substring(12, 14);
    if (!(isDate(str3 + "-" + str4 + "-" + str5)))
    {
      str1 = "生日无效。";
      return false;
    }
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    if ((localGregorianCalendar.get(1) - Integer.parseInt(str3) > 150) || (localGregorianCalendar.getTime().getTime() - localSimpleDateFormat.parse(str3 + "-" + str4 + "-" + str5).getTime() < 7206182634165108736L))
    {
      str1 = "生日不在有效范围。";
      return false;
    }
    if ((Integer.parseInt(str4) > 12) || (Integer.parseInt(str4) == 0))
    {
      str1 = "月份无效";
      return false;
    }
    if ((Integer.parseInt(str5) > 31) || (Integer.parseInt(str5) == 0))
    {
      str1 = "日期无效";
      return false;
    }
    Hashtable localHashtable = getAreaCode();
    if (localHashtable.get(str2.substring(0, 2)) == null)
      return false;
    int i = 0;
    for (int j = 0; j < 17; ++j)
      i += Integer.parseInt(String.valueOf(str2.charAt(j))) * Integer.parseInt(arrayOfString2[j]);
    j = i % 11;
    String str6 = arrayOfString1[j];
    str2 = str2 + str6;
    if (paramString.length() == 18)
      if (!(str2.equals(paramString)))
        str1 = "身份证无效，最后一位字母错误";
    else
      return true;
    return true;
  }

  public static final String getResourceSQL(boolean paramBoolean, String paramString)
  {
    String str1 = "select distinct rs.code,rs.name,rs.parent_code,rs.path,rs.data,rs.icon,rs.res_path,rs.sort_order from pub_resource rs, pub_role_resource rr where rs.code=rr.res_code and rs.status='1'";
    if (!(paramBoolean))
    {
      String[] arrayOfString1 = paramString.split(",");
      StringBuffer localStringBuffer = new StringBuffer();
      String[] arrayOfString2 = arrayOfString1;
      int i = arrayOfString2.length;
      for (int j = 0; j < i; ++j)
      {
        String str2 = arrayOfString2[j];
        localStringBuffer.append("'").append(str2).append("',");
      }
      paramString = localStringBuffer.substring(0, localStringBuffer.length() - 1);
      str1 = str1 + " and rr.role_code in(" + paramString + ")";
    }
    else
    {
      str1 = "select rs.code,rs.name,rs.parent_code,rs.path,rs.data,rs.res_path from pub_resource rs where rs.status='1'";
    }
    str1 = str1 + " order by rs.parent_code,rs.sort_order";
    return str1;
  }

  public static synchronized String getNewID()
  {
    if (Object >= 9999)
      Object = 1000;
    StringBuffer localStringBuffer = new StringBuffer(20);
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    localStringBuffer.append(localSimpleDateFormat.format(new Date()));
    localStringBuffer.append(Object++);
    localStringBuffer.append("00");
    return localStringBuffer.toString();
  }

  public static synchronized String getNewID(String paramString)
  {
    if (Object >= 9999)
      Object = 1000;
    StringBuffer localStringBuffer = new StringBuffer(20);
    if (StringUtils.isNotBlank(paramString))
      localStringBuffer.append(paramString);
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    localStringBuffer.append(localSimpleDateFormat.format(new Date()));
    localStringBuffer.append(Object++);
    localStringBuffer.append("00");
    return localStringBuffer.toString();
  }

  public static String getUUID()
  {
    String str = UUID.randomUUID().toString();
    str = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
    return str.toUpperCase();
  }

  public static String getID()
  {
    String str = UUID.randomUUID().toString();
    return str;
  }

  public static String getCookieKey()
  {
    return "COOKIE" + getUUID() + "$";
  }

  public static ArrayList<Object> filterList(ArrayList<Object> paramArrayList)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramArrayList.size(); ++i)
      if (!(localArrayList.contains(paramArrayList.get(i))))
        localArrayList.add(paramArrayList.get(i));
    return localArrayList;
  }

  public static String clob2string(Object paramObject)
  {
    String str = null;
    if (paramObject instanceof Clob)
    {
      Clob localClob = (Clob)paramObject;
      if (localClob != null)
        try
        {
          str = localClob.getSubString(7206197803989598209L, (int)localClob.length());
        }
        catch (SQLException localSQLException)
        {
          localSQLException.printStackTrace();
        }
    }
    return str;
  }

  public static String listToString(AbstractList<String> paramAbstractList)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramAbstractList.size(); ++i)
      localStringBuffer.append("'").append(String.valueOf(paramAbstractList.get(i))).append("',");
    if (localStringBuffer.length() > 0)
      return localStringBuffer.substring(0, localStringBuffer.length() - 1);
    return "''";
  }

  public static String arrayToString(String[] paramArrayOfString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("(");
    for (int i = 0; i < paramArrayOfString.length; ++i)
      localStringBuffer.append("'").append(paramArrayOfString[i]).append("',");
    return localStringBuffer.substring(0, localStringBuffer.length() - 1) + ")";
  }

  public static boolean validateCard(String paramString)
  {
    return IdcardUtils.validateCard(paramString);
  }

  public static String getGenderByIdCard(String paramString)
  {
    return IdcardUtils.getGenderByIdCard(paramString);
  }

  public static String getProvinceByIdCard(String paramString)
  {
    return IdcardUtils.getProvinceByIdCard(paramString);
  }

  public static int getAgeByIdCard(String paramString)
  {
    return IdcardUtils.getAgeByIdCard(paramString);
  }

  public static String getBirthdayByIdCard(String paramString)
  {
    return IdcardUtils.getBirthByIdCard(paramString);
  }

  public static void main(String[] paramArrayOfString)
  {
    System.out.println(getNewID());
    String str1 = "15019239901";
    System.out.println("15019239901 encrypt password is " + encrypt(str1));
    System.out.println(decrypt(encrypt(str1)));
    String str2 = "15019239901@139.com";
    System.out.println("15019239901 encrypt password is " + encrypt(str2));
    System.out.println(decrypt(encrypt(str2)));
  }
}