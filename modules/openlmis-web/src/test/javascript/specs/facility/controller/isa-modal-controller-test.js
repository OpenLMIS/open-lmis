/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("ISA Controller", function () {
  beforeEach(module('openlmis'));

  describe("Override ISA", function () {
    var scope, httpBackend, ctrl, routeParams;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      scope.$parent = $rootScope.$new();
      scope.$parent.facilityProgramProductsList = []
      routeParams = $routeParams;
      httpBackend = _$httpBackend_;
      ctrl = $controller(FacilityIsaModalController, {$scope: scope, facilityProgramProducts: null, $routeParams: routeParams});
    }));

    xit('should update ISA from current products', function()
    {
      scope.currentProgramProducts = [{programProductId: 1, facilityId: 1, overriddenIsa: 34},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45}];
      scope.currentProgram = {id : 1};
      httpBackend.when('GET', '/facility/1/program/1/isa.json').respond(scope.currentProgramProducts);
      scope.updateISA();
      expect(scope.$parent.facilityProgramProductsList[scope.currentProgram.id].length).toEqual(2);
    });

    it('should return immediately if current program is null', function() {
      scope.programProductsISAModal = true;
      scope.currentProgram = null;
      scope.$broadcast('showISAEditModal');
      scope.$apply();
      httpBackend.verifyNoOutstandingRequest();
    });

    xit('should set currentProgramProducts if already present in scope and return immediately', function()
    {
      scope.currentProgram = {id : 1};
      var currentProducts = [{programProductId: 1, facilityId: 1, overriddenIsa: 34},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45}];
      scope.$parent.facility = {catchmentPopulation : 89};
      scope.$parent.facilityProgramProductsList[scope.currentProgram.id] = currentProducts;
      scope.$broadcast('showISAEditModal');
      scope.$apply();
      httpBackend.verifyNoOutstandingRequest();
      expect(scope.currentProgramProducts).toEqual(currentProducts);
    });

    xit('should fetch facility program products for given programId and facilityId when not present', function()
    {
      scope.currentProgram = {id : 1};
      var currentProducts = [{programProductId: 1, facilityId: 1, overriddenIsa: 34,  calculatedIsa : '--'},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45,  calculatedIsa : '--' }];
      routeParams.facilityId = 1;
      scope.$parent.facility = {id:1, catchmentPopulation : 100};
      scope.$parent.facilityProgramProductsList[scope.currentProgram.id] = null;
      scope.$broadcast('showISAEditModal');
      var programProductList = {programProductList: currentProducts};
      httpBackend.expectGET('/facility/1/program/1/isa.json').respond(programProductList, 200);
      scope.$apply();
      httpBackend.flush();
      expect(scope.$parent.facilityProgramProductsList[scope.currentProgram.id]).toEqual(currentProducts);
      expect(scope.currentProgramProducts).toEqual(scope.$parent.facilityProgramProductsList[scope.currentProgram.id]);
    });

    it('should not calculate ISA values when catchment population and programProductISA(s) are null', function() {
      scope.currentProgram = {id : 1};
      var currentProducts = [
        {programProductId: 1, facilityId: 1, overriddenIsa: 34, programProductIsa: {}},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45, programProductIsa: {}}
      ];
      routeParams.facilityId = 1;
      scope.$parent.facility = {id:1, catchmentPopulation : null};
      scope.$parent.facilityProgramProductsList[scope.currentProgram.id] = null;
      scope.$broadcast('showISAEditModal');
      var programProductList = {programProductList: currentProducts};
      httpBackend.expectGET('/facility/1/program/1.json').respond(programProductList, 200);
      scope.$apply();
      httpBackend.flush();
      expect(scope.$parent.facilityProgramProductsList[scope.currentProgram.id][0].calculatedIsa).toEqual("--");
      expect(scope.$parent.facilityProgramProductsList[scope.currentProgram.id][1].calculatedIsa).toEqual("--");
    });

    xit('should calculate ISA values for all facility program products', function()
    {
      scope.currentProgram = {id : 1};
      var currentProducts = [
        {programProductId: 1, facilityId: 1, overriddenIsa: 34, programProductIsa: {
          whoRatio: 10,
          dosesPerYear: 10,
          wastageFactor: 10,
          bufferPercentage: 50,
          minimumValue: 10,
          adjustmentValue: 15
        }},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45, programProductIsa: {
          whoRatio: 1,
          dosesPerYear: 1,
          wastageFactor: 1,
          bufferPercentage: 5,
          minimumValue: 1,
          adjustmentValue: 5
        }}
      ];
      routeParams.facilityId = 1;
      scope.$parent.facility = {id:1, catchmentPopulation : 1000};
      scope.$parent.facilityProgramProductsList[scope.currentProgram.id] = null;
      scope.$broadcast('showISAEditModal');
      var programProductList = {programProductList: currentProducts};
      httpBackend.expectGET('/facility/1/program/1/isa.json').respond(programProductList, 200);
      scope.$apply();
      httpBackend.flush();
      expect(scope.$parent.facilityProgramProductsList[scope.currentProgram.id][0].calculatedIsa).toEqual(1265);
      expect(scope.$parent.facilityProgramProductsList[scope.currentProgram.id][1].calculatedIsa).toEqual(6);
      expect(scope.currentProgramProducts).toEqual(scope.$parent.facilityProgramProductsList[scope.currentProgram.id]);
    });

    it('should filter program products based on query', function() {
      var currentProducts = [
        {programProductId: 1, facilityId: 1, overriddenIsa: 34, programProductIsa: {}, product: {primaryName: 'abcd'}},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45, programProductIsa: {}, product: {primaryName: 'efgh'}}
      ];
      scope.currentProgramProducts = currentProducts;
      scope.query = 'ef';
      scope.updateCurrentProgramProducts();

      expect(scope.filteredProducts).toEqual([currentProducts[1]]);
    });

    it('should filter program products based on query even for case mismatch', function() {
      var currentProducts = [
        {programProductId: 1, facilityId: 1, overriddenIsa: 34, programProductIsa: {}, product: {primaryName: 'abcd'}},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45, programProductIsa: {}, product: {primaryName: 'efgh'}}
      ];
      scope.currentProgramProducts = currentProducts;
      scope.query = 'EF';
      scope.updateCurrentProgramProducts();

      expect(scope.filteredProducts).toEqual([currentProducts[1]]);
    });

    it('should have all program products if query is empty', function() {
      var currentProducts = [
        {programProductId: 1, facilityId: 1, overriddenIsa: 34, programProductIsa: {}, product: {primaryName: 'abcd'}},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45, programProductIsa: {}, product: {primaryName: 'efgh'}}
      ];
      scope.currentProgramProducts = currentProducts;
      scope.query = '';
      scope.updateCurrentProgramProducts();

      expect(scope.filteredProducts).toEqual(currentProducts);
    });

    it('should filter program products based on query even if query contains leading and trailing whitespace', function() {
      var currentProducts = [
        {programProductId: 1, facilityId: 1, overriddenIsa: 34, programProductIsa: {}, product: {primaryName: 'abcd'}},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45, programProductIsa: {}, product: {primaryName: 'efgh'}}
      ];
      scope.currentProgramProducts = currentProducts;
      scope.query = '  efgh  ';
      scope.updateCurrentProgramProducts();

      expect(scope.filteredProducts).toEqual([currentProducts[1]]);
    });

  });
});