package com.hightouchinc.model

import com.hightouchinc.model.base.FourthOctet
import com.hightouchinc.model.base.Identifier
import com.hightouchinc.model.base.Text

data class Peer(
   val id: Identifier,
   val name: Text,
   val fourthOctet: FourthOctet,
   val privateKey: PrivateWireGuardKey,
   val publicKey: PublicWireGuardKey,
   val presharedKey: PresharedWireGuardKey,
   val clientConfig: Text,
   val server: Server,
)
