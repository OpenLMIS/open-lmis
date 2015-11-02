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
  var scope, route, location, requisition, requisitionService;

  var rnrItemsVisible = [];

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($rootScope, $controller, $location, _requisitionService_) {
    scope = $rootScope.$new();
    location = $location;
    requisition = {lineItems: [], nonFullSupplyLineItems: [], regimenLineItems: [], equipmentLineItems :[], period: {numberOfMonths: 3}};
    route = {current: {params:{'programId': 2, 'rnr': 1, 'supplyType': 'fullSupply'}}}
    requisitionService =  _requisitionService_;
    $controller(ViewRnrViaDetailController, {$scope: scope, $route: route, $location:$location});
  }));


  it('should get rnr items ',function(){
    var rnrItems = {rnr: {facility:{code: "F10", name: "Health Facility 1"},
                          fullSupplyLineItems: [{beginningBalance: 98, quantityRequested: 12345, stockInHand: 261, totalLossesAndAdjustments: 140}],
                          period: {stringStartDate: "01/01/2014", stringEndDate: "31/01/2014"}}};

    scope.loadRequisitionDetail();

    //expect(scope.rnrItems).toEqual(rnrItems);
    //expect(scope.lossesAndAdjustmentsModal).toBeTruthy();
  });


});
