# laa-dces-report-service

This is a Java based Spring Boot application hosted on [MOJ Cloud Platform](https://user-guide.cloud-platform.service.justice.gov.uk/documentation/concepts/about-the-cloud-platform.html).

[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

### Modifying docker-compose.override.yml

The `docker-compose.override.yml` and some other files used to be encrypted using `git-crypt`.
However, the use of `git-crypt` is now deprecated and has since been removed from this repository.

If you make local changes to `docker-compose.override.yml`, be sure not to commit them.
In fact, it may make sense to remove `docker-compose.override.yml` and add it to `.gitignore`.

### Application Set up

Clone Repository

```sh
git clone git@github.com:ministryofjustice/laa-dces-report-service.git

cd dces-report-service
```

Make sure all tests are passed by running following ‘gradle’ Command

```sh
./gradlew clean test
```

You will need to build the artifacts for the source code, using `gradle`.

```sh
./gradlew clean build
```

You will need to add spring datasource configuration in order to run the application. It requires a Postgresql database, the same as the one used by https://github.com/ministryofjustice/laa-dces-drc-integration, for populating certain reports.
Once you have added the datasource settings, you will be able to build and launch the application locally using docker.

```sh
docker-compose build
docker-compose up
```

laa-dces-report-service application will be running on http://localhost:8089

### How to generate reports on demand

To run reports manually, it is required to be able to access the pods from local machine. If you need help read section #How-to-access-the-pods for help.

To trigger the report manually follow these simple instructions:

1. get access to the container where the app is running
2. Use the command to launch the corresponding report (see examples below)
3. In the command, replace <<reportTitle>> with the desired report title, e.g. Monthly, Daily, AdHoc etc.
4. Make sure to specify the start and end date in the correct format (yyyy-MM-dd), both values are inclusive.

There are 2 ways of doing this, you can either use the script provided for ease or type the full CURL command.

Check the following examples:

#### For contributions:

```sh
./contributionsReportAdHoc.sh <<reportTitle>> 2021-01-01 2021-01-26
```

or

```shell
curl -G localhost:8089/api/internal/v1/dces/report/contributions/<<reportTitle>>/01.01.2021/26.01.2021
```

#### For FDCs:

```sh
./fdcReportAdHoc.sh <<reportTitle>> 2021-01-01 2021-26-01
```

or

```sh
curl -G localhost:8089/api/internal/v1/dces/report/fdc/<<reportTitle>>/2021-01-01
```

#### For failures:

```sh
./failuresReportAdHoc.sh <<reportTitle>> 2021-01-01
```

or

```sh
curl -G localhost:8089/api/internal/v1/dces/report/failures/<<reportTitle>>/2021-01-01
```

### How to access the pods:

In order to access the pods, it is required to have Kubernetes installed and configured in your local machine. If you need help, check these documents:

- [Java Project Setup - Accessing Clusters](https://dsdmoj.atlassian.net/wiki/spaces/ASLST/pages/3761963077/Java+Project+Setup+with+CircleCI+and+Helm+on+Cloud+Platform#Accessing-the-clusters)
- [Connecting to the Cloud Platform's Kubernetes cluster - Cloud Platform User Guide](https://user-guide.cloud-platform.service.justice.gov.uk/documentation/getting-started/kubectl-config.html#installing-kubectl)

Assuming Kubernetes is all setup, follow these steps to access the pods.

1. Use the following command from terminal:

```sh
kubectl get pods -n {nameSpace}
```

Possible values for `nameSpace` are:

- laa-dces-report-service-dev
- laa-dces-report-service-uat
- laa-dces-report-service-prod

Check response from command below, you will need that for the following step

```sh
kubectl get pods -n {nameSpace}
```

Output:

    NAME                                 READY   STATUS    RESTARTS   AGE
    {poddName}                           1/1     Running   0          18m

2. Access the pod console using the following command:

```sh
kubectl exec -it {podName} -n {nameSpace} -- sh
```

Example:

```sh
kubectl get pods -n laa-dces-report-service-readme
```

Output should be similar to this:

    NAME                                  READY   STATUS    RESTARTS   AGE
    laa-dces-report-service-00000-xxxxx   1/1     Running   0          18m

```shell
kubectl exec -it laa-dces-report-service-00000-xxxxx -n laa-dces-report-service-readme -- sh
```

That should give you access to the pods terminal.
