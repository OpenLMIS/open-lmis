/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.restapi.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.restapi.domain.Agent;

import static com.natpryce.makeiteasy.Property.newProperty;

public class AgentBuilder {


  public static final Property<Agent, String> agentCode = newProperty();
  public static final Property<Agent, String> agentName = newProperty();
  public static final Property<Agent, String> baseFacilityCode = newProperty();
  public static final Property<Agent, String> phoneNumber = newProperty();
  public static final Property<Agent, String> active = newProperty();

  public static final Instantiator<Agent> defaultCHW = new Instantiator<Agent>() {
    @Override
    public Agent instantiate(PropertyLookup<Agent> lookup) {
      Agent agent = new Agent();
      agent.setAgentCode(lookup.valueOf(agentCode, "A1"));
      agent.setAgentName(lookup.valueOf(agentName, "AgentVinod"));
      agent.setParentFacilityCode(lookup.valueOf(baseFacilityCode, "F10"));
      agent.setPhoneNumber(lookup.valueOf(phoneNumber, "007"));
      agent.setActive(lookup.valueOf(active, "true"));
      return agent;
    }
  };
}
