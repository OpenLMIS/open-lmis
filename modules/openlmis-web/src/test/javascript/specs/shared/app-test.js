/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
describe("Message Directive", function () {
  var elm, scope;
  var messageService;
  var messageKey = "error.number.only";
  var compile;

  beforeEach(module('openlmis'));
  describe("Non Input", function () {
    beforeEach(inject(function ($compile, $rootScope, _messageService_) {
      messageService = _messageService_;
      elm = angular.element('<div openlmis-message="error" value="Original Text"> </div>');

      scope = $rootScope.$new();
      compile = $compile;
    }));

    it('should get message from message service if found in scope and message service', function () {
      spyOn(messageService, 'get').andReturn("actual message");
      scope.error = messageKey;

      compile(elm)(scope);
      scope.$digest();

      expect(elm.text()).toEqual("actual message");
      expect(elm.attr("value")).toEqual("Original Text");
    });

    it('should use scope value as message if not found in message service', function () {
      spyOn(messageService, 'get').andReturn(null);
      scope.error = messageKey;

      compile(elm)(scope);
      scope.$digest();

      expect(elm.text()).toEqual("error.number.only");
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

    var routeParams, httpBackend, scope;
    beforeEach(module('openlmis'));

    beforeEach(inject(function ($compile, $rootScope, $routeParams, $httpBackend) {
      scope = $rootScope.$new();
      httpBackend = $httpBackend;
      routeParams = $routeParams;
      elm = angular.element('<comment-box updatable="true" show="showCommentBox"></comment-box>');
      compile = $compile;
    }));

    it("should get comments for a Rnr", function () {
      routeParams.rnrId = 1;
      httpBackend.expect('GET', '/public/pages/template/comment-box.html').respond(200, "<div></div>");
      httpBackend.expect('GET', '/requisitions/comments.json').respond(200, {"comments": [
        {"id": 1}
      ]});

      compile(elm)(scope);
      httpBackend.flush();
      scope.$digest();
    })
  })

});