package org.openlmis.web.logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.joda.time.DateTime;
import org.openlmis.LmisThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApplicationLogger {
    private static Logger logger = LoggerFactory.getLogger(ApplicationLogger.class);

    @AfterThrowing(pointcut = "execution(* org.openlmis.web.controller..*(..))", throwing = "e")
    public void logException(JoinPoint joinPoint, Throwable e) {
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        String userName = LmisThreadLocal.get().toString();
        logger.error(String.format("%s | %s | An exception occurred in %s : %s ", DateTime.now(), userName, className, methodName), e);
    }


}
