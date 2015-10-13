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
  var program2 = {"id": 8, "name": "Program 8", "code": "P8", "description": "Program 8 Description"};
  var equipmentType = {"id": 3, "name": "Equipment Type 3", "code": "ET2",
    "description": "Equipment Type 3 Description"};
  var equipmentType2 = {"id": 9, "name": "Equipment Type 9", "code": "ET9",
    "description": "Equipment Type 9 Description"};
  var status = {"id": 4, "name": "Functional", "isBad": false};
  var status2 = {"id": 5, "name": "Not Functional", "isBad": true};
  var equipment = {"id": 6, name: "Dometic 400", code: "Dometic 400", "equipmentType": equipmentType,
    "equipmentTypeId": equipmentType.id, "manufacturer": "Dometic", "model": "400", "energyType": "gas"};
  var inventory = {"id": 7, "programId": program.id, "equipment": equipment, "facility": facility,
    "operationalStatusId": status.id};
  var pagination = {"totalRecords": 1, "page": 1};
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
    $httpBackend.whenGET('/equipment/inventory/facility/programs.json?facilityId='+facility.id).respond(200, {"programs": [program, program2]});
    $httpBackend.whenGET('/equipment/type/operational-status.json').respond(200, {"status": [status]});
    $httpBackend.flush();
  }));

  describe("Programs", function () {
    it("should populate my facility and its programs if 'my facility' selected and home facility assigned", function () {
      scope.selectedType = "0";
      scope.loadPrograms();
      $httpBackend.expectGET('/user/facilities.json').respond(200, {"facilityList": [facility]});
      $httpBackend.expectGET('/equipment/inventory/facility/programs.json?facilityId='+facility.id).respond(200, {"programs": [program, program2]});
      $httpBackend.flush();
      expect(scope.myFacility).toEqual(facility);
      expect(scope.facilityDisplayName).toEqual(facility.name);
      expect(scope.programs).toEqual([program, program2]);
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
      $httpBackend.expectGET('/equipment/inventory/programs.json').respond(200, {"programs": [program, program2]});
      $httpBackend.flush();
      expect(scope.programs).toEqual([program, program2]);
    });

    it("should select program and load equipment types when only one program for my facility", function () {
      scope.selectedType = "0";
      scope.loadPrograms();
      $httpBackend.expectGET('/user/facilities.json').respond(200, {"facilityList": [facility]});
      $httpBackend.expectGET('/equipment/inventory/facility/programs.json?facilityId='+facility.id).respond(200, {"programs": [program]});
      $httpBackend.expectGET('/equipment/manage/typesByProgram/'+program.id+'.json').respond(200, {"equipment_types": [equipmentType, equipmentType2]});
      $httpBackend.flush();
      expect(scope.selectedProgram).toEqual(program);
    });

    it("should select program and load equipment types when only one program for supervised facilities", function () {
      scope.selectedType = "1";
      scope.loadPrograms();
      $httpBackend.expectGET('/user/facilities.json').respond(200, {"facilityList": [facility]});
      $httpBackend.expectGET('/equipment/inventory/programs.json').respond(200, {"programs": [program]});
      $httpBackend.expectGET('/equipment/manage/typesByProgram/'+program.id+'.json').respond(200, {"equipment_types": [equipmentType, equipmentType2]});
      $httpBackend.flush();
      expect(scope.selectedProgram).toEqual(program);
    });
  });

  describe("Equipment types", function () {

    it("should load equipment types for a program", function () {
      scope.selectedProgram = program;
      scope.loadEquipmentTypes();
      $httpBackend.expectGET('/equipment/manage/typesByProgram/'+scope.selectedProgram.id+'.json').respond(200, {"equipment_types": [equipmentType, equipmentType2]});
      $httpBackend.flush();
      expect(scope.equipmentTypes).toEqual([equipmentType, equipmentType2]);
    });

    it("should select program and equipment type (and load inventory) when only one option for each", function () {
      scope.selectedType = "0";
      scope.selectedProgram = program;
      scope.loadEquipmentTypes();
      $httpBackend.expectGET('/equipment/manage/typesByProgram/'+program.id+'.json').respond(200, {"equipment_types": [equipmentType]});
      $httpBackend.whenGET('/equipment/inventory/list.json?equipmentTypeId='+equipmentType.id+'&page='+pagination.page+'&programId='+program.id+'&typeId='+scope.selectedType).respond(200, {"inventory": [inventory], "pagination": pagination});
      $httpBackend.flush();
      expect(scope.selectedEquipmentType).toEqual(equipmentType);
      expect(scope.inventory).toEqual([inventory]);
    });
  });

  describe("Inventory", function () {

    it("should load inventory if program selected and equipment type selected", function () {
      scope.selectedType = "0";
      scope.selectedProgram = program;
      scope.selectedEquipmentType = equipmentType;
      scope.loadInventory();
      $httpBackend.whenGET('/equipment/inventory/list.json?equipmentTypeId='+equipmentType.id+'&page='+pagination.page+'&programId='+program.id+'&typeId='+scope.selectedType).respond(200, {"inventory": [inventory], "pagination": pagination});
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
      $httpBackend.whenGET('/equipment/inventory/list.json?equipmentTypeId='+equipmentType.id+'&page='+pagination.page+'&programId='+program.id+'&typeId='+scope.selectedType).respond(200, {"inventory": [inventory], "pagination": pagination});
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
      item.prevStatusId = status2.id;
      item.operationalStatusId = status.id;
      scope.operationalStatusList = [status, status2];
      scope.updateStatus(item);
      $httpBackend.expectPOST('/equipment/inventory/status/update.json').respond(200, {"inventory": [inventory]});
      $httpBackend.flush();
      expect(item.showSuccess).toBeDefined();
      expect(item.showError).toBeUndefined();
      expect(item.prevStatusId).toEqual(item.operationalStatusId);
    });

    it("should open modal when status is different from previous and it is 'bad'", function () {
      var item = inventory;
      item.prevStatusId = status.id.toString();
      item.operationalStatusId = status2.id.toString();
      scope.operationalStatusList = [status, status2];
      scope.updateStatus(item);
      expect(scope.notFunctionalModal).toBeTruthy();
      expect(scope.modalItem).toBeDefined();
    });
  });

  describe("Initial load", function () {
    it("should select program and load equipment types when program is in route for my facility", function () {
      scope.selectedType = "0";
      routeParams.program = program.id.toString();
      routeParams.equipmentType = equipmentType.id.toString();
      scope.loadPrograms(true);
      $httpBackend.expectGET('/equipment/manage/typesByProgram/'+program.id+'.json').respond(200, {"equipment_types": [equipmentType]});
      $httpBackend.whenGET('/equipment/inventory/list.json?equipmentTypeId='+equipmentType.id+'&page='+pagination.page+'&programId='+program.id+'&typeId='+scope.selectedType).respond(200, {"inventory": [inventory], "pagination": pagination});
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
      $httpBackend.whenGET('/equipment/inventory/list.json?equipmentTypeId='+equipmentType.id+'&page='+pagination.page+'&programId='+program.id+'&typeId='+scope.selectedType).respond(200, {"inventory": [inventory], "pagination": pagination});
      $httpBackend.flush();
      expect(scope.selectedProgram).toEqual(program);
      expect(scope.selectedEquipmentType).toEqual(equipmentType);
      expect(scope.inventory).toEqual([inventory]);
    });
  });

  describe("Not Functional Modal", function () {
    it("should close modal on cancel, and reset values", function () {
      scope.origModalItem = angular.copy(inventory);
      scope.inventory = [inventory];
      scope.modalItem = inventory;
      scope.modalItem.operationalStatusId = status2.id.toString();
      scope.closeModal();
      expect(scope.notFunctionalModal).toBeFalsy();
//      expect(scope.inventory[0].operationalStatusId).toEqual(status.id.toString()); // TODO: need to uncomment and fix this
    });

    it("should save modal on successful save, and reset any values", function () {
      scope.modalItem = inventory;
      scope.notFunctionalForm = {};
      scope.notFunctionalForm.$invalid = false;
      scope.saveModal();
      $httpBackend.expectPOST('/equipment/inventory/save.json').respond(200, {"success": "Saved successfully"});
      $httpBackend.flush();
      expect(scope.notFunctionalModal).toBeFalsy();
      expect(scope.modalItem.showSuccess).toBeTruthy();
    });

    it("should give an error on invalid save", function () {
      scope.notFunctionalForm = {};
      scope.notFunctionalForm.$invalid = true;
      scope.saveModal();
      expect(scope.modalError).toEqual(messageService.get("message.equipment.inventory.data.invalid"));
    });

    it("should check for 'bad' functional status", function () {
      scope.notFunctionalStatusList = [status, status2];
      scope.modalItem = {};
      scope.checkForBadFunctionalStatus(status.id.toString());
      expect(scope.modalItem.badFunctionalStatusSelected).toEqual(status.isBad);
      scope.checkForBadFunctionalStatus(status2.id.toString());
      expect(scope.modalItem.badFunctionalStatusSelected).toEqual(status2.isBad);
    });
  });

  describe("Helper functions", function () {
    it("getAge should return age if installation year exists", function () {
      var testYear = 2015;
      var result = scope.getAge(testYear);
      expect(result).toBe(new Date().getFullYear() - testYear);
    });

    it("getAge should return null if installation year does NOT exist", function () {
      var testYear = undefined;
      var result = scope.getAge(testYear);
      expect(result).toBeNull();
    });

    it("getReplacementYear should return replacement year if installation year exists", function () {
      var testYear = 2015;
      var result = scope.getReplacementYear(testYear);
      expect(result).toBe(testYear + 10);
    });

    it("getReplacementYear should return null if installation year does NOT exist", function () {
      var testYear = undefined;
      var result = scope.getReplacementYear(testYear);
      expect(result).toBeNull();
    });
  });
});
