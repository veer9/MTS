(function () {
    var app = angular.module("simulationApp", []);
    app.controller("simulationCtrl", function($scope,$http) {
        $scope.rails = [];
        $scope.transitNames = [{name:'Bus',identifier:0},{name:'Rail',identifier:1},{name:'Either/Both',identifier:2}];
        $scope.buses = [];
        $scope.transitStops = [];
        $scope.transitRoutes = [];

        $http.get("/status")
            .then(function(response) {
                console.log(response);
                $http.get("/rail/vehicles")
                    .then(function(response) {
                        console.log(response);
                    });
                $http.get("/bus/vehicles")
                    .then(function(response) {
                        console.log(response);
                    });
            });
        $scope.init = function(){
            $http.get("/status")
                .then(function(response) {
                    data = response.data;
                    $("#current-rank").text(data.currentRank);
                    $("#bus-routes-count").text(data.busRoutesCount);
                    $("#rail-routes-count").text(data.railRoutesCount);
                    $("#bus-stops-count").text(data.busStopsCount);
                    $("#rail-stops-count").text(data.railStopsCount);
                    $("#buses-count").text(data.busesCount);
                    $("#trains-count").text(data.trainsCount);
                    //$("#passengers-count").text(data.passengersCount);
                    $scope.getVehicles();
            });
        }

        $scope.getVehicles = function() {
            $http.get("/rail/vehicles")
                .then(function(response) {
                    $scope.rails = response.data.vehicles;
                });

            $http.get("/bus/vehicles")
                .then(function(response) {
                    $scope.buses = response.data.vehicles;
                });
        }
        $scope.retrieveRoutes = function(){
                    $scope.transitRoutes = [];
            if ($scope.selectedTransit==0){

                $http.get("/bus/routes")
                    .then(function(response){
                        $scope.transitRoutes = response.data.routes;
                        console.log($scope.transitRoutes);
                    });
            }
            else {
                $http.get("/rail/routes")
                    .then(function(response){
                        $scope.transitRoutes = response.data.routes;
                        console.log($scope.transitRoutes);
                    });
            }

        }


        $scope.retrieveStops = function(){
                    $scope.transitStops = [];
            if ($scope.selectedTransit==0){
                $http.get("/bus/stops")
                    .then(function(response){
                        $scope.transitStops= response.data.stops;
                });
            }
            else {
                $http.get("/rail/stops")
                    .then(function(response){
                        $scope.transitStops= response.data.stops;
                    });
            }
        }

        $scope.submitRiderCreation = function(){
            $scope.riderCreationData = {beginStop:$scope.beginStop,endStop:$scope.endStop,travelMode:$scope.selectedTransit,routeIDs:[$scope.selectedRoute.id]};
            if ($scope.selectedTransit==0){
                $http.post("/bus/riders",$scope.riderCreationData)
                    .then (function(response){
                        $scope.beginStop = '';
                        $scope.endStop = '';
                        $scope.selectedTransit = '';
                        $scope.selectedRoute = '';
                        $scope.transitStops = [];
                        $scope.transitRoutes = [];
                    });
            }
            else {
                $http.post("/rail/riders",$scope.riderCreationData)
                    .then (function(response){
                        $scope.beginStop = '';
                        $scope.endStop = '';
                        $scope.selectedTransit = '';
                        $scope.selectedRoute = '';
                        $scope.transitStops = [];
                        $scope.transitRoutes = [];
                    });
            }
        }

        $scope.retrievePassengers = function() {
            $scope.riders = [];
            $http.get("/bus/riders")
                .then(function(response){
                    tempArr = [];
                    tempArr = response.data.riders;
                    var arr = [];
		console.log(arr.length);
                    tempArr.forEach(function(element){
                        var tvMode = 'Bus';
                        if (element.travelMode==2)
                            tvMode = 'Either/Both';
                        arr.push({
                            id: element.id,
                            beginStop:element.beginStop,
                            endStop:element.endStop,
                            routeIDs:-1,
                            travelMode:tvMode
                        });
                    });
                    $http.get("/rail/riders")
                        .then(function(response){


                            tempArr = [];
                            tempArr = response.data.riders;
                            tempArr.forEach(function(element){
                            var tvMode = 'Rail';
                                                        if (element.travelMode==2)
                                                            tvMode = 'Either/Both';
                                arr.push({
                                                            id: element.id,
                                                            beginStop:element.beginStop,
                                                            endStop:element.endStop,
                                                            routeIDs:-1,
                                                            travelMode:tvMode
                                                        });
                            });
                            $scope.riders = arr;
                        })

                })
        }


        $scope.init();
    });
})();
