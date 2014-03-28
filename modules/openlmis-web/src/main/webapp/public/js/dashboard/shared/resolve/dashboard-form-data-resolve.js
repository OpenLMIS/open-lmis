/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/26/14
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */

ResolveDashboardFormData = {
    programsList : function($q, $timeout, $rootScope, ProgramListBySupervisoryNodes){
        var deferred = $q.defer();
        $timeout(function () {

            ProgramListBySupervisoryNodes.get({}, function(data){
                deferred.resolve(data.programs);
            });

        },100);

        return deferred.promise;

    },
    userDefaultSupervisoryNode : function($q, $timeout, $rootScope, UserDefaultSupervisoryNode){
        var deferred = $q.defer();
        $timeout(function (){

            UserDefaultSupervisoryNode.get({}, function(data){
                deferred.resolve(data.supervisoryNode);
            })

        },100);

        return deferred.promise;

    },
    formInputValue : function(messageService){
               return {
                yearOptionAll : messageService.get('input.year.option.all'),
                programOptionSelect : messageService.get('input.program.option.select'),
                scheduleOptionSelect : messageService.get('input.schedule.option.select'),
                requisitionOptionAll : messageService.get('input.requisition.option.all'),
                facilityOptionSelect : messageService.get('input.facility.option.select'),
                periodOptionSelect : messageService.get('input.period.option.select'),
                supervisoryNodeOptionAll : messageService.get('input.supervisory.node.option.all')
            };
    }
};