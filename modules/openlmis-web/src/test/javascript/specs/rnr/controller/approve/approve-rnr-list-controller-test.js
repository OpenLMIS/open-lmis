/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('ApproveRnrListController', function () {

  var scope, ctrl, httpBackend, controller, messageService;
  var requisitionList;

  beforeEach(module('openlmis'));

  beforeEach(inject(function ($httpBackend, $rootScope, $controller, _messageService_) {
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;
    messageService = _messageService_;

    requisitionList = [
      {"facilityName":"first facility", "programName":"first program", "facilityCode":"first code"},
      {"facilityName":"second facility", "programName":"second program", "facilityCode":"second code"}
    ];
    ctrl = controller(ApproveRnrListController, {$scope:scope, requisitionList:requisitionList});
  }));

  it('should show all requisitions if filter is not applied', function () {
    expect(scope.filteredRequisitions).toEqual(requisitionList);
    expect(scope.query).toBeUndefined();
    expect(scope.searchField).toBeUndefined();
  });


  it('should Filter requisitions against program name', function () {
    scope.query = "first";
    scope.searchField = "programName";

    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitionList[0]);
  });

  it('should Filter requisitions against facility name', function () {
    scope.query = "second facility";
    scope.searchField = "facilityName";
    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitionList[1]);
  });

  it('should Filter requisitions against facility code', function () {
    scope.query = "second CO";
    scope.searchField = "facilityCode";
    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitionList[1]);
  });

  it('should be able to Filter requisitions against all fields also', function() {
    scope.query = "second";
    scope.searchField = "";

    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitionList[1]);
  });

  it('should be able to case-insensitively Filter requisitions', function() {
    scope.query = "seCOnD";
    scope.searchField = "";

    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(requisitionList[1]);
  });



});

