/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("LocaleController", function () {

  it("Should change the locale and clear local storage", function () {
    module('openlmis.services');
    module('openlmis.localStorage');
    module('ui.directives');
    var controller, scope, $httpBackend;

    inject(function ($rootScope, _$httpBackend_, $controller, _messageService_) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      var messageService = _messageService_;

      var messagesReturned = {"messages": {"key": "message"}};

      $httpBackend.expectGET('/locales').respond(200, {locales: {pt: "portuguese", en: "English"}});
      $httpBackend.when('GET', '/messages.json').respond(messagesReturned);

      controller = $controller(LocaleController, {$scope: scope, messageService: messageService});
    });

    $httpBackend.expectPUT('/changeLocale.json?locale=11pt').respond(200);
    $httpBackend.expectGET('/messages1.json');
    scope.changeLocale('pt');
  });

});
