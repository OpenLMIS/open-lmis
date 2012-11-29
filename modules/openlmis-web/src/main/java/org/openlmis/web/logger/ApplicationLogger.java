package org.openlmis.web.logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.openlmis.LmisThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApplicationLogger {
    private static Logger logger = LoggerFactory.getLogger(ApplicationLogger.class);

    @AfterThrowing(pointcut = "execution(* org.openlmis..*(..))", throwing = "e")
    public void logException(JoinPoint joinPoint, Throwable e) {
        Signature signature = joinPoint.getSignature();
        String message = String.format("%s | %s.%s(%s) | Exception", LmisThreadLocal.get(), signature.getDeclaringTypeName(), signature.getName(), joinPoint.getArgs()==null?"": joinPoint.getArgs());
        logException(message, e);
    }

    @Before("execution(* org.openlmis..*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();

        logMessage(LogLevel.INFO, String.format("%s | %s.%s(%s) | Enter", LmisThreadLocal.get(), signature.getDeclaringTypeName(), signature.getName(), joinPoint.getArgs()==null?"": joinPoint.getArgs()));
    }

    @Before("execution(* org.openlmis..*(..))")
    public void logMethodExit(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        logMessage(LogLevel.INFO, String.format("%s | %s.%s(%s) | Exit", LmisThreadLocal.get(), signature.getDeclaringTypeName(), signature.getName(), joinPoint.getArgs()==null?"": joinPoint.getArgs()));
    }


    private void logMessage(LogLevel logLevel, String message) {
        switch (logLevel) {
            case ERROR:
                logger.error(message);
                break;
            case DEBUG:
                logger.debug(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case TRACE:
            default:
                logger.trace(message);
        }
    }

    private void logException(String message, Throwable e) {
        logger.error(message,e);
    }

    private enum LogLevel {
        ERROR,
        INFO,
        DEBUG,
        WARN,
        TRACE
    }
}
