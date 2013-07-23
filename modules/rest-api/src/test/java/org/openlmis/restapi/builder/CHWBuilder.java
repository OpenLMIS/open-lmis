package org.openlmis.restapi.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.restapi.domain.CHW;
import org.openlmis.restapi.domain.Report;

import static com.natpryce.makeiteasy.Property.newProperty;

public class CHWBuilder {


  public static final Property<CHW, String> agentCode = newProperty();
  public static final Property<CHW, String> agentName = newProperty();
  public static final Property<CHW, String> baseFacilityCode = newProperty();
  public static final Property<CHW, String> phoneNumber = newProperty();
  public static final Property<CHW, Boolean> active = newProperty();

  public static final Instantiator<CHW> defaultCHW = new Instantiator<CHW>() {
    @Override
    public CHW instantiate(PropertyLookup<CHW> lookup) {
      CHW chw = new CHW();
      chw.setAgentCode(lookup.valueOf(agentCode, "A1"));
      chw.setAgentName(lookup.valueOf(agentName, "AgentVinod"));
      chw.setBaseFacilityCode(lookup.valueOf(baseFacilityCode, "F10"));
      chw.setPhoneNumber(lookup.valueOf(phoneNumber, "007"));
      chw.setActive(lookup.valueOf(active, true));
      return chw;
    }
  };
}
