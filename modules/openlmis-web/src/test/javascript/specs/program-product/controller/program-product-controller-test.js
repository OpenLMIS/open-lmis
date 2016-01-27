/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('Test to print out jasmine version', function()
{
  it('prints jasmine version', function()
  {
    console.log('jasmine-version:' + jasmine.getEnv().versionString());
  });
});

describe('program product controller', function () {

  beforeEach(module('openlmis'));

  var mainScope, ppcScope, icmcScope;
  var $httpBackend, programProducts, programProductsService, programProductsISAService;

  beforeEach(inject(function (_$rootScope_, _$httpBackend_, $controller, ProgramProducts, ProgramProductsISA)
  {
    mainScope = _$rootScope_.$new();
    mainScope.isaForm = {$error: { required: "" }};

    $httpBackend = _$httpBackend_;
    programProductsService = ProgramProducts;
    programProductsISAService = ProgramProductsISA;

    var testDemographicCategories = [
      {'id': 1, 'name': 'Children Under Two'},
      {'id': 1, 'name': 'Pregnant Women'}
    ];

    var testPrograms = [
      {"id": 1, "name": "program1"},
      {"id": 2, "name": "program2"}
    ];
    
    $controller
    (
        ProgramProductController,
        {
          $scope: mainScope,
          programs: testPrograms,
          ProgramProducts: programProductsService,
          ProgramProductsISA: programProductsISAService,
          demographicCategories: testDemographicCategories
        }
    );

    ppcScope = mainScope.$new();

    $controller
    (
        ISACoefficientsModalController,
        {
          $scope: ppcScope,
          $rootScope: _$rootScope_
        }
    );

    icmcScope = ppcScope.$new();

    programProducts = [
      {"id": 1, "push": true, "program": {"id": 5}, "product": {"id": 1, "primaryName": "abc", "createdDate": 1371014384494,
        "modifiedDate": 1371014384494, "code": "P10"}, "dosesPerMonth": 30, "active": true},
      {"id": 1, "push": true, "program": {"id": 5}, "product": {"id": 1, "primaryName": "name", "createdDate": 1371014384494,
        "modifiedDate": 1371014384494, "code": "P10"}, "dosesPerMonth": 30, "active": true}
    ];
  }));

  it('should get program products', function () {
    mainScope.programId = 1;
    $httpBackend.expectGET('/programProducts/programId/1.json').respond(200, {"programProductList": programProducts});
    mainScope.loadProgramProducts();
    $httpBackend.flush();
    expect(mainScope.programProducts).toEqual(programProducts);
    expect(mainScope.filteredProducts).toEqual(programProducts);

  });

  it('should filter products', function () {
    mainScope.query = "abc";
    mainScope.programProducts = programProducts;
    mainScope.filterProducts();

    expect(mainScope.filteredProducts).toEqual([programProducts[0]]);
    expect(mainScope.filteredProducts.length).toEqual(1);
  });

  it('should set current program product to selected program product and enable modal', function()
  {
    var programProduct =
    {
      "id": 1, "push": true, "program": {"id": 5},
      "product": {"id": 1, "primaryName": "abc", "createdDate": 1371014384494, "modifiedDate": 1371014384494, "code": "P10"},
      "dosesPerMonth": 30, "active": true
    }

    var spyOnProgramProductISA = spyOn(window,'ProgramProductISA').andCallThrough();

    icmcScope.showProductISA(programProduct);

    expect(spyOnProgramProductISA).toHaveBeenCalled();
    expect(ppcScope.currentProgramProduct).toEqual(programProduct);
    expect(ppcScope.programProductISAModal).toBeTruthy();
  });

  it('should set current program product to null and disable modal', function () {
    icmcScope.clearAndCloseProgramProductISAModal();
    expect(icmcScope.population).toEqual(0);
    expect(icmcScope.inputClass).toBeFalsy();
    expect(icmcScope.currentProgramProduct).toBeNull();
    expect(icmcScope.programProductISAModal).toBeFalsy();
  });

  it("should highlight error when value is undefined", function () {
    mainScope.inputClass = true;
    var returnValue = icmcScope.highlightRequired(undefined);

    expect(returnValue).toEqual("required-error");
  });

  it("should not highlight error when value is defined", function () {
    icmcScope.inputClass = true;
    var returnValue = icmcScope.highlightRequired("abc");
    expect(returnValue).toEqual(null);
  });

  it("should update program product ISA if id already exists", function ()
  {
    var programProductIsa = {"id": 1, "whoRatio": 4, "dosesPerYear": 5, "bufferPercentage": 6, "adjustmentValue": 55};
    var productIsa = new ProgramProductISA();
    productIsa.init(programProductIsa);
    mainScope.currentProgramProduct = {"id": 1, "programProductIsa": productIsa};
    mainScope.isaToEdit = productIsa;

    $httpBackend.expect('PUT','/programProducts/1/isa/1.json', productIsa).respond(200);

    icmcScope.saveProductISA();
    $httpBackend.flush();
    expect(icmcScope.message).toEqual("message.isa.save.success");
    expect(icmcScope.programProductISAModal).toBeFalsy();
    expect(icmcScope.error).toEqual("");
  });

  it("should save program product ISA if id does not exist", function ()
  {
    var programProductIsa = {"whoRatio": 4, "dosesPerYear": 5, "bufferPercentage": 6, "adjustmentValue": 55};
    var productIsa = new ProgramProductISA();
    productIsa.init(programProductIsa);
    mainScope.currentProgramProduct = {"id": 1, "programProductIsa": productIsa};
    mainScope.isaToEdit = productIsa;
    $httpBackend.expect('POST','/programProducts/1/isa.json', productIsa).respond(200);

    icmcScope.saveProductISA();

    $httpBackend.flush();
    expect(icmcScope.message).toEqual("message.isa.save.success");
    expect(icmcScope.programProductISAModal).toBeFalsy();
    expect(icmcScope.error).toEqual("");
  });

  it("should not save ISA if required fields are not filled", function () {
    var programProductIsa = {"whoRatio": 4, "dosesPerYear": 5, "bufferPercentage": 6, "adjustmentValue": 55};
    var productIsa = new ProgramProductISA();
    productIsa.init(programProductIsa);
    icmcScope.currentProgramProduct = {"id": 1, "programProductIsa": productIsa};
    icmcScope.isaForm.$error.required = true;

    icmcScope.saveProductISA();

    expect(icmcScope.inputClass).toBeTruthy();
    expect(icmcScope.error).toEqual("form.error");
    expect(icmcScope.message).toEqual("");
  });

  it("should not save ISA if maximum isa value is less than minimum isa value", function ()
  {
    var programProductIsa =
    {
      "whoRatio": 4,
      "dosesPerYear": 5,
      "bufferPercentage": 6,
      "adjustmentValue": 55,
      "minimumValue":50,
      "maximumValue":5
    };

    var productIsa = new ProgramProductISA();
    productIsa.init(programProductIsa);
    mainScope.currentProgramProduct = {"id": 1, "programProductIsa": productIsa};
    mainScope.isaToEdit = productIsa;
    icmcScope.saveProductISA();

    expect(icmcScope.error).toEqual("error.minimum.greater.than.maximum");
    expect(icmcScope.message).toEqual("");
    expect(icmcScope.population).toEqual(0);
    expect(icmcScope.isaValue).toEqual(0);
  });


  it("should return calculated value based on formula if form is valid",function(){
    var programProductIsa = {"whoRatio": 2, "dosesPerYear": 1, "wastageFactor": 47, "bufferPercentage": 45, "adjustmentValue": 6, "minimumValue":2};
    var productIsa = new ProgramProductISA();
    productIsa.init(programProductIsa);
    icmcScope.population = 2;
    var spyOnCalculate = spyOn(productIsa,'calculate').andReturn(7);

    icmcScope.calculateValue(productIsa);

    expect(spyOnCalculate).toHaveBeenCalled();
    expect(icmcScope.isaValue).toEqual(7);
  });

  it("should not calculate isa value and show error if form is not valid",function()
  {
    var programProductIsa = {"whoRatio": 2, "dosesPerYear": 1, "wastageFactor": 47, "bufferPercentage": 45, "adjustmentValue": 6, "minimumValue":22, "maximumValue":3};
    var productIsa = new ProgramProductISA();
    productIsa.init(programProductIsa);
    mainScope.population = 2;
    var spyOnIsMaxLessThanMin = spyOn(productIsa,'isMaxLessThanMinValue').andReturn(true);

    icmcScope.calculateValue(productIsa);

    expect(spyOnIsMaxLessThanMin).toHaveBeenCalled();
    expect(icmcScope.population).toEqual(0);
    expect(icmcScope.isaValue).toEqual(0);
    expect(icmcScope.message).toEqual("");
    expect(icmcScope.error).toEqual("error.minimum.greater.than.maximum");
  });

})