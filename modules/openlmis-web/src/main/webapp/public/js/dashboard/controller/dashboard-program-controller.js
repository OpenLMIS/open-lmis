/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function DashboardProgramController($scope,$routeParams,$timeout, dashboardMenuServiceNew, UserSupervisedActivePrograms, GetLastPeriods) {
    var dashboardMenuService = dashboardMenuServiceNew;

    $scope.dashboardTabs = dashboardMenuService.tabs;
    $scope.dashboardTabs = [];
    UserSupervisedActivePrograms.get(function(data){
        $scope.programsList = data.programs;
        angular.forEach(data.programs, function(program){

            dashboardMenuService.addTab("'"+program.name+"'",'/public/pages/dashboard/index_new.html#/dashboard-new?programId='+program.id,program.name,false, program.id);
        });
        dashboardMenuService.addTab('Facility','/public/pages/dashboard/index_new.html#/dashboard-new?facilityId=0','Facility',false, 'Facility');
        dashboardMenuService.addTab('Notification','/public/pages/dashboard/index_new.html#/dashboard-new?notificationId=0','Notification',false, 'Notification');
        $scope.dashboardTabs = dashboardMenuService.tabs;

            if(!isUndefined($routeParams.programId)){
                $scope.currentTab = $scope.programId = $routeParams.programId;
            }else if(!isUndefined($routeParams.facilityId)){

                $scope.currentTab = 'Facility';
            }else if(!isUndefined($routeParams.notificationId)){

                $scope.currentTab = 'Notification';
            }else{

                $scope.currentTab = $scope.programId =  dashboardMenuService.getTab(0).id;
            }

    });


$timeout(function(){

    GetLastPeriods.get({}, function(data){
        $scope.lastPeriods = data.lastPeriods;
        dashboardMenuService.tabs = [];
        angular.forEach( $scope.lastPeriods, function(period){

            dashboardMenuService.addTab("'"+period.name+"'",'/public/pages/dashboard/index_new.html#/dashboard-new?programId='+$scope.currentTab +'&periodId='+period.id,period.name,false, period.id);
        });
        $scope.dashboardPeriodTabs = dashboardMenuService.tabs;
            if(!isUndefined($routeParams.periodId)){
                $scope.currentSubTab = $scope.periodId = $routeParams.periodId;
            }else{
                $scope.currentSubTab = $scope.periodId =  $scope.dashboardPeriodTabs[0].id;
            }

        dashboardMenuService.tabs = [];
    });

},100);


    if(!isUndefined($scope.programId) && !isUndefined($scope.periodId)){

    }


}
