# TallyBackend AWS EC2 Deployment Guide

## Prerequisites
1. AWS EC2 instance (t3.medium or larger recommended)
2. Amazon Linux 2023 or Ubuntu 22.04
3. Security Group with ports 8080 and 22 open
4. SSH key pair for instance access

## Deployment Steps

### Option 1: Docker Deployment (Recommended)

1. **Connect to EC2:**
   ```bash
   ssh -i your-key.pem ec2-user@your-ec2-public-ip
   ```

2. **Install Docker:**
   ```bash
   sudo yum update -y
   sudo yum install -y docker
   sudo systemctl start docker
   sudo systemctl enable docker
   sudo usermod -a -G docker ec2-user
   
   # Install Docker Compose
   sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
   sudo chmod +x /usr/local/bin/docker-compose
   ```

3. **Upload deployment files:**
   ```bash
   # From your local machine
   scp -i your-key.pem -r deploy/ ec2-user@your-ec2-public-ip:~/
   ```

4. **Deploy:**
   ```bash
   cd deploy
   docker-compose up -d
   ```

### Option 2: Native Deployment

1. **Upload and run setup script:**
   ```bash
   chmod +x deploy-ec2.sh
   ./deploy-ec2.sh
   ```

2. **Start the service:**
   ```bash
   sudo systemctl enable tallybackend
   sudo systemctl start tallybackend
   ```

## Post-Deployment

1. **Check application status:**
   ```bash
   curl http://your-ec2-public-ip:8080/actuator/health
   ```

2. **View logs:**
   ```bash
   # Docker
   docker-compose logs -f tallybackend
   
   # Native
   sudo journalctl -u tallybackend -f
   ```

3. **Update environment variables:**
   - Edit `docker-compose.yml` or `/opt/tallybackend/.env`
   - Add your AWS SES/SNS credentials
   - Update JWT secret

## Security Recommendations

1. **Use Application Load Balancer with SSL**
2. **Configure RDS PostgreSQL instead of local DB**
3. **Use AWS Secrets Manager for credentials**
4. **Enable CloudWatch logging**
5. **Set up auto-scaling group**

## Monitoring

- Application: http://your-ec2-public-ip:8080
- Health Check: http://your-ec2-public-ip:8080/actuator/health
- Metrics: http://your-ec2-public-ip:8080/actuator/metrics

## Troubleshooting

- Check security group allows port 8080
- Verify Java 17 is installed
- Check database connectivity
- Review application logs for errors