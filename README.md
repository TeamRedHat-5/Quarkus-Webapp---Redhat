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

## Health, JSON serialization, and entity audit demos

Start the application in dev mode and open [http://localhost:8080/](http://localhost:8080/). Enter the configured API key (`API_KEY`, or `change-me` with the default local configuration); the page sends it with health and API requests and does not save it.

- **System Health:** Select **Refresh health checks** to request `/q/health/live` and `/q/health/ready`. The page shows each probe's actual JSON response and check time. A valid `DOWN` probe result is shown separately from authentication failures, network errors, and unexpected HTTP responses.
- **JSON Inspector:** Select **Fetch contacts JSON** to retrieve and pretty-print the live `GET /contacts` response. The diagnostics report date/time fields and any null-valued properties that the server actually returned. To exercise null omission, create a contact with the optional State field blank using the existing form, then refresh the inspector and confirm its response has no `state: null`. To exercise unknown-property handling, select **Include an unknown JSON property** on the form and submit valid contact details; the successful response should contain the Contact fields but not `uiUnknownProperty`.
- **Audit Information:** Select **Load contacts**, choose a returned contact, and select **Show audit information**. The creation and modification values are read from `GET /contacts/{id}`; each displays a localized time and the original backend ISO-8601 value. To verify modification auditing, select **Edit this contact**, change a valid field, and submit the form. This sends the existing `PUT /contacts/{id}` request, then reloads the saved contact so its persisted `updatedAt` value can be inspected. `createdAt` should remain unchanged.

All demonstrations use the running application's real endpoints and response bodies; the UI does not generate health results, entity audit timestamps, or JSON examples.

## Optimistic locking, API authentication, and pagination demos

Use the same local dashboard and enter only a development API key. Browser-entered keys are sent in the `X-API-KEY` request header and are not persisted by the page, but cannot be kept confidential from the browser user; never enter a production secret.

- **Record Updates:** Load contacts in **Audit Information** and select one in **Record Updates**. Choose **Load two independent views**, change the first name in View A, and save it. Change the first name in View B and save it using its older displayed backend version; the backend should reject the stale request with HTTP 409. The conflict response is shown, and neither editor is overwritten. Use **Reload latest record** to inspect server state without changing either view, or load both views again.
- **API Authentication:** Use **Request without API key**, **Request with invalid API key**, and **Request with configured API key**. The first two should display HTTP 401 and the backend's structured JSON error. The configured-key probe should return the protected contacts resource on success. The entered key is never included in the displayed response.
- **Paginated Records:** Select **Load flights** to request `/flight/findAllFlightsPaged`. The page displays backend `pageIndex`, `pageSize`, `totalElements`, and `totalPages` metadata with the returned flights. Use Previous/Next or change page size; each action sends a new request. Empty data and request failures are reported separately.
