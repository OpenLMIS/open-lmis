/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function RequisitionGroupProgramScheduleListController($scope, $location, navigateBackService, RequisitionGroupCompleteList, ProgramCompleteList,ScheduleCompleteList, LoadSchedulesForRequisitionGroupProgram, SaveRequisitionGroupProgramSchedule) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.showRequisitionGroupsList('txtFilterRequisitionGroups');
    });
    $scope.previousQuery = '';
    $scope.isDataChanged = false;
    $scope.originalSchedule = null;
    $scope.selectedSchedule = null;

    $scope.showRequisitionGroupsList = function (id) {

        RequisitionGroupCompleteList.get(function (data) {
            $scope.filteredRequisitionGroups = data.requisitionGroups;
            $scope.requisitionGroupsList = $scope.filteredRequisitionGroups;
        });

        var query = document.getElementById(id).value;
        $scope.query = query;

        filterRequisitionGroupsByName(query);
        return true;
    };

    ProgramCompleteList.get(function(data){
        $scope.programs = data.programs;
    });

    ScheduleCompleteList.get(function(data){
        $scope.schedules = data.schedules;
    });

    $scope.editRequisitionGroup = function (id) {
        var data = {query: $scope.query};
        navigateBackService.setData(data);
        $location.path('edit/' + id);
    };


    $scope.clearSearch = function () {
        $scope.query = "";
        $scope.resultCount = 0;
        angular.element("#txtFilterRequisitionGroups").focus();
    };

    var filterRequisitionGroupsByName = function (query) {
        query = query || "";

        if (query.length === 0) {
            $scope.filteredRequisitionGroups = $scope.requisitionGroupsList;
        }
        else {
            $scope.filteredRequisitionGroups = [];
            angular.forEach($scope.requisitionGroupsList, function (reqGroup) {

                if (reqGroup.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                    $scope.filteredRequisitionGroups.push(reqGroup);
                }
            });
            $scope.resultCount = $scope.filteredRequisitionGroups.length;
        }
    };

    $scope.filterRequisitionGroups = function (id) {
        var query = document.getElementById(id).value;
        $scope.query = query;
        filterRequisitionGroupsByName(query);
    };

    $scope.getReqColor = function(reqGroup){
        if(!$scope.selectedRequisitionGroup){
            return 'none';
        }

        if($scope.selectedRequisitionGroup.code == reqGroup.code){
            return "background-color : teal; color: white";
        }
        else{
            return 'none';
        }

    };

    $scope.setSelectedReqGroup = function (reqGroup){
        $scope.selectedRequisitionGroup = reqGroup;
        $scope.selectedSchedule = null;
    };

    $scope.getProgramColor = function(program){
        if(!$scope.selectedProgram){
            return 'none';
        }

        if($scope.selectedProgram.code == program.code){
            return "background-color : teal; color: white";
        }
        else{
            return 'none';
        }

    };


    $scope.getScheduleColor = function(schedule){
        if(!$scope.selectedSchedule){
            return 'none';
        }

        if($scope.selectedSchedule.code == schedule.code){
            return "background-color : teal; color: white";
        }
        else{
            return 'none';
        }

    };

    $scope.setSelectedProgram = function (program){
        $scope.selectedProgram = program;
    };

    $scope.$watchCollection('[selectedRequisitionGroup, selectedProgram]', function(){
        $scope.loadRequisitionGroupProgramSchedule();
    });

    $scope.loadRequisitionGroupProgramSchedule = function (){
        if(!$scope.selectedRequisitionGroup || !$scope.selectedProgram){
            return;
        }

        LoadSchedulesForRequisitionGroupProgram.get({rgId: $scope.selectedRequisitionGroup.id, pgId: $scope.selectedProgram.id},function(data){
            $scope.selectedRequisitionGroupProgramSchedule = data.requisitionGroupProgramSchedule;

            if($scope.selectedRequisitionGroupProgramSchedule === null){
                $scope.message = "No schedule configured for " + $scope.selectedRequisitionGroup.name + " in program: " + $scope.selectedProgram.name;
                $scope.showMessage = true;
                $scope.setOriginallySelectedSchedule( null );
            }
            else{
                $scope.message="";
                $scope.showMessage = false;

                $scope.setOriginallySelectedSchedule( $scope.selectedRequisitionGroupProgramSchedule.processingSchedule );
            }
        },{});
    };

    $scope.setOriginallySelectedSchedule = function ( schedule ){
        if(schedule === null){
            $scope.selectedSchedule = null;
        }
        else {
            angular.forEach($scope.schedules, function ( scheduleEntry ) {
                if (scheduleEntry.id == schedule.id) {
                    $scope.selectedSchedule = scheduleEntry;
                }
            });
        }
    };

    $scope.setSelectedSchedule = function(schedule){
        $scope.selectedSchedule = schedule;
    };

    $scope.saveRequisitionGroupProgramSchedule = function(){
        var successHandler = function (response) {
            $scope.requisitionGroupProgramSchedule = response.requisitionGroupProgramSchedule;
            $scope.showError = false;
            $scope.error = "";
            $scope.message = response.success;
            $scope.showMessage = true;
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = response.data.error;
        };

        if(!$scope.selectedRequisitionGroupProgramSchedule){
            $scope.selectedRequisitionGroupProgramSchedule = {};
            $scope.selectedRequisitionGroupProgramSchedule.directDelivery = true;
        }

        $scope.selectedRequisitionGroupProgramSchedule.requisitionGroup = $scope.selectedRequisitionGroup;
        $scope.selectedRequisitionGroupProgramSchedule.program = $scope.selectedProgram;
        $scope.selectedRequisitionGroupProgramSchedule.processingSchedule = $scope.selectedSchedule;

        SaveRequisitionGroupProgramSchedule.save($scope.selectedRequisitionGroupProgramSchedule,successHandler,errorHandler);
    };

}