/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


app.directive('vaccineZoneFilter', ['TreeGeographicZoneList', 'TreeGeographicZoneListByProgram', 'GetUserUnassignedSupervisoryNode', 'messageService', 'VaccineSupervisedIvdPrograms',
    function (TreeGeographicZoneList, TreeGeographicZoneListByProgram, GetUserUnassignedSupervisoryNode, messageService, VaccineSupervisedIvdPrograms) {


        var onCascadedVarsChanged = function ($scope, attr) {

            VaccineSupervisedIvdPrograms.get({},function (data) {

                $scope.filter.program = data.programs[0].id;
                TreeGeographicZoneListByProgram.get({
                    program: $scope.filter.program
                }, function (data) {
                    $scope.zones = data.zone;


                });

            });


        };

        var categoriseZoneBySupervisoryNode = function ($scope) {



            GetUserUnassignedSupervisoryNode.get({
                program: $scope.filter.program
            }, function (data) {
                $scope.user_geo_level = messageService.get('vaccine.report.filter.all.geographic.zones');
                if (!angular.isUndefined(data.supervisory_nodes)) {
                    if (data.supervisory_nodes === 0)
                        $scope.user_geo_level = messageService.get('label.vaccine.geographic.zone');
                }
            });
        };

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {
                scope.registerRequired('zone', attr);

                if (attr.districtOnly) {
                    scope.showDistrictOnly = true;
                }
                categoriseZoneBySupervisoryNode(scope);

                var onParamsChanged = function () {

                    onCascadedVarsChanged(scope, attr);
                };

                onParamsChanged();
            },
            templateUrl: 'filter2-zone-template'
        };
    }
]);
app.directive('vaccineDropoutFilter', ['DropoutProducts', 'messageService',
    function ( DropoutProducts, messageService) {





        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {
                scope.registerRequired('zone', attr);
                DropoutProducts.get({},function(data){
                    scope.dropoutProductsList = data.dropoutProductsList;
                });

            },
            templateUrl: 'filter2-vaccine-dropout-template'
        };
    }
]);


app.directive('vaccinePeriodRangeFilter', [ 'messageService',
    function (  messageService) {

        var onPeriodChange = function ($scope) {
            if ($scope.filter.periodType != 5) {
                var currentDate = new Date();
                var endDate;
                var startDate;
                var months = 0;
                var monthBack = 0;
                var currentDays = currentDate.getDate();
                if (currentDays <= maxReportSubmission) {
                    monthBack = 1;
                }
                endDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - monthBack, 0);
                startDate = new Date(endDate.getFullYear(), endDate.getMonth() + 1, 1);

                //endDate.set
                switch ($scope.filter.periodType) {
                    case '1':
                        months = startDate.getMonth() - 1;
                        break;
                    case '2':
                        months = startDate.getMonth() - 3;

                        break;
                    case '3':
                        months = startDate.getMonth() - 6;
                        break;
                    case '4':
                        months = startDate.getMonth() - 12;
                        break;
                    default :
                        months = 0;
                }
                startDate.setMonth(months);
                $scope.filter.periodStart = $.datepicker.formatDate("yy-mm-dd", startDate);
                $scope.filter.periodEnd = $.datepicker.formatDate("yy-mm-dd", endDate);

            }
        };



        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {

onPeriodChange(scope);
            },
            templateUrl: 'filter2-vaccine-period-template'
        };
    }
]);

