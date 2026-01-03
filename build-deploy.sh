#!/bin/bash

# Build and Deploy TallyBackend to AWS EC2

set -e

echo "🔨 Building TallyBackend..."

# Build the application
mvn clean package -DskipTests

echo "📦 Creating deployment package..."

# Create deployment directory
mkdir -p deploy
cp target/tallybackend-*.jar deploy/tallybackend.jar
cp Dockerfile deploy/
cp docker-compose.yml deploy/
cp deploy-ec2.sh deploy/
cp -r sql deploy/

# Create deployment instructions
cat > deploy/README.md << EOF
# TallyBackend AWS EC2 Deployment

## Option 1: Docker Deployment (Recommended)

1. Install Docker and Docker Compose on EC2:
   \`\`\`bash
   sudo yum update -y
   sudo yum install -y docker
   sudo systemctl start docker
   sudo systemctl enable docker
   sudo usermod -a -G docker ec2-user
   
   # Install Docker Compose
   sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-\$(uname -s)-\$(uname -m)" -o /usr/local/bin/docker-compose
   sudo chmod +x /usr/local/bin/docker-compose
   \`\`\`

2. Upload deployment files to EC2
3. Run: \`docker-compose up -d\`

## Option 2: Native Deployment

1. Run: \`chmod +x deploy-ec2.sh\`
2. Run: \`./deploy-ec2.sh\`
3. Upload tallybackend.jar to /opt/tallybackend/
4. Start service: \`sudo systemctl start tallybackend\`

## Security Group Settings

Open these ports in your EC2 Security Group:
- Port 8080 (HTTP) - for application access
- Port 22 (SSH) - for server access
- Port 5432 (PostgreSQL) - only if external DB access needed

## Environment Variables

Update the .env file or docker-compose.yml with:
- AWS credentials for SES/SNS
- JWT secret key
- Domain name for CORS

## Health Check

Application will be available at: http://your-ec2-public-ip:8080
Health check: http://your-ec2-public-ip:8080/actuator/health
EOF

echo "✅ Deployment package created in 'deploy' directory"
echo "📋 Next steps:"
echo "1. Upload 'deploy' directory to your EC2 instance"
echo "2. Follow instructions in deploy/README.md"
echo "3. Configure Security Group to allow port 8080"