README

This directory contains the following files for the parking assignment
of the 2IMN15 course.

32800.xml
	LwM2M object definition for the Parking Spot object.
	Copy this file to leshan-server-demo/src/main/resources/models/
	and add it to the list in LwM2mDemoConstant.java, located in
	leshan-server-demo/src/main/java/org/eclipse/leshan/server/demo/
32801.xml
	LwM2M object definition for Vehicle Counter object.
	Use this file similar to 32801.xml .

lwm2m-client.jar
	LwM2M client implementation based on Leshan client demo.
	To start it, use a command line like:

	java -jar lwm2m-client.jar -u 127.0.0.1:5683 -pos "3.0:4.0" -parkingspot -vehiclecounter

	By running the standard leshan-server-demo application on the
	same machine, the client will connect to that server and you
	can interact with the client through the provided website
        (on http://127.0.0.1:8080/ )

	The lwm2m-client applications opens a number of windows, one for each
	object, which can be used to adjust resource values to test your
	server modifications. Some resource values within an object are
	updated together, for example, changing 'Last Plate' also increments
	'Vehicle Counter'.
