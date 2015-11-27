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



app.directive('vaccineZoneFilter', ['TreeGeographicZoneList', 'TreeGeographicZoneListByProgram', 'GetUserUnassignedSupervisoryNode', 'messageService','VaccineSupervisedIvdPrograms' ,
    function(TreeGeographicZoneList, TreeGeographicZoneListByProgram, GetUserUnassignedSupervisoryNode, messageService,VaccineSupervisedIvdPrograms) {


        var onCascadedVarsChanged = function($scope, attr) {
            VaccineSupervisedIvdPrograms.get(function (data) {
                $scope.filter.program = data.programs[0].id;

            });
                TreeGeographicZoneListByProgram.get({
                    program: $scope.filter.program
                }, function(data) {
                    $scope.zones = data.zone;

                });
        };

        var categoriseZoneBySupervisoryNode = function($scope) {
            GetUserUnassignedSupervisoryNode.get({
                program: $scope.filter.program
            }, function(data) {
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
            link: function(scope, elm, attr) {
                scope.registerRequired('zone', attr);

                if (attr.districtOnly) {
                    scope.showDistrictOnly = true;
                }
                categoriseZoneBySupervisoryNode(scope);

                var onParamsChanged = function() {
                    if (!scope.showDistrictOnly) {
                        categoriseZoneBySupervisoryNode(scope);
                    }
                    onCascadedVarsChanged(scope, attr);
                };
            },
            templateUrl: 'filter2-zone-template'
        };
    }
]);