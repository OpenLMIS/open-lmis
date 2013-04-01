/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("Message Directive", function () {

  beforeEach(module('openlmis'));

  var elm, scope;
  var messageService;
  var messageKey = "rnr.number.error";
  var compile;

  beforeEach(inject(function ($compile, $rootScope, _messageService_) {
    messageService = _messageService_;
    elm = angular.element('<div openlmis-message="error"></div>');

    scope = $rootScope.$new();
    compile = $compile;
  }));

  it('should get message from message service if found in scope and message service', function () {
    spyOn(messageService, 'get').andReturn("actual message");
    scope.error = messageKey;

    compile(elm)(scope);
    scope.$digest();

    expect(elm.text()).toEqual("actual message");
  });

  it('should use scope value as message if not found in message service', function () {
    spyOn(messageService, 'get').andReturn(null);
    scope.error = messageKey;

    compile(elm)(scope);
    scope.$digest();

    expect(elm.text()).toEqual("rnr.number.error");
  });

  it('should use message service if key is not in scope', function () {
    spyOn(messageService, 'get').andReturn("actual message");
    scope.error = null;

    compile(elm)(scope);
    scope.$digest();

    expect(elm.text()).toEqual("actual message");
    expect(messageService.get).toHaveBeenCalledWith('error');

  });

});