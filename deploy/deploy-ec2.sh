#!/bin/bash

# AWS EC2 Deployment Script for TallyBackend
# Run this script on your EC2 instance

set -e

echo "🚀 Starting TallyBackend deployment on AWS EC2..."

# Update system
sudo yum update -y

# Install Java 17
sudo yum install -y java-17-amazon-corretto-devel

# Install PostgreSQL
sudo yum install -y postgresql15-server postgresql15
sudo postgresql-setup --initdb
sudo systemctl enable postgresql
sudo systemctl start postgresql

# Configure PostgreSQL
sudo -u postgres psql -c "CREATE DATABASE tallybackend;"
sudo -u postgres psql -c "CREATE USER tallyuser WITH PASSWORD 'TallyPass2024!';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE tallybackend TO tallyuser;"

# Configure PostgreSQL for remote connections
sudo sed -i "s/#listen_addresses = 'localhost'/listen_addresses = '*'/" /var/lib/pgsql/data/postgresql.conf
echo "host all all 0.0.0.0/0 md5" | sudo tee -a /var/lib/pgsql/data/pg_hba.conf
sudo systemctl restart postgresql

# Install Maven
cd /opt
sudo wget https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
sudo tar xzf apache-maven-3.9.6-bin.tar.gz
sudo ln -s apache-maven-3.9.6 maven
echo 'export PATH=/opt/maven/bin:$PATH' | sudo tee /etc/profile.d/maven.sh
source /etc/profile.d/maven.sh

# Create application directory
sudo mkdir -p /opt/tallybackend
sudo chown ec2-user:ec2-user /opt/tallybackend

# Set environment variables
cat > /opt/tallybackend/.env << EOF
DB_URL=jdbc:postgresql://localhost:5432/tallybackend
DB_USERNAME=tallyuser
DB_PASSWORD=TallyPass2024!
JWT_SECRET=your-production-jwt-secret-key-change-this-to-secure-random-string-minimum-256-bits
SSL_ENABLED=false
AWS_SES_REGION=us-east-1
AWS_SES_ACCESS_KEY=your_aws_access_key
AWS_SES_SECRET_KEY=your_aws_secret_key
AWS_SES_FROM_EMAIL=noreply@yourdomain.com
AWS_SNS_REGION=us-east-1
AWS_SNS_ACCESS_KEY=your_aws_access_key
AWS_SNS_SECRET_KEY=your_aws_secret_key
APP_BASE_URL=http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4):8080
SPRING_PROFILES_ACTIVE=production
EOF

# Create systemd service
sudo tee /etc/systemd/system/tallybackend.service > /dev/null << EOF
[Unit]
Description=TallyBackend Spring Boot Application
After=network.target postgresql.service

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/opt/tallybackend
ExecStart=/usr/bin/java -jar /opt/tallybackend/tallybackend.jar
EnvironmentFile=/opt/tallybackend/.env
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Open firewall ports
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --permanent --add-port=5432/tcp
sudo firewall-cmd --reload

echo "✅ EC2 setup complete!"
echo "📝 Next steps:"
echo "1. Upload your JAR file to /opt/tallybackend/tallybackend.jar"
echo "2. Update .env file with your AWS credentials"
echo "3. Run: sudo systemctl enable tallybackend"
echo "4. Run: sudo systemctl start tallybackend"
echo "5. Check status: sudo systemctl status tallybackend"