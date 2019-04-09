pipeline {
    agent {
        node {
            label 'docker'
        }
    }
    stages {
        stage('Build') {
            agent {
                docker {
                    image 'gradle:jdk8-alpine'
                    args '-v $HOME/.gradle:/home/gradle/.gradle'
                }
            }
            steps {
                sh 'gradle clean build -x test'
            }
        }
        stage('Test') {
            parallel {
                stage('Gradle Test') {
                    agent {
                        docker {
                            image 'gradle:jdk8-alpine'
                            args '-v $HOME/.gradle:/home/gradle/.gradle'
                        }
                    }
                    steps {
                        sh 'gradle test'
                    }
                    post {
                        always {
                            junit 'build/test-results/test/*.xml'
                            publishHTML target: [
                                allowMissing: false,
                                alwaysLinkToLastBuild: false,
                                keepAll: true,
                                reportDir: 'build/reports/tests/test',
                                reportFiles: 'index.html',
                                reportName: 'Gradle Report'
                            ]
                        }
                    }
                }
            }
        }
    }
}