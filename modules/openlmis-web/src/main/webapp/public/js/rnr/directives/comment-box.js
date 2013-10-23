/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

//  Description:
//  Comment box behavior on the R&R screen

app.directive('commentBox',function (RequisitionComment, $routeParams) {
  return {
    restrict:'E',
    scope:{
      show:'=',
      updatable:'='
    },
    link:function (scope) {

      var commentContainer = document.getElementById('comments-list');

      RequisitionComment.get({id:$routeParams.rnr}, function (data) {
        scope.rnrComments = data.comments;
      }, {});

      var commentEscapeKeyHandler  = function(e) {
        if (e.which == 27) {
          scope.show = false;
          scope.comment = "";
          scope.$apply();
        }
      };

      scope.$watch("show", function () {
        if (scope.show) {
          angular.element(document).bind("keyup", commentEscapeKeyHandler);
        } else {
          angular.element(document).unbind("keyup", commentEscapeKeyHandler);
        }
      });

      scope.$watch("comment", function () {
        if (scope.comment === undefined) return;
        scope.comment = scope.comment.substring(0, 250);
      });

      scope.addComments = function () {
        if (isUndefined(scope.comment)) return;
        var comment = {"commentText":scope.comment };

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

        RequisitionComment.save({id:$routeParams.rnr}, comment, successHandler, errorHandler);

      };
    },
    templateUrl:'/public/pages/template/comment-box.html',
    replace:true
  };
});