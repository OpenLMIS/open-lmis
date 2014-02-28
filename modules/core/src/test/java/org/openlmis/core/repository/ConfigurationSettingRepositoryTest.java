package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ConfigurationSetting;
import org.openlmis.core.repository.mapper.ConfigurationSettingMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationSettingRepositoryTest {

  @Mock
  private ConfigurationSettingMapper mapper;

  @InjectMocks
  private ConfigurationSettingRepository repository;


  @Test
  public void shouldGetByKey() throws Exception {

    ConfigurationSetting expectedConfiguration = new ConfigurationSetting();
    expectedConfiguration.setKey("COUNTRY");

    when(mapper.getByKey("123")).thenReturn(expectedConfiguration);

    ConfigurationSetting actual = repository.getByKey("123");

    verify(mapper).getByKey("123");
    assertEquals(actual.getKey(), expectedConfiguration.getKey());

  }


  @Test
  public void shouldGetAll() throws Exception {
    List<ConfigurationSetting> expectedConfigSettings = new ArrayList<ConfigurationSetting>();
    expectedConfigSettings.add(new ConfigurationSetting());
    when(mapper.getAll()).thenReturn(expectedConfigSettings);

    List<ConfigurationSetting> actualSettings = repository.getAll();
    verify(mapper).getAll();
    assertEquals(expectedConfigSettings.size(), actualSettings.size());
  }


  @Test
  public void shouldSetValue() throws Exception {
    ConfigurationSetting setting = new ConfigurationSetting();
    setting.setKey("COUNTRY");

    repository.setValue(setting);

    verify(mapper).updateValue(setting);
  }


} 
