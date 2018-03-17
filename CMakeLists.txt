cmake_minimum_required(VERSION 3.9)
project(WPS)

set(CMAKE_CXX_STANDARD 11)

add_executable(WPS
        src/main/java/no/pk/http/HttpHeader.java
        src/main/java/no/pk/http/Status.java
        src/main/java/no/pk/klient/DatagramCommunicator5000.java
        src/main/java/no/pk/klient/Main.java
        src/main/java/no/pk/krypter/Decrypt.java
        src/main/java/no/pk/krypter/Encrypt.java
        src/main/java/no/pk/shutdown/IShutdownThread.java
        src/main/java/no/pk/shutdown/ShutdownThread.java
        src/main/java/no/pk/webproxyserver/Main.java
        src/main/java/no/pk/webproxyserver/Test.java
        src/main/java/no/pk/webproxyserver/Test2.java
        src/main/java/no/pk/webproxyserver/WebProxyServer.java
        src/main/java/no/pk/webproxyserver/WebProxyUtil.java
        src/test/java/no/pk/UDPTest.java
        target/classes/no/pk/http/HttpHeader.class
        target/classes/no/pk/http/Status.class
        target/classes/no/pk/klient/DatagramCommunicator5000.class
        target/classes/no/pk/klient/Main.class
        target/classes/no/pk/krypter/Decrypt.class
        target/classes/no/pk/krypter/Encrypt.class
        target/classes/no/pk/shutdown/IShutdownThread.class
        target/classes/no/pk/shutdown/ShutdownThread.class
        target/classes/no/pk/webproxyserver/Main.class
        target/classes/no/pk/webproxyserver/Test.class
        target/classes/no/pk/webproxyserver/Test2.class
        target/classes/no/pk/webproxyserver/WebProxyServer.class
        target/classes/no/pk/webproxyserver/WebProxyUtil.class
        target/test-classes/no/pk/UDPTest.class
        pom.xml
        README.md
        WPS.iml)
