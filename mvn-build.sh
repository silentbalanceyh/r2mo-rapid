# export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home
mvn clean package install -Dquickly -DskipTests=true -Dmaven.javadoc.skip=true -Dmaven.compile.fork=true -T 16C