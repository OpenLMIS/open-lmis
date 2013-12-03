/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
      agent.setAgentName(lookup.valueOf(agentName, "Agent A1"));
      agent.setParentFacilityCode(lookup.valueOf(baseFacilityCode, "F10"));
      agent.setPhoneNumber(lookup.valueOf(phoneNumber, "007"));
      agent.setActive(lookup.valueOf(active, "true"));
      return agent;
    }
  };
}
