package org.openlmis.core.message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Enumeration;
import java.util.ResourceBundle;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ResourceBundle.class)
public class OpenLmisMessageTest {

  @Test
  public void shouldGetMessageWithCodeAndParams() {
    OpenLmisMessage openLmisMessage = new OpenLmisMessage("code", "1", "2", "3");
    assertThat(openLmisMessage.toString(), is("code: code, params: { 1; 2; 3 }"));
  }

  @Test
  public void shouldResolveCodeFromProvidedResourceBundle() {
    ResourceBundle resourceBundle = resourceBundle();
    OpenLmisMessage openLmisMessage = new OpenLmisMessage("code", "1", "2", "3");

    String displayText = openLmisMessage.resolve(resourceBundle);
    assertThat(displayText, is("message for code with params 1, 2, 3"));
  }

  @Test
  public void shouldResolveParamsWhichAreCodesFromProvidedResourceBundle() {
    ResourceBundle resourceBundle = resourceBundle();
    OpenLmisMessage openLmisMessage = new OpenLmisMessage("code", "code1", "2", "3");

    String displayText = openLmisMessage.resolve(resourceBundle);
    assertThat(displayText, is("message for code with params code1 message, 2, 3"));
  }

  private ResourceBundle resourceBundle() {
    return new ResourceBundle() {

        @Override
        protected Object handleGetObject(String key) {
          if (key.equalsIgnoreCase("code"))
            return "message for code with params %s, %s, %s";
          if(key.equalsIgnoreCase("code1"))
            return "code1 message";
          return null;
        }

        @Override
        public Enumeration<String> getKeys() {
          return (Enumeration<String>) asList("code", "code1");
        }
      };
  }
}
