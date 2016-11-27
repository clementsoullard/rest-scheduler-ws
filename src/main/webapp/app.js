'use strict';


// Declare app level module which depends on views, and components
angular.module('myApp', [
  'ngRoute',
  'myApp.television',
  'myApp.vacances',
   'myApp.view2',
  'myApp.version',
  'ng-fusioncharts',
  'ngMaterial'
]).
config(['$locationProvider', '$routeProvider', function($locationProvider, $routeProvider) {
  $locationProvider.hashPrefix('!');

  $routeProvider.otherwise({redirectTo: '/television'});
}]);
