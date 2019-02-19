package org.openlmis.restapi.interceptor;

import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Interceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserMapper userMapper;

    @Value("${andorid.app.old.version.expiration.date}")
    private String expirationDate;

    @Value("${andorid.app.version.code}")
    private String versionCode;


    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Date expirationDateOfAndoridApp = getExpirationDate();

        // if the android version is less than 86, the request version vode will be null
        validAppVersion(request, expirationDateOfAndoridApp);

        if(null != request.getHeader("UniqueId")) {
            validAndUpdateUserDeviceId(request);
        }
        return true;
    }

    private void validAppVersion(HttpServletRequest request, Date expirationDateOfAndoridApp) {
        Integer androidVersionCode = Integer.valueOf(versionCode);

        if(null == request.getHeader("VersionCode")
                && new Date().after(expirationDateOfAndoridApp)) {
            throw new DataException(String.format("Please upgrade your android version"));
        }

        if(null != request.getHeader("VersionCode")
                && Integer.valueOf(request.getHeader("VersionCode")) < androidVersionCode
                && new Date().after(expirationDateOfAndoridApp)) {
            throw new DataException(String.format("Please upgrade your android version"));
        }
    }

    private void validAndUpdateUserDeviceId(HttpServletRequest request) {
        User user = userMapper.getUserByNameAndFacilityName(request.getHeader("UserName"), request.getHeader("FacilityName"));
        if(null == user) {
            throw new DataException(String.format("Could not find user by username %s and facilityname %s",
                    request.getHeader("UserName"), request.getHeader("FacilityName")));
        }

        User usersByDeviceId = userMapper.getUsersByDeviceId(request.getHeader("UniqueId"));
        if(usersByDeviceId != null && usersByDeviceId.getId() != user.getId()) {
            throw new DataException(String.format("The device id has been used by the other user %s", usersByDeviceId.getUserName()));
        }

        if(null == user.getDeviceId()) {
            userMapper.updateDeviceId(user.getId(), request.getHeader("UniqueId"));
            return;
        }
        if(!user.getDeviceId().equals(request.getHeader("UniqueId"))) {
            throw new DataException(String.format("Invalid deviceid %s", request.getHeader("UniqueId")));
        }
    }

    private Date getExpirationDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(expirationDate);
    }
}