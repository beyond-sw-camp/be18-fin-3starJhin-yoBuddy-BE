pipeline {
    agent any

    environment {
        SPRING_PROFILES_ACTIVE = "default"
        DISCORD_WEBHOOK = "https://discord.com/api/webhooks/1428195972617605312/V160D67nwFsb-mQnyawkpQ1arTLCvzTUBYs3SiHbKczq02vkzHr5ZHhdVQ47dQXvMk_O"
    }

    options {
        timestamps()
        skipDefaultCheckout()
    }

    triggers {
        githubPush()
    }

    stages {
        stage('소스 코드 가져오기') {
            steps {
                echo '소스 코드를 가져오는 중입니다...'
                git branch: 'main', url: 'https://github.com/beyond-sw-camp/be18-fin-3starJhin-yoBuddy-BE.git'
            }
        }

        stage('프로젝트 빌드') {
            steps {
                echo 'Gradle로 프로젝트를 빌드합니다...'
                bat '''
                if exist build (
                    echo 기존 빌드 캐시를 재사용합니다.
                ) else (
                    echo 전체 빌드를 수행합니다.
                )
                gradlew clean build -x test
                '''
            }
        }

        stage('배포') {
            steps {
                echo 'Docker Compose로 애플리케이션을 배포합니다...'
                bat '''
                docker-compose down --remove-orphans || exit 0
                docker-compose up -d --build
                '''
            }
        }

        stage('헬스 체크') {
            steps {
                echo '애플리케이션 상태를 점검합니다...'
                bat '''
                timeout 30 curl -f http://localhost:8080/actuator/health || (
                    echo 애플리케이션 헬스 체크 실패.
                    exit 1
                )
                '''
            }
        }
    }

    post {
        success {
            echo '빌드 및 배포가 정상적으로 완료되었습니다.'
            script {
                def message = """
                ✅ **YoBuddy 서버 배포 성공**
                - 프로젝트: YoBuddy
                - 상태: 정상 완료
                - 시간: ${new Date().format("yyyy-MM-dd HH:mm:ss")}
                """
                bat """curl -H "Content-Type: application/json" -X POST -d "{\\"content\\": \\"${message.replace("\"", "\\\\\"")}\\"}" %DISCORD_WEBHOOK%"""
            }
        }

        failure {
            echo '빌드 또는 배포 과정에서 오류가 발생했습니다.'
            script {
                def message = """
                ❌ **YoBuddy 서버 배포 실패**
                - 프로젝트: YoBuddy
                - 상태: 오류 발생
                - 시간: ${new Date().format("yyyy-MM-dd HH:mm:ss")}
                - 확인: Jenkins 로그 참고
                """
                bat """curl -H "Content-Type: application/json" -X POST -d "{\\"content\\": \\"${message.replace("\"", "\\\\\"")}\\"}" %DISCORD_WEBHOOK%"""
            }
        }
    }
}
