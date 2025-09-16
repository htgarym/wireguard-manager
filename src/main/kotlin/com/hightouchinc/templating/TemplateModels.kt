package com.hightouchinc.templating

import com.hightouchinc.model.Peer
import com.hightouchinc.model.Server

data class ClientTemplateModel(
   val id: String,
   val name: String,
   val publicKey: String,
   val privateKey: String,
   val presharedKey: String,
   val fourthOctet: Int,
   val allowedIps: String,
) {
   constructor(peer: Peer) :
      this(
         id = peer.id.value.toString(),
         name = peer.name.toString(),
         publicKey = peer.publicKey.value,
         privateKey = peer.privateKey.value,
         presharedKey = peer.presharedKey.value,
         fourthOctet = peer.fourthOctet.value,
         allowedIps = "192.168.10.${peer.fourthOctet.value}/32",
      )
}

data class ServerTemplateModel(
   val publicKey: String,
   val privateKey: String,
   val publicAddress: String,
   val iface: String,
   val clients: List<ClientTemplateModel> = emptyList(),
) {
   constructor(server: Server):
      this(
         server = server,
         peers = emptyList()
      )

   constructor(server: Server, peers: List<Peer>):
      this(
         publicKey = server.publicKey.value,
         privateKey = server.privateKey.value,
         publicAddress = server.publicAddress.value,
         iface = server.iface.value,
         clients = peers.map(::ClientTemplateModel)
      )
}
