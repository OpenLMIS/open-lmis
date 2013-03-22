/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("MessageService", function () {

  beforeEach(module('openlmis.services'));

  beforeEach(module('openlmis.localStorage'));

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
    spyOn(localStorageService,'get').andReturn({"key":"message"});
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