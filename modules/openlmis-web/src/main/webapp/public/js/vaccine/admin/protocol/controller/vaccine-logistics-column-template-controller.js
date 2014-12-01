/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function LogisticsColumnTemplate($scope){

  $scope.sortableColumns = [
    {
      mandatory: true,
      visible: true,
      description: 'Product Name',
      label: 'Product',
      indicator: 'A'
    },
    {
      mandatory: false,
      visible: true,
      description: 'Opening Balance',
      label: 'Opening Balance',
      indicator: 'B'
    },
    {
      mandatory: false,
      visible: true,
      description: 'Received',
      label: 'Received',
      indicator: 'C'
    }
    ,
    {
      mandatory: false,
      visible: true,
      description: 'VVM Alerted',
      label: 'VVM Alerted',
      indicator: 'D'
    }
    ,
    {
      mandatory: false,
      visible: true,
      description: 'Freezed',
      label: 'Freezed',
      indicator: 'E'
    }
    ,
    {
      mandatory: false,
      visible: true,
      description: 'Expired',
      label: 'Expired',
      indicator: 'F'
    }
    ,
    {
      mandatory: false,
      visible: true,
      description: 'Other',
      label: 'Other',
      indicator: 'G'
    }
    ,
    {
      mandatory: false,
      visible: true,
      description: 'Discarded Unopened',
      label: 'Discarded Unopened',
      indicator: 'H'
    },
    {
      mandatory: false,
      visible: true,
      description: 'Discarded Opened',
      label: 'Discarded Opened',
      indicator: 'I'
    }
  ];

}

LogisticsColumnTemplate.resolve = {

};