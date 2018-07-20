#!/usr/bin/python

import random
import csv
import math

#Top level variables
numRows   = 1000
numRiders = 10000
numTrainStops = 40
numStops  = 10
numRoutes = 4

#Simple script to create SQL commands to populate tables with random data
#Usage: $0 (from base repository directory)

def valStr(val, isArray):
    retStr=""
    if type(val) is list:
        retStr = "\'{" + ", ".join(map(lambda x: valStr(x, False), val)) + "}\'"
    elif type(val) is str:
        if isArray:
            retStr = "\'{\"" + val + "\"}\'"
        else:
            retStr = "\'" + val + "\'"
    else:
        retStr = str(val)

    return retStr

def createCmdStr(numRows, tableName, colList, genValList, isArray=False):
    cmdStr = "INSERT INTO " + tableName + " ("

    #Add Column list to command
    for i in range(len(colList) - 1):
        cmdStr = cmdStr + str(colList[i]) + ", "
    cmdStr = cmdStr + str(colList[-1]) + ") VALUES\n"

    insertStrs = []

    for i in range(numRows):
        valList = genValList(i)
        valStrs = map(lambda v: valStr(v, isArray), valList)
        insertStrs.append("(" + ", ".join(valStrs) + ")")
    
    #Finalize command String
    cmdStr = cmdStr + ",\n".join(insertStrs) + ";\n"
    return cmdStr

def randInt(aNum):
    return int(random.randint(0, aNum-1))

def randIntList(aNum):
    return [randInt(aNum)];

def randItem(l):
    return l[randInt(len(l))]

def randKey(d):
    keys = d.keys()
    return keys[randInt(len(keys))]

def randStr(aList):
    if len(aList) < 2:
        return "Some Value"
    else:
        return str(aList[random.randint(1, len(aList)-1)]) #Skip first value with will be the column name

#Various utility functions

def createTrainRoutes(numRoutes, numStops):
    stopsPerRoute = math.ceil(numStops / numRoutes)
    allStops = range(0, numStops, 1)
    routesToStops = {}
    for i in range(0, numRoutes, 1):
        routesToStops[i] = []
    while len(allStops) > 0:
        stopPosition = randInt(len(allStops))
        routesToStops[randInt(numRoutes)].append(allStops[stopPosition])
        del allStops[stopPosition]
    return routesToStops

def reverseIndexMapList(stopsToRoutes):
    routesToStops = {}
    for stopId in stopsToRoutes.keys():
        routes = stopsToRoutes[stopId]
        for routeId in routes:
            existingStops = routesToStops.get(routeId, [])
            existingStops.append(stopId)
            routesToStops[routeId] = existingStops
    return routesToStops

#Read CSV Dat_
print "Reading in Random Names..."
csvReader = csv.reader(open("random_names.csv"))

#Extract Columns from CSV
rows      = [list(i) for i in csvReader]
routes    = [i[0]    for i in rows]
direction = [i[1]    for i in rows]
stops     = [i[2]    for i in rows]
traffic   = [i[3]    for i in rows]

#Create file with SQL commands
filename = "create_random_table_data.sql"
fp = open(filename, 'w')

#generate stop->route index (Buses only)
busStopToRouteReader = csv.reader(open("stops_to_routes.csv"))
busStopsToRoutes = {}
for row in busStopToRouteReader:
    if row[0] != 'stop_id':
        busStopsToRoutes[int(row[0])] = map(lambda x: int(x), row[1].split(';'))

busRoutesToStops = reverseIndexMapList(busStopsToRoutes)
railRoutesToStops = createTrainRoutes(numRoutes, numTrainStops)
railStopsToRoutes = reverseIndexMapList(railRoutesToStops)

def createRiderDistribution(i):
    travel_mode = randInt(2)
    stopId = 0
    routes = []    
    endStopId = 0
    if travel_mode == 1: # Rail
        stopId = randKey(railStopsToRoutes)
        routes = railStopsToRoutes[stopId]
        routeId = randItem(routes)
        # This should check for connection
        endStopId = randItem(railRoutesToStops[routeId])
    else:
        stopId = randKey(busStopsToRoutes)
        routes = busStopsToRoutes[stopId]
        routeId = randItem(routes)
        endStopId = randItem(busRoutesToStops[routeId])
        
    return [i, routes, stopId, endStopId, travel_mode]

# This doesn't compute a set of all roads - we should change the code so
# that it generates a road for all stops in a given route, for all routes
def createRoadInfo(i):
    routeId = randKey(busRoutesToStops)
    stops = busRoutesToStops[routeId]
    firstStopIndex = randInt(len(stops))
    firstStopId = stops[firstStopIndex]
    secondStopId = stops[(firstStopIndex + 1) % len(stops)]
    return [i, routeId, firstStopId, secondStopId, randInt(2), random.randint(15,66), randStr(traffic)]

def createTrainData(i):
    routeId = railStopsToRoutes[i]
    return [i, routeId[0], i, randStr(routes), randStr(stops), randStr(direction)]
    
def createRouteInfo(count):
    routeInfoSet = set()
    while len(routeInfoSet) < count:
        railRouteId = randInt(numRoutes)
        railStopId = randItem(railRoutesToStops[railRouteId])
        busRouteId = busRoutesToStops.keys()[randInt(len(busRoutesToStops))]
        busStopId = randItem(busRoutesToStops[busRouteId])
        routeInfoSet.add((railRouteId, railStopId, busRouteId, busStopId))
    return routeInfoSet

print "Creating Random Table data with", numRows, "rows..."

#Insert into rider distribution table
cmdStr = createCmdStr(numRiders,
                      "rider_distribution",
                      ["rider_id", "route", "start_stop", "dest_stop", "travel_mode"],
                      createRiderDistribution,
                      True) #isArray
fp.write(cmdStr)

fp.write("\n")

#Insert into road info table
cmdStr = createCmdStr(numRows,
                      "road_info",
                      ["road_id", "route_id", "stop1", "stop2", "road_work", "speed_limit", "traffic"],
                      createRoadInfo)
fp.write(cmdStr)

fp.write("\n")

routeInfoList = list(createRouteInfo(numRows))
#Insert into transit_info table
# Note: We should extract this to a function and generate consistent data.
cmdStr = createCmdStr(numRows,
                      "transit_info",
                      ["rail_route", "rail_stop", "bus_route", "bus_stop"],
                      lambda i: list(routeInfoList[i]))
fp.write(cmdStr)

fp.write("\n")

#Insert into train_data table
cmdStr = createCmdStr(numTrainStops,
                      "train_data",
                      ["train_id", "route_id", "stop_id", "route_name", "stop_name", "route_direction"],
                      createTrainData)
fp.write(cmdStr)

fp.write("\n")
fp.close()

print "Finished, Run commands from PostGreSQL by executing \"\\i {0}\" from psql interpreter".format(filename)

