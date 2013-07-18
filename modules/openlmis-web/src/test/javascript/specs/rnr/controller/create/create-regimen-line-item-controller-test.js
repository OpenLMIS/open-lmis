/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('CreateRegimenLineItemController', function () {
  var scope;

  beforeEach(inject(function ($rootScope, $controller) {
    scope = $rootScope.$new();
    scope.rnr = {regimenLineItems: [
      {category: {name: "cName"}},
      {category: {name: "cName"}}
    ]};
    scope.regimenColumns = [
      {name: 'name', visible: true},
      {name: 'name1', visible: false}
    ];
    $controller(CreateRegimenLineItemController, {$scope: scope});
  }));

  it('should return false if category are same', function () {
    expect(scope.showCategory(1)).toBeFalsy();
  });

  it('should return true if category are different', function () {
    scope.rnr.regimenLineItems[0].category.name = 'bName';
    expect(scope.showCategory(1)).toBeTruthy();
  });
});