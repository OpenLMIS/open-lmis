/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

app.directive('valerts', ['messageService',function(messageService) {
    var loadData = function($scope, data)
    {
            $scope.dataRows={alert:[{type:"stock",description:"BCG is running below buffer stock"},
                                        {type:"cce",description:"Arusha DC changed status to non-functional"}]};

    };
    return{
            restrict: 'EA',
            templateUrl: '/public/pages/vaccine/inventory/partials/alert-template.html',
            link: function(scope, elem, attrs){
            var data = scope[attrs.ngModel];
            //scope.$watch('data', function(d){
                loadData(scope, data);
            //});
            }
        };
}]);
