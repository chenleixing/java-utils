package com.inspur.sso.auth.common;

import com.inspur.sso.SSOConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookieHelper
{
  private static final Logger logger = LoggerFactory.getLogger(CookieHelper.class);
  public static final int CLEAR_BROWSER_IS_CLOSED = -1;
  public static final int CLEAR_IMMEDIATELY_REMOVE = 0;

  public static void authJSESSIONID(HttpServletRequest request, String value)
  {
    request.getSession().invalidate();
    request.getSession().setAttribute("INSPUR-SSO-" + value, Boolean.valueOf(true));
  }

  public static Cookie findCookieByName(HttpServletRequest request, String cookieName)
  {
    Cookie[] cookies = request.getCookies();
    if (cookies == null)
      return null;
    for (int i = 0; i < cookies.length; ++i)
      if (cookies[i].getName().equals(cookieName))
        return cookies[i];


    return null;
  }

  public static void clearCookieByName(HttpServletResponse response, String cookieName)
  {
    Cookie cookie = new Cookie(cookieName, "");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

  public static void clearAllCookie(HttpServletRequest request, HttpServletResponse response, String domain, String path)
  {
    Cookie[] cookies = request.getCookies();
    for (int i = 0; i < cookies.length; ++i)
      clearCookie(response, cookies[i].getName(), domain, path);

    logger.info("clearAllCookie in  domain " + domain);
  }

  public static boolean clearCookieByName(HttpServletRequest request, HttpServletResponse response, String cookieName, String domain, String path)
  {
    boolean result = false;
    Cookie ck = findCookieByName(request, cookieName);
    if (ck != null)
      result = clearCookie(response, cookieName, domain, path);

    return result;
  }

  private static boolean clearCookie(HttpServletResponse response, String cookieName, String domain, String path)
  {
    boolean result = false;
    try {
      Cookie cookie = new Cookie(cookieName, "");
      cookie.setMaxAge(0);
      cookie.setDomain(domain);
      cookie.setPath(path);
      response.addCookie(cookie);
      logger.info("clear cookie" + cookieName);
      result = true;
    } catch (Exception e) {
      logger.error("clear cookie" + cookieName + " is exception!", e);
    }
    return result;
  }

  public static void addCookie(HttpServletResponse response, String name, String value)
  {
    addCookie(response, null, name, value);
  }

  public static void addCookie(HttpServletResponse response, String domain, String name, String value)
  {
    addCookie(response, domain, "/", name, value, SSOConfig.getCookieMaxage(), false, false);
  }

  public static void addCookie(HttpServletResponse response, String domain, String path, String name, String value, int maxAge, boolean httpOnly, boolean secured)
  {
    Cookie cookie = new Cookie(name, value);

    if ((domain != null) && (!("".equals(domain))))
      cookie.setDomain(domain);

    cookie.setPath(path);
    cookie.setMaxAge(maxAge);

    if (secured) {
      cookie.setSecure(secured);
    }

    if (httpOnly) {
      addHttpOnlyCookie(response, cookie);
    }
    else
      response.addCookie(cookie);
  }

  public static void addHttpOnlyCookie(HttpServletResponse response, Cookie cookie)
  {
    if (cookie == null) {
      return;
    }

    String cookieName = cookie.getName();
    String cookieValue = cookie.getValue();
    int maxAge = cookie.getMaxAge();
    String path = cookie.getPath();
    String domain = cookie.getDomain();
    boolean isSecure = cookie.getSecure();
    StringBuffer sf = new StringBuffer();
    sf.append(cookieName + "=" + cookieValue + ";");

    if (maxAge >= 0) {
      sf.append("Max-Age=" + cookie.getMaxAge() + ";");
    }

    if (domain != null) {
      sf.append("domain=" + domain + ";");
    }

    if (path != null) {
      sf.append("path=" + path + ";");
    }

    if (isSecure)
      sf.append("secure;HTTPOnly;");
    else {
      sf.append("HTTPOnly;");
    }

    response.addHeader("Set-Cookie", sf.toString());
  }
}