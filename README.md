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

If it is required to trigger the report manually follow these simple instructions:

1. get access to the container where the app is running
2. launch a CURL command to the corresponding endpoint
3. Make sure to specify the start and end date in the correct format (dd.MM.yyyy)

#### For contributions:

    `curl -G localhost:8089/api/internal/v1/dces/report/contributions/{startDate}/{endDate}`

Example

    `curl -G localhost:8089/api/internal/v1/dces/report/contributions/01.01.2021/26.01.2021`

#### For FDCs:

    `curl -G localhost:8089/api/internal/v1/dces/report/fdc/{startDate}/{endDate}`

Example

    `curl -G localhost:8089/api/internal/v1/dces/report/fdc/01.01.2021/26.01.2021`

Alternatively you can opt to use the simplified bash commands:

#### Alternative for contributions

    `contributionsReportAdHoc.sh {startDate} {endDate}`

Example: `./contributions.sh 01.01.2021 26.01.2021`

#### Alternative for FDCs

    `fdcReportAdHoc.sh {startDate} {endDate}`

Example: `./fdcReportAdHoc.sh 01.01.2021 26.01.2021`  

