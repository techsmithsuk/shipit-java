ShipIt Inventory Management
===========================

Copyright 2007.

# Setup Instructions

## Running the application

In general, to run the application, build it via `mvn install`, then drop the resulting
war file into any tomcat instance.

### With IntelliJ

To run the app via intelliJ:

* Ensure that [Apache Tomcat](http://tomcat.apache.org/) is installed.  Any
  version should be OK.
* Create a run configuration via `Run` -> `Edit Configurations` -> `+` ->
* `Tomcat Server` -> `Local`.
* Under name write `shipit-dev`, In the `Deployment` tab, click `+` ->
* `Artifact` -> `shipit:war exploded`, and in the `Startup / Connection` tab add
* the environment variables:
  * JDBC_CONNECTION_STRING - the JDBC connection string for your DB instance,
   eg. `JDBC_CONNECTION_STRING=jdbc:mysql://<host>:<post>/shipit`
  * and MYSQL_PASSWORD - the password for your remote DB instance
* Click `OK`.
* Select your `shipit-dev` configuration from the drop-down menu at the top
  right and then click the play button.

### On AWS Elastic Beanstalk

To update a running [AWS Elastic Beanstalk](https://aws.amazon.com/elasticbeanstalk/) instance:

* Update the version number in `pom.xml`.
* Build via `mvn install`, note the name of the war which was just built.
* From the AWS console, go to `Services` -> `Elastic Beanstalk`, and choose your instance
  from the dashboard.   Choose `Upload and Deploy`, then `Browse...` and choose the war
  which you just build with `mvn install`.  Click `Deploy`.  Wait for the deployment to
  be marked as successful.

To check the logs:  From the AWS console, go to `Services` -> `Elastic Beanstalk`, and
choose your instance from the dashboard.   Click `Logs` on the left, then `Request Logs`.

In the unlikely event that you need to change any of the injected configuration, for
example the database connection string or password, then these are available under
`Configuration` -> `Software Configuration`.

Information on the CPU utilisation, and network utilisation is available under `Monitoring`,
it may also be interesting to look at the utilisation or logs of the MySQL database instance
which backs this application.  These are available under `Services` -> `RDS` -> `Instances`
-> `shipit`.

## Unit Tests

Run the tests with `mvn test`, or in intelliJ by right clicking on `src/main/test` and
choosing "Run 'All Tests'".

Due to the "age" of the codebase, the unit tests rely on a connection to the database.
Therefore you will need to ensure that you have a database setup whose schema matches
the production database.  This database can be local or remote.  The location of this
database is set in the file `src/test/resources/test-context.xml`.
