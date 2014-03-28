
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function AlertsController($scope, Alerts, ngTableParams) {

    $scope.$watch('formFilter.supervisoryNodeId', function(){
        if(isUndefined($scope.formFilter.supervisoryNodeId) || _.isEmpty($scope.formFilter.supervisoryNodeId)){
            resetAlertsData();
            return;
        }

        Alerts.get({supervisoryNodeId: $scope.formFilter.supervisoryNodeId},function(data){
                if(!isUndefined(data.alerts)){
                    $scope.alertData = _.filter(data.alerts,function(alertData){if(alertData.category == 'ALERT'){return alertData;}});
                    $scope.stockOutData = _.filter(data.alerts,function(alertData){if(alertData.category == 'STOCkOUT'){return alertData;}});
                }

        });


    });
    var resetAlertsData = function(){
      $scope.alertData = $scope.stockOutData = null;
    };

    $scope.alertTableParams = new ngTableParams({
        page: 1,            // show first page
        total:0,
        count: 5,
        counts:[]
    });
    $scope.stockOutsTableParams = new ngTableParams({
        page: 1,            // show first page
        total:0,
        count: 5,
        counts:[]
    });
/*
    $scope.loadData =  function(params){
        if(params === undefined || params === null){
            params = new ngTableParams();
        }
        if($scope.alertData === undefined){
            $scope.datarows = [];
            params.total = 0;
        }else{
            var data = $scope.alertData;
            params.total = data.length;

                $scope.datarows = data.slice((params.page - 1) * params.count, params.page * params.count);

        }
    };*/

  /*  $scope.$watch('tableParams', function(selection){
        $scope.loadData($scope.tableParams);
    },true);*/
 /*   $scope.tableParam2 = new ngTableParams({
        page: 1,            // show first page
        total:0,
        count: 5
    });*/
   /* $scope.tableParams2 = new ngTableParams({
        page: 1,            // show first page
        total:0,
        count: 5 ,
        counts:[]
    });*/

   /* $scope.datarows2 = $scope.alertData.slice(($scope.tableParams2.page- 1) * $scope.tableParams2.count, $scope.tableParams2.page * $scope.tableParams2.count);

    $scope.loadMoreData =  function(params){
        if(params === undefined || params === null){
            params = new ngTableParams();
        }
        if($scope.alertData === undefined){
            $scope.datarows2 = [];
            params.total = 0;
        }else{
            var data = $scope.alertData;
            total = data.length;
            //params.total = data.length;
             params.counts = [];
             if((params.count * (params.page + 1)) < total){

             params.page = params.page ? params.page + 1 : 1;

            $scope.datarows2 = data.slice((1 - 1) * params.count, params.page * params.count);

             }
        }
    };*/
}

