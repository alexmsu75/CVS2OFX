# CVS2OFX [![Build Status](https://travis-ci.org/alexmsu75/CVS2OFX.svg?branch=master)](https://travis-ci.org/alexmsu75/CVS2OFX)
CSV to OFX converter, mainly used to track transactions from Capital One Spark.

## Usage

### Get it
Checkout using your favorite Git client or maven

``$ mvn org.apache.maven.plugins:maven-scm-plugin:1.9.4:checkout -DcheckoutDirectory=csv-ofx-converter -DconnectionUrl=scm:git:https://github.com/alexmsu75/CVS2OFX.git``

### Build it

```
$ cd csv-ofx-converter
$ mvn package
```

### Run it

```
$ java -jar target/csv-ofx-converter-1.0.0-SNAPSHOT.jar --xlsFileLocation=C:\tmp\Transaction-History-36019963115.csv
```