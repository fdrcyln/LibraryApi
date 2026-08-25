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

        stage('Push Docker Images') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_TOKEN'
                    )
                ]) {
                    sh '''
                        echo "$DOCKER_TOKEN" | docker login \
                        -u "$DOCKER_USER" \
                        --password-stdin

                        docker tag library-api:${BUILD_NUMBER} \
                        $DOCKER_USER/library-api:${BUILD_NUMBER}

                        docker tag library-frontend:${BUILD_NUMBER} \
                        $DOCKER_USER/library-frontend:${BUILD_NUMBER}

                        docker push $DOCKER_USER/library-api:${BUILD_NUMBER}
                        docker push $DOCKER_USER/library-frontend:${BUILD_NUMBER}
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'library-deploy-ssh',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    )
                ]) {
                    sh '''
                        ssh -i "$SSH_KEY" \
                        -p 2222 \
                        "$SSH_USER"@host.docker.internal \
                        "cd ~/library-deploy && \
                        IMAGE_TAG=${BUILD_NUMBER} docker compose pull && \
                        IMAGE_TAG=${BUILD_NUMBER} docker compose up -d"
                    '''
                }
            }
        }
    }

    post {
        failure {
            emailext(
                to: 'furkandurceylan@gmail.com',
                subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                Jenkins pipeline failed.

                Job: ${env.JOB_NAME}
                Build: #${env.BUILD_NUMBER}

                Console:
                ${env.BUILD_URL}console
            """
        )
    }
    }
}