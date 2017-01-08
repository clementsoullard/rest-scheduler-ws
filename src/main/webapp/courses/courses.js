'use strict';

angular
		.module('myApp.courses', [ 'ngRoute' ])

		.config([ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/courses', {
				templateUrl : 'courses/courses.html',
				controller : 'CoursesCtrl'
			});
		} ])

		.controller(
				'CoursesCtrl',
				[	'$scope','$http','$mdDialog','$mdMedia','$interval',function($scope, $http, $mdDialog, $mdMedia, $interval) {
					
					
					/**
					 * Modify achat done (or not)
					 */
							
					 $scope.update = function (achat) {
							console.log("Update acaht");
							$http.post('ws-update-achat',achat).
						        success(function(data) {
						     	  	$scope.message='Achat enregistré';
						       	  	$scope.error=false;
						            list();
						        }).
								error(function(data) {
						     	  	$scope.message='An issue occured';
						       	  	$scope.error=true;
								})
								};
					/**
					 * Create an new achat 
					 */
										
				$scope.create = function (achat) {
					console.log("Create one achat");
						    $http.post('ws-create-achat',achat).
							        success(function(data) {
							     	  	$scope.message='Achat enregistré';
							       	  	$scope.error=false;
									         list();
						}).
										error(function(data) {
									     	  	$scope.message='An issue occured';
									       	  	$scope.error=true;
										})
									};
		
							
							/**
							 * List the tasks
							 */		
								 function list(){
									 $http.get('ws-active-achat').
								      success(function(data) {
								        	console.log(JSON.stringify(data));
								            $scope.achats = data;
								            console.log("Recuperation de la liste " );
								        });
									 }
								/**
								* Remove a task
								*/		
								$scope.remove = function(id){ $http.delete('repository/achat/'+id).
										success(function(data) {
									  	$scope.message='The entry has been removed.';
										list();
									});
								}
					/**
					 * Executed systematically
					 */				
						list(); 			
					
				} ]);
