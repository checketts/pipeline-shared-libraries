#!/usr/bin/groovy
def call(body) {
  // evaluate the body block, and collect configuration into the object
  def config = [:]
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
    sh "./gradlew $gradleTasks --refresh-dependencies"

    stage 'Gradle Test'
    sh "./gradlew $gradleTasks"

    stage 'Gradle Publish'
    sh "./gradlew $gradleTasks"

    if(dockerTasks.toLowercase() != "none"){
      stage 'Docker Build'
      sh "./gradlew $dockerTasks"
    }
  }

}