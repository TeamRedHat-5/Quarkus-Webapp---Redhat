#!/bin/bash
curl -X POST http://localhost:8080/flight/createFlight -H "Content-Type: application/json" -d '{"flightNumber": "M2347", "departure": "BLR", "destination": "MAA"}'
echo
curl -X POST http://localhost:8080/flight/createFlight -H "Content-Type: application/json" -d '{"flightNumber": "M2348", "departure": "HYD", "destination": "CCU"}'
echo
curl -X POST http://localhost:8080/flight/createFlight -H "Content-Type: application/json" -d '{"flightNumber": "M2349", "departure": "DEL", "destination": "BOM"}'
echo
curl -X POST http://localhost:8080/flight/createFlight -H "Content-Type: application/json" -d '{"flightNumber": "M2350", "departure": "MAA", "destination": "NCL"}'
echo
curl -X POST http://localhost:8080/flight/createFlight -H "Content-Type: application/json" -d '{"flightNumber": "M2351", "departure": "CCU", "destination": "HYD"}'
echo
