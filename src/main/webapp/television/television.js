'use strict';

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
	 * Retrieve the TV status
	 * 
	 */

 $scope.tv = function (sec) {
    $http.get('/tvscheduler/credit?value='+sec).
        success(function(data) {
            $scope.tvstatus = data.status;
        })
		};
		
 $scope.punir = function (point,rationale) {
    $http.post('/tvscheduler/punition',{'value': point, 'rationale': rationale}).
        success(function(data) {
            $scope.punitionMessage = data.message;
            $scope.error = false;
        }).
		error(function(data) {
            $scope.punitionMessage = 'Un problème a eu lieu';
			$scope.error = true;
		})
	};
/**
 * Confirmation dialog
 */

			  
			  $scope.showAdvanced = function(ev,point,rationale) {
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
				        $scope.punitionMessage = 'Le code entré n\'est pas correct';
						$scope.error = true;

					if(result.isOk){
					    $scope.punir(point,rationale);
					}
					 }, function() {
					    	console.log('Echec');
					  });
			  };

				  
			  
}]);



function DialogController($scope, $mdDialog,pinInfo,punition) {
	
	console.log('>>>>>>> '+pinInfo.middlePin);
	console.log('>>>>>>> '+punition.point);
	
	$scope.pinInfo=pinInfo;
	$scope.pinEntered='0';

	
	  $scope.hide = function() {
	    $mdDialog.hide();
	  };
	  $scope.cancel = function() {
	    $mdDialog.cancel();
	  };
	  $scope.answer = function(answer) {
		  var completePin=$scope.pinInfo.requiredPin;
		  if(answer==completePin){
			   $mdDialog.hide({isOk: true});
		  }else{
			   $mdDialog.hide({isOk: false});
		  }
	  };
}