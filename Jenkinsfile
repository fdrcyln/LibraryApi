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
    }
}