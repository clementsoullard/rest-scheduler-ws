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
					
					var potentialAchat=[];
					var selected={};
					$scope.selected=selected;
					
					/**
					 * Modify achat done (or not)
					 */
							
					 $scope.update = function (achat) {
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
								console.log("Submit de l'achat "+JSON.stringify(achat));
							    $http.post('ws-create-achat',achat).
								success(function(data) {
								    	  	$scope.message='Achat enregistré';
								    	  	$scope.error=false;
							  	list();
								$scope.achat={};
								  
									}).
													error(function(data) {
												     	  	$scope.message='An issue occured';
												       	  	$scope.error=true;
													})
												};
												
			
							
				

					    /**
						* Terminer les courses
						*/
																	
						$scope.finish = function (achat) {
						console.log("Courses terminées");
						$http.post('ws-finish-achat',achat).
						success(function(data) {
						$scope.message='Cousrses terminée';
						$scope.error=false;
						list();
						}).
						error(function(data) {
						$scope.message='An issue occured';
						$scope.error=true;
						})
						};

												
							
							/**
							 * List the active achat to procure
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
							 * List the active achat to procure for the combo box
							 */		
							function getDistinct(){
									 $http.get('ws-suggest-achat').
								      success(function(data) {
								    	  $scope.potentialAchatsArray = data;
								    	  potentialAchat=data;
								        	
								            console.log("Recuperation des element distinct "+JSON.stringify(data) );
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
						getDistinct();
						$scope.achat={};
					
				} ]);
