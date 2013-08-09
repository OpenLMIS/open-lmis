/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("LocaleController", function () {


  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(module('ngCookies'));

  var controller, scope, $httpBackend, $cookies;

  beforeEach(inject(function (_$rootScope_, _$httpBackend_, $controller, _messageService_, _localStorageService_, _$cookies_) {
    scope = _$rootScope_.$new();
    $httpBackend = _$httpBackend_;
    $cookies = _$cookies_;
    var messageService = _messageService_;

    var messagesReturned = {"messages": {"key": "message"}};

    $httpBackend.expectGET('/locales.json').respond(200, {locales: {pt: "portuguese", en: "English"}});
    $httpBackend.when('GET', '/messages.json').respond(messagesReturned);

    controller = $controller(LocaleController, {$scope: scope, $rootScope: _$rootScope_, messageService: messageService,
      localStorageService: _localStorageService_, $cookies: _$cookies_});
  }));

  it("Should change the locale and clear local storage", function () {
    $httpBackend.expectPUT('/changeLocale.json?locale=pt').respond(200);
    $httpBackend.expectGET('/messages.json');
    scope.changeLocale('pt');
    $httpBackend.flush();
  });

});
