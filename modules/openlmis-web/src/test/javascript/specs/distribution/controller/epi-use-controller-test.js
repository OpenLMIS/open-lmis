/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

describe('EPI Use controller', function () {

  var scope, controller;

  beforeEach(inject(function ($rootScope, $controller) {
    scope = $rootScope.$new();
    controller = $controller(EpiUseRowController, {$scope: scope});
  }));

  it("should compute total of 'stockAtFirstOfMonth' and 'received' fields", function () {
    scope.groupReading = { reading: {stockAtFirstOfMonth: {value: 50}, received: {value: 75} } };

    var total = scope.getTotal();

    expect(total).toEqual(125);
  });

  it("should ignore not recorded 'stockAtFirstOfMonth' or 'received' fields in total calculation", function () {
    scope.groupReading = { reading: {stockAtFirstOfMonth: {value: 50}, received: {notRecorded: true} } };

    var total = scope.getTotal();

    expect(total).toEqual(50);
  });

  it("should return total as zero if reading object is not available", function () {
    scope.groupReading = { };

    var total = scope.getTotal();

    expect(total).toEqual(0);
  });

  it("should return total as zero if group reading object is not available", function () {

    var total = scope.getTotal();

    expect(total).toEqual(0);
  });

  it('should set input class to true if not recorded is set for a field', function() {
    scope.clearError(true);

    expect(scope.inputClass).toBeTruthy();
  });

  it('should set input class to warning class if not recorded is not set for a field', function() {
    scope.clearError(false);

    expect(scope.inputClass).toEqual('warning-error');
  });

});