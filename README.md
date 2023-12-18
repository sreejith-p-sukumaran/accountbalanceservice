# Account Balance Service

This is a sample Kotlin / Gradle / Spring Boot (version 3.2.0) application that can be used to performs money transactions like
pay-ins and pay-outs. Those transactions compose the customer's account balance.

## How to Run Locally

This application is packaged as a jar. You run it using the ```java -jar``` command.

* Make sure you are using JDK 17 and Gradle 8.x (I have not tested with the below versions)
* You can build the project and run the tests by running ```./gradlew build```
* Once successfully built, you can run the service by the below method:
```
        java -jar build/libs/accountbalanceservice-0.0.1-SNAPSHOT.jar"
```

Once the application runs you should see something like this

```
2023-12-15T19:26:53.230+05:30  INFO 20856 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path ''
2023-12-15T19:26:53.240+05:30  INFO 20856 --- [           main] c.a.AccountBalanceServiceApplicationKt   : Started AccountBalanceServiceApplicationKt in 3.348 seconds (process running for 3.671)
```

## How to Run On Docker

This application is packaged as a jar. 

You can build the project and run the tests by running ./gradlew build

Once the jar is ready, create a docker image running ```docker build -t accountbalanceservice:latest .``` command from the root directory.

Once the Docker image generated, run the image using  ```docker run -p 8080:8080 accountbalanceservice:latest  ``` command.

Once the application runs you should see something like this

```
2023-12-15T19:26:53.230+05:30  INFO 20856 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path ''
2023-12-15T19:26:53.240+05:30  INFO 20856 --- [           main] c.a.AccountBalanceServiceApplicationKt   : Started AccountBalanceServiceApplicationKt in 3.348 seconds (process running for 3.671)
```

## About the Service

The service is an account balance REST service. Each customer of the company's multitenant platform performs money transactions like
pay-ins and pay-outs. Those transactions compose the customer's account balance. 

It uses an in-memory database (H2) to store the data. You can also do with a relational database like MySQL or PostgreSQL. If your database connection properties work, you can call some REST endpoints defined in ```com.accountbalanceservice.controller``` on **port 8080**.

Here is what this little application demonstrates:

* Default data (Tenants, Customers and Transactions) is being injected into the database during application startup.
* CRUD apis to book, rollback, list and audit against the data source using Spring JPA *Repository* pattern
* Basic request and service validations implemented
* Exception mapping from application exceptions to the right HTTP response with exception details in the body
* Basic logging has been added using Log4j2
* Demonstrates Mockito MockMVC test framework with associated tests
* Demonstrates how to set up healthcheck, metrics, info, environment, etc. endpoints automatically on a configured port.

Here are some endpoints you can call:

### Get information about system health, configurations, etc.

```
http://localhost:8080/actuator/env
http://localhost:8080/actuator/health
http://localhost:8080/actuator/info
http://localhost:8080/actuator/metrics
```

### Book a transaction

```
POST http://localhost:8080/api/transactions/book
Accept: application/json
Content-Type: application/json
{
    "tenantNumber": 1,
    "customerNumber": 1,
    "amount": 210.0
}

RESPONSE: HTTP 200 (Created)
{
    "code": 200,
    "status": "OK",
    "data": "Transaction with ID: 5 booked successfully!!"
}
```

### Rollback a transaction

```
PATCH http://localhost:8080/api/transactions/rollback/1
Response: HTTP 200
Content-Type: application/json 

RESPONSE: HTTP 200 (Patched)
{
    "code": 200,
    "status": "OK",
    "data": "Transaction with ID 5 rolled back successfully."
}
```

### Fetch recent transactions per customer and tenant

```
GET http://localhost:8080/api/transactions/list/1/2
Content-Type: application/json

RESPONSE: HTTP 200 
{
    "code": 200,
    "status": "OK",
    "data": [
        {
            "tenantName": "enterprise-all-inclusive.com",
            "customerName": "enterprise customer one",
            "accountBalance": -110.0,
            "transactions": [
                {
                    "id": 1,
                    "amount": 100.0,
                    "operation": "BOOK",
                    "bookedAt": "17/12/2023 19:03:39",
                    "rollbackAt": ""
                },
                {
                    "id": 5,
                    "amount": 210.0,
                    "operation": "ROLLBACK",
                    "bookedAt": "17/12/2023 19:03:46",
                    "rollbackAt": "17/12/2023 19:04:06"
                }
            ]
        }
    ]
}
```
### Audit transactions per customer and tenant

```
GET http://localhost:8080/api/transactions/audit
Content-Type: application/json

RESPONSE: HTTP 200 
{
    "code": 200,
    "status": "OK",
    "data": [
        {
            "tenantName": "enterprise-all-inclusive.com",
            "customerName": "enterprise customer one",
            "amount": 100.0,
            "operation": "BOOK",
            "time": "17/12/2023 18:52:36"
        },
        {
            "tenantName": "enterprise-all-inclusive.com",
            "customerName": "enterprise customer one",
            "amount": 0.0,
            "operation": "ROLLBACK",
            "time": "17/12/2023 18:54:44"
        },
        {
            "tenantName": "enterprise-all-inclusive.com",
            "customerName": "enterprise customer two",
            "amount": 200.0,
            "operation": "BOOK",
            "time": "17/12/2023 18:52:36"
        },
        {
            "tenantName": "betrieb-alles-inklusive.de",
            "customerName": "betrieb customer one",
            "amount": 300.0,
            "operation": "BOOK",
            "time": "17/12/2023 18:52:36"
        },
        {
            "tenantName": "betrieb-alles-inklusive.de",
            "customerName": "betrieb customer two",
            "amount": 400.0,
            "operation": "BOOK",
            "time": "17/12/2023 18:52:36"
        }
    ]
}
```



