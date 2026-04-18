# FairShare

NUS-ISS GCDMSS

## Prerequisites

1. MySQL Workbench
2. mvn
3. Java 17

## Initial Set-Up

1. Start a MySQL localhost connection

   1. Take note of your set username, password, and the host url
2. Create a schema aka database

   1. Name: `fairshare`
   2. Keep the rest of the fields as default fields
3. In application-dev.properties (src/main/resources/application-dev.properties), replace the following (do NOT touch the other fields)

   ```
   spring.datasource.url=#REPLACE
   spring.datasource.username=#REPLACE
   spring.datasource.password=#REPLACE
   ```

## Launching in DEV environment

Run the following in order, from the root folder

`cd backend`

`mvn clean install`

`mvn spring-boot:run -Dspring-boot.run.profiles=dev`

This just runs spring-boot:run but specifies that the profile is 'dev'. This makes sure that the build is using application-dev.properties


## Note for US_013 to US_017

Run the following SQL script prior to using code from these user stories:

   ```
   INSERT INTO tb_role (role_id, name, scope)
   VALUES 
   (1, 'GROUP_ADMIN', 'GROUP'),
   (2, 'GROUP_MEMBER', 'GROUP');
   ```

## Contribution

1. Create PR's to main only
   1. do NOT merge to main branch directly
2. Process of creating PR:
   1. In your own branch, run  `mvn clean verify`	to test if it will past the build test
