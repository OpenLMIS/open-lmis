package org.openlmis.web.controller; 

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.ConfigurationSetting;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.report.model.dto.Product;
import org.openlmis.web.model.ConfigurationDTO;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.security.Principal;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.response.OpenLmisResponse.SUCCESS;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenLmisResponse.class)
@Category(UnitTests.class)
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
    configSettingList.add(new ConfigurationSetting());

    ConfigurationDTO dto = new ConfigurationDTO();
    dto.setList(configSettingList);

    when(configurationService.getConfigurations()).thenReturn(configSettingList);

    ResponseEntity<OpenLmisResponse> expectResponse = new ResponseEntity<>(new OpenLmisResponse(), HttpStatus.OK);
    when(OpenLmisResponse.response("settings", dto)).thenReturn(expectResponse);

    ResponseEntity<OpenLmisResponse> response = controller.getAll();

    verify(configurationService).getConfigurations();
    assertThat(response, is(expectResponse));
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
    ResponseEntity<OpenLmisResponse> responseEntity = controller.updateSettings(configurationDTO);
    verify(configurationService).update(configurationDTO.getList());
  }


} 
