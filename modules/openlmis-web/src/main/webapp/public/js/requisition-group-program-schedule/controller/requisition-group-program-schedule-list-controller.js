function RequisitionGroupProgramScheduleListController($scope, $location, navigateBackService, RequisitionGroupCompleteList, ProgramCompleteList,ScheduleCompleteList,$dialog, messageService) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.showRequisitionGroupsList('txtFilterRequisitionGroups');
    });
    $scope.previousQuery = '';

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
            return {'background-color':'#f9f9f9'};
        }
        else{
            return 'none';
        }

    };

    $scope.setSelectedReqGroup = function (reqGroup){
        $scope.selectedRequisitionGroup = reqGroup;
    };

    $scope.getProgramColor = function(program){
        if($scope.selectedProgram== null){
            return 'none';
        }

        if($scope.selectedProgram.code == program.code){
            return {'background-color':'#f9f9f9'};
        }
        else{
            return 'none';
        }

    };

    $scope.setSelectedSchedule = function (schedule){
        $scope.selectedSchedule = schedule;
    };

    $scope.getScheduleColor = function(schedule){
        if($scope.selectedSchedule== null){
            return 'none';
        }

        if($scope.selectedSchedule.code == schedule.code){
            return {'background-color':'#f9f9f9'};
        }
        else{
            return 'none';
        }

    };

    $scope.setSelectedProgram = function (schedule){
        $scope.selectedSchedule = schedule;
    };
    
    
}