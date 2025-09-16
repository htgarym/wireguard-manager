package com.hightouchinc.command.render

import com.hightouchinc.model.Peer
import com.hightouchinc.model.Server
import org.jline.consoleui.prompt.builder.PromptBuilder

fun PromptBuilder.buildServerChooserPrompt(name: String, servers: List<Server>): PromptBuilder {
   val serversPrompt = this.createListPrompt().name(name).message("Choose server")

   for (server in servers) {
      serversPrompt
         .newItem(server.id.value.toString())
         .text(server.iface.value)
         .add()
   }

   serversPrompt.pageSize(5)
   serversPrompt.addPrompt()

   return this
}

fun PromptBuilder.buildPeersPrompt(name: String, peers: List<Peer>): PromptBuilder {
   val peersPrompt = this.createListPrompt().name(name).message("Choose peer")

   for (peer in peers) {
      peersPrompt
         .newItem(peer.id.value.toString())
         .text(peer.name.value)
         .add()
   }

   peersPrompt.pageSize(5)
   peersPrompt.addPrompt()

   return this
}
