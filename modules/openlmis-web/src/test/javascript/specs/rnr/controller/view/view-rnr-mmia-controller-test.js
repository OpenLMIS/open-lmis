/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


describe('ViewRnrViaDetailController', function () {
  var httpBackend, scope, route, requisition, messageService, downloadPdfService, downloadSimamService;

  var submitterText = "submitterText";
  var approverText = "approverText";

  var mockedRnrItem = {
    rnr: {
      facility: {code: "F10", name: "Health Facility 1"},
      fullSupplyLineItems: [
        {id: 1, expirationDate: null, categoryName: "Adult", productCode: "0A002"},
        {id: 2, categoryName: "Adult", productCode: "0A001"},
        {id: 3, expirationDate: "28/02/2000", categoryName: "Adult", productCode: "0B001"},
        {id: 4, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 5, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 6, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 7, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 8, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 9, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 10, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 11, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 12, expirationDate: "28/02/2000", categoryName: "Adult"},
        {id: 13, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 14, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 15, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 16, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 17, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 18, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 19, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 20, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 21, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 22, expirationDate: "28/02/2000", categoryName: "Children"},
        {id: 23, expirationDate: "28/02/2000", categoryName: "Solution"},
        {id: 24, expirationDate: "28/02/2000", categoryName: "Solution"}],
      period: {stringStartDate: "01/01/2014", stringEndDate: "31/01/2014", stringYear: "2014"},
      regimenLineItems: [
        {id: 40, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 41, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 42, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 43, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 44, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 45, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 46, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 47, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 48, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 49, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 50, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 51, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 52, categoryName: "Adults", patientsOnTreatment: 1},
        {id: 53, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 54, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 55, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 57, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 56, categoryName: "Paediatrics", patientsOnTreatment: 1},
        {id: 57, categoryName: "Paediatrics", patientsOnTreatment: 1}],
      patientQuantifications: [
        {id: 1, total: 1},
        {id: 2, total: 1},
        {id: 3, total: 1},
        {id: 4, total: 1},
        {id: 5, total: 1},
        {id: 6, total: 1},
        {id: 7, total: 1}
      ],
      rnrSignatures: [{type: "SUBMITTER",text: submitterText},{type: "APPROVER",text: approverText}]
    }
  };

  beforeEach(module('openlmis'));

  beforeEach(inject(function ($httpBackend, $rootScope, $controller, _messageService_, _downloadPdfService_, _downloadSimamService_) {
    httpBackend = $httpBackend;
    scope = $rootScope.$new();
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], period: {numberOfMonths: 3}};
    route = {current: {params:{'programId': 2, 'rnr': 1, 'supplyType': 'fullSupply'}}};
    messageService =  _messageService_;
    downloadPdfService = _downloadPdfService_;
    downloadSimamService = _downloadSimamService_;
    spyOn(downloadPdfService, "init").andReturn(function(a,b){});
    spyOn(downloadSimamService, "init").andReturn(function(a,b){});
    $controller(ViewRnrMmiaController, {$scope: scope, $route: route});
  }));


  it('should get rnr item',function(){
    initMockRequisition();

    expect(scope.rnr.fullSupplyLineItems.length).toBe(24);
    expect(scope.rnr.regimenLineItems.length).toBe(19);
    expect(scope.rnr.patientQuantifications.length).toBe(7);
  });

  it('should format year and month',function(){
    spyOn(messageService, "get").andReturn("January");
    initMockRequisition();

    expect(scope.year).toBe("2014");
    expect(scope.month).toBe("January");
  });

  it('should get the correct submitter and approver on mmia view',function(){
    initMockRequisition();

    expect(scope.submitterSignature).toEqual(submitterText);
    expect(scope.approverSignature).toEqual(approverText);
  });

  it('should format validate',function(){
    scope.rnr = mockedRnrItem.rnr;
    scope.initProduct();

    var actualResult = _.groupBy(scope.rnrLineItems, function (item) {
      return item.categoryName;
    });

    expect(actualResult.Adult[0].productCode).toEqual("0A001");
    expect(actualResult.Adult[1].productCode).toEqual("0A002");
    expect(actualResult.Adult[2].productCode).toEqual("0B001");
    expect(actualResult.Adult[1].expirationDate).toBe(null);
    expect(actualResult.Adult[0].expirationDate).toBe(undefined);
    expect(actualResult.Adult[2].expirationDate).toEqual("Feb 2000");
    expect(actualResult.Adult.length).toBe(14);
    expect(actualResult.Adult[13].productCode).toBe(undefined);
    expect(actualResult.Children.length).toBe(11);
    expect(actualResult.Children[10].productCode).toBe(undefined);
    expect(actualResult.Solution.length).toBe(3);
    expect(actualResult.Solution[2].productCode).toBe(undefined);
  });

  it('should initProduct exclude undefined category',function(){
    var mockedRnrItem = {
      rnr: {
        facility: {code: "F10", name: "Health Facility 1"},
        fullSupplyLineItems: [
          {id: 1, expirationDate: null, categoryName: "Adult", productCode: "0A002"},
          {id: 24, expirationDate: "28/02/2000", categoryName: "Solution"}],
      }
    };

    scope.rnr = mockedRnrItem.rnr;
    scope.initProduct();

    var actualResult = _.groupBy(scope.rnrLineItems, function (item) {
      return item.categoryName;
    });

    expect(actualResult.Adult.length).toBe(3);
    expect(actualResult.Children.length).toBe(1);
    expect(actualResult.Solution.length).toBe(2);
  });

  it('should initRegime exclude undefined category',function(){
    var mockedRnrItem = {
      rnr: {
        facility: {code: "F10", name: "Health Facility 1"},
        regimenLineItems: [
          {id: 40, categoryName: "Adults", patientsOnTreatment: 1}]
      }
    };
    scope.rnr = mockedRnrItem.rnr;
    scope.initRegime();

    var actualRegimens = _.groupBy(scope.regimens, function (item) {
      return item.categoryName;
    });

    expect(actualRegimens.Adults.length).toBe(3);
    expect(actualRegimens.Paediatrics.length).toBe(2);
  });

  it('should calculate regime total',function(){
    scope.rnr = mockedRnrItem.rnr;
    scope.initRegime();

    var actualRegimens = _.groupBy(scope.regimens, function (item) {
      return item.categoryName;
    });

    expect(scope.regimeTotal).toBe(19);
    expect(actualRegimens.Adults.length).toBe(11);
    expect(actualRegimens.Paediatrics.length).toBe(12);
  });

  function initMockRequisition() {
    var expectedUrl = "/requisitions/1/skipped.json";
    httpBackend.expect('GET', expectedUrl).respond(200, mockedRnrItem);
    scope.loadMmiaDetail();
    httpBackend.flush();
  }
});
