/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.openlmis.LmisThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This class is used for logging purpose.
 */

@Aspect
@Component
public class ApplicationLogger {
  private static Logger logger = LoggerFactory.getLogger(ApplicationLogger.class);

  @AfterThrowing(pointcut = "execution(* org.openlmis..*(..))", throwing = "e")
  public void logException(JoinPoint joinPoint, Throwable e) {
    Signature signature = joinPoint.getSignature();
    String message = String.format("%s | %s.%s(%s) | Exception", LmisThreadLocal.get(), signature.getDeclaringTypeName(), signature.getName(), joinPoint.getArgs() == null ? "" : joinPoint.getArgs());
    logException(message, e);
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
    logger.error(message, e);
  }

  private enum LogLevel {
    ERROR,
    INFO,
    DEBUG,
    WARN,
    TRACE
  }
}
