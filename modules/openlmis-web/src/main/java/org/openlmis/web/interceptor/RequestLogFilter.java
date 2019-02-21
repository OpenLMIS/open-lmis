package org.openlmis.web.interceptor;

import org.apache.log4j.Logger;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class RequestLogFilter extends AbstractRequestLoggingFilter {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        //the request body is only available after request, so don't log anything here
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.info("User: " + request.getHeader("UserName") +
                " Facility: " + request.getHeader("FacilityName") +
                " Device:" + request.getHeader("DeviceInfo") +
                " Unique Device id:" + request.getHeader("UniqueId"));
        if (!message.contains("password")) {
            logger.info(message);
        } else {
            logger.info("request body contains sensitive data, skipping");
        }
    }
}