/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.*;

@Category(UnitTests.class)
public class SupervisoryNodeTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none() ;

  @Test
  public void shouldThrowIfParentNodeIsInvalid() throws Exception{
    SupervisoryNode parent = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setParent(parent);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.supervisory.node.parent.invalid");

    supervisoryNode.validateParent();
  }

  @Test
  public void shouldThrowIfParentNodeIsSame(){
    SupervisoryNode parent = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode,with(SupervisoryNodeBuilder.id,1L),with(SupervisoryNodeBuilder.code,"N1")));
    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode,with(SupervisoryNodeBuilder.id,1L),with(SupervisoryNodeBuilder.code,"N2")));
    supervisoryNode.setParent(parent);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.supervisory.node.parent.invalid");

    supervisoryNode.validateParent();
  }
}
