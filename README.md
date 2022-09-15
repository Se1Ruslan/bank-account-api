### Requirements

Java 11

### how to run

`./mvnw clean install spring-boot:run`


### how to test

#### Get status:

```
curl --location --request GET 'http://localhost:8080/api/account/status/1000123'
```


#### Check account balance inquiry:
```
curl --location --request GET 'http://localhost:8080/api/account/balance/1000123'
```


#### Debit/Credit side operations :
```
curl --location --request POST 'http://localhost:8080/api/account/operation' \
--header 'Content-Type: application/json' \
--data-raw '{
    "accountNumber": "1000123",
    "currency": 978,
    "amount": 10.0,
    "operationSign": "DEBIT"
}'


curl --location --request POST 'http://localhost:8080/api/account/operation' \
--header 'Content-Type: application/json' \
--data-raw '{
    "accountNumber": "1000123",
    "currency": 978,
    "amount": 10.0,
    "operationSign": "CREDIT"
}'
```