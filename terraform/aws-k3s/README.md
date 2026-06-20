# AWS k3s con Terraform

Se crea VPC, subnet pública, Internet Gateway, rutas, Security Group y una instancia EC2 cifrada con k3s. Es más económica que EKS, pero sigue generando costes de EC2, EBS y dirección IPv4 pública.

Crear el archivo local `terraform.tfvars` con los valores reales del entorno:

```hcl
aws_region          = "eu-west-1"
aws_profile         = "nombre_profile"
instance_type       = "t3.small"
ssh_key_name        = "nombre-del-key-pair-existente"
ssh_cidr            = "IP_PUBLICA_ADMIN/32"
kubernetes_api_cidr = "CIDR_AUTORIZADO/32"
```

```bash
AWS_PROFILE=nombre_profile terraform init
AWS_PROFILE=nombre_profile terraform plan -out=tfplan
AWS_PROFILE=nombre_profile terraform apply tfplan
terraform output
```

Descargar el kubeconfig con el comando del output, sustituir `127.0.0.1` por el `public_ip`, exportar `KUBECONFIG` y desplegar el overlay:

```bash
sed -i.bak "s/127.0.0.1/$(terraform output -raw public_ip)/" kubeconfig
chmod 600 kubeconfig
export KUBECONFIG="$PWD/kubeconfig"
kubectl apply -f ../../k8s/base/namespace.yaml
kubectl -n demo-devops create secret generic demo-devops-db \
  --from-literal=SPRING_DATASOURCE_USERNAME=user \
  --from-literal=SPRING_DATASOURCE_PASSWORD='replace-me' \
  --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -k ../../k8s/overlays/aws
```

Se debe configurar la variable de GitHub Environment `KUSTOMIZE_PATH=k8s/overlays/aws` para que el pipeline use Traefik, incluido en k3s. Un runner hospedado por GitHub necesita acceso al puerto 6443; para evitar abrirlo globalmente se recomienda un runner self-hosted en la VPC o actualizar el CIDR del Security Group de forma controlada.

Para eliminar todo usar el siguiente comando:

```bash
AWS_PROFILE=nombre_profile terraform destroy
```

`terraform.tfvars` es la configuración estándar del entorno local y está excluido de Git. El contrato de variables, tipos, valores predeterminados y validaciones que sí se versiona está en `variables.tf`. Tampoco se versionan `tfstate`, kubeconfig, claves privadas ni credenciales AWS.
