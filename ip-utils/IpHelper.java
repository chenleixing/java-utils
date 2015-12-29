package com.inspur.sso.auth.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;

public class IpHelper
{
  private static String LOCAL_IP_STAR_STR = "192.168.";
  public static final String LOCAL_IP;
  public static final String HOST_NAME;

  static
  {
    String ip = null;
    String hostName = null;
    try {
      hostName = InetAddress.getLocalHost().getHostName();
      InetAddress[] ipAddr = InetAddress.getAllByName(hostName);
      for (int i = 0; i < ipAddr.length; ++i) {
        ip = ipAddr[i].getHostAddress();
        if (ip.startsWith(LOCAL_IP_STAR_STR))
          break;
      }

      if (ip != null) break label74;
      ip = ipAddr[0].getHostAddress();
    }
    catch (UnknownHostException e)
    {
      e.printStackTrace();
    }

    label74: LOCAL_IP = ip;
    HOST_NAME = hostName;
  }

  public static String getIpAddr(HttpServletRequest request)
  {
    String ip = request.getHeader("x-forwarded-for");
    if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip)))
      ip = request.getHeader("Proxy-Client-IP");

    if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip)))
      ip = request.getHeader("WL-Proxy-Client-IP");

    if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
      ip = request.getRemoteAddr();
      if (ip.equals("127.0.0.1"))
      {
        InetAddress inet = null;
        try {
          inet = InetAddress.getLocalHost();
          ip = inet.getHostAddress();
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }

      }

    }

    if ((ip != null) && (ip.length() > 15) && 
      (ip.indexOf(",") > 0)) {
      ip = ip.substring(0, ip.indexOf(","));
    }

    return ip;
  }
}