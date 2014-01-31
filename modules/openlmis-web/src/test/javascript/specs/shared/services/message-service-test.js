/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("MessageService", function () {

  beforeEach(module('openlmis'));

  var httpBackend, messageService,localStorageService;

  beforeEach(inject(function (_$httpBackend_, _localStorageService_,_messageService_) {
    httpBackend = _$httpBackend_;
    localStorageService = _localStorageService_;
    messageService =  _messageService_;
  }));

  it('should populate messages if not in local storage', function () {
    spyOn(localStorageService, 'get').andReturn('');
    spyOn(localStorageService, 'add');

    var messagesReturned = {"messages":{"key":"message"}};
    httpBackend.when('GET', '/messages.json').respond(messagesReturned);
    messageService.populate();
    httpBackend.flush();
    expect(localStorageService.add).toHaveBeenCalledWith('message.key', "message");
  });

  it("should not get messages if present in local storage", function(){
    spyOn(localStorageService,'get').andReturn("@version@");
    spyOn(localStorageService, 'add');

    messageService.populate();
    expect(localStorageService.add).not.toHaveBeenCalled();
  })

  it("should get message for a key", function(){
    spyOn(localStorageService,'get').andReturn("message");

    var message = messageService.get("key");
    expect(message).toEqual("message");
  });
});