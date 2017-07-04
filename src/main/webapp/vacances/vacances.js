'use strict';

angular
		.module('myApp.vacances', [ 'ngRoute' ])

		.config([ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/vacances', {
				templateUrl : 'vacances/vacances.html',
				controller : 'VacancesCtrl'
			});
		} ])

		.controller(
				'VacancesCtrl',
				[			'$scope',		'$http',	'$mdDialog',		'$mdMedia',		'$interval',		function($scope, $http, $mdDialog, $mdMedia, $interval) {

					$scope.menuSelected="vacances"; 	
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
							* Remove a vacances
							*/		
							$scope.remove = function(id){ $http.delete('repository/vacances/'+id).
									success(function(data) {
								  	$scope.message='The vacances has been removed.';
								  	 listVacances();
								  	 $scope.vacances={};
								});
							}
							
							/**
							 * Exceuté au chargement de la page
							 */
							
							 listVacances()
				
				} ]);
