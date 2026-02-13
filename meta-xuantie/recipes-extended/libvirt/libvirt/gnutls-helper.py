#!/usr/bin/env python3
#
# Copyright (C) 2019 Wind River Systems, Inc.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os, sys, getopt

banner = \
'''\
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
!! "ip_address" field of server.info must be IP address of the server. !!
!! For more details, please refer to:                                  !!
!! https://libvirt.org/remote.html#Remote_certificates                 !!
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

Please deploy cacert.pem     to CA and server and client /etc/pki/CA/cacert.pem
Please deploy serverkey.pem  to server /etc/pki/libvirt/private/serverkey.pem
Please deploy servercert.pem to server /etc/pki/libvirt/servercert.pem
Please deploy clientkey.pem  to client /etc/pki/libvirt/private/clientkey.pem
Please deploy clientcert.pem to client /etc/pki/libvirt/clientcert.pem"
'''

if os.system('which certtool > /dev/null 2>&1') != 0:
    print('certtool is not available. It is provided by \n\
gnutls-bin on Yocto like Linux or \n\
gnutls-bin on Debian like distribution or \n\
gnutls-utils on Redhat like distribution.')
    sys.exit()

cainfo = ""
serverinfo = ""
clientinfo = ""
yes = 0

try:
    opts, args = getopt.getopt(sys.argv[1:], "ha:b:c:y", ["help", "ca-info=", "server-info=", "client-info=", "yes"])
except getopt.GetoptError:
    print('Usage:\n{} [-a|--ca-info] <ca.info> [-b|--server-info] <server.info> [-c|--client-info] <client.info> [-y|--yes]'.format(sys.argv[0]))
    print('If ca.info or server.info or client.info is not provided, a corresponding sample file will be generated.')
    sys.exit(2)
for opt, arg in opts:
    if opt in ("-h", "--help"):
        print('Usage:\n{} [-a|--ca-info] <ca.info> [-b|--server-info] <server.info> [-c|--client-info] <client.info> [-y|--yes]'.format(sys.argv[0]))
        print('If ca.info or server.info or client.info is not provided, a corresponding sample file will be generated.\n')
        print(banner)
        sys.exit()
    elif opt in ("-a", "--ca-info"):
        cainfo = arg
    elif opt in ("-b", "--server-info"):
        serverinfo = arg
    elif opt in ("-c", "--client-info"):
        clientinfo = arg
    elif opt in ("-y", "--yes"):
        yes = 1

cainfodefault = \
'''cn = CA
ca
cert_signing_key
'''

serverinfodefault = \
'''organization = Organization
cn = Server
dns_name = DNS Name
ip_address = 127.0.0.1
tls_www_server
encryption_key
signing_key
'''

clientinfodefault = \
'''country = Country
state = State
locality = Locality
organization = Organization
cn = Client
tls_www_client
encryption_key
signing_key
'''

if not cainfo:
    if yes == 0:
        opt = input('{}\nca.info not provided by -a, the above will be used [y/n]?'.format(cainfodefault))
        if opt != 'y':
            exit()
    cainfo = "ca.info"
    with open(cainfo, mode='w') as f:
        f.write(cainfodefault)

if not serverinfo:
    if yes == 0:
        opt = input('{}\nserver.info not provided by -b, the above will be used [y/n]?'.format(serverinfodefault))
        if opt != 'y':
            exit()
    serverinfo = "server.info"
    with open(serverinfo, mode='w') as f:
        f.write(serverinfodefault)

if not clientinfo:
    if yes == 0:
        opt = input('{}\nclient.info not provided by -c, the above will be used [y/n]?'.format(clientinfodefault))
        if opt != 'y':
            sys.exit()
    clientinfo = "client.info"
    with open(clientinfo, mode='w') as f:
        f.write(clientinfodefault)

if os.system("certtool --generate-privkey > cakey.pem") != 0:
    print('ca private key failed.')
    sys.exit()

if os.system("certtool --generate-self-signed --load-privkey cakey.pem --template {} --outfile cacert.pem".format(cainfo)) != 0:
    print('ca cert failed.')
    sys.exit()

if os.system("certtool --generate-privkey > serverkey.pem") != 0:
    print('server private key failed.')
    sys.exit()

if os.system("certtool --generate-certificate --load-privkey serverkey.pem --load-ca-certificate cacert.pem --load-ca-privkey cakey.pem --template {} --outfile servercert.pem".format(serverinfo)) != 0:
    print('server cert failed.')
    sys.exit()

if os.system("certtool --generate-privkey > clientkey.pem") != 0:
    print('client private key failed.')
    sys.exit()

if os.system("certtool --generate-certificate --load-privkey clientkey.pem --load-ca-certificate cacert.pem --load-ca-privkey cakey.pem --template {} --outfile clientcert.pem".format(clientinfo)) != 0:
    print('client cert failed.')
    sys.exit()

print(banner)
