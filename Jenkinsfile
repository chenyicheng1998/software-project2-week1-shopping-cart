pipeline {
    agent any

    environment {
        // Docker Hub credentials stored in Jenkins credentials manager
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        IMAGE_NAME = "YOUR_DOCKERHUB_USERNAME/shopping-cart"
        IMAGE_TAG  = "latest"
    }

    stages {

        stage('Checkout') {
            steps {
                // Clone source code from GitHub
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // Compile the Java source code
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                // Run unit tests with JaCoCo coverage
                sh 'mvn test'
            }
            post {
                always {
                    // Publish JUnit test results
                    junit 'target/surefire-reports/*.xml'
                    // Publish JaCoCo coverage report
                    jacoco execPattern: 'target/jacoco.exec'
                }
            }
        }

        stage('Package') {
            steps {
                // Package the application into a JAR file
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Build Docker image from Dockerfile
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Push to Docker Hub') {
            steps {
                // Login to Docker Hub and push the image
                sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"
                sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }
    }

    post {
        always {
            // Always logout from Docker Hub after pipeline finishes
            sh 'docker logout'
        }
    }
}