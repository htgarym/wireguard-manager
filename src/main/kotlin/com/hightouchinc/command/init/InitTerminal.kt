package com.hightouchinc.command.init

import java.net.NetworkInterface
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import org.jline.consoleui.prompt.builder.PromptBuilder

private fun loadEc2PublicIpAddress(): String? =
   try {
      HttpClient.newHttpClient().use { httpClient ->
         val tokenRequest = HttpRequest
            .newBuilder(URI.create("http://169.254.169.254/latest/api/token"))
            .header("X-aws-ec2-metadata-token-ttl-seconds", "60")
            .method("PUT", HttpRequest.BodyPublishers.noBody())
            .timeout(Duration.ofSeconds(1))
            .build()
         val tokenResponse = httpClient.send(tokenRequest, HttpResponse.BodyHandlers.ofString())

         if (tokenResponse.statusCode() == 200) {
            val token = tokenResponse.body()
            val ipAddressRequest = HttpRequest
               .newBuilder(URI.create("http://169.254.169.254/latest/meta-data/public-ipv4"))
               .header("X-aws-ec2-metadata-token", token)
               .GET()
               .timeout(Duration.ofSeconds(1))
               .build()
            val ipAddressResponse = httpClient.send(ipAddressRequest, HttpResponse.BodyHandlers.ofString())

            if (ipAddressResponse.statusCode() == 200) {
               ipAddressResponse.body()
            } else {
               null
            }
         } else {
            null
         }
      }
   } catch(_: HttpConnectTimeoutException) { // probably not running in EC2
      null
   }

fun PromptBuilder.buildPromptForInterface(name: String): PromptBuilder {
   val interfaces = NetworkInterface.networkInterfaces().toList()
   val interfacesPrompt =
      this.createListPrompt()
         .name(name)
         .message("Choose interface")


   for (i in interfaces) {
      interfacesPrompt
         .newItem(i.name)
         .text(i.displayName)
         .add()
   }

   interfacesPrompt.pageSize(5)
   interfacesPrompt.addPrompt()

   return this
}

fun PromptBuilder.buildPublicIpAddress(name: String): PromptBuilder {
   val ec2PublicIpAddress = loadEc2PublicIpAddress()

   if (ec2PublicIpAddress == null) {
      this.createInputPrompt()
         .name(name)
         .message("Enter public IP address")
         .addPrompt()
   } else {
      this.createInputPrompt()
         .name(name)
         .message("Enter public IP address")
         .defaultValue(ec2PublicIpAddress)
         .addPrompt()
   }

   return this
}
