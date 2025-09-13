package com.hightouchinc.command.user

import com.hightouchinc.model.Server
import org.jline.consoleui.prompt.builder.PromptBuilder

fun PromptBuilder.buildServerPrompt(name: String, servers: List<Server>): PromptBuilder {
   val listPrompt =
      this.createListPrompt()
         .name(name)
         .message("Choose User Server")

   listPrompt.pageSize(3)

   for (server in servers) {
      listPrompt
         .newItem(server.id.toString())
         .text("${server.iface}-${server.firstThreeOctets}-${server.port}")
         .add()
   }

   listPrompt.addPrompt()

   return this
}
