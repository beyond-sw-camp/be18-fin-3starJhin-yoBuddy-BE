pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-org/Jhin-yoBuddy-BE.git'
            }
        }

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