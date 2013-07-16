/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("ISA Controller", function () {
  beforeEach(module('openlmis.services'));

  describe("Override ISA", function () {
    var scope, httpBackend, ctrl, routeParams;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      scope.$parent = $rootScope.$new();
      scope.$parent.facilityProgramProductsList = []
      routeParams = $routeParams;
      httpBackend = _$httpBackend_;
      ctrl = $controller(IsaModalController, {$scope: scope, facilityProgramProducts: null, $routeParams: routeParams});
    }));

    it('should update ISA from current products', function() {
      scope.currentProgramProducts = [{programProductId: 1, facilityId: 1, overriddenIsa: 34},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45}];
      scope.currentProgram = {id : 1};
      httpBackend.when('GET', '/facility/1/program/1/isa.json').respond(scope.currentProgramProducts);
      scope.updateISA();
      expect(scope.$parent.facilityProgramProductsList[scope.currentProgram.id].length).toEqual(2);
    });

    it('should return immediately if modal is closed', function() {
      scope.$parent.programProductsISAModal = false;
      scope.currentProgram = {id : 1};
      scope.$apply();
      httpBackend.verifyNoOutstandingRequest();
    });

    it('should return immediately if current program is null', function() {
      scope.$parent.programProductsISAModal = true;
      scope.currentProgram = null;
      scope.$apply();
      httpBackend.verifyNoOutstandingRequest();
    });

    it('should set currentProgramProducts if already present in scope and return immediately', function() {
      scope.currentProgram = {id : 1};
      var currentProducts = [{programProductId: 1, facilityId: 1, overriddenIsa: 34},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45}];
      scope.$parent.facility = {catchmentPopulation : 89};
      scope.$parent.facilityProgramProductsList[scope.currentProgram.id] = currentProducts;
      scope.$parent.programProductsISAModal = true;
      scope.$apply();
      httpBackend.verifyNoOutstandingRequest();
      expect(scope.currentProgramProducts).toEqual(currentProducts);
    });

    it('should fetch facility program products for given programId and facilityId when not present', function() {
      scope.currentProgram = {id : 1};
      var currentProducts = [{programProductId: 1, facilityId: 1, overriddenIsa: 34,  calculatedIsa : '--'},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45,  calculatedIsa : '--' }];
      routeParams.facilityId = 1;
      scope.$parent.facility = {id:1, catchmentPopulation : 100};
      scope.$parent.facilityProgramProductsList[scope.currentProgram.id] = null;
      scope.$parent.programProductsISAModal = true;
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
      scope.$parent.programProductsISAModal = true;
      var programProductList = {programProductList: currentProducts};
      httpBackend.expectGET('/facility/1/program/1/isa.json').respond(programProductList, 200);
      scope.$apply();
      httpBackend.flush();
      expect(scope.$parent.facilityProgramProductsList[scope.currentProgram.id][0].calculatedIsa).toEqual("--");
      expect(scope.$parent.facilityProgramProductsList[scope.currentProgram.id][1].calculatedIsa).toEqual("--");
    });

    it('should calculate ISA values for all facility program products', function() {
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
      scope.$parent.programProductsISAModal = true;
      var programProductList = {programProductList: currentProducts};
      httpBackend.expectGET('/facility/1/program/1/isa.json').respond(programProductList, 200);
      scope.$apply();
      httpBackend.flush();
      expect(scope.$parent.facilityProgramProductsList[scope.currentProgram.id][0].calculatedIsa).toEqual(153);
      expect(scope.$parent.facilityProgramProductsList[scope.currentProgram.id][1].calculatedIsa).toEqual(6);
      expect(scope.currentProgramProducts).toEqual(scope.$parent.facilityProgramProductsList[scope.currentProgram.id]);
    });

    it('should reset all isa values', function() {
      var currentProducts = [
        {programProductId: 1, facilityId: 1, overriddenIsa: 34, programProductIsa: {}},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45, programProductIsa: {}}
      ];
      scope.currentProgramProducts = currentProducts;

      scope.resetAllToCalculatedIsa();

      expect(scope.currentProgramProducts[0].overriddenIsa).toEqual(null);
      expect(scope.currentProgramProducts[1].overriddenIsa).toEqual(null);
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