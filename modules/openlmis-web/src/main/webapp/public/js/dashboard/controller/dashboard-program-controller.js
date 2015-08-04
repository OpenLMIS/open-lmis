
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function DashboardProgramController($scope,$routeParams,$timeout, dashboardMenuService, UserSupervisedActivePrograms, GetLastPeriods) {

    $scope.dashboardTabs = dashboardMenuService.tabs;
    $scope.dashboardTabs = [];
    UserSupervisedActivePrograms.get(function(data){
        $scope.programsList = data.programs;
        angular.forEach(data.programs, function(program){

            dashboardMenuService.addTab("'"+program.name+"'",'/public/pages/dashboard/index.html#/dashboard?programId='+program.id,program.name,false, program.id);
        });
        $scope.dashboardTabs = dashboardMenuService.tabs;

            if(!isUndefined($routeParams.programId)){
                $scope.currentTab = $scope.programId = $routeParams.programId;
            }else{
                $scope.currentTab = $scope.programId =  dashboardMenuService.getTab(0).id;
            }

    });


$timeout(function(){

    GetLastPeriods.get({}, function(data){
        $scope.lastPeriods = data.lastPeriods;
        dashboardMenuService.tabs = [];
        angular.forEach( $scope.lastPeriods, function(period){

            dashboardMenuService.addTab("'"+period.name+"'",'/public/pages/dashboard/index.html#/dashboard?programId='+$scope.currentTab +'&periodId='+period.id,period.name,false, period.id);
        });
        $scope.dashboardPeriodTabs = dashboardMenuService.tabs;
            if(!isUndefined($routeParams.periodId)){
                $scope.currentSubTab = $scope.periodId = $routeParams.periodId;
            }else{
                $scope.currentSubTab = $scope.periodId =  $scope.dashboardPeriodTabs[0].id;
            }

        dashboardMenuService.tabs = [];
    });

},10);


    if(!isUndefined($scope.programId) && !isUndefined($scope.periodId)){

    }


}
