# Group 48 Project 8

## Building

To build the project using maven, use:

```
mvn compile
```

To test use

```
mvn test
```

(Note that these tasks may also be run from an IDE, if configured appropriately)

## Running

To run the spark-java server from maven, use:

```
mvn exec:java -Dexec.mainClass=edu.gatech.server.Run
```

## Building

Use `mvn package` to build a release. The resulting jar can be run as follows:

```
java -jar target/mass_transit-1.0-SNAPSHOT-jar-with-dependencies.jar
```

This will start a webserver serving our application on [http://localhost:4567](http://localhost:4567).

## Generate Database data`

Data for the backend is stored in a PostGres SQL Database.
The name of the database is "martadb". To setup the database from 
scratch (using the official VM, from the base repository directory)

```
psql martadb
\i martadb_structure.backup
\i martadb_schema_new_tables
\i create_random_table_data.sql
```

Random data is generated using a python script. The default number of
rows is 100000. You can regenerate the "create_random_table_data.sql"
file by running the python script as shown below:

```
python generate_random_table_data.py
```
