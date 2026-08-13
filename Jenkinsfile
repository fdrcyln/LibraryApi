pipeline {
    agent any

    stages {
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