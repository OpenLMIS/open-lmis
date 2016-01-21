/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.message.ExposedMessageSourceImpl;
import org.openlmis.core.message.OpenLmisMessage;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.util.Locale;
import java.util.Set;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({Locale.class, MessageService.class})
public class MessageServiceTest {

  @Rule
  public PowerMockRule rule = new PowerMockRule();

  @Mock
  ExposedMessageSourceImpl messageSource;

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
    when(messageSource.getMessage(key, null, key, usLocale)).thenReturn(expectedMessage);
    String message = messageService.message(key);
    verify(messageSource).getMessage(key, null, key, usLocale);
    assertThat(message, is(expectedMessage));
  }

  @Test
  public void shouldGetRequestScopedMessageServiceInstance() throws Exception {
    ExposedMessageSourceImpl exposedMessageService = mock(ExposedMessageSourceImpl.class);
    whenNew(ExposedMessageSourceImpl.class).withNoArguments().thenReturn(exposedMessageService);
    MessageService messageServiceRequestInstance = mock(MessageService.class);
    whenNew(MessageService.class).withArguments(exposedMessageService, "en").thenReturn(messageServiceRequestInstance);

    MessageService requestInstanceMessageService = MessageService.getRequestInstance();

    verifyNew(ExposedMessageSourceImpl.class).withNoArguments();
    verify(exposedMessageService).setBasename("messages");
    verifyNew(MessageService.class).withArguments(exposedMessageService, "en");
    assertThat(requestInstanceMessageService, is(messageServiceRequestInstance));
  }

  @Test
  public void shouldGetMessageForKeyWithArgs() {
    String key = "key";
    Object[] args = {"arg1", "arg2"};
    String expectedMessage = "message";
    when(messageSource.getMessage(key, args, key, usLocale)).thenReturn(expectedMessage);
    String message = messageService.message(key, args);
    verify(messageSource).getMessage(key, args, key, usLocale);
    assertThat(message, is(expectedMessage));
  }

  @Test
  public void shouldResolveOpenlmisMessage() {
    String[] args = {"arg1", "arg2"};
    String key = "key";
    OpenLmisMessage openLmisMessage = new OpenLmisMessage(key, args);
    String expectedMessage = "message";
    when(messageSource.getMessage(key, args, key, usLocale)).thenReturn(expectedMessage);
    String message = messageService.message(openLmisMessage);
    verify(messageSource).getMessage(key, args, key, usLocale);
    assertThat(message, is(expectedMessage));

  }

  @Test
  public void shouldReturnLocalesCodes() throws Exception {
    MessageService service = new MessageService(messageSource, "en, pt, fr");

    Set<String> locales = service.getLocales();
    assertThat(locales.size(), is(3));
    assertTrue(locales.contains("en"));
    assertTrue(locales.contains("pt"));
    assertTrue(locales.contains("fr"));

  }
}
