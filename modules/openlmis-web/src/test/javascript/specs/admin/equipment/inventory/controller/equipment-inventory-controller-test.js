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
  var facility = {"id": 1, "name": "Test Facility", "code": "F1", "description": "Test Facility Description"};
  var program = {"id": 2, "name": "Program 2", "code": "P2", "description": "Program 2 Description"};
  var equipmentType = {"id": 3, "name": "Equipment Type 3", "code": "ET2", "description": "Equipment Type 3 Description"};
  var status = {"id": 4, "name": "Fully Operational"};

  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams, _messageService_) {
    scope = $rootScope.$new();
    routeParams = $routeParams;
    $httpBackend = _$httpBackend_;
    messageService = _messageService_;

    ctrl = $controller(EquipmentInventoryController, {$scope: scope, $routeParams: routeParams,
      messageService: messageService});
    $httpBackend.whenGET('/user/facilities.json').respond(200, {"facilityList": [facility]});
    $httpBackend.whenGET('/equipment/inventory/facility/programs.json?facilityId=1').respond(200, {"programs": [program]});
    $httpBackend.whenGET('/equipment/type/operational-status.json').respond(200, {"status": [status]});
    $httpBackend.flush();
  }));

  describe("Programs", function () {
    it("should populate my facility and its programs if 'my facility' selected and home facility assigned", function () {
      scope.selectedType = "0";
      scope.loadPrograms(scope.selectedType);
      $httpBackend.expectGET('/user/facilities.json').respond(200, {"facilityList": [facility]});
      $httpBackend.expectGET('/equipment/inventory/facility/programs.json?facilityId=1').respond(200, {"programs": [program]});
      $httpBackend.flush();
      expect(scope.myFacility).toEqual(facility);
      expect(scope.facilityDisplayName).toEqual(facility.code + " - " + facility.name);
      expect(scope.programs).toEqual([program]);
    });

    it("should populate none assigned facility and no programs if 'my facility' selected and home facility not assigned", function () {
      scope.selectedType = "0";
      scope.loadPrograms(scope.selectedType);
      $httpBackend.expectGET('/user/facilities.json').respond(200, {"facilityList": []});
      $httpBackend.flush();
      expect(scope.myFacility).toEqual(undefined);
      expect(scope.facilityDisplayName).toEqual(messageService.get("label.none.assigned"));
      expect(scope.programs).toEqual(undefined);
    });

    it("should populate programs if 'supervised facilities' selected", function () {
      scope.selectedType = "1";
      scope.loadPrograms(scope.selectedType);
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
});