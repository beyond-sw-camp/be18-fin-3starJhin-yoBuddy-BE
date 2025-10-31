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

        stage('docker-compose 설치 확인') {
            steps {
                echo 'docker-compose 설치 여부를 확인합니다...'
                sh '''
                if ! command -v docker-compose &> /dev/null; then
                    echo "docker-compose가 설치되어 있지 않습니다. 설치를 진행합니다..."
                    apt-get update -y
                    apt-get install -y curl jq
                    curl -SL https://github.com/docker/compose/releases/download/v2.29.2/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose
                    chmod +x /usr/local/bin/docker-compose
                    echo "docker-compose 설치 완료"
                else
                    echo "docker-compose가 이미 설치되어 있습니다"
                fi
                docker-compose version
                '''
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
                NOW=$(date "+%Y-%m-%d %H:%M:%S")
                JSON="{\\"content\\": \\"✅ YoBuddy 서버 배포 성공 🎉\\\\n상태: 정상 완료\\\\n시간: $NOW\\"}"
                curl -H "Content-Type: application/json" -d "$JSON" $WEBHOOK_URL
                '''
            }
        }
        failure {
            withCredentials([string(credentialsId: 'DISCORD_WEBHOOK', variable: 'WEBHOOK_URL')]) {
                sh '''
                NOW=$(date "+%Y-%m-%d %H:%M:%S")
                JSON="{\\"content\\": \\"❌ YoBuddy 서버 배포 실패 ⚠️\\\\n상태: 오류 발생\\\\n시간: $NOW\\\\n확인: Jenkins 로그 참고\\"}"
                curl -H "Content-Type: application/json" -d "$JSON" $WEBHOOK_URL
                '''
            }
        }
    }
}
