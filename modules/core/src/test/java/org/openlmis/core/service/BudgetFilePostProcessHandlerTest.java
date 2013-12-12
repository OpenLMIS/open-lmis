package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.BudgetFileInfo;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;

import java.io.File;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
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
