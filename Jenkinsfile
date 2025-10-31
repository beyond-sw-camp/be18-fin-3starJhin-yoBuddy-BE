pipeline {
    agent any

    environment {
        SPRING_PROFILES_ACTIVE = "default"
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
                sh './gradlew clean build -x test'
            }
        }

        stage('배포') {
            steps {
                echo 'Docker Compose로 애플리케이션을 배포합니다...'
                sh '''
                docker-compose down --remove-orphans || true
                docker-compose up -d --build
                '''
            }
        }

        stage('헬스 체크') {
            steps {
                echo '애플리케이션 상태를 점검합니다...'
                sh '''
                echo "서버가 시작될 때까지 20초 대기..."
                sleep 20
                if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
                    echo "✅ 헬스 체크 성공"
                else
                    echo "❌ 헬스 체크 실패"
                    exit 1
                fi
                '''
            }
        }
    }

    post {
        success {
            withCredentials([string(credentialsId: 'DISCORD_WEBHOOK', variable: 'WEBHOOK_URL')]) {
                sh '''
                curl -H "Content-Type: application/json" \
                     -d "{\"content\": \"✅ YoBuddy 서버 배포 성공 🎉\\n상태: 정상 완료\\n시간: $(date '+%Y-%m-%d %H:%M:%S')\"}" \
                     $WEBHOOK_URL
                '''
            }
        }
        failure {
            withCredentials([string(credentialsId: 'DISCORD_WEBHOOK', variable: 'WEBHOOK_URL')]) {
                sh '''
                curl -H "Content-Type: application/json" \
                     -d "{\"content\": \"❌ YoBuddy 서버 배포 실패 ⚠️\\n상태: 오류 발생\\n시간: $(date '+%Y-%m-%d %H:%M:%S')\\n확인: Jenkins 로그 참고\"}" \
                     $WEBHOOK_URL
                '''
            }
        }
    }
}
