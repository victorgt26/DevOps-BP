variable "aws_region" {
  description = "AWS region where the k3s node is created."
  type        = string
  default     = "eu-west-1"
}

variable "aws_profile" {
  description = "Optional local AWS CLI profile. Prefer AWS_PROFILE in CI."
  type        = string
  default     = null
  nullable    = true
}

variable "instance_type" {
  description = "EC2 size. t3.small is the practical minimum for k3s plus two Java pods."
  type        = string
  default     = "t3.small"
}

variable "ssh_key_name" {
  description = "Name of an existing EC2 key pair."
  type        = string
}

variable "ssh_cidr" {
  description = "CIDR allowed to connect through SSH, normally YOUR_PUBLIC_IP/32."
  type        = string

  validation {
    condition     = var.ssh_cidr != "0.0.0.0/0"
    error_message = "Do not expose SSH to the whole internet; use a /32 administrator CIDR."
  }
}

variable "kubernetes_api_cidr" {
  description = "CIDR allowed to reach the Kubernetes API. GitHub-hosted runners need a broader strategy or a self-hosted runner."
  type        = string
}

variable "tags" {
  description = "Tags applied to all supported AWS resources."
  type        = map(string)
  default = {
    Project   = "devsu-demo-devops-java"
    ManagedBy = "terraform"
  }
}
