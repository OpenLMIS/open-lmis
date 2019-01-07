package org.openlmis.restapi.interceptor;

import org.openlmis.core.exception.DataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Interceptor extends HandlerInterceptorAdapter {
    @Value("${andorid.app.old.version.expiration.date}")
    private String expirationDate;

    @Value("${andorid.app.version.code}")
    private String versionCode;


    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Date expirationDateOfAndoridApp = getExpirationDate();
        Integer androidVersionCode = Integer.valueOf(versionCode);

        // if the android version is less than 86, the request version vode will be null
        if(null == request.getHeader("VersionCode")
                && new Date().after(expirationDateOfAndoridApp)) {
            throw new DataException(String.format("Please upgrade your android version"));
        }

        if(null != request.getHeader("VersionCode")
                && Integer.valueOf(request.getHeader("VersionCode")) < androidVersionCode
                && new Date().after(expirationDateOfAndoridApp)) {
            throw new DataException(String.format("Please upgrade your android version"));
        }
        return true;
    }

    private Date getExpirationDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(expirationDate);
    }
}