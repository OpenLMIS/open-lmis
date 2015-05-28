/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller.equipment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.ColdChainEquipment;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.service.EquipmentService;
import org.openlmis.equipment.service.EquipmentTypeService;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EquipmentControllerTest {

  @Mock
  EquipmentService service;

  @Mock
  MessageService messageService;

  @Mock
  EquipmentTypeService equipmentTypeService;

  @InjectMocks
  EquipmentController controller;

  private MockHttpServletRequest request;

  @Before
  public void setUp() {
    initMocks(this);
    request = new MockHttpServletRequest(USER, USER);
    request.getSession().setAttribute(USER_ID, 1L);
    when(messageService.message(anyString())).thenReturn("the message");
  }
  private EquipmentType makeAnEquipmentType() {
    EquipmentType equipmentType=new EquipmentType();
    equipmentType.setColdChain(false);
    equipmentType.setId(1L);
    equipmentType.setName("LAB");
    return equipmentType;
  }
  private Equipment makeAnEquipment() {
    Equipment equipment = new Equipment();
    equipment.setId(1L);
    equipment.setName("Ice Maker");
    equipment.setEquipmentTypeId(1L);
    return equipment;
  }

  private ColdChainEquipment makeAnColdChainEquipment(){
    ColdChainEquipment coldChainEquipment=new ColdChainEquipment();
    coldChainEquipment.setId(1L);
    coldChainEquipment.setName("Refrigerator");
    coldChainEquipment.setCode("CODE");
    coldChainEquipment.setDesignationId(1L);
    coldChainEquipment.setPqsStatusId(1L);
    coldChainEquipment.setEquipmentTypeId(1L);
    return coldChainEquipment;
  }


  @Test
  public void shouldGetEquipmentById() throws Exception {
    Equipment equipment = makeAnEquipment();
    when(service.getById(2L)).thenReturn(equipment);
    ResponseEntity<OpenLmisResponse> response = controller.getEquipmentById(2L);
    assertThat(equipment, is(response.getBody().getData().get("equipment")));
  }

  @Test
  public void shouldGetList() throws Exception {
    Equipment equipment = makeAnEquipment();
    ColdChainEquipment coldChainEquipment=makeAnColdChainEquipment();
    EquipmentType equipmentType=new EquipmentType();
    when(service.getAllByType(1L)).thenReturn(asList(equipment));
    when(service.getAllCCE()).thenReturn(asList(coldChainEquipment));
    when(equipmentTypeService.getTypeById(1L)).thenReturn(equipmentType);

    ResponseEntity<OpenLmisResponse> response = controller.getList(1L);
    assertThat(asList(equipment), is(response.getBody().getData().get("equipments")));
  }

  @Test
  public void shouldSaveChanges() throws Exception {
    EquipmentType equipmentType=makeAnEquipmentType();
    Equipment equipment = makeAnEquipment();
    ColdChainEquipment coldChainEquipment=makeAnColdChainEquipment();
    equipment.setEquipmentType(equipmentType);
    coldChainEquipment.setEquipmentType(equipmentType);

    when(equipmentTypeService.getTypeById(equipment.getEquipmentTypeId())).thenReturn(equipmentType);
    doNothing().when(service).saveEquipment(any(Equipment.class));
    doNothing().when(service).saveColdChainEquipment(any(ColdChainEquipment.class));
    doNothing().when(service).updateEquipment(any(Equipment.class));
    doNothing().when(service).updateColdChainEquipment(any(ColdChainEquipment.class));

    ResponseEntity<OpenLmisResponse> equipmentResponse = controller.save(equipment);
    assertThat(equipment, is(equipmentResponse.getBody().getData().get("equipment")));
    assertThat(equipmentResponse.getBody().getSuccessMsg(), is(notNullValue()));

    ResponseEntity<OpenLmisResponse> coldChainResponse = controller.save(coldChainEquipment);
    assertThat(coldChainEquipment, is( coldChainResponse.getBody().getData().get("equipment")));
    assertThat(coldChainResponse.getBody().getSuccessMsg(), is(notNullValue()));
  }

}