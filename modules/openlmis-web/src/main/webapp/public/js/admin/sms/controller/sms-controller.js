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

