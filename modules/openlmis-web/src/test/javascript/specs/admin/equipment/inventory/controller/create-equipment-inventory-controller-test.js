/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2015 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("In Create Equipment Inventory Controller,", function () {
  var scope, $httpBackend, ctrl, routeParams, messageService, $controller;
  var facility = {"id": 1, "name": "Test Facility", "code": "F1", "description": "Test Facility Description"};
  var program = {"id": 2, "name": "Program 2", "code": "P2", "description": "Program 2 Description"};
  var equipmentType = {"id": 3, "name": "Equipment Type 3", "code": "ET2",
    "description": "Equipment Type 3 Description", coldChain: false};
  var cceEquipmentType = {"id": 3, "name": "Equipment Type 3", "code": "ET2",
    "description": "Equipment Type 3 Description", coldChain: true};
  var labStatus = {"id": 4, "name": "Fully Operational", category: "LAB"};
  var cceStatus = {"id": 9, "name": "Functional", category: "CCE", isBad: false};
  var cceStatus2 = {"id": 12, "name": "Not Functional", category: "CCE", isBad: true};
  var notFunctionalStatus = {"id": 10, "name": "Obsolete", category: "CCE Not Functional"};
  var statuses = [labStatus, cceStatus, notFunctionalStatus];
  var equipment = {"id": 6, name: "Dometic 400", code: "Dometic 400", "equipmentType": equipmentType,
    "equipmentTypeId": equipmentType.id, "manufacturer": "Dometic", "model": "400", "energyType": "gas"};
  var inventory = {"id": 7, "programId": program.id, "equipment": equipment, "facility": facility,
    "operationalStatusId": status.id, "facilityId": facility.id};
  var donor = {"id": 8, "name": "Donor 1"};
  var energyType = {"id": 11, "name": "Electricity"};

  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, _$controller_, $routeParams, _messageService_) {
    scope = $rootScope.$new();
    routeParams = $routeParams;
    $httpBackend = _$httpBackend_;
    messageService = _messageService_;
    $controller = _$controller_;

    routeParams.equipmentType = equipmentType.id;
    routeParams.program = program.id;
  }));

  describe("Initial load", function () {
    it("should load data from the server", function () {
      routeParams.from = "0";
      ctrl = $controller(CreateEquipmentInventoryController, {$scope: scope, $routeParams: routeParams});
      $httpBackend.expectGET('/equipment/manage/list-by-type.json?equipmentTypeId='+routeParams.equipmentType).respond(200, {"equipments": [equipment]});
      $httpBackend.expectGET('/equipment/type/id.json?id='+routeParams.equipmentType).respond(200, {"equipment_type": [equipmentType]});
      $httpBackend.expectGET('/user/facilities.json').respond(200, {"facilityList": [facility]});
      $httpBackend.expectGET('/equipment/type/operational-status.json').respond(200, {"status": statuses});
      $httpBackend.expectGET('/donor/list.json').respond(200, {"donors": [donor]});
      $httpBackend.expectGET('/equipment/energy-type/list.json').respond(200, {"energyType": [energyType]});
      $httpBackend.flush();
      expect(scope.equipments).toEqual([equipment]);
      expect(scope.manufacturers).toEqual([equipment.manufacturer]);
      expect(scope.equipmentType).toEqual([equipmentType]);
      expect(scope.labOperationalStatusList).toEqual([labStatus]);
      expect(scope.cceOperationalStatusList).toEqual([cceStatus]);
      expect(scope.cceNotFunctionalStatusList).toEqual([notFunctionalStatus]);
      expect(scope.donors).toEqual([donor]);
    });
  });

  describe("Facilities", function () {
    describe("Add inventory", function () {
      it("should get my facility if my facility was selected in previous screen", function () {
        routeParams.from = "0";
        ctrl = $controller(CreateEquipmentInventoryController, {$scope: scope, $routeParams: routeParams});
        $httpBackend.expectGET('/equipment/manage/list-by-type.json?equipmentTypeId=' + routeParams.equipmentType).respond(200, {"equipments": [equipment]});
        $httpBackend.expectGET('/equipment/type/id.json?id=' + routeParams.equipmentType).respond(200, {"equipment_type": [equipmentType]});
        $httpBackend.expectGET('/user/facilities.json').respond(200, {"facilityList": [facility]});
        $httpBackend.expectGET('/equipment/type/operational-status.json').respond(200, {"status": [statuses]});
        $httpBackend.expectGET('/donor/list.json').respond(200, {"donors": [donor]});
        $httpBackend.expectGET('/equipment/energy-type/list.json').respond(200, {"energyType": [energyType]});
        $httpBackend.flush();
        expect(scope.inventory.facility).toEqual(facility);
        expect(scope.inventory.facilityId).toEqual(facility.id);
        expect(scope.facilityDisplayName).toEqual(scope.inventory.facility.code + " - " + scope.inventory.facility.name);
      });

      it("should get supervised facilities, if supervised facilities was selected in previous screen", function () {
        routeParams.from = "1";
        ctrl = $controller(CreateEquipmentInventoryController, {$scope: scope, $routeParams: routeParams});
        $httpBackend.expectGET('/equipment/manage/list-by-type.json?equipmentTypeId=' + routeParams.equipmentType).respond(200, {"equipments": [equipment]});
        $httpBackend.expectGET('/equipment/type/id.json?id=' + routeParams.equipmentType).respond(200, {"equipment_type": [equipmentType]});
        $httpBackend.expectGET('/equipment/inventory/supervised/facilities.json?programId=' + routeParams.program).respond(200, {"facilities": [facility]});
        $httpBackend.expectGET('/equipment/type/operational-status.json').respond(200, {"status": [statuses]});
        $httpBackend.expectGET('/donor/list.json').respond(200, {"donors": [donor]});
        $httpBackend.expectGET('/equipment/energy-type/list.json').respond(200, {"energyType": [energyType]});
        $httpBackend.flush();
        expect(scope.facilities).toEqual([facility]);
      })
    });

    describe("Edit inventory", function () {
      it("should get my facility if my facility was selected in previous screen", function () {
        routeParams.from = "0";
        routeParams.id = inventory.id;
        ctrl = $controller(CreateEquipmentInventoryController, {$scope: scope, $routeParams: routeParams});
        $httpBackend.expectGET('/equipment/manage/list-by-type.json?equipmentTypeId=' + routeParams.equipmentType).respond(200, {"equipments": [equipment]});
        $httpBackend.expectGET('/equipment/type/id.json?id=' + routeParams.equipmentType).respond(200, {"equipment_type": [equipmentType]});
        $httpBackend.expectGET('/equipment/inventory/by-id.json?id=' + routeParams.id).respond(200, {"inventory": inventory});
        $httpBackend.expectGET('/equipment/type/operational-status.json').respond(200, {"status": [statuses]});
        $httpBackend.expectGET('/donor/list.json').respond(200, {"donors": [donor]});
        $httpBackend.expectGET('/equipment/energy-type/list.json').respond(200, {"energyType": [energyType]});
        $httpBackend.flush();
        expect(scope.inventory.facility).toEqual(facility);
        expect(scope.inventory.facilityId).toEqual(facility.id);
        expect(scope.facilityDisplayName).toEqual(scope.inventory.facility.code + " - " + scope.inventory.facility.name);
      });

      it("should get supervised facilities, if supervised facilities was selected in previous screen", function () {
        routeParams.from = "1";
        routeParams.id = inventory.id;
        ctrl = $controller(CreateEquipmentInventoryController, {$scope: scope, $routeParams: routeParams});
        $httpBackend.expectGET('/equipment/manage/list-by-type.json?equipmentTypeId=' + routeParams.equipmentType).respond(200, {"equipments": [equipment]});
        $httpBackend.expectGET('/equipment/type/id.json?id=' + routeParams.equipmentType).respond(200, {"equipment_type": [equipmentType]});
        $httpBackend.expectGET('/equipment/inventory/by-id.json?id=' + routeParams.id).respond(200, {"inventory": inventory});
        $httpBackend.expectGET('/equipment/type/operational-status.json').respond(200, {"status": [statuses]});
        $httpBackend.expectGET('/donor/list.json').respond(200, {"donors": [donor]});
        $httpBackend.expectGET('/equipment/energy-type/list.json').respond(200, {"energyType": [energyType]});
        $httpBackend.expectGET('/equipment/inventory/supervised/facilities.json?programId=' + routeParams.program).respond(200, {"facilities": [facility]});
        $httpBackend.flush();
        expect(scope.facilities).toEqual([facility]);
      })
    });
  });

  describe("Test functions", function () {
    beforeEach(function () {
      routeParams.from = "0";
      ctrl = $controller(CreateEquipmentInventoryController, {$scope: scope, $routeParams: routeParams});
      $httpBackend.expectGET('/equipment/manage/list-by-type.json?equipmentTypeId='+routeParams.equipmentType).respond(200, {"equipments": [equipment]});
      $httpBackend.expectGET('/equipment/type/id.json?id='+routeParams.equipmentType).respond(200, {"equipment_type": [equipmentType]});
      $httpBackend.expectGET('/user/facilities.json').respond(200, {"facilityList": [facility]});
      $httpBackend.expectGET('/equipment/type/operational-status.json').respond(200, {"status": [statuses]});
      $httpBackend.expectGET('/donor/list.json').respond(200, {"donors": [donor]});
      $httpBackend.expectGET('/equipment/energy-type/list.json').respond(200, {"energyType": [energyType]});
      $httpBackend.flush();
    });

    it("should update models when manufacturer selected", function () {
      scope.selected.manufacturer = equipment.manufacturer;
      scope.updateModels();
      expect(scope.models).toEqual([equipment.model]);
      expect(scope.inventory.equipment).toBeUndefined();
      expect(scope.inventory.equipmentId).toBeUndefined();
    });

    it("should update equipment info when manufacturer/model selected", function () {
      scope.selected.manufacturer = equipment.manufacturer;
      scope.selected.model = equipment.model;
      scope.updateEquipmentInfo();
      expect(scope.inventory.equipment).toEqual(equipment);
      expect(scope.inventory.equipmentId).toEqual(equipment.id);
    });

    it("should save inventory when form is valid and save is successful (for editing)", function () {
      scope.inventoryForm = {$invalid: false};
      scope.inventory = inventory;
      scope.screenType = 'edit';
      scope.saveInventory();
      $httpBackend.expectPOST('/equipment/inventory/save.json').respond(200, {"success": "Saved successfully"});
      $httpBackend.flush();
      expect(scope.$parent.message).toEqual(messageService.get("Saved successfully"));
      expect(scope.error).toEqual('');
    });

    it("should save inventory when form is valid and save is successful (for creating equipment)", function () {
      scope.inventoryForm = {$invalid: false};
      scope.inventory = inventory;
      scope.screenType = 'create';
      scope.equipmentType = equipmentType;
      scope.saveInventory();
      $httpBackend.expectPOST('/equipment/inventory/save.json').respond(200, {"success": "Saved successfully"});
      $httpBackend.flush();
      expect(scope.$parent.message).toEqual(messageService.get("Saved successfully"));
      expect(scope.error).toEqual('');
      expect(scope.inventory.equipment.equipmentTypeName).toEqual('equipment');
    });

    it("should save inventory when form is valid and save is successful (for creating cold chain equipment)", function () {
      scope.inventoryForm = {$invalid: false};
      scope.inventory = inventory;
      scope.screenType = 'create';
      scope.equipmentType = cceEquipmentType;
      scope.saveInventory();
      $httpBackend.expectPOST('/equipment/inventory/save.json').respond(200, {"success": "Saved successfully"});
      $httpBackend.flush();
      expect(scope.$parent.message).toEqual(messageService.get("Saved successfully"));
      expect(scope.error).toEqual('');
      expect(scope.inventory.equipment.equipmentTypeName).toEqual('coldChainEquipment');
    });

    it("should give error when form is NOT valid", function () {
      scope.inventoryForm = {$invalid: true};
      scope.saveInventory();
      expect(scope.error).toEqual(messageService.get('message.equipment.inventory.data.invalid'));
    });

    it("should check for 'bad' status", function () {
      scope.cceOperationalStatusList = [cceStatus, cceStatus2];
      scope.inventory = inventory;
      scope.inventory.operationalStatusId = cceStatus.id;
      scope.checkForBadStatus();
      expect(scope.badStatusSelected).toEqual(cceStatus.isBad);
      scope.inventory.operationalStatusId = cceStatus2.id;
      scope.checkForBadStatus();
      expect(scope.badStatusSelected).toEqual(cceStatus2.isBad);
    });
  });
});