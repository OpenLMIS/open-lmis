
/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
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