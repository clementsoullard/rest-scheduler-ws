'use strict';

angular.module('myApp.conso-tele', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/conso-tele', {
    templateUrl: 'conso/conso-tele.html',
    controller: 'ConsoTeleCtrl'
  });
}])

.controller('ConsoTeleCtrl', ['$scope','$http', function($scope,$http) {

$scope.myDataSource = {};
  $http.get('/tvscheduler/chart-channel').
        success(function(data) {
			console.log("Succes");
			$scope.myDataSource = data;
       	});

 
}])
;