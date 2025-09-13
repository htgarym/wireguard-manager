package com.hightouchinc.model

import com.hightouchinc.model.base.FirstThreeOctets
import com.hightouchinc.model.base.Identifier
import com.hightouchinc.model.base.Port
import com.hightouchinc.model.base.Text

data class Server(
   val id: Identifier,
   val iface: Text,
   val firstThreeOctets: FirstThreeOctets,
   val port: Port,
)
