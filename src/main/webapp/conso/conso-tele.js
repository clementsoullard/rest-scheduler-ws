'use strict';

angular.module('myApp.conso-tele', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/conso-tele', {
    templateUrl: 'conso/conso-tele.html',
    controller: 'ConsoTeleCtrl'
  });
}])

.controller('ConsoTeleCtrl', ['$scope','$http', function($scope,$http) {

	$scope.menuSelected="consoTV"; 
function load(){	
$scope.myDataSource = {};
console.log("Chagement de la datasource "+$scope.typeGraph);
if($scope.typeGraph)
  $http.get('/tvscheduler/'+$scope.typeGraph).
        success(function(data) {
			console.log("Succes");
			$scope.myDataSource = data;
       	});
}

load();

$scope.reload = function(){load();}
 
}])
;