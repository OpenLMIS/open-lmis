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
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.RnrLineItem;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.Property.newProperty;
import static java.util.Arrays.asList;

public class ReportBuilder {

  public static final Property<Report, String> agentCode = newProperty();
  public static final Property<Report, String> programCode = newProperty();
  public static final Property<Report, String> approverName = newProperty();
  public static final Property<Report, List<RnrLineItem>> products = newProperty();

  public static final Property<Report, Long> requisitionId = newProperty();

  public static final String DEFAULT_AGENT_CODE = "Agent Smith";
  public static final String DEFAULT_PROGRAM_CODE = "HIV";
  public static final String DEFAULT_APPROVER_NAME = "Approver";

  public static final Instantiator<Report> defaultReport = new Instantiator<Report>() {
    @Override
    public Report instantiate(PropertyLookup<Report> lookup) {
      Report report = new Report();
      report.setAgentCode(lookup.valueOf(agentCode, DEFAULT_AGENT_CODE));
      report.setProducts(lookup.valueOf(products, asList(make(a(RnrLineItemBuilder.defaultRnrLineItem)))));
      report.setProgramCode(lookup.valueOf(programCode, DEFAULT_PROGRAM_CODE));
      report.setApproverName(lookup.valueOf(approverName, DEFAULT_APPROVER_NAME));
      return report;
    }
  };
}
