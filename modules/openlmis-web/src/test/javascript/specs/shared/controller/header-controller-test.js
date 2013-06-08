/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("HeaderController", function () {

  beforeEach(module('openlmis.services'));

  beforeEach(module('openlmis.localStorage'));

  var scope, ctrl,httpBackend,messageService;

  beforeEach(inject(function ($rootScope, $controller,_messageService_,_$httpBackend_) {
    httpBackend = _$httpBackend_;
    scope = $rootScope.$new();
    messageService = _messageService_;
    spyOn(messageService, 'populate');
    httpBackend.when('GET','/user-context.json').respond({"id":123, "userName":"User420"});
    ctrl = $controller(HeaderController, {$scope:scope, messageService:messageService});
  }));

});