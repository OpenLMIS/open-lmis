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
  var httpBackend, scope, route, requisition, messageService;

  var submitterText = "submitterText";
  var approverText = "approverText";

  var mockedRnrItem = {
    rnr: {
      facility: {code: "F10", name: "Health Facility 1"},
      fullSupplyLineItems: [
        {id: 1, expirationDate: null},
        {id: 2},
        {id: 3, expirationDate: "28/02/2000"},
        {id: 4, expirationDate: "28/02/2000"},
        {id: 5, expirationDate: "28/02/2000"},
        {id: 6, expirationDate: "28/02/2000"},
        {id: 7, expirationDate: "28/02/2000"},
        {id: 8, expirationDate: "28/02/2000"},
        {id: 9, expirationDate: "28/02/2000"},
        {id: 10, expirationDate: "28/02/2000"},
        {id: 11, expirationDate: "28/02/2000"},
        {id: 12, expirationDate: "28/02/2000"},
        {id: 13, expirationDate: "28/02/2000"},
        {id: 14, expirationDate: "28/02/2000"},
        {id: 15, expirationDate: "28/02/2000"},
        {id: 16, expirationDate: "28/02/2000"},
        {id: 17, expirationDate: "28/02/2000"},
        {id: 18, expirationDate: "28/02/2000"},
        {id: 19, expirationDate: "28/02/2000"},
        {id: 20, expirationDate: "28/02/2000"},
        {id: 21, expirationDate: "28/02/2000"},
        {id: 22, expirationDate: "28/02/2000"},
        {id: 23, expirationDate: "28/02/2000"},
        {id: 24, expirationDate: "28/02/2000"}],
      period: {stringStartDate: "01/01/2014", stringEndDate: "31/01/2014", stringYear: "2014"},
      regimenLineItems: [
        {id: 40, patientsOnTreatment: 1},
        {id: 41, patientsOnTreatment: 1},
        {id: 42, patientsOnTreatment: 1},
        {id: 43, patientsOnTreatment: 1},
        {id: 44, patientsOnTreatment: 1},
        {id: 45, patientsOnTreatment: 1},
        {id: 46, patientsOnTreatment: 1},
        {id: 47, patientsOnTreatment: 1},
        {id: 48, patientsOnTreatment: 1},
        {id: 49, patientsOnTreatment: 1},
        {id: 50, patientsOnTreatment: 1},
        {id: 51, patientsOnTreatment: 1},
        {id: 52, patientsOnTreatment: 1},
        {id: 53, patientsOnTreatment: 1},
        {id: 54, patientsOnTreatment: 1},
        {id: 55, patientsOnTreatment: 1},
        {id: 57, patientsOnTreatment: 1},
        {id: 56, patientsOnTreatment: 1}],
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
  beforeEach(inject(function ($httpBackend, $rootScope, $controller, _messageService_) {
    httpBackend = $httpBackend;
    scope = $rootScope.$new();
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], period: {numberOfMonths: 3}};
    route = {current: {params:{'programId': 2, 'rnr': 1, 'supplyType': 'fullSupply'}}}
    messageService =  _messageService_;
    $controller(ViewRnrMmiaController, {$scope: scope, $route: route});
  }));


  it('should get rnr item',function(){
    initMockRequisition();

    expect(scope.rnr.fullSupplyLineItems.length).toBe(24);
    expect(scope.rnr.regimenLineItems.length).toBe(18);
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
    //initMockRequisition();

    expect(scope.adult[0].expirationDate).toBe(null);
    expect(scope.adult[1].expirationDate).toBe(undefined);
    expect(scope.adult[2].expirationDate).toEqual(new Date("2000-02-28"));
    expect(scope.adult.length).toBe(12);
    expect(scope.children.length).toBe(10);
    expect(scope.other.length).toBe(2);
  });

  it('should calculate regime total',function(){
    scope.rnr = mockedRnrItem.rnr;
    scope.initRegime();
    //initMockRequisition();

    expect(scope.regimeTotal).toBe(18);
    expect(scope.regimeAdult.length).toBe(8);
    expect(scope.regimeChildren.length).toBe(10);

  });

  function initMockRequisition() {
    var expectedUrl = "/requisitions/1/skipped.json";
    httpBackend.expect('GET', expectedUrl).respond(200, mockedRnrItem);
    scope.loadMmiaDetail();
    httpBackend.flush();
  }
});
