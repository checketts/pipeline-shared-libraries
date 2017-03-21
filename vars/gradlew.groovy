#!/usr/bin/groovy
def call(String tasks, String jdk = 'java-8-oracle') {
  def javaHome = tool(name: jdk)

  /* Set up environment variables for re-using our auto-installed tools */
  def customEnv = [
    "PATH+JDK=${javaHome}/bin",
    "JAVA_HOME=${javaHome}",
  ]

  withEnv(customEnv) {
    sh "./gradlew $tasks"
  }
}
