pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                bat 'gradlew clean build -x test'
            }
        }

        stage('Deploy') {
            steps {
                bat 'docker-compose down || exit 0'
                bat 'docker-compose up -d --build'
            }
        }
    }
}