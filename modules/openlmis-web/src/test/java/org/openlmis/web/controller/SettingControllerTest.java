package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.ConfigurationSetting;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.model.ConfigurationDTO;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(OpenLmisResponse.class)
public class SettingControllerTest {

  private Long userId;

  private MockHttpSession session;

  private MockHttpServletRequest httpServletRequest;


  @Mock
  private ConfigurationSettingService configurationService;

  @InjectMocks
  private SettingController controller;

  @Before
  public void setup(){
    initMocks(this);
    userId = 3L;
    httpServletRequest = new MockHttpServletRequest();
    session = new MockHttpSession();
    httpServletRequest.setSession(session);
    mockStatic(OpenLmisResponse.class);
  }


  @Test
  public void shouldGetAllSettings() throws Exception {
    ArrayList<ConfigurationSetting> configSettingList = new ArrayList<ConfigurationSetting>();
    ConfigurationSetting setting = new ConfigurationSetting();
    setting.setKey("123");
    configSettingList.add(setting);

    when(configurationService.getConfigurations()).thenReturn(configSettingList);
    controller.getAll();
    verify(configurationService).getConfigurations();
  }


  @Test
  public void shouldGetSettingByKey() throws Exception {
    ConfigurationSetting setting = new ConfigurationSetting();

    when(configurationService.getByKey("123")).thenReturn(setting);

    ResponseEntity<OpenLmisResponse> expectResponse = new ResponseEntity<>(new OpenLmisResponse(), HttpStatus.OK);
    when(OpenLmisResponse.response("settings", setting)).thenReturn(expectResponse);

    ResponseEntity<OpenLmisResponse> response = controller.getByKey("123");

    verify(configurationService).getByKey("123");
    assertThat(response, is(expectResponse));
  }


  @Test
  public void shouldUpdateSettings() throws Exception {
    ConfigurationDTO configurationDTO = new ConfigurationDTO();
    controller.updateSettings(configurationDTO);
    verify(configurationService).update(configurationDTO.getList());
  }


} 
