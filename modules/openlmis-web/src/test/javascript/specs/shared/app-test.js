/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
describe("Message Directive", function () {
  var elm, scope;
  var messageService;
  var messageKey = "rnr.number.error";
  var compile;

  beforeEach(module('openlmis'));
  describe("Non Input", function () {
    beforeEach(inject(function ($compile, $rootScope, _messageService_) {
      messageService = _messageService_;
      elm = angular.element('<div openlmis-message="error" value="Original Text"> </div>');

      scope = $rootScope.$new();
      compile = $compile;
    }));

    xit('should get message from message service if found in scope and message service', function () {
      spyOn(messageService, 'get').andReturn("actual message");
      scope.error = messageKey;

      compile(elm)(scope);
      scope.$digest();

      expect(elm.text()).toEqual("actual message");
      expect(elm.attr("value")).toEqual("Original Text");
    });

    xit('should use scope value as message if not found in message service', function () {
      spyOn(messageService, 'get').andReturn(null);
      scope.error = messageKey;

      compile(elm)(scope);
      scope.$digest();

      expect(elm.text()).toEqual("rnr.number.error");
    });

    xit('should use message service if key is not in scope', function () {
      spyOn(messageService, 'get').andReturn("actual message");
      scope.error = null;

      compile(elm)(scope);
      scope.$digest();

      expect(elm.text()).toEqual("actual message");
      expect(messageService.get).toHaveBeenCalledWith('error');

    });
  });

  describe("Input", function () {
    beforeEach(inject(function ($compile, $rootScope, _messageService_) {
      messageService = _messageService_;
      elm = angular.element('<input openlmis-message="error"></div>');

      scope = $rootScope.$new();
      compile = $compile;
    }));

    xit('should populate value attribute for input elements', function () {
      spyOn(messageService, 'get').andReturn("actual message");
      scope.error = messageKey;

      compile(elm)(scope);
      scope.$digest();

      expect(elm.attr("value")).toEqual("actual message");
    });
  });

  describe("CommentBox", function () {

    var routeParams, httpBackend,scope;
    beforeEach(module('openlmis.services'));

    beforeEach(inject(function ($compile, $rootScope, $routeParams, $httpBackend) {
      scope = $rootScope.$new();
      httpBackend = $httpBackend;
      routeParams = $routeParams;
      elm = angular.element('<comment-box updatable="true" show="showCommentBox"></comment-box>');
      compile = $compile;
    }));

    xit("should get comments for a Rnr", function () {
      routeParams.rnrId = 1;
      httpBackend.expect('GET', '/public/pages/template/comment-box.html').respond(200, "<div></div>");
      httpBackend.expect('GET', '/requisitions/comments.json').respond(200,{"comments":[
        {"id":1}
      ]});

      compile(elm)(scope);
      httpBackend.flush();
      scope.$digest();
    })
  })

});