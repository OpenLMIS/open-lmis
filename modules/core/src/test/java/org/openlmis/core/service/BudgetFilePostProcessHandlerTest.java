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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.BudgetFileInfo;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.io.File;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(MessageBuilder.class)
public class BudgetFilePostProcessHandlerTest {

  @Mock
  BudgetFileService budgetFileService;

  @InjectMocks
  private BudgetFilePostProcessHandler postProcessHandler;

  @Mock(name = "budgetFtpArchiveOutputChannel")
  private MessageChannel budgetFtpArchiveOutputChannel;

  @Mock(name = "budgetFtpErrorChannel")
  private MessageChannel budgetFtpErrorChannel;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Test
  public void shouldSaveBudgetFileInfoAndArchiveItIfNoError() {
    BudgetFileInfo budgetFileInfo = new BudgetFileInfo("BudgetFile", false);
    File budgetFile = mock(File.class);
    mockStatic(MessageBuilder.class);
    MessageBuilder<File> messageBuilder = mock(MessageBuilder.class);
    when(MessageBuilder.withPayload(budgetFile)).thenReturn(messageBuilder);
    Message<File> budgetFileMessage = mock(Message.class);
    when(messageBuilder.build()).thenReturn(budgetFileMessage);
    postProcessHandler.process(budgetFileInfo, budgetFile);

    verify(budgetFileService).save(budgetFileInfo);
    verify(budgetFtpArchiveOutputChannel).send(budgetFileMessage);
  }

  @Test
  public void shouldSaveBudgetFileInfoAndSendItToErrorChannelIfError() {
    BudgetFileInfo budgetFileInfo = new BudgetFileInfo("BudgetFile", true);
    File budgetFile = mock(File.class);
    mockStatic(MessageBuilder.class);
    MessageBuilder<File> messageBuilder = mock(MessageBuilder.class);
    when(MessageBuilder.withPayload(budgetFile)).thenReturn(messageBuilder);
    Message<File> budgetFileMessage = mock(Message.class);
    when(messageBuilder.build()).thenReturn(budgetFileMessage);
    postProcessHandler.process(budgetFileInfo, budgetFile);

    verify(budgetFileService).save(budgetFileInfo);
    verify(budgetFtpErrorChannel).send(budgetFileMessage);
  }


}
