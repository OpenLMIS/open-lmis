/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("LocaleController", function () {


  beforeEach(module('openlmis'));

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
