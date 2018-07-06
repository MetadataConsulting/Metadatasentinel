#!/bin/bash
set -e

export EXIT_STATUS=0

./gradlew clean

./gradlew -Dgrails.env=test test || EXIT_STATUS=$?

if [[ $EXIT_STATUS -ne 0 ]]; then
    echo "Unit Tests Failed"
    exit $EXIT_STATUS
fi

./gradlew -Dgrails.env=test -DJDBC_DIALECT=org.hibernate.dialect.MySQL5InnoDBDialect -DJDBC_USERNAME=root -DJDBC_DRIVER=com.mysql.jdbc.Driver  -DJDBC_PASSWORD=root -DJDBC_CONNECTION_STRING=jdbc:mysql://127.0.0.1:8889/metadatasentinel_test -Dgeb.env=chrome -Ddownload.folder=/Users/sdelamo/Downloads iT || EXIT_STATUS=$?

exit $EXIT_STATUS
