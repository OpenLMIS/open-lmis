/**
 * Created by chunkyvr on 5/4/15.
 */
/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2015 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("In Equipment Inventory Controller,", function () {
  var scope, $httpBackend, ctrl, routeParams, messageService;
  var geographicZone = {"name": "District 1", "parent": {"name": "Region 1"}};
  var facility = {"id": 1, "name": "Test Facility", "code": "F1", "description": "Test Facility Description",
    "geographicZone": geographicZone};
  var program = {"id": 2, "name": "Program 2", "code": "P2", "description": "Program 2 Description"};
  var equipmentType = {"id": 3, "name": "Equipment Type 3", "code": "ET2",
    "description": "Equipment Type 3 Description"};
  var status = {"id": 4, "name": "Functional"};
  var status2 = {"id": 5, "name": "Not Functional"};
  var equipment = {"id": 6, name: "Dometic 400", code: "Dometic 400", "equipmentType": equipmentType,
    "equipmentTypeId": equipmentType.id, "manufacturer": "Dometic", "model": "400", "energyType": "gas"};
  var inventory = {"id": 7, "programId": program.id, "equipment": equipment, "facility": facility,
    "operationalStatusId": status.id};
  var groups = {};
  groups[geographicZone.parent.name] = {};
  groups[geographicZone.parent.name][geographicZone.name] = [inventory];

  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams, _messageService_) {
    scope = $rootScope.$new();
    routeParams = $routeParams;
    $httpBackend = _$httpBackend_;
    messageService = _messageService_;

    ctrl = $controller(EquipmentInventoryController, {$scope: scope, $routeParams: routeParams,
      messageService: messageService});
    $httpBackend.whenGET('/user/facilities.json').respond(200, {"facilityList": [facility]});
    $httpBackend.whenGET('/equipment/inventory/facility/programs.json?facilityId='+facility.id).respond(200, {"programs": [program]});
    $httpBackend.whenGET('/equipment/type/operational-status.json').respond(200, {"status": [status]});
    $httpBackend.flush();
  }));

  describe("Programs", function () {
    it("should populate my facility and its programs if 'my facility' selected and home facility assigned", function () {
      scope.selectedType = "0";
      scope.loadPrograms();
      $httpBackend.expectGET('/user/facilities.json').respond(200, {"facilityList": [facility]});
      $httpBackend.expectGET('/equipment/inventory/facility/programs.json?facilityId='+facility.id).respond(200, {"programs": [program]});
      $httpBackend.flush();
      expect(scope.myFacility).toEqual(facility);
      expect(scope.facilityDisplayName).toEqual(facility.code + " - " + facility.name);
      expect(scope.programs).toEqual([program]);
    });

    it("should populate none assigned facility and no programs if 'my facility' selected and home facility not assigned", function () {
      scope.selectedType = "0";
      scope.loadPrograms();
      $httpBackend.expectGET('/user/facilities.json').respond(200, {"facilityList": []});
      $httpBackend.flush();
      expect(scope.myFacility).toEqual(undefined);
      expect(scope.facilityDisplayName).toEqual(messageService.get("label.none.assigned"));
    });

    it("should populate programs if 'supervised facilities' selected", function () {
      scope.selectedType = "1";
      scope.loadPrograms();
      $httpBackend.expectGET('/equipment/inventory/programs.json').respond(200, {"programs": [program]});
      $httpBackend.flush();
      expect(scope.programs).toEqual([program]);
    });
  });

  describe("Equipment types", function () {

    it("should load equipment types for a program", function () {
      scope.selectedProgram = program;
      scope.loadEquipmentTypes();
      $httpBackend.expectGET('/equipment/manage/typesByProgram/'+scope.selectedProgram.id+'.json').respond(200, {"equipment_types": [equipmentType]});
      $httpBackend.flush();
      expect(scope.equipmentTypes).toEqual([equipmentType]);
    });
  });

  describe("Inventory", function () {

    it("should load inventory if program selected and equipment type selected", function () {
      scope.selectedType = "0";
      scope.selectedProgram = program;
      scope.selectedEquipmentType = equipmentType;
      scope.loadInventory();
      $httpBackend.expectGET('/equipment/inventory/list.json?equipmentTypeId='+equipmentType.id+'&programId='+program.id+'&typeId='+scope.selectedType).respond(200, {"inventory": [inventory]});
      $httpBackend.flush();
      expect(scope.inventory).toEqual([inventory]);
    });

    it("should load EMPTY inventory if program selected and equipment type NOT selected", function () {
      scope.selectedType = "0";
      scope.selectedProgram = program;
      scope.selectedEquipmentType = undefined;
      scope.loadInventory();
      expect(scope.inventory).toBeUndefined();
    });

    it("should load EMPTY inventory if program NOT selected and equipment type NOT selected", function () {
      scope.selectedType = "0";
      scope.selectedProgram = undefined;
      scope.selectedEquipmentType = undefined;
      scope.loadInventory();
      expect(scope.inventory).toBeUndefined();
    });

    it("should load districts and regions for facility inventory", function () {
      scope.selectedType = "0";
      scope.selectedProgram = program;
      scope.selectedEquipmentType = equipmentType;
      scope.loadInventory();
      $httpBackend.expectGET('/equipment/inventory/list.json?equipmentTypeId='+equipmentType.id+'&programId='+program.id+'&typeId='+scope.selectedType).respond(200, {"inventory": [inventory]});
      $httpBackend.flush();
      expect(scope.groups).toEqual(groups);
    });
  });

  describe("Functional Status", function () {
    it("should NOT update status when inventory initially loaded", function () {
      var item = inventory;
      scope.updateStatus(item);
      expect(item.showSuccess).toBeUndefined();
      expect(item.prevStatusId).toEqual(item.operationalStatusId);
    });

    it("should NOT update status when status is the same as previous", function () {
      var item = inventory;
      item.prevStatusId = status.id;
      scope.updateStatus(item);
      expect(item.showSuccess).toBeUndefined();
      expect(item.prevStatusId).toEqual(item.operationalStatusId);
    });

    it("should update status when status is different from previous, and show success when successful", function () {
      var item = inventory;
      item.prevStatusId = status.id;
      item.operationalStatusId = status2.id;
      scope.updateStatus(item);
      $httpBackend.expectPOST('/equipment/inventory/status/update.json').respond(200, {"inventory": [inventory]});
      $httpBackend.flush();
      expect(item.showSuccess).toBeDefined();
      expect(item.showError).toBeUndefined();
      expect(item.prevStatusId).toEqual(item.operationalStatusId);
    });
  });

  describe("Initial load", function () {
    it("should select program and load equipment types when program is in route for my facility", function () {
      scope.selectedType = "0";
      routeParams.program = program.id.toString();
      routeParams.equipmentType = equipmentType.id.toString();
      scope.loadPrograms(true);
      $httpBackend.expectGET('/equipment/manage/typesByProgram/'+program.id+'.json').respond(200, {"equipment_types": [equipmentType]});
      $httpBackend.expectGET('/equipment/inventory/list.json?equipmentTypeId='+equipmentType.id+'&programId='+program.id+'&typeId='+scope.selectedType).respond(200, {"inventory": [inventory]});
      $httpBackend.flush();
      expect(scope.selectedProgram).toEqual(program);
      expect(scope.selectedEquipmentType).toEqual(equipmentType);
      expect(scope.inventory).toEqual([inventory]);
    });

    it("should select program and load equipment types when program is in route for supervised facilities", function () {
      scope.selectedType = "1";
      routeParams.program = program.id.toString();
      routeParams.equipmentType = equipmentType.id.toString();
      scope.loadPrograms(true);
      $httpBackend.expectGET('/equipment/inventory/programs.json').respond(200, {"programs": [program]});
      $httpBackend.expectGET('/equipment/manage/typesByProgram/'+program.id+'.json').respond(200, {"equipment_types": [equipmentType]});
      $httpBackend.expectGET('/equipment/inventory/list.json?equipmentTypeId='+equipmentType.id+'&programId='+program.id+'&typeId='+scope.selectedType).respond(200, {"inventory": [inventory]});
      $httpBackend.flush();
      expect(scope.selectedProgram).toEqual(program);
      expect(scope.selectedEquipmentType).toEqual(equipmentType);
      expect(scope.inventory).toEqual([inventory]);
    });
  });
});