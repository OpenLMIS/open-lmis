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
//  Hiding the navigation items based on the rights on the children nodes

app.directive('uiNav', function () {
  return {
    restrict: 'A',

    link: function (scope, element, attrs) {
      //Identify all the menu lists
      var lists = $(".navigation ul");

      //Sort the lists based their nesting, innermost to outermost
      lists.sort(function (a, b) {
        return $(b).parents("ul").length - $(a).parents("ul").length;
      });

      setTimeout(function () {

        lists.each(function () {
          var display = false;

          //Check if all the child items are hidden
          $(this).children("li:not(.beak)").each(function () {
            if ($(this).css('display') != 'none') {
              display = true;
              return false;
            }
          });
          //Hide the list and its containing li in case all the children are hidden
          if (!display) {
            $(this).parent().hide();
            $(this).parent().parent().hide();
          }
        });

        $(".navigation li > a").on("click", function () {
          $(this).next(".submenu").show();
        });

        //Removing border-top from first visible list item in submenu
        $('.navigation .submenu ul').each(function (index, submenu) {
          var firstVisibleListItem = $(submenu).children('li:not(.ng-hide):not(.beak)').first();
            firstVisibleListItem.addClass('border-top-none');
        });
      });
    }
  };
});
