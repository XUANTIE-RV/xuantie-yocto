# k3s: Lightweight Kubernetes

Rancher's [k3s](https://k3s.io/), available under
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0), provides
lightweight Kubernetes suitable for small/edge devices. There are use cases
where the
[installation procedures provided by Rancher](https://rancher.com/docs/k3s/latest/en/installation/)
are not ideal but a bitbake-built version is what is needed. And only a few
mods to the [k3s source code](https://github.com/rancher/k3s) is needed to
accomplish that.

## CNI

By default, K3s will run with flannel as the CNI, using VXLAN as the default
backend. It is both possible to change the flannel backend and to change from
flannel to another CNI.

Please see <https://rancher.com/docs/k3s/latest/en/installation/network-options/>
for further k3s networking details.

## Configure and run a k3s agent

The convenience script `k3s-agent` can be used to set up a k3s agent (service):

```shell
k3s-agent -t <token> -s https://<master>:6443
```
(Here `<token>` is found in `/var/lib/rancher/k3s/server/node-token` at the
k3s master.)

Example:
```shell
k3s-agent -t /var/lib/rancher/k3s/server/node-token -s https://localhost:6443
```

If you are running an all in one node (both the server and agent) for testing
purposes, do not run the above script. It will perform cleanup and break flannel
networking on your host.

Instead, run the following (note the space between 'k3s' and 'agent'):

```shell
k3s agent -t /var/lib/rancher/k3s/server/token --server http://localhost:6443/
```

## Notes:

Memory:

  if running under qemu, the default of 256M of memory is not enough, k3s will
  OOM and exit.

  Boot with qemuparams="-m 2048" to boot with 2G of memory (or choose the
  appropriate amount for your configuration)

Disk:

  if using qemu and core-image* you'll need to add extra space in your disks
  to ensure containers can start. The following in your image recipe, or
  local.conf would add 2G of extra space to the rootfs:

```shell
IMAGE_ROOTFS_EXTRA_SPACE = "2097152"
```

## Example qemux86-64 boot line:

```shell
runqemu qemux86-64 nographic kvm slirp qemuparams="-m 2048"
```

k3s logs can be seen via:


```shell
% journalctl -u k3s
```

or

```shell
% journalctl -xe
```

## Example output from qemux86-64 running k3s server:

```shell
root@qemux86-64:~# kubectl get nodes
NAME         STATUS   ROLES    AGE   VERSION
qemux86-64   Ready    master   46s   v1.18.9-k3s1

root@qemux86-64:~# kubectl get pods -n kube-system
NAME                                     READY   STATUS      RESTARTS   AGE
local-path-provisioner-6d59f47c7-h7lxk   1/1     Running     0          2m32s
metrics-server-7566d596c8-mwntr          1/1     Running     0          2m32s
helm-install-traefik-229v7               0/1     Completed   0          2m32s
coredns-7944c66d8d-9rfj7                 1/1     Running     0          2m32s
svclb-traefik-pb5j4                      2/2     Running     0          89s
traefik-758cd5fc85-lxpr8                 1/1     Running     0          89s

root@qemux86-64:~# kubectl describe pods -n kube-system

root@qemux86-64:~# ip a s
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
2: eth0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP group default qlen 1000
    link/ether 52:54:00:12:35:02 brd ff:ff:ff:ff:ff:ff
    inet 10.0.2.15/24 brd 10.0.2.255 scope global eth0
       valid_lft forever preferred_lft forever
    inet6 fec0::5054:ff:fe12:3502/64 scope site dynamic mngtmpaddr 
       valid_lft 86239sec preferred_lft 14239sec
    inet6 fe80::5054:ff:fe12:3502/64 scope link 
       valid_lft forever preferred_lft forever
3: sit0@NONE: <NOARP> mtu 1480 qdisc noop state DOWN group default qlen 1000
    link/sit 0.0.0.0 brd 0.0.0.0
4: flannel.1: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1450 qdisc noqueue state UNKNOWN group default 
    link/ether e2:aa:04:89:e6:0a brd ff:ff:ff:ff:ff:ff
    inet 10.42.0.0/32 brd 10.42.0.0 scope global flannel.1
       valid_lft forever preferred_lft forever
    inet6 fe80::e0aa:4ff:fe89:e60a/64 scope link 
       valid_lft forever preferred_lft forever
5: docker0: <NO-CARRIER,BROADCAST,MULTICAST,UP> mtu 1500 qdisc noqueue state DOWN group default 
    link/ether 02:42:be:3e:25:e7 brd ff:ff:ff:ff:ff:ff
    inet 172.17.0.1/16 brd 172.17.255.255 scope global docker0
       valid_lft forever preferred_lft forever
6: cni0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1450 qdisc noqueue state UP group default qlen 1000
    link/ether 82:8e:b4:f8:06:e7 brd ff:ff:ff:ff:ff:ff
    inet 10.42.0.1/24 brd 10.42.0.255 scope global cni0
       valid_lft forever preferred_lft forever
    inet6 fe80::808e:b4ff:fef8:6e7/64 scope link 
       valid_lft forever preferred_lft forever
7: veth82ac482e@if4: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1450 qdisc noqueue master cni0 state UP group default 
    link/ether ea:9d:14:c1:00:70 brd ff:ff:ff:ff:ff:ff link-netns cni-c52e6e09-f6e0-a47b-aea3-d6c47d3e2d01
    inet6 fe80::e89d:14ff:fec1:70/64 scope link 
       valid_lft forever preferred_lft forever
8: vethb94745ed@if4: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1450 qdisc noqueue master cni0 state UP group default 
    link/ether 1e:7f:7e:d3:ca:e8 brd ff:ff:ff:ff:ff:ff link-netns cni-86958efe-2462-016f-292d-81dbccc16a83
    inet6 fe80::8046:3cff:fe23:ced1/64 scope link 
       valid_lft forever preferred_lft forever
9: veth81ffb276@if4: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1450 qdisc noqueue master cni0 state UP group default 
    link/ether 2a:1d:48:54:76:50 brd ff:ff:ff:ff:ff:ff link-netns cni-5d77238e-6452-4fa3-40d2-91d48386080b
    inet6 fe80::acf4:7fff:fe11:b6f2/64 scope link 
       valid_lft forever preferred_lft forever
10: vethce261f6a@if4: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1450 qdisc noqueue master cni0 state UP group default 
    link/ether 72:a3:90:4a:c5:12 brd ff:ff:ff:ff:ff:ff link-netns cni-55675948-77f2-a952-31ce-615f2bdb0093
    inet6 fe80::4d5:1bff:fe5d:db3a/64 scope link 
       valid_lft forever preferred_lft forever
11: vethee199cf4@if4: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1450 qdisc noqueue master cni0 state UP group default 
    link/ether e6:90:a4:a3:bc:a1 brd ff:ff:ff:ff:ff:ff link-netns cni-4aeccd16-2976-8a78-b2c4-e028da3bb1ea
    inet6 fe80::c85a:8bff:fe0b:aea0/64 scope link 
       valid_lft forever preferred_lft forever


root@qemux86-64:~# kubectl describe nodes

Name:               qemux86-64
Roles:              master
Labels:             beta.kubernetes.io/arch=amd64
                    beta.kubernetes.io/instance-type=k3s
                    beta.kubernetes.io/os=linux
                    k3s.io/hostname=qemux86-64
                    k3s.io/internal-ip=10.0.2.15
                    kubernetes.io/arch=amd64
                    kubernetes.io/hostname=qemux86-64
                    kubernetes.io/os=linux
                    node-role.kubernetes.io/master=true
                    node.kubernetes.io/instance-type=k3s
Annotations:        flannel.alpha.coreos.com/backend-data: {"VtepMAC":"2e:52:6a:1b:76:d4"}
                    flannel.alpha.coreos.com/backend-type: vxlan
                    flannel.alpha.coreos.com/kube-subnet-manager: true
                    flannel.alpha.coreos.com/public-ip: 10.0.2.15
                    k3s.io/node-args: ["server"]
                    k3s.io/node-config-hash: MLFMUCBMRVINLJJKSG32TOUFWB4CN55GMSNY25AZPESQXZCYRN2A====
                    k3s.io/node-env: {}
                    node.alpha.kubernetes.io/ttl: 0
                    volumes.kubernetes.io/controller-managed-attach-detach: true
CreationTimestamp:  Tue, 10 Nov 2020 14:01:28 +0000
Taints:             <none>
Unschedulable:      false
Lease:
  HolderIdentity:  qemux86-64
  AcquireTime:     <unset>
  RenewTime:       Tue, 10 Nov 2020 14:56:27 +0000
Conditions:
  Type                 Status  LastHeartbeatTime                 LastTransitionTime                Reason                       Message
  ----                 ------  -----------------                 ------------------                ------                       -------
  NetworkUnavailable   False   Tue, 10 Nov 2020 14:43:46 +0000   Tue, 10 Nov 2020 14:43:46 +0000   FlannelIsUp                  Flannel is running on this node
  MemoryPressure       False   Tue, 10 Nov 2020 14:51:48 +0000   Tue, 10 Nov 2020 14:45:46 +0000   KubeletHasSufficientMemory   kubelet has sufficient memory available
  DiskPressure         False   Tue, 10 Nov 2020 14:51:48 +0000   Tue, 10 Nov 2020 14:45:46 +0000   KubeletHasNoDiskPressure     kubelet has no disk pressure
  PIDPressure          False   Tue, 10 Nov 2020 14:51:48 +0000   Tue, 10 Nov 2020 14:45:46 +0000   KubeletHasSufficientPID      kubelet has sufficient PID available
  Ready                True    Tue, 10 Nov 2020 14:51:48 +0000   Tue, 10 Nov 2020 14:45:46 +0000   KubeletReady                 kubelet is posting ready status
Addresses:
  InternalIP:  10.0.2.15
  Hostname:    qemux86-64
Capacity:
  cpu:                1
  ephemeral-storage:  39748144Ki
  memory:             2040164Ki
  pods:               110
Allocatable:
  cpu:                1
  ephemeral-storage:  38666994453
  memory:             2040164Ki
  pods:               110
System Info:
  Machine ID:                 6a4abfacbf83457e9a0cbb5777457c5d
  System UUID:                6a4abfacbf83457e9a0cbb5777457c5d
  Boot ID:                    f5ddf6c8-1abf-4aef-9e29-106488e3c337
  Kernel Version:             5.8.13-yocto-standard
  OS Image:                   Poky (Yocto Project Reference Distro) 3.2+snapshot-20201105 (master)
  Operating System:           linux
  Architecture:               amd64
  Container Runtime Version:  containerd://1.4.1-4-ge44e8ebea.m
  Kubelet Version:            v1.18.9-k3s1
  Kube-Proxy Version:         v1.18.9-k3s1
PodCIDR:                      10.42.0.0/24
PodCIDRs:                     10.42.0.0/24
ProviderID:                   k3s://qemux86-64
Non-terminated Pods:          (5 in total)
  Namespace                   Name                                      CPU Requests  CPU Limits  Memory Requests  Memory Limits  AGE
  ---------                   ----                                      ------------  ----------  ---------------  -------------  ---
  kube-system                 svclb-traefik-jpmnd                       0 (0%)        0 (0%)      0 (0%)           0 (0%)         54m
  kube-system                 metrics-server-7566d596c8-wh29d           0 (0%)        0 (0%)      0 (0%)           0 (0%)         56m
  kube-system                 local-path-provisioner-6d59f47c7-npn4d    0 (0%)        0 (0%)      0 (0%)           0 (0%)         56m
  kube-system                 coredns-7944c66d8d-md8hr                  100m (10%)    0 (0%)      70Mi (3%)        170Mi (8%)     56m
  kube-system                 traefik-758cd5fc85-phjr2                  0 (0%)        0 (0%)      0 (0%)           0 (0%)         54m
Allocated resources:
  (Total limits may be over 100 percent, i.e., overcommitted.)
  Resource           Requests    Limits
  --------           --------    ------
  cpu                100m (10%)  0 (0%)
  memory             70Mi (3%)   170Mi (8%)
  ephemeral-storage  0 (0%)      0 (0%)
Events:
  Type     Reason                   Age                From        Message
  ----     ------                   ----               ----        -------
  Normal   Starting                 56m                kube-proxy  Starting kube-proxy.
  Normal   Starting                 55m                kubelet     Starting kubelet.
  Warning  InvalidDiskCapacity      55m                kubelet     invalid capacity 0 on image filesystem
  Normal   NodeHasSufficientPID     55m (x2 over 55m)  kubelet     Node qemux86-64 status is now: NodeHasSufficientPID
  Normal   NodeHasSufficientMemory  55m (x2 over 55m)  kubelet     Node qemux86-64 status is now: NodeHasSufficientMemory
  Normal   NodeHasNoDiskPressure    55m (x2 over 55m)  kubelet     Node qemux86-64 status is now: NodeHasNoDiskPressure
  Normal   NodeAllocatableEnforced  55m                kubelet     Updated Node Allocatable limit across pods
  Normal   NodeReady                54m                kubelet     Node qemux86-64 status is now: NodeReady
  Normal   Starting                 52m                kube-proxy  Starting kube-proxy.
  Normal   NodeReady                50m                kubelet     Node qemux86-64 status is now: NodeReady
  Normal   NodeAllocatableEnforced  50m                kubelet     Updated Node Allocatable limit across pods
  Warning  Rebooted                 50m                kubelet     Node qemux86-64 has been rebooted, boot id: a4e4d2d8-ddb4-49b8-b0a9-e81d12707113
  Normal   NodeHasSufficientMemory  50m (x2 over 50m)  kubelet     Node qemux86-64 status is now: NodeHasSufficientMemory
  Normal   Starting                 50m                kubelet     Starting kubelet.
  Normal   NodeHasSufficientPID     50m (x2 over 50m)  kubelet     Node qemux86-64 status is now: NodeHasSufficientPID
  Normal   NodeHasNoDiskPressure    50m (x2 over 50m)  kubelet     Node qemux86-64 status is now: NodeHasNoDiskPressure
  Normal   NodeNotReady             17m                kubelet     Node qemux86-64 status is now: NodeNotReady
  Warning  InvalidDiskCapacity      15m (x2 over 50m)  kubelet     invalid capacity 0 on image filesystem
  Normal   Starting                 12m                kube-proxy  Starting kube-proxy.
  Normal   Starting                 10m                kubelet     Starting kubelet.
  Warning  InvalidDiskCapacity      10m                kubelet     invalid capacity 0 on image filesystem
  Normal   NodeAllocatableEnforced  10m                kubelet     Updated Node Allocatable limit across pods
  Warning  Rebooted                 10m                kubelet     Node qemux86-64 has been rebooted, boot id: f5ddf6c8-1abf-4aef-9e29-106488e3c337
  Normal   NodeHasSufficientMemory  10m (x2 over 10m)  kubelet     Node qemux86-64 status is now: NodeHasSufficientMemory
  Normal   NodeHasNoDiskPressure    10m (x2 over 10m)  kubelet     Node qemux86-64 status is now: NodeHasNoDiskPressure
  Normal   NodeHasSufficientPID     10m (x2 over 10m)  kubelet     Node qemux86-64 status is now: NodeHasSufficientPID
  Normal   NodeReady                10m                kubelet     Node qemux86-64 status is now: NodeReady
```
