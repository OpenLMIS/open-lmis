/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('program product controller', function () {

  beforeEach(module('openlmis.services'));
  var scope, ctrl, $httpBackend;
  var programProducts;

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    var program = [
      {"id": 1, "name": "program1"},
      {"id": 2, "name": "program2"}
    ];
    ctrl = $controller(ProgramProductController, {$scope: scope, programs: program});
    programProducts = [
      {"id": 1, "push": true, "program": {"id": 5}, "product": {"id": 1, "primaryName": "abc", "createdDate": 1371014384494,
        "modifiedDate": 1371014384494, "code": "P10"}, "dosesPerMonth": 30, "active": true},
      {"id": 1, "push": true, "program": {"id": 5}, "product": {"id": 1, "primaryName": "name", "createdDate": 1371014384494,
        "modifiedDate": 1371014384494, "code": "P10"}, "dosesPerMonth": 30, "active": true}
    ];
    scope.isaForm = {$error: { required: "" }};
  }));

  it('should get program products', function () {
    scope.programId = 1;
    $httpBackend.expectGET('/programProducts/programId/1.json').respond(200, {"programProductList": programProducts});
    scope.loadProgramProducts();
    $httpBackend.flush();
    expect(scope.programProducts).toEqual(programProducts);
    expect(scope.filteredProducts).toEqual(programProducts);

  });

  it('should filter products', function () {
    scope.query = "abc";
    scope.programProducts = programProducts;
    scope.filterProducts();

    expect(scope.filteredProducts).toEqual([programProducts[0]]);
    expect(scope.filteredProducts.length).toEqual(1);
  });

  it('should set current program product to selected program product and enable modal', function () {
    var programProduct = {"id": 1, "push": true, "program": {"id": 5}, "product": {"id": 1, "primaryName": "abc", "createdDate": 1371014384494,
      "modifiedDate": 1371014384494, "code": "P10"}, "dosesPerMonth": 30, "active": true}
    var spyOnProgramProductISA = spyOn(window,'ProgramProductISA').andCallThrough();

    scope.showProductISA(programProduct);

    expect(spyOnProgramProductISA).toHaveBeenCalled();
    expect(scope.currentProgramProduct).toEqual(programProduct);
    expect(scope.programProductISAModal).toBeTruthy();
  });

  it('should set current program product to null and disable modal', function () {
    scope.clearAndCloseProgramProductISAModal();
    expect(scope.population).toEqual(0);
    expect(scope.inputClass).toBeFalsy();
    expect(scope.currentProgramProduct).toBeNull();
    expect(scope.programProductISAModal).toBeFalsy();
  });

  it("should highlight error when value is undefined", function () {
    scope.inputClass = true;
    var returnValue = scope.highlightRequired(undefined);

    expect(returnValue).toEqual("required-error");
  });

  it("should not highlight error when value is defined", function () {
    scope.inputClass = true;
    var returnValue = scope.highlightRequired("abc");
    expect(returnValue).toEqual(null);
  });

  it("should update program product ISA if id already exists", function () {
    var programProductIsa = {"id": 1, "whoRatio": 4, "dosesPerYear": 5, "bufferPercentage": 6, "adjustmentValue": 55};
    var productIsa = new ProgramProductISA();
    productIsa.init(programProductIsa);
    scope.currentProgramProduct = {"id": 1, "programProductIsa": productIsa};

    $httpBackend.expect('PUT','/programProducts/1/isa/1.json', productIsa).respond(200);

    scope.saveProductISA();
    $httpBackend.flush();
    expect(scope.message).toEqual("message.isa.save.success");
    expect(scope.programProductISAModal).toBeFalsy();
    expect(scope.error).toEqual("");
  });

  it("should save program product ISA if id does not exist", function () {
    var programProductIsa = {"whoRatio": 4, "dosesPerYear": 5, "bufferPercentage": 6, "adjustmentValue": 55};
    var productIsa = new ProgramProductISA();
    productIsa.init(programProductIsa);
    scope.currentProgramProduct = {"id": 1, "programProductIsa": productIsa};
    $httpBackend.expect('POST','/programProducts/1/isa.json', productIsa).respond(200);

    scope.saveProductISA();

    $httpBackend.flush();
    expect(scope.message).toEqual("message.isa.save.success");
    expect(scope.programProductISAModal).toBeFalsy();
    expect(scope.error).toEqual("");
  });

  it("should not save ISA if required fields are not filled", function () {
    var programProductIsa = {"whoRatio": 4, "dosesPerYear": 5, "bufferPercentage": 6, "adjustmentValue": 55};
    var productIsa = new ProgramProductISA();
    productIsa.init(programProductIsa);
    scope.currentProgramProduct = {"id": 1, "programProductIsa": productIsa};
    scope.isaForm.$error.required = true;

    scope.saveProductISA();

    expect(scope.inputClass).toBeTruthy();
    expect(scope.error).toEqual("form.error");
    expect(scope.message).toEqual("");
  });

  it("should not save ISA if maximum isa value is less than minimum isa value", function () {
    var programProductIsa = {"whoRatio": 4, "dosesPerYear": 5, "bufferPercentage": 6, "adjustmentValue": 55 ,
      "minimumValue":50, "maximumValue":5};
    var productIsa = new ProgramProductISA();
    productIsa.init(programProductIsa);
    scope.currentProgramProduct = {"id": 1, "programProductIsa": productIsa};

    scope.saveProductISA();

    expect(scope.error).toEqual("error.minimum.greater.than.maximum");
    expect(scope.message).toEqual("");
    expect(scope.population).toEqual(0);
    expect(scope.isaValue).toEqual(0);
  });


  it("should return calculated value based on formula if form is valid",function(){
    var programProductIsa = {"whoRatio": 2, "dosesPerYear": 1, "wastageRate": 47, "bufferPercentage": 45, "adjustmentValue": 6, "minimumValue":2};
    var productIsa = new ProgramProductISA();
    productIsa.init(programProductIsa);
    scope.population = 2;
    var spyOnCalculate = spyOn(productIsa,'calculate').andReturn(7);

    scope.calculateValue(productIsa);

    expect(spyOnCalculate).toHaveBeenCalled();
    expect(scope.isaValue).toEqual(7);
  });

  it("should not calculate isa value and show error if form is not valid",function(){
    var programProductIsa = {"whoRatio": 2, "dosesPerYear": 1, "wastageRate": 47, "bufferPercentage": 45, "adjustmentValue": 6, "minimumValue":22, "maximumValue":3};
    var productIsa = new ProgramProductISA();
    productIsa.init(programProductIsa);
    scope.population = 2;
    var spyOnIsMaxLessThanMin = spyOn(productIsa,'isMaxLessThanMinValue').andReturn(true);

    scope.calculateValue(productIsa);

    expect(spyOnIsMaxLessThanMin).toHaveBeenCalled();
    expect(scope.population).toEqual(0);
    expect(scope.isaValue).toEqual(0);
    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("error.minimum.greater.than.maximum");
  });

})