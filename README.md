# laa-dces-report-service

This is a Java based Spring Boot application hosted on [MOJ Cloud Platform](https://user-guide.cloud-platform.service.justice.gov.uk/documentation/concepts/about-the-cloud-platform.html).

[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

### Decrypting docker-compose.override.yml

The `docker-compose.override.yml` is encrypted using [git-crypt](https://github.com/AGWA/git-crypt).

To run the app locally you need to be able to decrypt this file.

You will first need to create a GPG key. See [Create a GPG Key](https://docs.publishing.service.gov.uk/manual/create-a-gpg-key.html) for details on how to do this with `GPGTools` (GUI) or `gpg` (command line).
You can install either from a terminal or just download the UI version.

```
brew update
brew install gpg
brew install git-crypt
```

Once you have done this, a team member who already has access can add your key by running `git-crypt add-gpg-user USER_ID`\* and creating a pull request to this repo.

Once this has been merged you can decrypt your local copy of the repository by running `git-crypt unlock`.

\*`USER_ID` can be your key ID, a full fingerprint, an email address, or anything else that uniquely identifies a public key to GPG (see "HOW TO SPECIFY A USER ID" in the gpg man page).
The apps should then startup cleanly if you run

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

```sh
docker-compose build
docker-compose up
```

laa-dces-report-service application will be running on http://localhost:8089

### How to generate reports on demand

To run reports manually, it is required to be able to access the pods from local machine. If you need help read section #How-to-access-the-pods for help.

If it is required to trigger the report manually follow these simple instructions:

1. get access to the container where the app is running
2. Use the command to launch the corresponding report
3. Replace <<reportTitle>> with the desired report title, e.g. Monthly, Daily, AdHoc etc.
4. Make sure to specify the start and end date in the correct format (dd.MM.yyyy)

There are 2 ways of doing this, you can either use the script provided for ease or type the full CURL command.

Check the following examples:

#### For contributions:

```sh
./contributionsReportAdHoc.sh <<reportTitle>> 01.01.2021 26.01.2021
```

or

```shell
curl -G localhost:8089/api/internal/v1/dces/report/contributions/<<reportTitle>>/01.01.2021/26.01.2021
```

#### For FDCs:

```sh
./fdcReportAdHoc.sh <<reportTitle>> 01.01.2021 26.01.2021
```

or

```sh
curl -G localhost:8089/api/internal/v1/dces/report/fdc/<<reportTitle>>/01.01.2021/26.01.2021
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
- laa-dces-report-service-staging
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
