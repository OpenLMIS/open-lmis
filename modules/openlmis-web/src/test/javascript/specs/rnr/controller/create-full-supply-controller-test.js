/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('RequisitionFullSupplyController', function () {
  var scope, ctrl, httpBackend, location, routeParams, controller, localStorageService;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
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
    ctrl = controller(RequisitionFullSupplyController, {$scope:scope, $location:location, $routeParams:routeParams, localStorageService:localStorageService});

    scope.allTypes = [
      {"name":"some name"},
      {"name":"some other name"}
    ];
  }));

  it("should display modal window with appropriate type options to add losses and adjustments", function () {
    var lineItem = { "id":"1", lossesAndAdjustments:[
      {"type":{"name":"some name"}, "quantity":"4"}
    ]};
    scope.showLossesAndAdjustmentModalForLineItem(lineItem);
    expect(scope.lossesAndAdjustmentsModal).toBeTruthy();
    expect(scope.lossesAndAdjustmentTypesToDisplay).toEqual([
      {"name":"some other name"}
    ]);
  });

  it('should save Losses and Adjustments and close modal if valid', function () {
    var rnrLineItem = new RnrLineItem({});
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
    var rnrLineItem = new RnrLineItem({});
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
    expect(scope.modalError).toEqual('Please correct the highlighted fields before submitting');
  });
});

