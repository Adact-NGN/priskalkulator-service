provider "azurerm" {
  features {
  }
  nets_base_addr                  = var.nets_base_addr
  nets_secret_key                 = var.nets_secret_key
  nets_checkout_key               = var.nets_checkout_key
  vipps_base_addr                 = var.vipps_base_addr
  vipps_client_id                 = var.vipps_client_id
  vipps_client_secret             = var.vipps_client_secret
  vipps_ocp_apim_subscription_key = var.vipps_ocp_apim_subscription_key
  vipps_merchant_serial_number    = var.vipps_merchant_serial_number
}

terraform {
  backend "azurerm" {
      container_name = "value"
  }
}

data "azurerm_resource_group" "rg" {
  name = "ding-rg-ip-${var.environment}"
}

data "azurerm_api_management" "apim" {
  name                = "ding-apim-${var.environment}"
  resource_group_name = data.azurerm_resource_group.rg.name
}

data "azurerm_app_service" "pk_app_service" {
  name                = "ding-ip-app-pk-${var.environment}"
  resource_group_name = data.azurerm_resource_group.rg.name
}

// Creating or updating apim
# resource "azurerm_api_management_api" "payment_api" {
#   name                = "payment"
#   resource_group_name = data.azurerm_resource_group.rg.name
#   api_management_name = data.azurerm_api_management.apim.name
#   revision            = "2"

#   display_name          = "Payment API"
#   path                  = "payment"
#   protocols             = ["https"]
#   service_url           = "https://${data.azurerm_app_service.ecommerce_app.name}.azurewebsites.net"
#   subscription_required = true

//  import {
//    content_format = "openapi+json-link"
//    content_value  = "https://${data.azurerm_app_service.ecommerce_app.name}.azurewebsites.net/docs/v1/swagger.json"
//  }
//}


