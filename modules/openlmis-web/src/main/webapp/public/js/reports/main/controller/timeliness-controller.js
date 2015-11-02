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

function TimelinessReportController($scope, messageService, $window, getTimelinessReport, getTimelinessStatusData, getFacilityRnRTimelinessReportData, getTimelinessReportingDates) {

    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.filter);

        var url = '/reports/download/timeliness/' + type + '?' + params;

        if (type == "unscheduled-reporting") {
            url = '/reports/download/unscheduled_reporting/list/' + "pdf" + '?' + params;
        }
        $window.open(url);
    };


    $scope.OnFilterChanged = function () {
        // clear old data if there was any
        $scope.data = $scope.unscheduledData = $scope.datarows = $scope.reportingDates = [];
        $scope.filter.max = 10000;

        getTimelinessReport.get($scope.filter, function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;

                $scope.unscheduledData = _.pluck($scope.data, 'unscheduled');

                $scope.paramsChanged($scope.tableParams);
            }
        });
        if ($scope.filter.period !== undefined) {
            getTimelinessReportingDates.get({periodId: $scope.filter.period}, function (data) {

                $scope.reportingDates = data.reportingDates;

            });

        }
    };

    $scope.zoneIds = null;
    $scope.getOnTimelinessReport = function (feature, element) {
        $scope.zoneIds = feature.zoneId;
        if ($scope.filter.program !== undefined && $scope.filter.period !== undefined && $scope.zoneIds !== undefined) {

            var key = messageService.get('status.timeliness.report.reported.on.time');
            getTimelinessStatusData.get({programId: $scope.filter.program,
                periodId: $scope.filter.period,
                scheduleId: $scope.filter.schedule,
                zoneId: feature.zoneId,
                status: key}, function (data) {
                $scope.facilities = data.timelinessData;
                $scope.successModal = true;
                $scope.district = 'Facilities Reported on Time in  ' + feature.district + '  District';
            });
        }

    };


    $scope.getTimelinessLateReported = function (feature, element) {

        $scope.zoneIds = feature.zoneId;
        var key = messageService.get('status.timeliness.report.reported.late');
        if ($scope.filter.program !== undefined && $scope.filter.period !== undefined && $scope.zoneIds !== undefined) {
            getTimelinessStatusData.get({programId: $scope.filter.program,
                periodId: $scope.filter.period,
                scheduleId: $scope.filter.schedule,
                zoneId: feature.zoneId,
                status: key}, function (data) {
                $scope.facilities = data.timelinessData;
                $scope.successModal = true;
                $scope.district = 'Facilities Reported Late in  ' + feature.district + '  District';
            });
        }

    };
    $scope.reportingStatus = null;

    $scope.rnrStatusSelectChange = function (selected, statusData) {
        $scope.reportingStatus = statusData.reportingStatus;

        loadTimelinessStatusData();
    };


    var loadTimelinessStatusData = function () {
        $scope.facilitySelected = [];
        angular.forEach($scope.facilities, function (itm, idx) {
            if (itm.selected) {
                $scope.facilitySelected.push(itm.facilityId);
            }
        });


        getFacilityRnRTimelinessReportData.get({programId: $scope.filter.program,
            periodId: $scope.filter.period,
            scheduleId: $scope.filter.schedule,
            zoneId: $scope.zoneIds,
            status: $scope.reportingStatus,
            facilityIds: $scope.facilitySelected}, function (data) {
            $scope.statusData = data.timelinessStatusData;

            adjustDataForShow($scope.statusData);

        });


    };
    $scope.resetFacilityRnRStatusData = function () {

        $scope.facilityRnRStatusess = null;

    };

    $scope.facilityRnRStatusess = null;

    var adjustDataForShow = function (statusData) {


        if (isUndefined(statusData) || statusData.length === 0) {
            $scope.resetFacilityRnRStatusData();
        }


        var groupedByRnRId = _.chain(statusData).groupBy('rnrId').map(function (value, key) {
            return {rnrId: key, rnrStatus: _.first(value).status, rnrStatusData: value };
        }).value();

        $scope.facilityRnRStatusess = [];

        angular.forEach(groupedByRnRId, function (statusData) {

            var rnrId = _.first(statusData.rnrStatusData).rnrId;
            var status = _.pluck(statusData.rnrStatusData, 'status');
            var duration = _.pluck(statusData.rnrStatusData, 'duration');
            var facility = _.pluck(statusData.rnrStatusData, 'facilityName');
            var facilityType = _.pluck(statusData.rnrStatusData, 'facilityTypeName');

            var facilityStatusData = [];
            facilityStatusData = [
                {rnrId: rnrId, status: status, duration: duration, facility: facility, facilityType: facilityType}
            ];
            $scope.reportingDate = 'Reporting Dates for ';

            $.each(facilityStatusData, function (item, idx) {
                $scope.facilityRnRStatusess.push(idx);

            });
        });


    };

    $scope.greaterThan = function (prop, val) {

        return function (item) {
            return item[prop] > val;
        };
    };

}
