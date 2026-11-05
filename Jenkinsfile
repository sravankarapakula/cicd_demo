pipeline {
    agent any

    environment {
        EC2_HOST = '15.252.243.184'
        DOCKER_IMAGE = 'sravankarapakula/cicd-demo'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Gitleaks Security Scan') {
            steps {
                bat 'gitleaks detect --source . --redact --exit-code 1'
            }
        }

        stage('Maven Build and Test') {
            steps {
                bat 'mvn -B clean package'
            }
        }

        stage('Archive Build') {
            steps {
                archiveArtifacts artifacts: 'target/cicd-demo.jar',
                                 fingerprint: true
            }
        }

        stage('Prepare AWS Deployment Files') {
            steps {
                bat '''
                    if exist deploy rmdir /s /q deploy

                    mkdir deploy
                    mkdir deploy\\target

                    copy /Y target\\cicd-demo.jar deploy\\target\\cicd-demo.jar
                    copy /Y Dockerfile deploy\\Dockerfile

                    xcopy /E /I /Y k8s deploy\\k8s
                '''
            }
        }

        
        stage('Transfer Files to AWS') {
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'aws-ec2-key',
                        keyFileVariable: 'SSH_KEY'
                    ),
                    usernamePassword(
                        credentialsId: 'dockerhub-token',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_TOKEN'
                    )
                ]) {
                    bat '''
                        @echo off

                        echo %DOCKER_TOKEN%> deploy\\dockerhub-token.txt

                        ssh -i "%SSH_KEY%" ubuntu@%EC2_HOST% "mkdir -p /home/ubuntu/app/target /home/ubuntu/app/k8s && rm -f /home/ubuntu/app/target/*.jar"

                        if errorlevel 1 exit /b 1

                        scp -i "%SSH_KEY%" deploy\\target\\cicd-demo.jar ubuntu@%EC2_HOST%:/home/ubuntu/app/target/

                        if errorlevel 1 exit /b 1

                        scp -i "%SSH_KEY%" deploy\\Dockerfile ubuntu@%EC2_HOST%:/home/ubuntu/app/

                        if errorlevel 1 exit /b 1

                        scp -i "%SSH_KEY%" -r deploy\\k8s\\* ubuntu@%EC2_HOST%:/home/ubuntu/app/k8s/

                        if errorlevel 1 exit /b 1

                        scp -i "%SSH_KEY%" deploy\\dockerhub-token.txt ubuntu@%EC2_HOST%:/tmp/dockerhub-token.txt

                        if errorlevel 1 exit /b 1

                        del deploy\\dockerhub-token.txt
                    '''
                }
            }
        }


        stage('Build and Push Docker Image on AWS') {
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'aws-ec2-key',
                        keyFileVariable: 'SSH_KEY'
                    ),
                    usernamePassword(
                        credentialsId: 'dockerhub-token',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_TOKEN'
                    )
                ]) {
                    bat '''
                        @echo off

                        ssh -i "%SSH_KEY%" ubuntu@%EC2_HOST% "chmod 600 /tmp/dockerhub-token.txt && sudo docker login -u %DOCKER_USER% --password-stdin < /tmp/dockerhub-token.txt && sudo docker build -t %DOCKER_IMAGE%:%BUILD_NUMBER% /home/ubuntu/app && sudo docker push %DOCKER_IMAGE%:%BUILD_NUMBER% && sudo docker tag %DOCKER_IMAGE%:%BUILD_NUMBER% %DOCKER_IMAGE%:latest && sudo docker push %DOCKER_IMAGE%:latest && sudo docker logout && rm -f /tmp/dockerhub-token.txt"
                    '''
                }
            }
        }

        
        stage('Deploy to Kubernetes on AWS') {
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'aws-ec2-key',
                        keyFileVariable: 'SSH_KEY'
                    )
                ]) {
                    bat '''
                        @echo off

                        ssh -i "%SSH_KEY%" ubuntu@%EC2_HOST% "sed -i 's|^ *image:.*|          image: %DOCKER_IMAGE%:%BUILD_NUMBER%|' /home/ubuntu/app/k8s/deployment.yaml && sudo k3s kubectl apply -f /home/ubuntu/app/k8s/ && sudo k3s kubectl rollout status deployment/cicd-demo --timeout=180s"
                    '''
                }
            }
        }


        stage('Verify Deployment') {
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'aws-ec2-key',
                        keyFileVariable: 'SSH_KEY'
                    )
                ]) {
                    bat '''
                        @echo off

                        ssh -i "%SSH_KEY%" ubuntu@%EC2_HOST% "sudo k3s kubectl get deployments && sudo k3s kubectl get pods -o wide && sudo k3s kubectl get services"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'CI/CD pipeline completed successfully.'
        }

        failure {
            echo 'CI/CD pipeline failed. Check the failed stage and console output.'
        }
    }
}