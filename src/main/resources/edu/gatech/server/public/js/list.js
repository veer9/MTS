function linkBusStopNumbers(stops, id) {
    linkStopNumbers('bus')(stops, id);
}

function linkRailStopNumbers(stops, id) {
    linkStopNumbers('rail')(stops, id);
}

function linkBusRouteNumber(routeId, id) {
    linkRouteNumber('bus')(routeId, id);
}

function linkRailRouteNumber(routeId, id) {
    linkRouteNumber('rail')(routeId, id);
}

function linkStopNumbers(stem) {
    return function(stops, id) {
        $.when(...stops.map(stopId => getPath("/" + stem + "/stops/" + stopId)))
            .done(function() {
                console.log("linking stop numbers");
                var stopNames = Array.from(arguments).map((a, i) => a[0].stopName);
                $("#" + id).html(stopNames.join(", "));
            });
    }
}

function linkRouteNumber(stem){
    return function (routeId, id) {
        getPath("/" + stem + "/routes/" + routeId).done(function(routeResponse) {
          $("#" + id).html(routeResponse.routeName);
        });
    }
}
