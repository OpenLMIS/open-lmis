/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function RegionsController($scope, ngTableParams, $rootScope, PublicSiteData) {

    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1, // show first page
        total: 0, // length of data
        count: 3 // count per page
    });

   /*$scope.data =  $scope.datarows =
        [
            {zones: 'Lomia', regions: 'Andromeda', districtCount: 8, facilityCount: 45 },
            {zones: 'Nugorongoro', regions: 'Andromeda', districtCount: 8, facilityCount: 45 },
            {zones: 'Lomia', regions: 'Andromeda', districtCount: 8, facilityCount: 45 },
            {zones: 'Nugorongoro', regions: 'Andromeda', districtCount: 8, facilityCount: 45 }
        ];*/

    PublicSiteData.regions().get({}, function(data){
        $scope.data =  $scope.datarows = data.regions;
    });

}