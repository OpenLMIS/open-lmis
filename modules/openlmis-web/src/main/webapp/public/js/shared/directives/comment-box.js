/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

directives.directive('commentBox', function (RequisitionComment, $routeParams) {
  return {
    restrict: 'E',
    scope: {
      show: '=',
      updatable: '='
    },
    link: function (scope) {

      var commentContainer = document.getElementById('comments-list');

      RequisitionComment.get({id: $routeParams.rnr}, function (data) {
        scope.rnrComments = data.comments;
      }, {});

      angular.element(document).keyup(function (e) {
        if (e.which == 27) {
          scope.show = false;
          scope.comment = "";
        }
        scope.$apply();
      });

      scope.$watch("comment", function () {
        if (scope.comment == undefined) return;
        scope.comment = scope.comment.substring(0, 250);
      });

      scope.addComments = function () {
        if (isUndefined(scope.comment)) return;
        var comment = {"commentText": scope.comment };

        var successHandler = function (data) {
          scope.comment = "";
          scope.rnrComments = data.comments;

          setTimeout(function () {
            commentContainer.scrollTop = commentContainer.scrollHeight;
          }, 0);
        };

        var errorHandler = function (data) {
          scope.error = data.error;
        };

        RequisitionComment.save({id: $routeParams.rnr}, comment, successHandler, errorHandler);

      };
    },
    templateUrl: '/public/pages/template/comment-box.html',
    replace: true
  };
});