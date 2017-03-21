#!/usr/bin/groovy
def call(String resultsLocation = "**/test-results/*.xml") {
    step([$class: 'JUnitResultArchiver', testResults: resultsLocation])
}
