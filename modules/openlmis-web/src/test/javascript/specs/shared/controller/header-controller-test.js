/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("HeaderController", function () {

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis'));

  beforeEach(module('openlmis.localStorage'));
  beforeEach(module('ui.directives'));

  var scope, loginConfig;

  beforeEach(inject(function ($rootScope, $controller) {
    loginConfig = {a: {}, b: {}};
    scope = $rootScope.$new();
    $controller(HeaderController, {$scope: scope, loginConfig: loginConfig});
  }));

  it('should set login config in scope', function () {
    expect(scope.loginConfig).toEqual(loginConfig);
  })
});