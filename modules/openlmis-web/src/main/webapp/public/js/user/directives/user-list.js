/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

userModule.directive('userList', function ($timeout) {
  return {
    restrict: 'C',
    link: function (scope, element, attr) {

      scope.$watch(attr.ngModel, function ngModelWatchAction() {
        $('.user-actions a').css('display', 'none');
        var showButtonsOnLiHover = function(){
          var listAnchorElements = $(".user-list a");
          listAnchorElements.live("focus", function () {
            $(".user-actions a").hide();
            $(this).parents("li").find(".user-actions a").css("display", "inline-block");
          });
        };
        $timeout(showButtonsOnLiHover);
      });
    }
  };
});
