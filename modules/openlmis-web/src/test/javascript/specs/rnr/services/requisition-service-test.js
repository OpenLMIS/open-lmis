/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("requisitionService", function () {

  beforeEach(module('openlmis.services'));

  beforeEach(module('openlmis.localStorage'));


  var scope, requisitionService, location, routeParams, messageService;

  beforeEach(inject(function ($location, $routeParams, $rootScope, _requisitionService_, _messageService_) {
    location = $location;
    routeParams = $routeParams;
    scope = $rootScope.$new();
    requisitionService = _requisitionService_;
    messageService = _messageService_;
  }));

  it('should set error pages count', function () {
    var errorPages = {fullSupply: [1, 2], nonFullSupply: [5]};
    scope.rnr = {getErrorPages: function () {
      return errorPages;
    }};
    requisitionService.setErrorPages(scope);
    expect(scope.fullSupplyErrorPagesCount).toEqual(2);
    expect(scope.nonFullSupplyErrorPagesCount).toEqual(1);
    expect(scope.errorPages).toEqual(errorPages);
  });

  it('should reset error pages', function () {
    scope.errorPages = {fullSupply: [1, 2], nonFullSupply: [5]};
    requisitionService.resetErrorPages(scope);
    expect(scope.errorPages.fullSupply).toEqual([]);
    expect(scope.errorPages.nonFullSupply).toEqual([]);
  });

  it('should populate scope with required data and functions', function () {
    routeParams.supplyType = "fullSupply";
    scope.rnr = {emergency: false };
    spyOn(messageService, 'get');

    requisitionService.populateScope(scope, location, routeParams);

    expect(messageService.get).toHaveBeenCalledWith('label.currency.symbol');
    expect(scope.visibleTab).toEqual("fullSupply");
    expect(scope.requisitionType).toEqual("requisition.type.regular");
    expect(scope.switchSupplyType).toBeDefined();
    expect(scope.showCategory).toBeDefined();
    expect(scope.goToPage).toBeDefined();
    expect(scope.highlightRequired).toBeDefined();
  });

  it('refresh grid and not save rnr for view', function () {
    scope.saveRnr = function () {
    };
    spyOn(scope, 'saveRnr');
    spyOn(location, 'search');
    routeParams.supplyType = "fullSupply";
    scope.rnr = {fullSupplyLineItems: []};
    requisitionService.refreshGrid(scope, location, routeParams, false);

    expect(scope.page).toEqual({fullSupply: [], nonFullSupply: [], regimen: []});
    expect(scope.visibleTab).toEqual("fullSupply");
    expect(location.search).toHaveBeenCalledWith('supplyType', 'fullSupply');
    expect(scope.numberOfPages).toEqual(1);
    expect(scope.saveRnr).wasNotCalled();
  });

  it('refresh grid and save rnr for create and approve', function () {
    scope.saveRnr = function () {
    };
    spyOn(scope, 'saveRnr');
    spyOn(location, 'search');
    routeParams.supplyType = "nonFullSupply";
    scope.rnr = {nonFullSupplyLineItems: []};
    requisitionService.refreshGrid(scope, location, routeParams, true);

    expect(scope.page).toEqual({fullSupply: [], nonFullSupply: [], regimen: []});
    expect(scope.visibleTab).toEqual("nonFullSupply");
    expect(location.search).toHaveBeenCalledWith('supplyType', 'nonFullSupply');
    expect(scope.numberOfPages).toEqual(1);
    expect(scope.saveRnr).toHaveBeenCalled();
  });

});