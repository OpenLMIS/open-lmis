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

  beforeEach(module('openlmis'));

  var scope, requisitionService, location, routeParams, messageService;
  var columns = [
    {"id": 0, "name": "skipped", "position": 1, "source": {"description": "Reference Data", "name": "REFERENCE", "code": "R"}, "sourceConfigurable": false, "label": "Product Code", "formula": "", "indicator": "O", "used": true, "visible": true, "mandatory": true, "description": "Unique identifier for each commodity", "formulaValidationRequired": true},
    {"id": 1, "name": "productCode", "position": 1, "source": {"description": "Reference Data", "name": "REFERENCE", "code": "R"}, "sourceConfigurable": false, "label": "Product Code", "formula": "", "indicator": "O", "used": true, "visible": true, "mandatory": true, "description": "Unique identifier for each commodity", "formulaValidationRequired": true},
    {"id": 2, "name": "product", "position": 2, "source": {"description": "Reference Data", "name": "REFERENCE", "code": "R"}, "sourceConfigurable": false, "label": "Product", "formula": "", "indicator": "R", "used": true, "visible": true, "mandatory": true, "description": "Primary name of the product", "formulaValidationRequired": true},
    {"id": 3, "name": "dispensingUnit", "position": 3, "source": {"description": "Reference Data", "name": "REFERENCE", "code": "R"}, "sourceConfigurable": false, "label": "Unit/Unit of Issue", "formula": "", "indicator": "U", "used": true, "visible": true, "mandatory": false, "description": "Dispensing unit for this product", "formulaValidationRequired": true},
    {"id": 4, "name": "beginningBalance", "position": 4, "source": {"description": "User Input", "name": "USER_INPUT", "code": "U"}, "sourceConfigurable": false, "label": "Beginning Balance", "formula": "", "indicator": "A", "used": true, "visible": true, "mandatory": false, "description": "Stock in hand of previous period.This is quantified in dispensing units", "formulaValidationRequired": true},
    {"id": 7, "name": "lossesAndAdjustments", "position": 7, "source": {"description": "User Input", "name": "USER_INPUT", "code": "U"}, "sourceConfigurable": false, "label": "Total Losses / Adjustments", "formula": "D1 + D2+D3...DN", "indicator": "D", "used": true, "visible": false, "mandatory": false, "description": "All kind of looses/adjustments made at the facility", "formulaValidationRequired": true}
  ];
  var visibleFullScrollableColumns = [columns[3], columns[4]];
  var visibleFullFixedColumns = [columns[0], columns[1], columns[2]];

  var visibleNonFullScrollableColumns = [columns[3]];
  var visibleNonFullFixedColumns = [columns[1], columns[2]];

  beforeEach(inject(function ($location, $routeParams, $rootScope, _requisitionService_, _messageService_) {
    location = $location;
    routeParams = $routeParams;
    scope = $rootScope.$new();
    requisitionService = _requisitionService_;
    messageService = _messageService_;
  }));

  it('should set error pages count', function () {
    var errorPages = {fullSupply: [1, 2], nonFullSupply: [5], regimen: []};
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

  it("should return null for highlightRequired if skipped is true", function () {
    scope.rnr = {emergency: false };
    routeParams.supplyType = "fullSupply";
    requisitionService.populateScope(scope, location, routeParams);

    expect(scope.highlightRequired("some-random-string", undefined, true)).toEqual(null);
  });

  it('refresh grid and not save rnr for view', function () {
    scope.saveRnr = function () {
    };
    spyOn(scope, 'saveRnr');
    spyOn(location, 'search');
    routeParams.supplyType = "fullSupply";
    scope.rnr = {fullSupplyLineItems: []};
    requisitionService.refreshGrid(scope, location, routeParams, false);

    expect(scope.page).toEqual({fullSupply: [], nonFullSupply: [], regimen: [], equipment : []});
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

    expect(scope.page).toEqual({fullSupply: [], nonFullSupply: [], regimen: [], equipment: []});
    expect(scope.visibleTab).toEqual("nonFullSupply");
    expect(location.search).toHaveBeenCalledWith('supplyType', 'nonFullSupply');
    expect(scope.numberOfPages).toEqual(1);
    expect(scope.saveRnr).toHaveBeenCalled();
  });

  it('should return column map for full supply visible columns', function () {
    var mappedColumns = requisitionService.getMappedVisibleColumns(columns, ['skipped', 'productCode', 'product']);

    expect(mappedColumns.fullSupply.scrollable).toEqual(visibleFullScrollableColumns);
    expect(mappedColumns.fullSupply.fixed).toEqual(visibleFullFixedColumns);
  });

  it('should return column map for non full supply visible columns', function () {
    var mappedColumns = requisitionService.getMappedVisibleColumns(columns, ['skipped', 'productCode', 'product']);

    expect(mappedColumns.nonFullSupply.scrollable).toEqual(visibleNonFullScrollableColumns);
    expect(mappedColumns.nonFullSupply.fixed).toEqual(visibleNonFullFixedColumns);
  });

  it('should skip column from map', function () {
    var mappedColumns = requisitionService.getMappedVisibleColumns(columns, ['skipped', 'productCode', 'product'], ['beginningBalance']);

    expect(mappedColumns.fullSupply.scrollable.length).toEqual(1);
  });

});