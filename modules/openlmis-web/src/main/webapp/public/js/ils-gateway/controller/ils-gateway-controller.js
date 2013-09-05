/**
 * Created with IntelliJ IDEA.
 * User: henok
 * Date: 9/5/13
 * Time: 12:44 AM
 * To change this template use File | Settings | File Templates.
 */
function ILSGatewayDashboardController($scope, $routeParams, $location, $dialog, messageService) {

    $scope.setHeight = function(){

    }

    $scope.getHeight = function(contentId){
        var content = document.getElementById(contentId).contentWindow;
        if(content != null){
            var height = content.document.height;
            return "height :" + height;
        }
    }

}