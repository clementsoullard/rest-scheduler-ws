﻿'use strict';

angular.module('myApp.television', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/television', {
    templateUrl: 'television/television.html',
    controller: 'TelevisionCtrl'
  });
}])

.controller('TelevisionCtrl',  ['$scope','$http', '$mdDialog', '$mdMedia', function($scope,$http, $mdDialog, $mdMedia) {

	  /**
	   * Pin initialization for the session 
	   * 
	   */
	var rnd1=Math.floor((Math.random() * 10) );
	var rnd0=Math.floor((Math.random() * 10) );
	var requiredPin=1001+100*rnd1+10*rnd0;
	var middlePin=rnd1+''+rnd0;
	
	  var pinInfo={rnd0: rnd0,rnd1:rnd1,requiredPin:requiredPin,middlePin:middlePin };
	  
		$scope.pinInfo=pinInfo;

	  
	/**
	 * Retrieve the TV status at page Loading
	 * 
	 */
   $http.get('/tvscheduler/tvstatus').
        success(function(data) {
            $scope.tvstatus = data;
        });
	/**
	 * Control the TV
	 * 
	 */

 $scope.tv = function (sec) {
		console.log('Appel WS tv control 2');
    $http.get('/tvscheduler/credit?value='+sec).
        success(function(data) {
			console.log('Sussès de l\'appel WS tv control');
            $scope.punitionMessage = 'Les minutes ont été attribuées';
            $scope.tvstatus = data.status;
			$scope.error = false;
        }).
		error(function(data) {
			console.log('Echec de l\'appel WS tv control');
            $scope.punitionMessage = 'Un problème a eu lieu';
			$scope.error = true;
		})
		};
		/**
		 * Punishment
		 */
 $scope.punir = function (point,rationale) {
    $http.post('/tvscheduler/punition',{'value': point, 'rationale': rationale}).
        success(function(data) {
            $scope.punitionMessage = data.message;
            $scope.error = false;
        }).
		error(function(data) {
			console.log('Echec de l\'appel WS punir');
            $scope.punitionMessage = 'Un problème a eu lieu';
			$scope.error = true;
		})
	};
/**
 * Confirmation dialog for punition
 */

			  
$scope.showAdvancedConfirmPunition = function(ev,point,rationale) {
				    var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'))  && $scope.customFullscreen;
				    $mdDialog.show({
				       controller: DialogController,
				       templateUrl: 'television/pin.tmpl.html',
				       locals:{pinInfo: pinInfo, punition: {point:point , rationale: rationale}}, 
				      parent: angular.element(document.body),
				      targetEvent: ev,
				      clickOutsideToClose:true,
				      fullscreen: useFullScreen
				    })
				    .then(function(result) {
					    console.log('Resultat '+result.isOk);

					if(result.isOk){
					    $scope.punir(point,rationale);
					}
					else{
				        $scope.punitionMessage = 'Le code entré n\'est pas correct';
						$scope.error = true;
					}
					 }, function() {
					    	console.log('Echec');
					  });
			  };

/**
* Confirmation dialog for punition
*/

			  			  
			  $scope.showAdvancedControlTV = function(ev,credit) {
			  				    var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'))  && $scope.customFullscreen;
			  					console.log('>> Appel de showAdvancedControlTV '+ credit);
			  				    $mdDialog.show({
			  				       controller: DialogController,
			  				       templateUrl: 'television/pin.tmpl.html',
			  				       locals:{pinInfo: pinInfo, punition: {credit: credit}}, 
			  				      parent: angular.element(document.body),
			  				      targetEvent: ev,
			  				      clickOutsideToClose:true,
			  				      fullscreen: useFullScreen
			  				    })
			  				    .then(function(result) {
			  					    console.log('Resultat controle PIN TV '+result.isOk);

			  					if(result.isOk){
			  					    console.log('Appel de TV credit');
					  				$scope.tv(credit);
			  					}else{
			  				        $scope.punitionMessage = 'Le code entré n\'est pas correct';
			  						$scope.error = true;
			  					}
			  					 }, function() {
			  					    	console.log('Echec');
			  					  });
			  			  };
		
			  
			  
}]);



function DialogController($scope, $mdDialog,pinInfo,punition) {
	
	console.log('>>>>>>> '+ pinInfo.middlePin);
	console.log('>>>>>>> '+ punition.point);
	console.log('>>>>>>> '+ punition.credit);
	
	$scope.pinInfo=pinInfo;
	
	
	  $scope.hide = function() {
	    $mdDialog.hide();
	  };
	  $scope.cancel = function() {
	    $mdDialog.cancel();
	  };
	  $scope.answer = function(answer) {
		  var completePin=$scope.pinInfo.requiredPin;
		 console.log('Comparaison entre '+ answer +' et ' + completePin);
		  if(answer==completePin){
			   $mdDialog.hide({isOk: true});
		  }else{
			   $mdDialog.hide({isOk: false});
		  }
	  };
}