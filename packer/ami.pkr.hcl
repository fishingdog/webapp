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
  default = "ami-0b6edd8449255b799" # Ubuntu 22.04 LTS

  validation {
    condition     = length(var.source_ami) > 4 && substr(var.source_ami, 0, 4) == "ami-"
    error_message = "The source_ami value must be a valid AMI ID, starting with \"ami-\"."
  }

}

variable "ssh_username" {
  type    = string
  default = "ubuntu"
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
  default = "/dev/sda1"
}

variable "volume_size" {
  type    = number
  default = 8
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
  default = ["us-east-1", "us-west-2"]
}

variable "aws_polling_delay" {
  type    = number
  default = 120
}

variable "aws_polling_max_attempts" {
  type    = number
  default = 50
}

# https://www.packer.io/plugins/builders/amazon/ebs
source "amazon-ebs" "my-ami" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "${var.ami_description}"
  ami_regions     = "${var.ami_regions}"


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
}

build {
  sources = ["source.amazon-ebs.my-ami"]

  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1"
    ]
    inline = [
      "sudo apt-get update",
      "sudo apt-get upgrade -y",
      "..............", # nginx was for demo only. you do not need to install in your AMI.
      "sudo apt-get clean",
    ]
  }
}
