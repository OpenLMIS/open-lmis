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
  var httpBackend, scope, route, location, requisition, requisitionService;

  var rnrItemsVisible = [];

  var rnrItemsForPagination = {
    rnr: {
      facility: {code: "F10", name: "Health Facility 1"},
      fullSupplyLineItems: [
        {beginningBalance: 1, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 2, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 3, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 4, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 5, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 6, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 7, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 8, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 9, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 10, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 11, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 12, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 13, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 14, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 15, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 16, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 17, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 18, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 19, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 20, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 21, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
        {beginningBalance: 22, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140}],
      period: {stringStartDate: "01/01/2014", stringEndDate: "31/01/2014"}
    }
  };

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($httpBackend, $rootScope, $controller, $location, _requisitionService_) {
    httpBackend = $httpBackend;
    scope = $rootScope.$new();
    location = $location;
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], period: {numberOfMonths: 3}};
    route = {current: {params:{'programId': 2, 'rnr': 1, 'supplyType': 'fullSupply'}}}
    requisitionService =  _requisitionService_;
    $controller(ViewRnrViaDetailController, {$scope: scope, $route: route, $location:$location});
  }));


  it('should get rnr items size is same as pageSize',function(){
    var rnrItems = {
      rnr: {
        facility: {code: "F10", name: "Health Facility 1"},
        fullSupplyLineItems: [{beginningBalance: 98, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140}],
        period: {stringStartDate: "01/01/2014", stringEndDate: "31/01/2014"}
      }
    };

    initMockRequisition(rnrItems);
    expect(scope.rnrItems.length).toEqual(20);
  });

  it('should get numberPages is 2 ',function(){

    initMockRequisition(rnrItemsForPagination);

    expect(scope.numPages).toEqual(2);
  });

  it('should get the 2 page of visible rnr items ',function(){
    scope.currentPage = 2;
    var expectRnrList = [
      {beginningBalance: 21, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
      {beginningBalance: 22, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {},
      {}];
    initMockRequisition(rnrItemsForPagination);

    expect(scope.rnrItemsVisible).toEqual(expectRnrList);
  });

  it('should get the correct submitter and approver on via view',function(){
    var submitterText = "submitterText";
    var approverText = "approverText";

    var rnrItems = {
      rnr: {
        facility: {code: "F10", name: "Health Facility 1"},
        fullSupplyLineItems: [{beginningBalance: 98, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140}],
        period: {stringStartDate: "01/01/2014", stringEndDate: "31/01/2014"},
        rnrSignatures:[{type: "SUBMITTER",text: submitterText},{type: "APPROVER",text: approverText}]
      }
    };

    initMockRequisition(rnrItems);

    expect(scope.submitterSignature).toEqual(submitterText);
    expect(scope.approverSignature).toEqual(approverText);
  });

  function initMockRequisition(rnrItems) {
    var expectedUrl = "/requisitions/1/skipped.json";
    httpBackend.expect('GET', expectedUrl).respond(200, rnrItems);
    scope.loadRequisitionDetail();
    httpBackend.flush();
  }
});
