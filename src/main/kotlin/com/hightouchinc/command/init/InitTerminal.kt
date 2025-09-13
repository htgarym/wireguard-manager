package com.hightouchinc.command.init

import java.net.NetworkInterface
import org.jline.consoleui.prompt.builder.PromptBuilder

fun PromptBuilder.buildPromptForInterface(name: String): PromptBuilder {
   val interfaces = NetworkInterface.networkInterfaces().map { it.name }.toList()
   val listPrompt =
      this.createListPrompt()
         .name(name)
         .message("Choose interface")

   for (i in interfaces) {
      listPrompt.newItem(i).text(i).add()
   }

   listPrompt.pageSize(3)
   listPrompt.addPrompt()

   return this
}

fun PromptBuilder.buildFirstThreeOctet(name: String): PromptBuilder =
   this.createInputPrompt()
      .name(name)
      .message("Choose first three octets")
      .defaultValue("192.168.10")
      .addPrompt()


fun PromptBuilder.buildPromptForPort(name: String): PromptBuilder =
   this.createInputPrompt()
      .name(name)
      .message("Choose port")
      .defaultValue("51820")
      .addPrompt()
