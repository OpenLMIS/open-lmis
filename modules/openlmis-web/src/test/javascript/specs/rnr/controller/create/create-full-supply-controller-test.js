/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('CreateFullSupplyController', function () {
  var scope, ctrl, httpBackend, location, routeParams, controller, localStorageService;

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller, $routeParams, _localStorageService_) {
    scope = $rootScope.$new();
    $rootScope.hasPermission = function () {
    };

    scope.isFormDisabled = function () {
      return false;
    };

    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    scope.$parent.facility = "10134";
    scope.$parent.program = {code:"programCode", "id":1};

    scope.saveRnrForm = {$error:{ rnrError:false }};
    localStorageService = _localStorageService_;
    routeParams = {"facility":"1", "program":"1", "period":2};
    scope.$parent.rnr = {"id":"rnrId", "fullSupplyLineItems":[]};

    httpBackend.expect('GET', '/requisitions/lossAndAdjustments/reference-data.json').respond({"lossAdjustmentTypes":{}});
    $rootScope.fixToolBar = function () {
    };
    ctrl = controller(CreateFullSupplyController, {$scope:scope, $location:location, $routeParams:routeParams, localStorageService:localStorageService});

    scope.lossesAndAdjustmentTypes = [
      {"name":"some name"},
      {"name":"some other name"}
    ];
  }));

  it("should display modal window with appropriate type options to add losses and adjustments", function () {
    var lineItem = { "id":"1", lossesAndAdjustments:[
      {"type":{"name":"some name"}, "quantity":"4"}
    ]};
    scope.showLossesAndAdjustments(lineItem);
    expect(scope.lossesAndAdjustmentsModal).toBeTruthy();
    expect(scope.lossesAndAdjustmentTypesToDisplay).toEqual([
      {"name":"some other name"}
    ]);
  });

  it('should save Losses and Adjustments and close modal if valid', function () {
    var rnrLineItem = new RegularRnrLineItem({});
    spyOn(rnrLineItem, 'validateLossesAndAdjustments').andReturn(true);

    scope.$parent.rnr.fullSupplyLineItems.push(rnrLineItem);
    spyOn(rnrLineItem, 'reEvaluateTotalLossesAndAdjustments');

    scope.$parent.rnr = {"id":"rnrId", fullSupplyLineItems:[rnrLineItem]};
    scope.programRnrColumnList = [
      {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}}
    ];

    scope.lossesAndAdjustmentsModal = true;
    scope.currentRnrLineItem = rnrLineItem;
    scope.saveLossesAndAdjustmentsForRnRLineItem();

    expect(rnrLineItem.reEvaluateTotalLossesAndAdjustments).toHaveBeenCalled();
    expect(scope.lossesAndAdjustmentsModal).toBeFalsy();
    expect(scope.modalError).toEqual('');
    expect(scope.lossAndAdjustment).toBeUndefined();
  });

  it('should not save Losses and Adjustments and not close modal if not valid', function () {
    var rnrLineItem = new RegularRnrLineItem({});
    spyOn(rnrLineItem, 'validateLossesAndAdjustments').andReturn(false);

    scope.$parent.rnr.fullSupplyLineItems.push(rnrLineItem);
    spyOn(rnrLineItem, 'reEvaluateTotalLossesAndAdjustments');

    scope.$parent.rnr = {"id":"rnrId", fullSupplyLineItems:[rnrLineItem]};
    scope.programRnrColumnList = [
      {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}}
    ];
    scope.lossesAndAdjustmentsModal = true;
    scope.currentRnrLineItem = rnrLineItem;
    scope.saveLossesAndAdjustmentsForRnRLineItem();

    expect(rnrLineItem.reEvaluateTotalLossesAndAdjustments).not.toHaveBeenCalledWith(scope.$parent.rnr, scope.programRnrColumnList);
    expect(scope.lossesAndAdjustmentsModal).toBeTruthy();
    expect(scope.modalError).toEqual('error.correct.highlighted');
  });
});

