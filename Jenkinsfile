pipeline {
    agent any

    stages {
        stage('Test Jenkins') {
            steps {
                sh 'echo "Jenkinsfile GitHub reposundan calisiyor"'
                sh 'git --version'
                sh 'docker version'
            }
        }
    }
}