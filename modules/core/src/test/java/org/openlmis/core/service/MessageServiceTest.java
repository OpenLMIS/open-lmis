package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.message.OpenLmisMessage;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Locale.class)
public class MessageServiceTest {

  @Mock
  MessageSource messageSource;

  @InjectMocks
  MessageService messageService;

  Locale usLocale;

  @Before
  public void setUp() throws Exception {
    usLocale = Locale.US;
    mockStatic(Locale.class);
    when(Locale.getDefault()).thenReturn(usLocale);

  }

  @Test
  public void shouldGetMessageForKey() {
    String key = "key";
    String expectedMessage = "message";
    when(messageSource.getMessage(key, null, usLocale)).thenReturn(expectedMessage);
    String message = messageService.message(key);
    verify(messageSource).getMessage(key, null, usLocale);
    assertThat(message, is(expectedMessage));
  }

  @Test
  public void shouldGetMessageForKeyWithArgs() {
    String key= "key";
    Object[] args = {"arg1", "arg2"};
    String expectedMessage = "message";
    when(messageSource.getMessage(key, args, usLocale)).thenReturn(expectedMessage);
    String message = messageService.message(key, args);
    verify(messageSource).getMessage(key, args, usLocale);
    assertThat(message, is(expectedMessage));
  }

  @Test
  public void shouldResolveOpenlmisMessage(){
    String[] args = {"arg1", "arg2"};
    String key = "key";
    OpenLmisMessage openLmisMessage = new OpenLmisMessage(key, args);
    String expectedMessage = "message";
    when(messageSource.getMessage(key, args, usLocale)).thenReturn(expectedMessage);
    String message = messageService.message(openLmisMessage);
    verify(messageSource).getMessage(key, args, usLocale);
    assertThat(message, is(expectedMessage));

  }

}
