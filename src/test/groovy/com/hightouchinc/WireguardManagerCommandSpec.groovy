package com.hightouchinc

import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment

import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.io.ByteArrayOutputStream
import java.io.PrintStream

class WireguardManagerCommandSpec extends Specification {

    @Shared @AutoCleanup ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)

    void "test wireguard-manager with command line option"() {
        given:
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        System.setOut(new PrintStream(baos))

        String[] args = ['-h'] as String[]
        PicocliRunner.call(WireguardManagerCommand, ctx, args)

        expect:
        baos.toString() == '''Usage: wireguard-manager [-hV] [COMMAND]
CLI tool to manage a wireguard server and generate client configurations
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  init, initialize  Initialize application
  user              Manage users
'''
    }
}

