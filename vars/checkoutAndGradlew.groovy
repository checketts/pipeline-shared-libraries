#!/usr/bin/groovy
def call(body) {
  // evaluate the body block, and collect configuration into the object
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  def jdk = config.jdk ?: 'java-8-oracle'

  def buildTasks = config.buildTasks ?: 'clean buildInfo assemble'
  def testTasks = config.testTasks ?: 'test'
  def publishTasks = config.publishTasks ?: 'publish'
  def dockerTasks = config.dockerTasks ?: 'dockerBuild dockerPush dockerClean'

  stage 'Clone sources'
  checkout scm

  def javaHome = tool(name: jdk)

  /* Set up environment variables for re-using our auto-installed tools */
  def customEnv = [
    "PATH+JDK=${javaHome}/bin",
    "JAVA_HOME=${javaHome}",
  ]

  withEnv(customEnv) {
    stage 'Gradle Build'
    sh "./gradlew $buildTasks --refresh-dependencies"

    if(!testTasks.toLowerCase().equals("none")){
      stage 'Gradle Test'
      sh "./gradlew $testTasks"
    }

    if(!publishTasks.toLowerCase().equals("none")){
      stage 'Gradle Publish'
      sh "./gradlew $publishTasks"
    }

    if(!dockerTasks.toLowerCase().equals("none")){
      stage 'Docker Build'
      sh "./gradlew $dockerTasks"
    }
  }
}
