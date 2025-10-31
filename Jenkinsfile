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
                sh '''
                chmod +x ./gradlew
                ./gradlew clean build -x test
                '''
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
                echo "서버 시작 대기중 (20초)..."
                sleep 20
                curl -f http://localhost:8080/actuator/health || exit 1
                '''
            }
        }
    }

    post {
        success {
            withCredentials([string(credentialsId: 'DISCORD_WEBHOOK', variable: 'WEBHOOK_URL')]) {
                sh '''
                cat <<EOF | curl -H "Content-Type: application/json" -d @- $WEBHOOK_URL
                {
                  "content": "✅ YoBuddy 서버 배포 성공 🎉\\n상태: 정상 완료\\n시간: $(date '+%Y-%m-%d %H:%M:%S')"
                }
                EOF
                '''
            }
        }
        failure {
            withCredentials([string(credentialsId: 'DISCORD_WEBHOOK', variable: 'WEBHOOK_URL')]) {
                sh '''
                cat <<EOF | curl -H "Content-Type: application/json" -d @- $WEBHOOK_URL
                {
                  "content": "❌ YoBuddy 서버 배포 실패 ⚠️\\n상태: 오류 발생\\n시간: $(date '+%Y-%m-%d %H:%M:%S')\\n확인: Jenkins 로그 참고"
                }
                EOF
                '''
            }
        }
    }
}
