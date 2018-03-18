# Metadatasentinel

Demonstration application to show use of MDX as validator source

**Metadata Sentinel** consumes rules exposed by MDX at endpoint: 

`/api/modelCatalogue/core/validationRule/rules`

## Description

This applications allows the user to import Data as CSV format 

If you import a CSV file of two columns

```
NHSNumber,NHSNumberStatus
"1234567890","01"
"1234567890","01" 
```

You need to create a mapping string for each of the elements. To create a mapping, once the record collection has been imported go
to the mapping screen and select matching catalogue elements. 

If one of your csv header values does not match any MDX element leave the value in the mapping blank.

### DRL rules

The project contains a class `metadata.Validation` to ease drl validation rule creation.

Next is an example of DRL rule 

```
package metadata;

global java.util.List output;
global java.lang.String diagnosticTestDate;
global java.lang.String dateOfBirth;

rule "Difference between diagnosticTestDate and dateOfBirth is larger than 18"
when
    eval(Validation.yearsBetween(diagnosticTestDate, dateOfBirth, 'yyyy-MM-dd') < 18)
then
    output.add("Difference between diagnosticTestDate and dateOfBirth is larger than 18");
end 
```

You need to include  DROOLS Global name `output` of type `java.util.List`. When you find a violation you should add a string describing the rule violation to that list.

The previous example uses two variables `diagnosticTestDate` and `dateOfBirth`. You need to define in 
the MDX Business Rule which elements those variables correspond to. 

See screenshot: 

![MDX Business Rules Extensions Values](screenshot.png)
  

## Technology
 
This project is a [Grails 3](http://grails.org) application.

## Configuration 

You can configure the MDX server URL and credentials using configuration parameters `metadata.url`, `metadata.username`, `metadata.apiKey`. 

Add to `grails-app/conf/application.yml`

```
metadata:
    url: http://localhost:8080
    username: john
    apiKey: X151ABBVC
```

### Persistence
The app GORM for Hibernate implementation as an data access toolkit. It uses MySQL. You will 
need to configure your driver, dialect, database url, user, password at `grails-app/conf/application.yml`.

Currently the application contains the MySQL Driver and the H2 driver as a `testRuntime` dependency. 


## Running

To run the app

`./gradlew -DJDBC_DRIVER=com.mysql.jdbc.Driver -DJDBC_DIALECT=org.hibernate.dialect.MySQL5InnoDBDialect -DJDBC_CONNECTION_STRING=jdbc:mysql://127.0.0.1:8889/metadatasentinel_dev -DJDBC_USERNAME=root -DJDBC_PASSWORD=root bootRun`

To run the unit tests:

`./gradlew test`

To run the integration tests:

`./gradlew -DJDBC_DRIVER=com.mysql.jdbc.Driver -DJDBC_DIALECT=org.hibernate.dialect.MySQL5InnoDBDialect -DJDBC_CONNECTION_STRING=jdbc:mysql://127.0.0.1:8889/metadatasentinel_dev -DJDBC_USERNAME=root -DJDBC_PASSWORD=root iT`