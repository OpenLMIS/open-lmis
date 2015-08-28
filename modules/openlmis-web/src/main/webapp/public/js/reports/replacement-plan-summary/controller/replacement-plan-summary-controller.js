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
function ReplacementPlanSummary($scope, ngTableParams, messageService, SettingsByKey, EquipmentsInNeedForReplacement, ReplacementPlanSummaryReport) {
    $scope.equipmentsForReplacementModal = false;


    $scope.statuses = [];
    Initialize();

    function Initialize() {


        $scope.statuses =
            [
                {
                    id: 0, name: "Highest Priority Replacement", "children": [
                    {id: 0, name: 'O', value: 'All Obsolete'},
                    {id: 1, name: 'W', value: 'Capacity Gap'}
                ]

                },
                {
                    id: 1, name: "Priority Replacement", "children": [
                    {id: 0, name: 'temp', value: '> 20 Temp  Excursion'},
                    {id: 2, name: 'breakDown', value: ' >5 Breakdowns'},
                    {id: 3, name: 'g', value: 'Source is Gas'}
                ]

                },
                {
                    id: 0, name: "Eventual Replacement", "children": [
                    {id: 0, name: 'a', value: 'All Under Ten Years'},
                    {id: 1, name: 'n', value: 'Non PQS Model'}
                ]

                }

            ];
        $scope.statuses.unshift({'name': '-- Select Indicator --', 'value': -1});

    }

    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.filter);
        var url = '/reports/download/replacement_plan_summary' + (($scope.filter.disaggregated === true) ? '_annual' : '') + '/' + type + '?' + params;
        window.open(url);
    };

    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 10           // count per page
    });


    $scope.OnFilterChanged = function () {

        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;

        ReplacementPlanSummaryReport.get($scope.filter, function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {

                $scope.data = $scope.datarows = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);

            }
        });

    };


    $scope.getEquipmentList = function (feature, element, years) {
        $scope.facility = [];
        $scope.facilityParam = feature;
        $scope.plannedYear = years;
        var fullReportFilter = angular.extend($scope.filter, {
            facility: $scope.facilityParam,
            plannedYear: $scope.plannedYear
        });

        EquipmentsInNeedForReplacement.get(fullReportFilter,

            function (data) {
                if (data.pages !== undefined && data.pages.rows !== undefined) {
                    $scope.facility = data.pages.rows;

                    var label = 'report.title.equipment.in.need.of.replacement';

                    $scope.title = messageService.get(label) + '  In  ' + years + '  For  ' + element.facilityName + '  ' + element.facilityTypeName;

                    $scope.equipmentsForReplacementModal = true;
                }

            });

        $scope.exportEquipmentNeedForReplacement = function (type) {
            var url = '/reports/download/equipment_replacement_list/' + type + '?' + $.param(fullReportFilter);
            window.open(url);
        };

    };

    $scope.years = [];

    SettingsByKey.get({key: messageService.get('YEAR_OF_EQUIPMENT_REPLACEMENT')}, function (data) {
        var value = data.settings.value;
        for (var i = 0; i < value; i++)

            $scope.years.push(i + new Date().getFullYear());
        return $scope.years;
    });

    $scope.first_year = [];
    $scope.first_year = (new Date().getFullYear());
}
