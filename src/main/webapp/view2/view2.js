'use strict';

angular.module('myApp.view2', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view2', {
    templateUrl: 'view2/view2.html',
    controller: 'View2Ctrl'
  });
}])

.controller('View2Ctrl', ['$scope','$http', function($scope,$http) {

$scope.myDataSource = {};
  $http.get('/tvscheduler/chart-channel').
        success(function(data) {
			console.log("Succes");
			$scope.myDataSource = data;
       	});

 
}]);