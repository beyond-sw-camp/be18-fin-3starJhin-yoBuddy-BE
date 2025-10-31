pipeline {
    agent any
    environment {
        SPRING_PROFILES_ACTIVE = "default"
        DISCORD_WEBHOOK = credentials('DISCORD_WEBHOOK')
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
                bat 'gradlew clean build -x test'
            }
        }

        stage('배포') {
            steps {
                echo 'Docker Compose로 애플리케이션을 배포합니다...'
                bat 'docker-compose down --remove-orphans || exit 0'
                bat 'docker-compose up -d --build'
            }
        }

        stage('헬스 체크') {
            steps {
                echo '애플리케이션 상태를 점검합니다...'
                bat '''
                ping 127.0.0.1 -n 10 >nul
                curl -f http://192.168.0.111:8080/actuator/health >nul 2>&1 || (
                    echo 애플리케이션 헬스 체크 실패.
                    exit 1
                )
                '''
            }
        }
    }

    post {
            success {
                withCredentials([string(credentialsId: 'DISCORD_WEBHOOK', variable: 'WEBHOOK_URL')]) {
                    powershell '''
                    $ErrorActionPreference = "Stop"
                    try {
                        $payload = @{
                            content = "✅ YoBuddy 서버 배포 성공 🎉`n프로젝트: YoBuddy`n상태: 정상 완료`n시간: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
                        } | ConvertTo-Json

                        Invoke-RestMethod -Uri "$env:WEBHOOK_URL" -Method Post -Body $payload -ContentType "application/json"
                        Write-Host "✅ 디스코드 알림 전송 성공"
                    }
                    catch {
                        Write-Host "❌ 디스코드 알림 전송 실패: $($_.Exception.Message)"
                    }
                    '''
                }
            }

            failure {
                withCredentials([string(credentialsId: 'DISCORD_WEBHOOK', variable: 'WEBHOOK_URL')]) {
                    powershell '''
                    $ErrorActionPreference = "Stop"
                    try {
                        $payload = @{
                            content = "❌ YoBuddy 서버 배포 실패 ⚠️`n프로젝트: YoBuddy`n상태: 오류 발생`n시간: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')`n확인: Jenkins 로그 참고"
                        } | ConvertTo-Json

                        Invoke-RestMethod -Uri "$env:WEBHOOK_URL" -Method Post -Body $payload -ContentType "application/json"
                        Write-Host "✅ 디스코드 알림 전송 성공"
                    }
                    catch {
                        Write-Host "❌ 디스코드 알림 전송 실패: $($_.Exception.Message)"
                    }
                    '''
                }
            }
        }
    }
}
