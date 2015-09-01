/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.logger;


import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.LmisThreadLocal;
import org.openlmis.core.logging.ApplicationLogger;
import org.openlmis.db.categories.UnitTests;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Mockito.when;
@Category(UnitTests.class)
public class ApplicationLoggerTest {
    private Logger logger;
    private ByteArrayOutputStream outputStream;
    @Mock @SuppressWarnings("unused")
    private JoinPoint joinPoint;
    @Mock @SuppressWarnings("unused")
    private Signature signature;
    private ApplicationLogger applicationLogger;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        logger = Logger.getLogger(ApplicationLogger.class);
        outputStream = new ByteArrayOutputStream();
        logger.addAppender(new WriterAppender(new SimpleLayout(), outputStream));
        applicationLogger = new ApplicationLogger();
        LmisThreadLocal.set("TEST_USER");
    }

    @Test
    public void shouldLogExceptions() {
        Exception exception = new RuntimeException("An exception was thrown !!");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("Method Name");
        when(signature.getDeclaringTypeName()).thenReturn("com.x.y.Class");

        applicationLogger.logException(joinPoint, exception);
        String lineSeparator = System.getProperty("line.separator");
        assertThat(outputStream.toString(),
                containsString("ERROR - TEST_USER | com.x.y.Class.Method Name() | Exception"+lineSeparator+"java.lang.RuntimeException: An exception was thrown !!"));
    }

}
