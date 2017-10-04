# Service Broker Dashboard

![alt text](https://github.com/evoila/cf-service-broker-dashboard/blob/master/docs/dashboard-ui.png "Dashboard UI")

## General
To manage your Service Broker instance it is mandatory to have a Dashboard component, which supplies you with capabilites like basic Monitoring, Configuration Management and Backup/Restore.

This project is intended to be the fundamental implementation of Dashboard, which could be applied to any Service Broker. We provide a sample implementation of the Dashboard in the `Example Service Broker`, which can be found here: https://github.com/evoila/cf-service-broker-example

## How does the Dashboard work?
The Dashboard is an Angular JS 4.X Application which is provided with the Service Broker itself. 
A Dashboard of a Service Instance can be accessed, when the Service Broker backend has implemented the following Auth-Flow:

[Dashboard Single Sign-On](https://docs.cloudfoundry.org/services/dashboard-sso.html)

# Quickstart

## Requirements
1. Install Node.js [Node.js](https://nodejs.org/en/download/package-manager/)
2. Install yarn [Yarn](https://yarnpkg.com/lang/en/docs/install/#alternatives-tab)

## Use and start project
1. Run the project: `yarn run start:service-broker` e.g. `yarn run start:auto-scaler`
2. Build the project `yarn run build:service-broker` e.g. `yarn run build:auto-scaler`

# Technical Components

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 1.0.0-rc.1.

## Development server
Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive/pipe/service/class/module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `-prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).
Before running the tests make sure you are serving the app via `ng serve`.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
