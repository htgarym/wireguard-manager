package com.hightouchinc.templating

import com.hightouchinc.model.Peer
import com.hightouchinc.model.Server
import gg.jte.ContentType
import gg.jte.TemplateEngine
import gg.jte.output.WriterOutput
import gg.jte.resolve.ResourceCodeResolver
import jakarta.inject.Singleton
import java.io.Writer

@Singleton
class TemplatingService {
   fun renderServerConfig(server: Server, peers: List<Peer>, writer: Writer) {
      val codeResolver = ResourceCodeResolver("template")
      val jteEngine = TemplateEngine.create(codeResolver, ContentType.Plain)
      val templateOutput = WriterOutput(writer)

      jteEngine.render("wgX.conf.kte", ServerTemplateModel(server, peers), templateOutput)
   }

   fun renderClientConfig(peer: Peer, writer: Writer) {
      val codeResolver = ResourceCodeResolver("template")
      val jteEngine = TemplateEngine.create(codeResolver, ContentType.Plain)
      val templateOutput = WriterOutput(writer)

      jteEngine.render(
         "wg-client.conf.kte",
         mapOf("client" to ClientTemplateModel(peer), "server" to ServerTemplateModel(peer.server)),
         templateOutput,
      )
   }
}
