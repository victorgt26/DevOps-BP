output "public_ip" {
  description = "Current public IP of the k3s node."
  value       = aws_instance.k3s.public_ip
}

output "application_url" {
  description = "Public HTTP URL after the application has been deployed."
  value       = "http://${aws_instance.k3s.public_ip}/api/swagger-ui.html"
}

output "kubeconfig_command" {
  description = "Run this command to download kubeconfig, then replace 127.0.0.1 with the public IP."
  value       = "ssh -i <private-key> ubuntu@${aws_instance.k3s.public_ip} 'sudo cat /etc/rancher/k3s/k3s.yaml' > kubeconfig"
}
