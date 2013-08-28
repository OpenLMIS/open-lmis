function RequisitionGroupProgramScheduleListController($scope, $location, navigateBackService, RequisitionGroupCompleteList, ProgramCompleteList,ScheduleCompleteList, LoadSchedulesForRequisitionGroupProgram, SaveRequisitionGroupProgramSchedule, $dialog, messageService) {
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

        if (query.length == 0) {
            $scope.filteredRequisitionGroups = $scope.requisitionGroupsList;
        }
        else {
            $scope.filteredRequisitionGroups = [];
            angular.forEach($scope.requisitionGroupsList, function (geographicZone) {

                if (geographicZone.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                    $scope.filteredRequisitionGroups.push(geographicZone);
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
        if($scope.selectedRequisitionGroup== null){
            return 'none';
        }

        if($scope.selectedRequisitionGroup.code == reqGroup.code){
            return 'background-color:#a9a9a9';
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
        if($scope.selectedProgram== null){
            return 'none';
        }

        if($scope.selectedProgram.code == program.code){
            return 'background-color:#a9a9a9';
        }
        else{
            return 'none';
        }

    };


    $scope.getScheduleColor = function(schedule){
        if($scope.selectedSchedule== null){
            return 'none';
        }

        if($scope.selectedSchedule.code == schedule.code){
            return 'background-color:#a9a9a9';
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
        if($scope.selectedRequisitionGroup == null || $scope.selectedProgram == null){
            return;
        }

        LoadSchedulesForRequisitionGroupProgram.get({rgId: $scope.selectedRequisitionGroup.id, pgId: $scope.selectedProgram.id},function(data){
            $scope.selectedRequisitionGroupProgramSchedule = data.requisitionGroupProgramSchedule;

            if($scope.selectedRequisitionGroupProgramSchedule==null){
                $scope.message = "No schedule configured for " + $scope.selectedRequisitionGroup.name + " in program: " + $scope.selectedProgram.name;
                $scope.showMessage = true;
            }
            else{
                $scope.message="";
                $scope.showMessage=false;

                $scope.setOriginallySelectedSchedule($scope.selectedRequisitionGroupProgramSchedule.processingSchedule)
            }
        },{});
    };

    $scope.setOriginallySelectedSchedule = function (schedule){
        angular.forEach($scope.schedules,function(scheduleEntry){
            if(scheduleEntry.id == schedule.id){
                $scope.selectedSchedule = scheduleEntry;
            }
        });
    }

    $scope.setSelectedSchedule = function(schedule){
        //$scope.selectedSchedule = schedule;
        if($scope.selectedSchedule.id != $scope.selectedRequisitionGroupProgramSchedule.processingSchedule.id){
            $scope.isDataChanged = true;
        }
        else{
            $scope.isDataChanged = false;
        }
    }

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

        $scope.selectedRequisitionGroupProgramSchedule.requisitionGroup = $scope.selectedRequisitionGroup;
        $scope.selectedRequisitionGroupProgramSchedule.program = $scope.selectedProgram;
        $scope.selectedRequisitionGroupProgramSchedule.processingSchedule = $scope.selectedSchedule;

        SaveRequisitionGroupProgramSchedule.save($scope.selectedRequisitionGroupProgramSchedule,successHandler,errorHandler);
    }

    
}