#!/usr/bin/groovy
def call(body) {
  // evaluate the body block, and collect configuration into the object
  def config = [:]
  body.delegate = config
  body()

  echo "Hello World!"
}
