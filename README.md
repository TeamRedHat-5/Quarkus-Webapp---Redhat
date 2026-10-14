# CSC8104 Quickstart Project

This quickstart project is provided for students on the CSC8104 Enterprise Middleware module and provides a foundation for starting their coursework. Students are expected to download and build their solution within the provided project and should not aim to create a new project from scratch.

Within the project there is an example REST service for creating and storing contacts which can be accessed via the Swagger UI endpoint (http://localhost:8080/q/swagger-ui). It is encouraged that students spend spend time reading through this code to gain a strong understanding of how the project works. Not only this, but students are also encouraged to follow a similar packaging structure.

Students are not required to remove the contact service and can leave this functionality in their submission.

Throughout the coursework specification there are many links to various guides to help you complete the coursework. It is strongly encouraged that you spend time working through these guides before attempting to implement the specification requirements.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Interactive error, validation, and security-header demos

Start the application in dev mode and open [http://localhost:8080/](http://localhost:8080/). Enter the configured API key (`API_KEY`, or `change-me` when using the default local configuration) in the API key field. The key is sent only with requests from the page and is not saved.

- **Error Handling:** Use **Trigger unexpected HTTP 500** to call the authenticated `/demo/errors/unexpected` demonstration endpoint. Its deliberately unexpected exception is handled by the global exception mapper, which returns a sanitized JSON error. Use **Trigger HTTP 404** to request a contact ID that does not exist and inspect the preserved 404 status and response fields.
- **Input Validation:** Submit the contact form with blank or overlong names, an invalid email or phone number, or a future birth date to see backend-generated field errors. Enter valid contact details and a past birth date to create a contact and see the success response. The form disables browser-side validation so requests reach Jakarta Bean Validation on the server. Submitting a duplicate email returns the API's conflict response.
- **Security Headers:** Select **Refresh header inspection** to make a new request to `GET /contacts`. The table compares the received `X-Content-Type-Options`, `X-Frame-Options`, `X-XSS-Protection`, and `Strict-Transport-Security` values with the configured values. If a browser does not expose one of the values to JavaScript, inspect the request's Response Headers in browser developer tools.

Run the focused backend coverage for these demos with:

```shell
./mvnw -Dtest=ErrorDemoRestServiceTest test
```
