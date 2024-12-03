pipeline {
    agent any

    environment {
        REPO_URL = 'github.com/Aloui-GIT/UserFront.git'
        REPO_URLL = 'github.com/Aloui-GIT/Back.git'
        GIT_BRANCH = 'main'
        GIT_BRANCHH = 'backed'
        REPO_DIR = 'UserFront'
        REPO_DIRR = 'Back'
        DOCKER_CREDENTIALS = 'dockerhubtoken'
        DOCKER_IMAGE_FRONT = 'userfront'
        DOCKER_IMAGE_BACK = 'new-back-image'
        DOCKERHUB_USERNAME = 'alouiyassine'
        DB_CONTAINER = 'mysql-container'
        DB_IMAGE = 'mysql:8.0'
        DB_PORT = '3306'
        DB_HOST = '192.168.200.136'
        DB_NAME = 'Projet1'
        DB_USER = 'root'
        DB_PASSWORD = 'root'
    }

    stages {
        stage('Clone Repositories from Git') {
            steps {
                script {
                    sh 'git config --global http.postBuffer 524288000'
                    sh 'git config --global http.lowSpeedLimit 0'
                    sh 'git config --global http.lowSpeedTime 999999'

                    retry(3) {
                        if (fileExists(REPO_DIR)) {
                            dir(REPO_DIR) {
                                sh 'git pull origin ${GIT_BRANCH} --rebase'
                            }
                        } else {
                            withCredentials([string(credentialsId: 'githubtoken', variable: 'GIT_TOKEN')]) {
                                sh "git clone --branch ${GIT_BRANCH} https://${GIT_TOKEN}@${REPO_URL} ${REPO_DIR}"
                            }
                        }

                        sh "rm -rf ${REPO_DIRR}"
                        withCredentials([string(credentialsId: 'githubtoken', variable: 'GIT_TOKEN')]) {
                            sh "git clone --branch ${GIT_BRANCHH} https://${GIT_TOKEN}@${REPO_URLL} ${REPO_DIRR}"
                        }
                    }
                }
            }
        }

        stage('Build Project') {
            steps {
                dir(REPO_DIR) {
                    script {
                        sh 'npm install'
                        sh 'npm run build'
                    }
                }
            }
        }

        stage('Build Frontend Docker Image') {
            steps {
                dir(REPO_DIR) {
                    script {
                        sh 'docker build -t ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE_FRONT} .'
                    }
                }
            }
        }

        /*stage('Push Frontend Docker Image to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhubtoken', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                        sh 'docker push ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE_FRONT}'
                    }
                }
            }
        }*/

        stage('Build Backend Docker Image') {
            steps {
                script {
                    sh 'ls -l target/GenerateurForlulaire-0.0.1.jar'
                    sh 'docker build --no-cache -t ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE_BACK} .'
                }
            }
        }

        stage('Push Docker Images to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhubtoken', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                        sh 'docker push ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE_BACK}'
                    }
                }
            }
        }

        stage('Scan Docker Image with Trivy') {
    steps {
        script {
            // Create cache directory
            sh 'mkdir -p trivy-cache'
            
            // Run Trivy scan for the frontend image with extended timeout and cache
            sh """
                trivy image \
                --cache-dir trivy-cache \
                --timeout 5m \
                --severity HIGH,CRITICAL \
                --format json \
                --output frontend_scan_report.json \
                ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE_FRONT}
            """
        }
    }
}

/*stage('Scan Backend Docker Image with Trivy') {
    steps {
        script {
            // Create cache directory
            sh 'mkdir -p trivy-cache'

            // Run Trivy scan for the backend image with extended timeout and cache
            sh """
                trivy image \
                --cache-dir trivy-cache \
                --timeout 5m \
                --severity HIGH,CRITICAL \
                --format json \
                --output backend_scan_report.json \
                ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE_BACK}
            """
        }
    }
}*/

        stage('Run Docker Compose') {
            steps {
                script {
                    checkout scmGit(
                        branches: [[name: '*/main']],
                        extensions: [],
                        userRemoteConfigs: [[credentialsId: 'dockerhubtoken', url: 'https://github.com/Aloui-GIT/Back.git']]
                    )
                    sh 'docker-compose up -d'
                }
            }
        }

        stage('Monitoring') {
            steps {
                script {
                    checkout scmGit(
                        branches: [[name: '*/main']],
                        extensions: [],
                        userRemoteConfigs: [[credentialsId: 'dockerhubtoken', url: 'https://github.com/Aloui-GIT/Back.git']]
                    )
                    sh 'docker-compose -f docker-composeM.yml up -d'
                }
            }
        }
    }

    post {
        always {
            // Archive Trivy scan reports
            archiveArtifacts artifacts: 'frontend_scan_report.json, backend_scan_report.json', allowEmptyArchive: true

            // Echo the report files location
            echo 'Trivy scan reports saved:'
            echo '- frontend_scan_report.json'
            echo '- backend_scan_report.json'
        }
    }
}
