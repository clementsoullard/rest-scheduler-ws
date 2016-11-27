'use strict';

angular
		.module('myApp.courses', [ 'ngRoute' ])

		.config([ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/courses', {
				templateUrl : 'courses/courses.html',
				controller : 'coursesCtrl'
			});
		} ])

		.controller(
				'CoursesCtrl',
				[			'$scope',		'$http',	'$mdDialog',		'$mdMedia',		'$interval',		function($scope, $http, $mdDialog, $mdMedia, $interval) {

					
					/**
					 * List the teams that are displayed on the right to select for the match
					 */		
					function listVacances(){
					 $http.get('repository/vacances').
					 success(function(data) {
					//	console.log(JSON.stringify(data._embedded));
					     $scope.vacancess = data._embedded.vacances;
					});
					};

					
							/**
							 * Soumetttre des vacances
							 */
					
							$scope.submitVacances = function(vacances) {
								$http
										.post(
												'create-vacances',
												vacances)
										.success(
												function(data) {
													console
															.log('Succès de la sauvegarde de vacances');
													console.log('Echec de l\'appel WS punir');
										         
													listVacances();
												})
										.error(
												function(data) {
													   $scope.message = data.message;
														$scope.error = true;

													console
															.log('Echec de la sauvegarde de vacances');
												})
							}
						
				
							
							/**
							 * Exceuté au chargement de la page
							 */
							
							 listVacances()
				
				} ]);
