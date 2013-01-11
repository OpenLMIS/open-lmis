package org.openlmis.core.message;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Data
public class OpenLmisMessage {

  private String code;
  private Object[] params = new Object[0];

  public OpenLmisMessage(String code) {
    this.code = code;
  }

  public OpenLmisMessage(String code, String... params) {
    this.code = code;
    this.params = params;
  }

  public String resolve(ResourceBundle bundle) {

    String message = getDisplayText(bundle);
    List<String> paramList = resolveParams(bundle);
    return String.format(message, paramList.toArray());
  }

  private List<String> resolveParams(ResourceBundle bundle) {
    List<String> paramList = new ArrayList<>();
    for (Object code : params) {
      paramList.add(getDisplayText(code, bundle));
    }
    return paramList;
  }

  private String getDisplayText(Object code, ResourceBundle bundle) {
    try {
      return bundle.getString(code.toString());
    } catch (MissingResourceException e) {
      return code.toString();
    }
  }

  public String getDisplayText(ResourceBundle bundle) {
   return getDisplayText(code, bundle);
  }
}
