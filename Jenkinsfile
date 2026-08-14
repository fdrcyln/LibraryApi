pipeline {
    agent any

    stages {
        stage('Start Test Database') {
            steps {
            sh '''
                docker rm -f library-ci-db 2>/dev/null || true

                docker run -d \
                --name library-ci-db \
              -e POSTGRES_DB=postgres \
              -e POSTGRES_USER=postgres \
              -e POSTGRES_PASSWORD=admin \
              -p 5433:5432 \
              postgres:15-alpine
        '''
            }
        }

        stage('Backend Build') {
            steps {
                dir('LibApi') {
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Frontend Build') {
            steps {
                dir('library-ui') {
                    sh '''
                        docker run --rm \
                          -v "$(pwd):/app" \
                          -w /app \
                          node:22-alpine \
                          sh -c "npm ci && npm run build"
                    '''
                }
            }
        }
    }
}