package org.openlmis.core.message;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(PowerMockRunner.class)

@PrepareForTest(ExposedMessageSourceImpl.class)
public class ExposedMessageSourceImplTest {

    private ExposedMessageSourceImpl spyResource;

    @Before
    public void setUp() throws Exception {
        ExposedMessageSourceImpl exposedMessageSource =  new ExposedMessageSourceImpl();
        spyResource = spy(exposedMessageSource);

        Properties baseProperties = new Properties();
        baseProperties.put("basePropertiesKey","basePropertiesValue");
        Properties enProperties = new Properties();
        enProperties.put("enPropertiesKey","enPropertiesValue");
        Properties ptProperties = new Properties();
        ptProperties.put("ptPropertiesKey","ptPropertiesValue");
        Properties enMozProperties = new Properties();
        enMozProperties.put("enMozPropertiesKey","enMozPropertiesValue");
        Properties ptMozProperties = new Properties();
        ptMozProperties.put("ptMozPropertiesKey","ptMozPropertiesValue");

        doReturn(baseProperties).when(spyResource).getAllPropertiesByFileName("messages");
        doReturn(enProperties).when(spyResource).getAllPropertiesByFileName("messages_en");
        doReturn(ptProperties).when(spyResource).getAllPropertiesByFileName("messages_pt");
        doReturn(enMozProperties).when(spyResource).getAllPropertiesByFileName("messages_en_MOZ");
        doReturn(ptMozProperties).when(spyResource).getAllPropertiesByFileName("messages_pt_MOZ");
    }

    @Test
    public void shouldGetAllMessagesWhenLocaleIsEnglish() {
        Map<String, String> enMap = spyResource.getAll(new Locale("en"));
        assertTrue(enMap.containsKey("basePropertiesKey"));
        assertTrue(enMap.containsKey("enPropertiesKey"));
        assertTrue(enMap.containsKey("enMozPropertiesKey"));
    }

    @Test
    public void shouldGetAllMessagesWhenLocaleIsPortuguese() {
        Map<String, String> enMap = spyResource.getAll(new Locale("pt"));
        assertTrue(enMap.containsKey("basePropertiesKey"));
        assertTrue(enMap.containsKey("ptPropertiesKey"));
        assertTrue(enMap.containsKey("ptMozPropertiesKey"));
    }
}
