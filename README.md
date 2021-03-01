# lunatech-beginner-quarkus-course

Lunatech's beginner Quarkus training course aimed at developers who may or may not already be familiar with other frameworks.

## Repository Structure

### `slides`

The `slides` directory contain the reveal.js slides with the course material. Run it with:

    npm install
    npm run build
    npm start

and then browse to `http://localhost:8000/`

### `slides-code-samples-app`

This is a Quarkus app that contains the code that's used in the slides. So we can make sure it works :)

### `student-app`

This contains the 'finished' student app; the HIQUEA catalogue app.

### `student-app-frontend`

This is a React app that contains a frontend for the JSON version of the student app. Run it with:

    npm install
    npm start

Then go to http://localhost:3000/

It's configured to connect to the Quarkus app running on http://localhost:8080/

## Info about the course

Currently maintained in a Google Document: https://docs.google.com/document/u/0/?tgif=c
