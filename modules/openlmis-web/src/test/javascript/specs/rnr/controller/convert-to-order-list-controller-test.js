/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

describe('ConvertToOrderListController', function () {

  var scope, ctrl, httpBackend, controller, routeParams, location;
  var requisitions, $dialog, messageService;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(module('ui.bootstrap.dialog'));

  beforeEach(inject(function ($httpBackend, $rootScope, $controller, $routeParams, $location, _$dialog_, _messageService_) {
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;
    routeParams = $routeParams;
    location = $location;
    $dialog = _$dialog_;
    messageService = _messageService_;
    spyOn(OpenLmisDialog, 'newDialog');

    scope.maxNumberOfPages = 2;

    requisitions = [
      {"facilityName": "first facility", "programName": "first program", "facilityCode": "first code", supplyingDepot: "supplying depot first "},
      {"facilityName": "second facility", "programName": "second program", "facilityCode": "second code", supplyingDepot: "supplying depot second"},
      {"facilityName": "third facility", "programName": "third program", "facilityCode": "third code", supplyingDepot: "supplying depot third"}
    ];

    ctrl = controller;

    httpBackend.expect('GET', '/requisitions-for-convert-to-order.json?page=1&searchType=all').respond({"rnr_list": [requisitions[0], requisitions[1]]});

    ctrl(ConvertToOrderListController, {$scope: scope, $location: location, $routeParams: routeParams});
  }));

  it('should set page line items based on pageSize', function () {
    routeParams.page = 2;
    httpBackend.expect('GET', '/requisitions-for-convert-to-order.json?page=2&searchType=all').respond(200, {"rnr_list": [requisitions[2]]});

    ctrl(ConvertToOrderListController, {$scope: scope, $location: location, $routeParams: routeParams});

    httpBackend.flush();

    expect(scope.currentPage).toEqual(2);
    expect(scope.filteredRequisitions).toEqual([requisitions[2]]);
  });

  it('should set page line items based on pageSize on route update', function () {
    routeParams.page = 2;
    routeParams.searchType = 'facilityCode';
    routeParams.searchVal = 'first';
    httpBackend.expect('GET', '/requisitions-for-convert-to-order.json?page=2&searchType=facilityCode&searchVal=first').respond(200, {"rnr_list": [requisitions[2]], "number_of_pages": 3});

    scope.$broadcast('$routeUpdate');

    httpBackend.flush();

    expect(scope.selectedItems.length).toEqual(0);
    expect(scope.currentPage).toEqual(2);
    expect(scope.query).toEqual('first');
    expect(scope.filteredRequisitions).toEqual([requisitions[2]]);
    expect(scope.numberOfPages).toEqual(3);
  });

  it('should change page to 1 if server responds with page not found', function () {
    routeParams.page = 2;
    routeParams.searchType = 'facilityCode';
    routeParams.searchVal = 'first';
    httpBackend.expect('GET', '/requisitions-for-convert-to-order.json?page=2&searchType=facilityCode&searchVal=first').respond(404);

    scope.$broadcast('$routeUpdate');

    httpBackend.flush();

    expect(location.$$search.page).toEqual(1);
  });

  it('should set page to 1 and search type to all by default on route update', function () {
    httpBackend.expect('GET', '/requisitions-for-convert-to-order.json?page=1&searchType=all').respond(200, {rnr_list: [requisitions[0]]});

    scope.$broadcast('$routeUpdate');

    httpBackend.flush();

    expect(scope.currentPage).toEqual(1);
    expect(scope.selectedSearchOption.value).toEqual('all');
  });

  it('should Filter requisitions against program name', function () {
    scope.query = "first";
    scope.selectedSearchOption = {value: "programName", name: ""};

    scope.updateSearchParams();

    expect(location.$$search.searchType).toEqual("programName");
    expect(location.$$search.searchVal).toEqual('first');
  });

  it('should un-select selected requisitions upon grid data refresh', function () {
    scope.query = "second CO";
    scope.searchField = "facilityCode";
    scope.selectedItems = [requisitions[0]];

    ctrl(ConvertToOrderListController, {$scope: scope, $location: location, $routeParams: routeParams});
    scope.$broadcast('$routeUpdate');

    expect(scope.selectedItems.length).toEqual(0);
  });

  it('should give message if no requisition selected', function () {
    scope.selectedItems = [];
    ctrl(ConvertToOrderListController, {$scope: scope, $location: location, $routeParams: routeParams});

    scope.convertToOrder();

    expect(scope.noRequisitionSelectedMessage).toEqual("msg.select.atleast.one.rnr");
  });

  it('should show confirm modal if requisitions selected to be converted to order', function () {
    scope.selectedItems.push(requisitions[0], requisitions[1]);

    scope.convertToOrder();

    expect(OpenLmisDialog.newDialog).toHaveBeenCalledWith(jasmine.any(Object), jasmine.any(Function), $dialog, messageService);
  });

  function getDialogCallback() {
    scope.selectedItems.push(requisitions[0], requisitions[1]);
    scope.convertToOrder();
    var dialogClickCallback = OpenLmisDialog.newDialog.calls[0].args[1];
    return dialogClickCallback;
  }

  it('should convert selected requisitions to order upon click of ok of dialog', function () {
    httpBackend.expect('POST', '/orders.json', [requisitions[0], requisitions[1]]).respond(200);
    httpBackend.expect('GET', '/requisitions-for-convert-to-order.json?page=1&searchType=all').respond({rnr_list: [requisitions[0]]});

    getDialogCallback()(true);

    httpBackend.flush();
  });

  it('should show success message requisitions converted to order', function () {
    httpBackend.expect('POST', '/orders.json').respond(200);
    httpBackend.expect('GET', '/requisitions-for-convert-to-order.json?page=1&searchType=all').respond({rnr_list: [requisitions[0]]});

    getDialogCallback()(true);

    httpBackend.flush();

    expect(scope.error).toEqual("");
    expect(scope.message).toEqual('msg.rnr.converted.to.order');
  });

  it('should give error message if convert to order fails with conflicting data', function () {
    httpBackend.expect('POST', '/orders.json').respond(409, {error: 'error!!'});
    httpBackend.expect('GET', '/requisitions-for-convert-to-order.json?page=1&searchType=all').respond({rnr_list: [requisitions[0]]});

    getDialogCallback()(true);

    httpBackend.flush();

    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("error!!");
  });

  it('should give error message if convert to order fails for some reason', function () {
    httpBackend.expect('POST', '/orders.json').respond(400);
    httpBackend.expect('GET', '/requisitions-for-convert-to-order.json?page=1&searchType=all').respond({rnr_list: [requisitions[0]]});

    getDialogCallback()(true);

    httpBackend.flush();

    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("msg.error.occurred");
  });

  it('should not make convert to order if cancel clicked', function () {
    httpBackend.flush();

    getDialogCallback()(false);

    httpBackend.verifyNoOutstandingExpectation();
  });
});

