/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function ColdChainEquipmentReportController($scope, $log, ColdChainEquipmentService, ngTableParams, $filter, $http, $routeParams, $location) {

    $scope.log = $log;

    //Add this temporarily. It should really be set by filters on the page
    $scope.filter = {};
    $scope.filter.max = 10000;

    $scope.exportReport = function (type)
    {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.filter);

        var sortOrderParams = jQuery.param($scope.tableParams.sorting);
        sortOrderParams = sortOrderParams.split('=');
        sortOrderParams = { sortBy:sortOrderParams[0], order:sortOrderParams[1] };
        sortOrderParams = jQuery.param(sortOrderParams);

        var url = '/reports/download/cold_chain_equipment/' + type + '?' + sortOrderParams +'&'+ params;
        window.open(url);
    };

    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10           // count per page
    });


    $scope.getRowFacilityAddress = function(row)
    {
        var ret = '';
        if (row.facilityAddress1)
            ret = row.facilityAddress1;
        if (row.facilityAddress2)
            ret += ' ' +  row.facilityAddress2;
        return ret;
    };


    ColdChainEquipmentService.get
    (
        {
            page: $scope.page,
            max: 900000,
            filter: $scope.filter
        } ,

        function (data)
        {
            $scope.data = $scope.datarows = data.pages.rows;
            $scope.pages = data.pages;
            $scope.tableParams.total = $scope.pages.total;
        }
    );

    $scope.getLargestRecordShown = function()
    {
        var max = $scope.tableParams.page * $scope.tableParams.count;
        if($scope.pages)
            return ($scope.pages.total > max) ? max : $scope.pages.total;
        else
            return max;
    };
}
