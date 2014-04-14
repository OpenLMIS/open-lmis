/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function SmsController($scope,Users,$routeParams,GetSMSInfo, GetMessagesForMobile,GetReplyMessages) {

        $scope.sms = {};

        $scope.list = [];
         $scope.Msg= [];

        GetReplyMessages.get($scope.sms);

        Users.get({id: $routeParams.userId},function(data){

            $scope.sms.fullname= data.user.firstName + ' ' + data.user.lastName;

            console.log(data.user);
            $scope.username = data.user.username;
            $scope.sms.mobile = data.user.cellPhone;

            // get the user's mobile interactions
            GetMessagesForMobile.get({mobile: data.user.cellPhone},function(data){
                $scope.list = data.sms;
            });
        });

        $scope.sendSMS = function(){
            GetSMSInfo.get($scope.sms);
            $scope.message = 'New SMS sent successfully';
            $scope.sms.content = '';
            return true;
        } ;
}

