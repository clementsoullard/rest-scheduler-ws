'use strict';

angular.module('myApp.tasksHome', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/tasksHome', {
    templateUrl: 'tasks/tasksHome.html',
    controller: 'tasksHomeCtrl'
  });
}])

.controller('tasksHomeCtrl',  ['$scope','$http', '$mdDialog', '$mdMedia','$interval', function($scope,$http, $mdDialog, $mdMedia,$interval) {


	 
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
	       	  	$scope.error=true;
			})
			};
		/**
			 * Insert a new entry fonction
			 */
					
			 $scope.newForToday = function (task) {
			    $http.post('ws-create-todo',task).
			        success(function(data) {
			     	  	$scope.message='Thanks for applying. You have been properly registred. You can also register husband/wife and children after closing this window.';
			       	  	$scope.error=false;
			       	  	$scope.task={expireAtTheEndOfTheDay: false, owner:'Home'};
			            list();
			        }).
					error(function(data) {
			     	  	$scope.message='An issue occured';
			       	  	$scope.error=false;
					})
					};
				
	/**
	 * List the tasks
	 */		
		 function list(){
			 $http.get('today-tasks-home').
		      success(function(data) {
		        	console.log(JSON.stringify(data._embedded));
		            $scope.tasks = data;
		        });
			 }
		/**
		* Remove a task
		*/		
		$scope.remove = function(id){ $http.delete('repository/task/'+id).
				success(function(data) {
			  	$scope.message='The entry has been removed.';
				list();
			});
		}
		
		/**
		 * Functions called at loadding
		 */
	  	$scope.task={expireAtTheEndOfTheDay: false, owner:'Home'};
	  	list(); 
	
}]);
