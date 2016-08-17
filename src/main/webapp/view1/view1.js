'use strict';

angular.module('myApp.view1', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view1', {
    templateUrl: 'view1/view1.html',
    controller: 'View1Ctrl'
  });
}])

.controller('View1Ctrl',  ['$scope','$http', '$mdDialog', '$mdMedia', function($scope,$http, $mdDialog, $mdMedia) {

   $http.get('/tvscheduler/tvstatus').
        success(function(data) {
            $scope.tvstatus = data;
        });

 $scope.tv = function (sec) {
    $http.get('/tvscheduler/credit?value='+sec).
        success(function(data) {
            $scope.tvstatus = data.status;
        })
		};
		
 $scope.punition = function (point,rationale) {
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
		  $scope.showPrompt = function(ev,point,rationale) {
			    // Appending dialog to document.body to cover sidenav in docs app
			  var rnd0=Math.floor((Math.random() * 10) );
			  var rnd1=Math.floor((Math.random() * 10) );
			  var requiredPin=1000+rnd1*100+rnd0*10+1;
			  var confirm = $mdDialog.prompt()
			      .title('Code de confirmation')
			      .textContent('Entrez le code de confirmation '+rnd1 +''+rnd0)
			      .placeholder('Code confirmation')
			      .ariaLabel('Code confirmation')
				      .targetEvent(ev)
			      .ok('Ok')
			      .cancel('Annuler');
			    $mdDialog.show(confirm).then(function(result) {
			    console.log(result);
			    if(result==requiredPin){
			        $scope.punition(point,rationale);
			    }else{
			    	console.log('refusé'+requiredPin);
			    	$scope.punitionMessage = 'Le code pin entré nest pas correct';
					$scope.error = true;

			    }
						    
			    }, function() {
			      $scope.status = 'You didn\'t name your dog.';
			    });
			  };
			  
			  $scope.showAdvanced = function(ev,point,rationale) {
				    var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'))  && $scope.customFullscreen;
				    $mdDialog.show({
				       controller: DialogController,
				       templateUrl: 'view1/pin.tmpl.html',
				      parent: angular.element(document.body),
				      targetEvent: ev,
				      clickOutsideToClose:true,
				      fullscreen: useFullScreen
				    })
				    .then(function(result,requiredPin) {
					    console.log(result);
					
								    
					    }, function() {
					      $scope.status = 'You didn\'t name your dog.';
					    });
			  };
		
}]);



function DialogController($scope, $mdDialog) {
    // Appending dialog to document.body to cover sidenav in docs app
	  var rnd0=Math.floor((Math.random() * 10) );
	  var rnd1=Math.floor((Math.random() * 10) );
	  var requiredPin=rnd1*10+rnd0*1;
	  $scope.requiredPin=requiredPin;

	
	  $scope.hide = function() {
	    $mdDialog.hide();
	  };
	  $scope.cancel = function() {
	    $mdDialog.cancel();
	  };
	  $scope.answer = function(answer,requiredPin) {
		  var completePin=$scope.requiredPin*10 + 1001;
		  if(answer==completePin){
		        $scope.punition(point,rationale);
		    }else{
		    	console.log('refusé'+ completePin + ' '+answer);
		    	$scope.punitionMessage = 'Le code pin entré nest pas correct';
				$scope.error = true;

		    }
	    $mdDialog.hide(answer);
	  };
	}