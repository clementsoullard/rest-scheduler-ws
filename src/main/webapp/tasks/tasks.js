'use strict';

angular.module('myApp.television', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/tasks', {
    templateUrl: 'tasks/tasks.html',
    controller: 'tasksCtrl'
  });
}])

.controller('tasksCtrl',  ['$scope','$http', '$mdDialog', '$mdMedia','$interval', function($scope,$http, $mdDialog, $mdMedia,$interval) {


	 
	/**
	 * Insert a new entry fonction
	 */
			
	 $scope.update = function (task) {
	    $http.post('repository/task',task).
	        success(function(data) {
	     	  	$scope.message='Thanks for applying. You have been properly registred. You can also register husband/wife and children after closing this window.';
	       	  	$scope.error=false;
	            list();
	        }).
			error(function(data) {
	     	  	$scope.message='An issue occured';
	       	  	$scope.error=false;
			})
			};
				
	/**
	 * List the entries
	 */		
		 function list(){
			 $http.get('today-tasks').
		      success(function(data) {
		        	console.log(JSON.stringify(data._embedded));
		            $scope.tasks = data;
		        });
			 }
		/**
		* List the entries
		*/		
		$scope.remove = function(id){ $http.delete('repository/task/'+id).
				success(function(data) {
			  	$scope.message='The entry has been removed.';
				list();
			});
		}
			
list(); 
	
}]);
