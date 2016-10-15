'use strict';

angular.module('myApp.television', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/tasks', {
    templateUrl: 'tasks/tasks.html',
    controller: 'tasksCtrl'
  });
}])

.controller('tasksCtrl',  ['$scope','$http', '$mdDialog', '$mdMedia','$interval', function($scope,$http, $mdDialog, $mdMedia,$interval) {
	
	
}]);
