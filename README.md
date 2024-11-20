## Maven build
mvn -T 1C --batch-mode clean install -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -B

## Open api 3.0
https://springdoc.org/