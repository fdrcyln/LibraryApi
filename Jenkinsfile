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

              until docker exec library-ci-db pg_isready -U postgres; do
                sleep 1
              done

              docker exec library-ci-db \
                psql -U postgres -d postgres \
                -c "CREATE SCHEMA IF NOT EXISTS library;"
        '''
            }
        }

        stage('Backend Test and Build') {
    steps {
        dir('LibApi') {
            withEnv([
                'SPRING_DATASOURCE_URL=jdbc:postgresql://docker:5433/postgres?currentSchema=library',
                'SPRING_DATASOURCE_USERNAME=postgres',
                'SPRING_DATASOURCE_PASSWORD=admin'
            ]) {
                sh './mvnw clean package'
            }
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

        stage('Build Docker Images') {
            steps {
                sh '''
                    docker build -t library-api:${BUILD_NUMBER} ./LibApi
                    docker build -t library-frontend:${BUILD_NUMBER} ./library-ui
                '''
            }
        }
    }
}