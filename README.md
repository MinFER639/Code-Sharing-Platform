
# Code-Sharing-Platform
Secure version of a code-sharing platform.

Basic usage of HTML, CSS, JS.
Tech stack: Gradle, Spring Boot, FreeMarker.

## Examples of usage:

### Example 1

Request: **POST /api/code/new** with the following body:

    {
        "code": "class Code { ...",
        "time": 0,
        "views": 0
    }
    
Response:

    { "id" : "7dc53df5-703e-49b3-8670-b1c468f47f1f" }

Request **POST /api/code/new** with the following body:

    {
        "code": "public static void ...",
        "time": 0,
        "views": 0
    }

Response: 

    { "id" : "e6780274-c41c-4ab4-bde6-b32c18b4c489" }

Request **POST /api/code/new** with the following body:

    {
        "code": "Secret code",
        "time": 5000,
        "views": 5
    }

Response: 

    { "id" : "2187c46e-03ba-4b3a-828b-963466ea348c" }

### Example 2

Request: **GET /api/code/2187c46e-03ba-4b3a-828b-963466ea348c**

Response:

    {
        "code": "Secret code",
        "date": "2020/05/05 12:01:45",
        "time": 4995,
        "views": 4
    }

Another request **GET /api/code/2187c46e-03ba-4b3a-828b-963466ea348c**

Response:

    {
        "code": "Secret code",
        "date": "2020/05/05 12:01:45",
        "time": 4991,
        "views": 3
    }


### Example 3

Request: **GET /code/2187c46e-03ba-4b3a-828b-963466ea348c**

Response:

![image](https://user-images.githubusercontent.com/78106413/164552266-ba787021-ab03-4520-8eff-6a1c5d2d0707.png)


### Example 4

Request: **GET /api/code/latest**

Response:

    [
        {
            "code": "public static void ...",
            "date": "2020/05/05 12:00:43",
            "time": 0,
            "views": 0
        },
        {
            "code": "class Code { ...",
            "date": "2020/05/05 11:59:12",
            "time": 0,
            "views": 0
        }
    ]

### Example 5

Request: **GET /code/latest**

Response:
![image](https://user-images.githubusercontent.com/78106413/164550106-d9c8d019-de3e-41c6-ba4b-1a06c08c5274.png)



### Example 6

Request: **GET /code/new**

Response:
![image](https://user-images.githubusercontent.com/78106413/164550163-a788b949-10ce-4b62-b2f7-510606448ca1.png)


