
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function AlertsController($scope, ngTableParams) {


    $scope.alertData   = [
        {alert: "Requisitions Pending Approval", percent: 10},
        {alert: "Facilities stocked out", percent: 20},
        {alert: "Commodities have been rationed", percent: 30},
        {alert: "products have been recalled", percent: 46},
        {alert: "Requisitions Pending Approval", percent: 17},
        {alert: "Facilities stocked out", percent: 25},
        {alert: "Commodities have been rationed", percent: 36},
        {alert: "products have been recalled", percent: 42},
        {alert: "Requisitions Pending Approval", percent: 70},
        {alert: "Facilities stocked out", percent: 27},
        {alert: "Commodities have been rationed", percent: 33},
        {alert: "products have been recalled", percent: 46},
        {alert: "Requisitions Pending Approval", percent: 18},
        {alert: "Facilities stocked out", percent: 20},
        {alert: "Commodities have been rationed", percent: 50},
        {alert: "products have been recalled", percent: 76},
        {alert: "Requisitions Pending Approval", percent: 10},
        {alert: "Facilities stocked out", percent: 21},
        {alert: "Commodities have been rationed", percent: 32},
        {alert: "products have been recalled", percent: 44},
        {alert: "Requisitions Pending Approval", percent: 15},
        {alert: "Facilities stocked out", percent: 29},
        {alert: "Commodities have been rationed", percent: 31},
        {alert: "products have been recalled", percent: 67},
        {alert: "Requisitions Pending Approval", percent: 45},
        {alert: "Facilities stocked out", percent: 55},
        {alert: "Commodities have been rationed", percent: 88},
        {alert: "products have been recalled", percent: 99}];

    $scope.totalAlerts = $scope.alertData.length;
    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total:0,
        count: 5,
        counts:[]            // count per page
    });

    $scope.datarows = $scope.alertData.slice(($scope.tableParams.page - 1) * $scope.tableParams.count, $scope.tableParams.page * $scope.tableParams.count);

    $scope.loadData =  function(params){

        if(params === undefined || params === null){
            params = new ngTableParams();
        }
        if($scope.data === undefined){
            $scope.datarows = [];
            params.total = 0;
        }else{
            var data = $scope.alertData;
            var total = data.length;

            params.counts = [];
            if((params.count * (params.page + 1)) < total){

                params.page = params.page ? params.page + 1 : 1;

                $scope.datarows = data.slice((1 - 1) * params.count, params.page * params.count);
            }
        }
    };

}

