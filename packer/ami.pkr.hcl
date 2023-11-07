packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = ">= 1.0.0, < 2.0.0"
    }
  }
}


variable "aws_region" {
  type    = string
  default = "us-west-2"
}

variable "source_ami" {
  type    = string
  default = "ami-0b6edd8449255b799" # Debian 12

  validation {
    condition     = length(var.source_ami) > 4 && substr(var.source_ami, 0, 4) == "ami-"
    error_message = "The source_ami value must be a valid AMI ID, starting with \"ami-\"."
  }

}

variable "ssh_username" {
  type    = string
  default = "admin"
}

variable "subnet_id" {
  type    = string
  default = "subnet-077d8ee2014ba9718"
}

variable "instance_type" {
  type    = string
  default = "t2.micro"
}

variable "delete_on_termination" {
  type    = bool
  default = true
}

variable "device_name" {
  type    = string
  default = "/dev/sdf"
}

variable "volume_size" {
  type    = number
  default = 25
}

variable "volume_type" {
  type    = string
  default = "gp2"
}


variable "ami_description" {
  type    = string
  default = "AMI for CSYE 6225 A5"
}

variable "ami_regions" {
  type    = list(string)
  default = ["us-west-2"]
}

variable "aws_polling_delay" {
  type    = number
  default = 120
}

variable "aws_polling_max_attempts" {
  type    = number
  default = 50
}

variable "user_name" {
  description = "Username for Debian."
  type        = string
  default     = "admin"
}


variable "allowed_ami_users" {
  description = "users who can access created AMI"
  type        = list(string)
  default     = ["855584951376", "346759736871"]
}



# https://www.packer.io/plugins/builders/amazon/ebs
source "amazon-ebs" "my-ami" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "${var.ami_description}"
  ami_regions     = "${var.ami_regions}"
  ami_users       = "${var.allowed_ami_users}"



  aws_polling {
    delay_seconds = "${var.aws_polling_delay}"
    max_attempts  = "${var.aws_polling_max_attempts}"
  }

  instance_type = "${var.instance_type}"
  source_ami    = "${var.source_ami}"
  ssh_username  = "${var.ssh_username}"
  subnet_id     = "${var.subnet_id}"

  launch_block_device_mappings {
    delete_on_termination = "${var.delete_on_termination}"
    device_name           = "${var.device_name}"
    volume_size           = "${var.volume_size}"
    volume_type           = "${var.volume_type}"
  }

  tags = {
    CreatedBy = "Packer"
  }

}

build {
  sources = [
    "source.amazon-ebs.my-ami",
  ]

  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1",
      "SSH_USERNAME=${var.user_name}",
    ]
    inline = [
      "sudo apt-get update",
      "sudo apt-get upgrade -y",


      "export PATH=$PATH:/usr/sbin/",

      "echo \"Installing Java\"",
      "sudo apt install -y openjdk-17-jre",

      "echo \"Installing Maven\"",
      "sudo apt install -y maven",

      "if [ ! -f \"/opt/users.csv\" ]; then",
      "  sudo sh -c 'cat << EOF > /opt/users.csv",
      "first_name,last_name,email,password",
      "john,doe,john.doe@example.com,abc123",
      "jane,doe,jane.doe@example.com,xyz456",
      "EOF'",
      "fi",

      "mkdir webapp",


    ]
  }

  #  provisioner "file" {
  #    source      = "../src/main/resources/static/amazon-cloudwatch-agent.json"
  #    destination = "~/amazon-cloudwatch-agent.json"
  #  }

  provisioner "file" {
    source      = "../target/webapp-0.0.1-SNAPSHOT.jar"
    destination = "~/webapp/webapp-0.0.1-SNAPSHOT.jar"
  }

  provisioner "shell" {
    inline = ["timeout 60s java -jar webapp-0.0.1-SNAPSHOT.jar || true"]
  }


  # systemd path for triggering webapp
  provisioner "file" {
    source      = "../src/main/resources/static/csye6225.path"
    destination = "/tmp/csye6225.path"
  }

  # systemd service starts webapp
  provisioner "file" {
    source      = "../src/main/resources/static/csye6225.service"
    destination = "/tmp/csye6225.service"
  }

  # systemd path for triggering configure and start cloudwatch
  provisioner "file" {
    source      = "../src/main/resources/static/cloudwatch.path"
    destination = "/tmp/cloudwatch.path"
  }

  # systemd service triggering configure and start cloudwatch
  provisioner "file" {
    source      = "../src/main/resources/static/cloudwatch.service"
    destination = "/tmp/cloudwatch.service"
  }

  provisioner "shell" {
    inline = [

      "sudo mv /tmp/csye6225.path /etc/systemd/system/",
      "sudo mv /tmp/csye6225.service /etc/systemd/system/",
      "sudo mv /tmp/cloudwatch.path /etc/systemd/system/",
      "sudo mv /tmp/cloudwatch.service /etc/systemd/system/",
      "sudo groupadd csye6225",
      "sudo useradd -s /bin/false -g csye6225 csye6225",
      "sudo chown csye6225:csye6225 -R /home/admin/webapp",
      "sudo touch /opt/webapp.log",
      "sudo chown -R csye6225:csye6225 /opt",
      "sudo chmod 664 /opt/webapp.log",

      "sudo systemctl daemon-reload",
      "sudo systemctl enable csye6225.path",

      #install amazoncloudwatch agent and enable systemd service(path)
      "wget https://amazoncloudwatch-agent.s3.amazonaws.com/debian/amd64/latest/amazon-cloudwatch-agent.deb",
      "sudo dpkg -i -E ./amazon-cloudwatch-agent.deb",
      "sudo systemctl enable cloudwatch.path",

    ]
  }

}
