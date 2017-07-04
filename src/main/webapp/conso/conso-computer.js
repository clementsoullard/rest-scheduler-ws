'use strict';

angular.module('myApp.conso-computer', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/conso-computer', {
    templateUrl: 'conso/conso-computer.html',
    controller: 'ConsoComputerCtrl'
  });
}])

.controller('ConsoComputerCtrl', ['$scope','$http', function($scope,$http) {
$scope.menuSelected="consoPC"; 
	
$scope.myDataSource = {};
  $http.get('/tvscheduler/chart-computer').
        success(function(data) {
			console.log("Succes");
			$scope.myDataSource = data;
       	});

 
}])
;