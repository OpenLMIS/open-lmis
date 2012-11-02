package org.openlmis.web.logger;


import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.reflect.FieldSignatureImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;

public class ApplicationLoggerTest {
    private Logger logger;
    private ByteArrayOutputStream outputStream;
    @Mock
    private JoinPoint joinPoint;
    @Mock
    private Signature signature;
    private ApplicationLogger applicationLogger;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        logger = Logger.getLogger(ApplicationLogger.class);
        outputStream = new ByteArrayOutputStream();
        logger.addAppender(new WriterAppender(new SimpleLayout(), outputStream));
        applicationLogger = new ApplicationLogger();
    }

    @Test
    public void shouldLogExceptions() {
        Exception exception = new RuntimeException("An exception was thrown !!");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("Method Name");
        when(signature.getDeclaringTypeName()).thenReturn("com.x.y.Class");

        applicationLogger.logException(joinPoint, exception);

        assertThat(outputStream.toString(),
                containsString("ERROR - An exception occurred in com.x.y.Class : Method Name\njava.lang.RuntimeException: An exception was thrown !!"));
    }

}
