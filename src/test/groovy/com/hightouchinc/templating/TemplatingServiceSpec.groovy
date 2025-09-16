package com.hightouchinc.templating

import com.github.f4b6a3.uuid.UuidCreator
import com.hightouchinc.model.Peer
import com.hightouchinc.model.Server
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class TemplatingServiceSpec extends Specification {
    @Inject
    TemplatingService templatingService

    void "render server template with no peers"() {
        given:
        def stringWriter = new StringWriter()
        def server = new Server(
                UuidCreator.getTimeOrderedEpoch(),
                "ens5",
                "w8H10aOK/2vaj3axOUx1oIfoYnAFE7lIWxxOeJljgUs=",
                "SFk2P5GZGf1qCYGuKrm9ldqy1PlxRu+wbF2USKUtnE0=",
                "192.168.11.1",
        )

        when:
        templatingService.renderServerConfig(server, [], stringWriter)

        then:
        stringWriter.toString() == """[Interface]
PrivateKey = w8H10aOK/2vaj3axOUx1oIfoYnAFE7lIWxxOeJljgUs=
Address = 192.168.10.1/24
ListenPort = 51820
SaveConfig = false

# Create nftables table and chains for WireGuard
PostUp = nft add table inet wireguard
PostUp = nft add chain inet wireguard forward_wg { type filter hook forward priority 0\\; policy accept\\; }
PostUp = nft add chain inet wireguard postrouting_wg { type nat hook postrouting priority srcnat\\; policy accept\\; }

# Allow forwarding for WireGuard interface
PostUp = nft add rule inet wireguard forward_wg iifname "%i" accept
PostUp = nft add rule inet wireguard forward_wg oifname "%i" accept

# Enable NAT masquerading for outbound traffic
PostUp = nft add rule inet wireguard postrouting_wg oifname "ens5" masquerade

# Add route to 10.0.0.0/8 network via WireGuard interface
PostUp = ip route add 10.0.0.0/8 via 192.168.10.1 dev %i

# Cleanup when bringing down the interface
PostDown = nft delete table inet wireguard 2>/dev/null || true
PostDown = ip route del 10.0.0.0/8 via 192.168.10.1 dev %i 2>/dev/null || true

# Client configurations

# Client configurations
"""
    }

    void "render server template with a single peer"() {
        given:
        def stringWriter = new StringWriter()
        def server = new Server(
                UuidCreator.getTimeOrderedEpoch(),
                "ens5",
                "w8H10aOK/2vaj3axOUx1oIfoYnAFE7lIWxxOeJljgUs=",
                "SFk2P5GZGf1qCYGuKrm9ldqy1PlxRu+wbF2USKUtnE0=",
                "192.168.11.1")
        def peers = [
                new Peer(
                        UUID.fromString("e4abc0b8-9415-11f0-b1a9-a8a1594fece3"),
                        "peer1",
                        2,
                        "yCR3HNA3gKKw9xgSHDqjX8hinM9wSczMTgSfd1Z7Umw=",
                        "FpYYbcVzDO0iImR8aeIqzO86qJNWvsorzqmWRNbX3A4=",
                        "AuOkyITV+fY0aaCuLN4UWKtWHxzVL8fyqvwvY8VnirI=",
                        "",
                        server
                )
        ]

        when:
        templatingService.renderServerConfig(server, peers, stringWriter)

        then:
        stringWriter.toString() == """[Interface]
PrivateKey = w8H10aOK/2vaj3axOUx1oIfoYnAFE7lIWxxOeJljgUs=
Address = 192.168.10.1/24
ListenPort = 51820
SaveConfig = false

# Create nftables table and chains for WireGuard
PostUp = nft add table inet wireguard
PostUp = nft add chain inet wireguard forward_wg { type filter hook forward priority 0\\; policy accept\\; }
PostUp = nft add chain inet wireguard postrouting_wg { type nat hook postrouting priority srcnat\\; policy accept\\; }

# Allow forwarding for WireGuard interface
PostUp = nft add rule inet wireguard forward_wg iifname "%i" accept
PostUp = nft add rule inet wireguard forward_wg oifname "%i" accept

# Enable NAT masquerading for outbound traffic
PostUp = nft add rule inet wireguard postrouting_wg oifname "ens5" masquerade

# Add route to 10.0.0.0/8 network via WireGuard interface
PostUp = ip route add 10.0.0.0/8 via 192.168.10.1 dev %i

# Cleanup when bringing down the interface
PostDown = nft delete table inet wireguard 2>/dev/null || true
PostDown = ip route del 10.0.0.0/8 via 192.168.10.1 dev %i 2>/dev/null || true

# Client configurations

[Peer]
# Client ID: e4abc0b8-9415-11f0-b1a9-a8a1594fece3
PublicKey = FpYYbcVzDO0iImR8aeIqzO86qJNWvsorzqmWRNbX3A4=
PresharedKey = AuOkyITV+fY0aaCuLN4UWKtWHxzVL8fyqvwvY8VnirI=
AllowedIPs = 192.168.10.2/32

# Client configurations
"""
    }

    void "render peer template"() {
        given:
        def stringWriter = new StringWriter()
        def server = new Server(
                UuidCreator.getTimeOrderedEpoch(),
                "ens5",
                "w8H10aOK/2vaj3axOUx1oIfoYnAFE7lIWxxOeJljgUs=",
                "SFk2P5GZGf1qCYGuKrm9ldqy1PlxRu+wbF2USKUtnE0=",
                "192.168.11.1",
                "",
        )
        def peer =
            new Peer(
                    UUID.fromString("e4abc0b8-9415-11f0-b1a9-a8a1594fece3"),
                    "peer1",
                    2,
                    "yCR3HNA3gKKw9xgSHDqjX8hinM9wSczMTgSfd1Z7Umw=",
                    "FpYYbcVzDO0iImR8aeIqzO86qJNWvsorzqmWRNbX3A4=",
                    "AuOkyITV+fY0aaCuLN4UWKtWHxzVL8fyqvwvY8VnirI=",
                    "",
                    server
            )

        when:
        templatingService.renderClientConfig(peer, stringWriter)

        then:
        stringWriter.toString() == """[Interface]
PrivateKey = yCR3HNA3gKKw9xgSHDqjX8hinM9wSczMTgSfd1Z7Umw=
Address = 192.168.10.2/32

[Peer]
# Client ID: e4abc0b8-9415-11f0-b1a9-a8a1594fece3
# Client Name: peer1
PresharedKey = AuOkyITV+fY0aaCuLN4UWKtWHxzVL8fyqvwvY8VnirI=
PublicKey = SFk2P5GZGf1qCYGuKrm9ldqy1PlxRu+wbF2USKUtnE0=
Endpoint = 192.168.11.1:51820
AllowedIPs = 192.168.10.0/24, 10.0.0.0/8
PersistentKeepalive = 25"""

    }
}