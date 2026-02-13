SUMMARY = "Production-Grade Container Scheduling and Management"
DESCRIPTION = "Lightweight Kubernetes, intended to be a fully compliant Kubernetes."
HOMEPAGE = "https://k3s.io/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/src/import/LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI = "git://github.com/rancher/k3s.git;branch=master;name=k3s;protocol=https \
           file://k3s.service \
           file://k3s-agent.service \
           file://k3s-agent \
           file://k3s-clean \
           file://cni-containerd-net.conf \
           file://0001-Finding-host-local-in-usr-libexec.patch;patchdir=src/import \
           file://k3s-killall.sh \
           file://modules.txt \
          "

SRC_URI[k3s.md5sum] = "363d3a08dc0b72ba6e6577964f6e94a5"
SRCREV_k3s = "24da6adfa98cd05849edf1c42de3862f843a05f4"

SRCREV_FORMAT = "k3s_fuse"
PV = "v1.24.3+k3s1+git${SRCREV_k3s}"

include src_uri.inc

CNI_NETWORKING_FILES ?= "${WORKDIR}/cni-containerd-net.conf"

inherit go
inherit goarch
inherit systemd
inherit cni_networking

PACKAGECONFIG = ""
PACKAGECONFIG[upx] = ",,upx-native"
GO_IMPORT = "import"
GO_BUILD_LDFLAGS = "-X github.com/k3s-io/k3s/pkg/version.Version=${PV} \
                    -X github.com/k3s-io/k3s/pkg/version.GitCommit=${@d.getVar('SRCREV_k3s', d, 1)[:8]} \
                    -w -s \
                   "
BIN_PREFIX ?= "${exec_prefix}/local"

inherit features_check
REQUIRED_DISTRO_FEATURES ?= "seccomp"

DEPENDS += "rsync-native"

do_compile() {
        export GOPATH="${S}/src/import/.gopath:${S}/src/import/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go"
        export CGO_ENABLED="1"
        export GOFLAGS="-mod=vendor"

        # TAGS="static_build ctrd no_btrfs netcgo osusergo providerless"
	TAGS="static_build netcgo osusergo providerless"

        cd ${S}/src/import

	sites="bazil.org/fuse:github.com/bazil/fuse \
           bitbucket.org/bertimus9/systemstat:bitbucket.org/bertimus9/systemstat \
           cloud.google.com/go:github.com/googleapis/google-cloud-go \
           cloud.google.com/go/bigquery:github.com/googleapis/google-cloud-go/bigquery//bigquery \
           cloud.google.com/go/datastore:github.com/googleapis/google-cloud-go/datastore//datastore \
           cloud.google.com/go/firestore:github.com/googleapis/google-cloud-go/datastore//firestore \
           cloud.google.com/go/pubsub:github.com/googleapis/google-cloud-go/pubsub//pubsub \
           cloud.google.com/go/storage:github.com/googleapis/google-cloud-go/storage//storage \
           dmitri.shuralyov.com/gpu/mtl:dmitri.shuralyov.com/gpu/mtl \
           github.com/360EntSecGroup-Skylar/excelize:github.com/360EntSecGroup-Skylar/excelize \
           github.com/Azure/azure-sdk-for-go:github.com/Azure/azure-sdk-for-go \
           github.com/Azure/go-ansiterm:github.com/Azure/go-ansiterm \
           github.com/Azure/go-autorest:github.com/Azure/go-autorest \
           github.com/Azure/go-autorest/autorest:github.com/Azure/go-autorest/autorest//autorest \
           github.com/Azure/go-autorest/autorest/adal:github.com/Azure/go-autorest/autorest/adal//autorest/adal \
           github.com/Azure/go-autorest/autorest/azure/auth:github.com/Azure/go-autorest/autorest/azure/auth//autorest/azure/auth \
           github.com/Azure/go-autorest/autorest/azure/cli:github.com/Azure/go-autorest/autorest/azure/cli//autorest/azure/cli \
           github.com/Azure/go-autorest/autorest/date:github.com/Azure/go-autorest/autorest/date//autorest/date \
           github.com/Azure/go-autorest/autorest/mocks:github.com/Azure/go-autorest/autorest/mocks//autorest/mocks \
           github.com/Azure/go-autorest/autorest/to:github.com/Azure/go-autorest/autorest/to//autorest/to \
           github.com/Azure/go-autorest/autorest/validation:github.com/Azure/go-autorest/autorest/validation//autorest/validation \
           github.com/Azure/go-autorest/logger:github.com/Azure/go-autorest/logger//logger \
           github.com/Azure/go-autorest/tracing:github.com/Azure/go-autorest/tracing//tracing \
           github.com/BurntSushi/toml:github.com/BurntSushi/toml \
           github.com/BurntSushi/xgb:github.com/BurntSushi/xgb \
           github.com/GoogleCloudPlatform/k8s-cloud-provider:github.com/GoogleCloudPlatform/k8s-cloud-provider \
           github.com/JeffAshton/win_pdh:github.com/JeffAshton/win_pdh \
           github.com/MakeNowJust/heredoc:github.com/MakeNowJust/heredoc \
           github.com/Microsoft/go-winio:github.com/Microsoft/go-winio \
           github.com/Microsoft/hcsshim:github.com/Microsoft/hcsshim \
           github.com/NYTimes/gziphandler:github.com/NYTimes/gziphandler \
           github.com/OneOfOne/xxhash:github.com/OneOfOne/xxhash \
           github.com/PuerkitoBio/goquery:github.com/PuerkitoBio/goquery \
           github.com/PuerkitoBio/purell:github.com/PuerkitoBio/purell \
           github.com/PuerkitoBio/urlesc:github.com/PuerkitoBio/urlesc \
           github.com/Rican7/retry:github.com/Rican7/retry \
           github.com/agext/levenshtein:github.com/agext/levenshtein \
           github.com/agnivade/levenshtein:github.com/agnivade/levenshtein \
           github.com/ajstarks/svgo:github.com/ajstarks/svgo \
           github.com/alecthomas/template:github.com/alecthomas/template \
           github.com/alecthomas/units:github.com/alecthomas/units \
           github.com/alexflint/go-filemutex:github.com/alexflint/go-filemutex \
           github.com/andreyvit/diff:github.com/andreyvit/diff \
           github.com/andybalholm/cascadia:github.com/andybalholm/cascadia \
           github.com/antihax/optional:github.com/antihax/optional \
           github.com/antlr/antlr4/runtime/Go/antlr:github.com/antlr/antlr4/runtime/Go/antlr//runtime/Go/antlr \
           github.com/apparentlymart/go-dump:github.com/apparentlymart/go-dump \
           github.com/apparentlymart/go-textseg:github.com/apparentlymart/go-textseg \
           github.com/apparentlymart/go-textseg/v13:github.com/apparentlymart/go-textseg/v13 \
           github.com/armon/circbuf:github.com/armon/circbuf \
           github.com/armon/consul-api:github.com/armon/consul-api \
           github.com/armon/go-metrics:github.com/armon/go-metrics \
           github.com/armon/go-radix:github.com/armon/go-radix \
           github.com/armon/go-socks5:github.com/armon/go-socks5 \
           github.com/asaskevich/govalidator:github.com/asaskevich/govalidator \
           github.com/auth0/go-jwt-middleware:github.com/auth0/go-jwt-middleware \
           github.com/aws/aws-lambda-go:github.com/aws/aws-lambda-go \
           github.com/aws/aws-sdk-go:github.com/aws/aws-sdk-go \
           github.com/benbjohnson/clock:github.com/benbjohnson/clock \
           github.com/beorn7/perks:github.com/beorn7/perks \
           github.com/bgentry/go-netrc:github.com/bgentry/go-netrc \
           github.com/bgentry/speakeasy:github.com/bgentry/speakeasy \
           github.com/bketelsen/crypt:github.com/bketelsen/crypt \
           github.com/blang/semver:github.com/blang/semver \
           github.com/blang/semver/v4:github.com/blang/semver/v4//v4 \
           github.com/boltdb/bolt:github.com/boltdb/bolt \
           github.com/boombuler/barcode:github.com/boombuler/barcode \
           github.com/bronze1man/goStrongswanVici:github.com/bronze1man/goStrongswanVici \
           github.com/buger/jsonparser:github.com/buger/jsonparser \
           github.com/canonical/go-dqlite:github.com/canonical/go-dqlite \
           github.com/census-instrumentation/opencensus-proto:github.com/census-instrumentation/opencensus-proto \
           github.com/certifi/gocertifi:github.com/certifi/gocertifi \
           github.com/cespare/xxhash:github.com/cespare/xxhash \
           github.com/cespare/xxhash/v2:github.com/cespare/xxhash/v2 \
           github.com/chai2010/gettext-go:github.com/chai2010/gettext-go \
           github.com/checkpoint-restore/go-criu/v5:github.com/checkpoint-restore/go-criu/v5 \
           github.com/cheggaaa/pb:github.com/cheggaaa/pb \
           github.com/chzyer/logex:github.com/chzyer/logex \
           github.com/chzyer/readline:github.com/chzyer/readline \
           github.com/chzyer/test:github.com/chzyer/test \
           github.com/cilium/ebpf:github.com/cilium/ebpf \
           github.com/cloudnativelabs/kube-router:github.com/cloudnativelabs/kube-router \
           github.com/clusterhq/flocker-go:github.com/clusterhq/flocker-go \
           github.com/cncf/udpa/go:github.com/cncf/udpa/go//go \
           github.com/cncf/xds/go:github.com/cncf/xds/go//go \
           github.com/cockroachdb/datadriven:github.com/cockroachdb/datadriven \
           github.com/cockroachdb/errors:github.com/cockroachdb/errors \
           github.com/cockroachdb/logtags:github.com/cockroachdb/logtags \
           github.com/container-storage-interface/spec:github.com/container-storage-interface/spec \
           github.com/containerd/aufs:github.com/containerd/aufs \
           github.com/containerd/btrfs:github.com/containerd/btrfs \
           github.com/containerd/cgroups:github.com/containerd/cgroups \
           github.com/containerd/console:github.com/containerd/console \
           github.com/containerd/containerd:github.com/containerd/containerd \
           github.com/containerd/continuity:github.com/containerd/continuity \
           github.com/containerd/fifo:github.com/containerd/fifo \
           github.com/containerd/fuse-overlayfs-snapshotter:github.com/containerd/fuse-overlayfs-snapshotter \
           github.com/containerd/go-cni:github.com/containerd/go-cni \
           github.com/containerd/go-runc:github.com/containerd/go-runc \
           github.com/containerd/imgcrypt:github.com/containerd/imgcrypt \
           github.com/containerd/nri:github.com/containerd/nri \
           github.com/containerd/stargz-snapshotter:github.com/containerd/stargz-snapshotter \
           github.com/containerd/stargz-snapshotter/estargz:github.com/containerd/stargz-snapshotter//estargz \
           github.com/containerd/ttrpc:github.com/containerd/ttrpc \
           github.com/containerd/typeurl:github.com/containerd/typeurl \
           github.com/containerd/zfs:github.com/containerd/zfs \
           github.com/containernetworking/cni:github.com/containernetworking/cni \
           github.com/containernetworking/plugins:github.com/containernetworking/plugins \
           github.com/containers/ocicrypt:github.com/containers/ocicrypt \
           github.com/coredns/caddy:github.com/coredns/caddy \
           github.com/coredns/corefile-migration:github.com/coredns/corefile-migration \
           github.com/coreos/bbolt:github.com/coreos/bbolt \
           github.com/coreos/etcd:github.com/coreos/etcd \
           github.com/coreos/go-iptables:github.com/coreos/go-iptables \
           github.com/coreos/go-oidc:github.com/coreos/go-oidc \
           github.com/coreos/go-semver:github.com/coreos/go-semver \
           github.com/coreos/go-systemd:github.com/coreos/go-systemd \
           github.com/coreos/go-systemd/v22:github.com/coreos/go-systemd/v22 \
           github.com/coreos/pkg:github.com/coreos/pkg \
           github.com/cpuguy83/go-md2man/v2:github.com/cpuguy83/go-md2man/v2 \
           github.com/creack/pty:github.com/creack/pty \
           github.com/cyphar/filepath-securejoin:github.com/cyphar/filepath-securejoin \
           github.com/d2g/dhcp4:github.com/d2g/dhcp4 \
           github.com/d2g/dhcp4client:github.com/d2g/dhcp4client \
           github.com/d2g/dhcp4server:github.com/d2g/dhcp4server \
           github.com/d2g/hardwareaddr:github.com/d2g/hardwareaddr \
           github.com/danieljoos/wincred:github.com/danieljoos/wincred \
           github.com/davecgh/go-spew:github.com/davecgh/go-spew \
           github.com/daviddengcn/go-colortext:github.com/daviddengcn/go-colortext \
           github.com/denverdino/aliyungo:github.com/denverdino/aliyungo \
           github.com/dgrijalva/jwt-go:github.com/dgrijalva/jwt-go \
           github.com/dgryski/go-farm:github.com/dgryski/go-farm \
           github.com/dgryski/go-sip13:github.com/dgryski/go-sip13 \
           github.com/dimchansky/utfbom:github.com/dimchansky/utfbom \
           github.com/dnaeon/go-vcr:github.com/dnaeon/go-vcr \
           github.com/docker/cli:github.com/docker/cli \
           github.com/docker/distribution:github.com/docker/distribution \
           github.com/docker/docker:github.com/docker/docker \
           github.com/docker/docker-credential-helpers:github.com/docker/docker-credential-helpers \
           github.com/docker/go-connections:github.com/docker/go-connections \
           github.com/docker/go-events:github.com/docker/go-events \
           github.com/docker/go-metrics:github.com/docker/go-metrics \
           github.com/docker/go-units:github.com/docker/go-units \
           github.com/docker/spdystream:github.com/docker/spdystream \
           github.com/docopt/docopt-go:github.com/docopt/docopt-go \
           github.com/dustin/go-humanize:github.com/dustin/go-humanize \
           github.com/dustmop/soup:github.com/dustmop/soup \
           github.com/eapache/channels:github.com/eapache/channels \
           github.com/eapache/queue:github.com/eapache/queue \
           github.com/elazarl/goproxy:github.com/elazarl/goproxy \
           github.com/elazarl/goproxy/ext:github.com/elazarl/goproxy/ext//ext \
           github.com/emicklei/go-restful:github.com/emicklei/go-restful \
           github.com/envoyproxy/go-control-plane:github.com/envoyproxy/go-control-plane \
           github.com/envoyproxy/protoc-gen-validate:github.com/envoyproxy/protoc-gen-validate \
           github.com/erikdubbelboer/gspt:github.com/erikdubbelboer/gspt \
           github.com/etcd-io/gofail:github.com/etcd-io/gofail \
           github.com/euank/go-kmsg-parser:github.com/euank/go-kmsg-parser \
           github.com/evanphx/json-patch:github.com/evanphx/json-patch \
           github.com/exponent-io/jsonpath:github.com/exponent-io/jsonpath \
           github.com/fanliao/go-promise:github.com/fanliao/go-promise \
           github.com/fatih/camelcase:github.com/fatih/camelcase \
           github.com/fatih/color:github.com/fatih/color \
           github.com/felixge/httpsnoop:github.com/felixge/httpsnoop \
           github.com/flannel-io/flannel:github.com/flannel-io/flannel \
           github.com/flynn/go-shlex:github.com/flynn/go-shlex \
           github.com/fogleman/gg:github.com/fogleman/gg \
           github.com/form3tech-oss/jwt-go:github.com/form3tech-oss/jwt-go \
           github.com/frankban/quicktest:github.com/frankban/quicktest \
           github.com/fsnotify/fsnotify:github.com/fsnotify/fsnotify \
           github.com/fvbommel/sortorder:github.com/fvbommel/sortorder \
           github.com/getkin/kin-openapi:github.com/getkin/kin-openapi \
           github.com/getsentry/raven-go:github.com/getsentry/raven-go \
           github.com/ghodss/yaml:github.com/ghodss/yaml \
           github.com/globalsign/mgo:github.com/globalsign/mgo \
           github.com/go-bindata/go-bindata:github.com/go-bindata/go-bindata \
           github.com/go-errors/errors:github.com/go-errors/errors \
           github.com/go-gl/glfw:github.com/go-gl/glfw \
           github.com/go-gl/glfw/v3.3/glfw:github.com/go-gl/glfw/v3.3/glfw//v3.3/glfw \
           github.com/go-ini/ini:github.com/go-ini/ini \
           github.com/go-kit/kit:github.com/go-kit/kit \
           github.com/go-kit/log:github.com/go-kit/log \
           github.com/go-logfmt/logfmt:github.com/go-logfmt/logfmt \
           github.com/go-logr/logr:github.com/go-logr/logr \
           github.com/go-logr/zapr:github.com/go-logr/zapr \
           github.com/go-openapi/analysis:github.com/go-openapi/analysis \
           github.com/go-openapi/errors:github.com/go-openapi/errors \
           github.com/go-openapi/jsonpointer:github.com/go-openapi/jsonpointer \
           github.com/go-openapi/jsonreference:github.com/go-openapi/jsonreference \
           github.com/go-openapi/loads:github.com/go-openapi/loads \
           github.com/go-openapi/runtime:github.com/go-openapi/runtime \
           github.com/go-openapi/spec:github.com/go-openapi/spec \
           github.com/go-openapi/strfmt:github.com/go-openapi/strfmt \
           github.com/go-openapi/swag:github.com/go-openapi/swag \
           github.com/go-openapi/validate:github.com/go-openapi/validate \
           github.com/go-ozzo/ozzo-validation:github.com/go-ozzo/ozzo-validation \
           github.com/go-sql-driver/mysql:github.com/go-sql-driver/mysql \
           github.com/go-stack/stack:github.com/go-stack/stack \
           github.com/go-task/slim-sprig:github.com/go-task/slim-sprig \
           github.com/go-test/deep:github.com/go-test/deep \
           github.com/godbus/dbus:github.com/godbus/dbus \
           github.com/godbus/dbus/v5:github.com/godbus/dbus/v5 \
           github.com/gofrs/flock:github.com/gofrs/flock \
           github.com/gofrs/uuid:github.com/gofrs/uuid \
           github.com/gogo/googleapis:github.com/gogo/googleapis \
           github.com/gogo/protobuf:github.com/gogo/protobuf \
           github.com/golang/freetype:github.com/golang/freetype \
           github.com/golang/glog:github.com/golang/glog \
           github.com/golang/groupcache:github.com/golang/groupcache \
           github.com/golang/mock:github.com/golang/mock \
           github.com/golang/protobuf:github.com/golang/protobuf \
           github.com/golang/snappy:github.com/golang/snappy \
           github.com/golangplus/bytes:github.com/golangplus/bytes \
           github.com/golangplus/fmt:github.com/golangplus/fmt \
           github.com/golangplus/testing:github.com/golangplus/testing \
           github.com/google/btree:github.com/google/btree \
           github.com/google/cadvisor:github.com/google/cadvisor \
           github.com/google/cel-go:github.com/google/cel-go \
           github.com/google/cel-spec:github.com/google/cel-spec \
           github.com/google/gnostic:github.com/google/gnostic \
           github.com/google/go-cmp:github.com/google/go-cmp \
           github.com/google/go-containerregistry:github.com/google/go-containerregistry \
           github.com/google/gofuzz:github.com/google/gofuzz \
           github.com/google/martian:github.com/google/martian \
           github.com/google/martian/v3:github.com/google/martian/v3 \
           github.com/google/pprof:github.com/google/pprof \
           github.com/google/renameio:github.com/google/renameio \
           github.com/google/shlex:github.com/google/shlex \
           github.com/google/uuid:github.com/google/uuid \
           github.com/googleapis/gax-go/v2:github.com/googleapis/gax-go/v2//v2 \
           github.com/googleapis/gnostic:github.com/googleapis/gnostic \
           github.com/gophercloud/gophercloud:github.com/gophercloud/gophercloud \
           github.com/gopherjs/gopherjs:github.com/gopherjs/gopherjs \
           github.com/gorilla/mux:github.com/gorilla/mux \
           github.com/gorilla/websocket:github.com/gorilla/websocket \
           github.com/gregjones/httpcache:github.com/gregjones/httpcache \
           github.com/grpc-ecosystem/go-grpc-middleware:github.com/grpc-ecosystem/go-grpc-middleware \
           github.com/grpc-ecosystem/go-grpc-prometheus:github.com/grpc-ecosystem/go-grpc-prometheus \
           github.com/grpc-ecosystem/grpc-gateway:github.com/grpc-ecosystem/grpc-gateway \
           github.com/gruntwork-io/go-commons:github.com/gruntwork-io/go-commons \
           github.com/gruntwork-io/terratest:github.com/gruntwork-io/terratest \
           github.com/hanwen/go-fuse/v2:github.com/hanwen/go-fuse/v2 \
           github.com/hashicorp/consul/api:github.com/hashicorp/consul/api//api \
           github.com/hashicorp/consul/sdk:github.com/hashicorp/consul/sdk//sdk \
           github.com/hashicorp/errwrap:github.com/hashicorp/errwrap \
           github.com/hashicorp/go-cleanhttp:github.com/hashicorp/go-cleanhttp \
           github.com/hashicorp/go-getter:github.com/hashicorp/go-getter \
           github.com/hashicorp/go-hclog:github.com/hashicorp/go-hclog \
           github.com/hashicorp/go-immutable-radix:github.com/hashicorp/go-immutable-radix \
           github.com/hashicorp/go-msgpack:github.com/hashicorp/go-msgpack \
           github.com/hashicorp/go-multierror:github.com/hashicorp/go-multierror \
           github.com/hashicorp/go-retryablehttp:github.com/hashicorp/go-retryablehttp \
           github.com/hashicorp/go-rootcerts:github.com/hashicorp/go-rootcerts \
           github.com/hashicorp/go-safetemp:github.com/hashicorp/go-safetemp \
           github.com/hashicorp/go-sockaddr:github.com/hashicorp/go-sockaddr \
           github.com/hashicorp/go-syslog:github.com/hashicorp/go-syslog \
           github.com/hashicorp/go-uuid:github.com/hashicorp/go-uuid \
           github.com/hashicorp/go-version:github.com/hashicorp/go-version \
           github.com/hashicorp/go.net:github.com/hashicorp/go.net \
           github.com/hashicorp/golang-lru:github.com/hashicorp/golang-lru \
           github.com/hashicorp/hcl:github.com/hashicorp/hcl \
           github.com/hashicorp/hcl/v2:github.com/hashicorp/hcl/v2 \
           github.com/hashicorp/logutils:github.com/hashicorp/logutils \
           github.com/hashicorp/mdns:github.com/hashicorp/mdns \
           github.com/hashicorp/memberlist:github.com/hashicorp/memberlist \
           github.com/hashicorp/serf:github.com/hashicorp/serf \
           github.com/hashicorp/terraform-json:github.com/hashicorp/terraform-json \
           github.com/heketi/heketi:github.com/heketi/heketi \
           github.com/heketi/tests:github.com/heketi/tests \
           github.com/hpcloud/tail:github.com/hpcloud/tail \
           github.com/hugelgupf/socketpair:github.com/hugelgupf/socketpair \
           github.com/ianlancetaylor/demangle:github.com/ianlancetaylor/demangle \
           github.com/imdario/mergo:github.com/imdario/mergo \
           github.com/inconshreveable/mousetrap:github.com/inconshreveable/mousetrap \
           github.com/insomniacslk/dhcp:github.com/insomniacslk/dhcp \
           github.com/ishidawataru/sctp:github.com/ishidawataru/sctp \
           github.com/j-keck/arping:github.com/j-keck/arping \
           github.com/jessevdk/go-flags:github.com/jessevdk/go-flags \
           github.com/jinzhu/copier:github.com/jinzhu/copier \
           github.com/jmespath/go-jmespath:github.com/jmespath/go-jmespath \
           github.com/jmespath/go-jmespath/internal/testify:github.com/jmespath/go-jmespath/internal/testify//internal/testify \
           github.com/joho/godotenv:github.com/joho/godotenv \
           github.com/jonboulle/clockwork:github.com/jonboulle/clockwork \
           github.com/josharian/intern:github.com/josharian/intern \
           github.com/josharian/native:github.com/josharian/native \
           github.com/jpillora/backoff:github.com/jpillora/backoff \
           github.com/jsimonetti/rtnetlink:github.com/jsimonetti/rtnetlink \
           github.com/json-iterator/go:github.com/json-iterator/go \
           github.com/jstemmer/go-junit-report:github.com/jstemmer/go-junit-report \
           github.com/jtolds/gls:github.com/jtolds/gls \
           github.com/julienschmidt/httprouter:github.com/julienschmidt/httprouter \
           github.com/jung-kurt/gofpdf:github.com/jung-kurt/gofpdf \
           github.com/k-sone/critbitgo:github.com/k-sone/critbitgo \
           github.com/k3s-io/helm-controller:github.com/k3s-io/helm-controller \
           github.com/k3s-io/kine:github.com/k3s-io/kine \
           github.com/karrick/godirwalk:github.com/karrick/godirwalk \
           github.com/kisielk/errcheck:github.com/kisielk/errcheck \
           github.com/kisielk/gotool:github.com/kisielk/gotool \
           github.com/klauspost/compress:github.com/klauspost/compress \
           github.com/klauspost/cpuid:github.com/klauspost/cpuid \
           github.com/konsorten/go-windows-terminal-sequences:github.com/konsorten/go-windows-terminal-sequences \
           github.com/kr/fs:github.com/kr/fs \
           github.com/kr/logfmt:github.com/kr/logfmt \
           github.com/kr/pretty:github.com/kr/pretty \
           github.com/kr/pty:github.com/kr/pty \
           github.com/kr/text:github.com/kr/text \
           github.com/kubernetes-sigs/cri-tools:github.com/kubernetes-sigs/cri-tools \
           github.com/kylelemons/godebug:github.com/kylelemons/godebug \
           github.com/lib/pq:github.com/lib/pq \
           github.com/libopenstorage/openstorage:github.com/libopenstorage/openstorage \
           github.com/liggitt/tabwriter:github.com/liggitt/tabwriter \
           github.com/lithammer/dedent:github.com/lithammer/dedent \
           github.com/lpabon/godbc:github.com/lpabon/godbc \
           github.com/magiconair/properties:github.com/magiconair/properties \
           github.com/mailru/easyjson:github.com/mailru/easyjson \
           github.com/mattn/go-colorable:github.com/mattn/go-colorable \
           github.com/mattn/go-isatty:github.com/mattn/go-isatty \
           github.com/mattn/go-runewidth:github.com/mattn/go-runewidth \
           github.com/mattn/go-shellwords:github.com/mattn/go-shellwords \
           github.com/mattn/go-sqlite3:github.com/mattn/go-sqlite3 \
           github.com/mattn/go-zglob:github.com/mattn/go-zglob \
           github.com/matttproud/golang_protobuf_extensions:github.com/matttproud/golang_protobuf_extensions \
           github.com/mdlayher/ethernet:github.com/mdlayher/ethernet \
           github.com/mdlayher/ethtool:github.com/mdlayher/ethtool \
           github.com/mdlayher/genetlink:github.com/mdlayher/genetlink \
           github.com/mdlayher/netlink:github.com/mdlayher/netlink \
           github.com/mdlayher/raw:github.com/mdlayher/raw \
           github.com/mdlayher/socket:github.com/mdlayher/socket \
           github.com/miekg/dns:github.com/miekg/dns \
           github.com/miekg/pkcs11:github.com/miekg/pkcs11 \
           github.com/mikioh/ipaddr:github.com/mikioh/ipaddr \
           github.com/mindprince/gonvml:github.com/mindprince/gonvml \
           github.com/minio/highwayhash:github.com/minio/highwayhash \
           github.com/minio/md5-simd:github.com/minio/md5-simd \
           github.com/minio/minio-go/v7:github.com/minio/minio-go/v7 \
           github.com/minio/sha256-simd:github.com/minio/sha256-simd \
           github.com/minio/sio:github.com/minio/sio \
           github.com/mistifyio/go-zfs:github.com/mistifyio/go-zfs \
           github.com/mitchellh/cli:github.com/mitchellh/cli \
           github.com/mitchellh/copystructure:github.com/mitchellh/copystructure \
           github.com/mitchellh/go-homedir:github.com/mitchellh/go-homedir \
           github.com/mitchellh/go-testing-interface:github.com/mitchellh/go-testing-interface \
           github.com/mitchellh/go-wordwrap:github.com/mitchellh/go-wordwrap \
           github.com/mitchellh/gox:github.com/mitchellh/gox \
           github.com/mitchellh/iochan:github.com/mitchellh/iochan \
           github.com/mitchellh/mapstructure:github.com/mitchellh/mapstructure \
           github.com/mitchellh/reflectwalk:github.com/mitchellh/reflectwalk \
           github.com/moby/ipvs:github.com/moby/ipvs \
           github.com/moby/locker:github.com/moby/locker \
           github.com/moby/spdystream:github.com/moby/spdystream \
           github.com/moby/sys/mountinfo:github.com/moby/sys/mountinfo//mountinfo \
           github.com/moby/sys/symlink:github.com/moby/sys/symlink/symlink \
           github.com/moby/term:github.com/moby/term \
           github.com/moby/vpnkit:github.com/moby/vpnkit \
           github.com/modern-go/concurrent:github.com/modern-go/concurrent \
           github.com/modern-go/reflect2:github.com/modern-go/reflect2 \
           github.com/mohae/deepcopy:github.com/mohae/deepcopy \
           github.com/monochromegane/go-gitignore:github.com/monochromegane/go-gitignore \
           github.com/morikuni/aec:github.com/morikuni/aec \
           github.com/mrunalp/fileutils:github.com/mrunalp/fileutils \
           github.com/munnerz/goautoneg:github.com/munnerz/goautoneg \
           github.com/mvdan/xurls:github.com/mvdan/xurls \
           github.com/mwitkow/go-conntrack:github.com/mwitkow/go-conntrack \
           github.com/mxk/go-flowrate:github.com/mxk/go-flowrate \
           github.com/natefinch/lumberjack:github.com/natefinch/lumberjack \
           github.com/nats-io/jsm.go:github.com/nats-io/jsm.go \
           github.com/nats-io/jwt/v2:github.com/nats-io/jwt/v2//v2 \
           github.com/nats-io/nats-server/v2:github.com/nats-io/nats-server/v2 \
           github.com/nats-io/nats.go:github.com/nats-io/nats.go \
           github.com/nats-io/nkeys:github.com/nats-io/nkeys \
           github.com/nats-io/nuid:github.com/nats-io/nuid \
           github.com/networkplumbing/go-nft:github.com/networkplumbing/go-nft \
           github.com/niemeyer/pretty:github.com/niemeyer/pretty \
           github.com/nxadm/tail:github.com/nxadm/tail \
           github.com/oklog/ulid:github.com/oklog/ulid \
           github.com/olekukonko/tablewriter:github.com/olekukonko/tablewriter \
           github.com/onsi/ginkgo:github.com/onsi/ginkgo \
           github.com/onsi/ginkgo/v2:github.com/onsi/ginkgo/v2 \
           github.com/onsi/gomega:github.com/onsi/gomega \
           github.com/opencontainers/go-digest:github.com/opencontainers/go-digest \
           github.com/opencontainers/image-spec:github.com/opencontainers/image-spec \
           github.com/opencontainers/runc:github.com/opencontainers/runc \
           github.com/opencontainers/runtime-spec:github.com/opencontainers/runtime-spec \
           github.com/opencontainers/selinux:github.com/opencontainers/selinux \
           github.com/opentracing/opentracing-go:github.com/opentracing/opentracing-go \
           github.com/oracle/oci-go-sdk:github.com/oracle/oci-go-sdk \
           github.com/osrg/gobgp:github.com/osrg/gobgp \
           github.com/otiai10/copy:github.com/otiai10/copy \
           github.com/otiai10/curr:github.com/otiai10/curr \
           github.com/otiai10/mint:github.com/otiai10/mint \
           github.com/pascaldekloe/goe:github.com/pascaldekloe/goe \
           github.com/paulmach/orb:github.com/paulmach/orb \
           github.com/pborman/uuid:github.com/pborman/uuid \
           github.com/pelletier/go-toml:github.com/pelletier/go-toml \
           github.com/peterbourgon/diskv:github.com/peterbourgon/diskv \
           github.com/pierrec/lz4:github.com/pierrec/lz4 \
           github.com/pkg/errors:github.com/pkg/errors \
           github.com/pkg/sftp:github.com/pkg/sftp \
           github.com/pmezard/go-difflib:github.com/pmezard/go-difflib \
           github.com/posener/complete:github.com/posener/complete \
           github.com/pquerna/cachecontrol:github.com/pquerna/cachecontrol \
           github.com/pquerna/otp:github.com/pquerna/otp \
           github.com/prometheus/client_golang:github.com/prometheus/client_golang \
           github.com/prometheus/client_model:github.com/prometheus/client_model \
           github.com/prometheus/common:github.com/prometheus/common \
           github.com/prometheus/procfs:github.com/prometheus/procfs \
           github.com/prometheus/tsdb:github.com/prometheus/tsdb \
           github.com/qri-io/starlib:github.com/qri-io/starlib \
           github.com/quobyte/api:github.com/quobyte/api \
           github.com/rancher/dynamiclistener:github.com/rancher/dynamiclistener \
           github.com/rancher/lasso:github.com/rancher/lasso \
           github.com/rancher/remotedialer:github.com/rancher/remotedialer \
           github.com/rancher/wharfie:github.com/rancher/wharfie \
           github.com/rancher/wrangler:github.com/rancher/wrangler \
           github.com/remyoudompheng/bigfft:github.com/remyoudompheng/bigfft \
           github.com/robfig/cron/v3:github.com/robfig/cron/v3 \
           github.com/rogpeppe/fastuuid:github.com/rogpeppe/fastuuid \
           github.com/rogpeppe/go-charset:github.com/rogpeppe/go-charset \
           github.com/rogpeppe/go-internal:github.com/rogpeppe/go-internal \
           github.com/rootless-containers/rootlesskit:github.com/rootless-containers/rootlesskit \
           github.com/rs/xid:github.com/rs/xid \
           github.com/rubiojr/go-vhd:github.com/rubiojr/go-vhd \
           github.com/russross/blackfriday:github.com/russross/blackfriday \
           github.com/russross/blackfriday/v2:github.com/russross/blackfriday/v2 \
           github.com/ryanuber/columnize:github.com/ryanuber/columnize \
           github.com/safchain/ethtool:github.com/safchain/ethtool \
           github.com/satori/go.uuid:github.com/satori/go.uuid \
           github.com/sclevine/agouti:github.com/sclevine/agouti \
           github.com/sean-/seed:github.com/sean-/seed \
           github.com/sebdah/goldie:github.com/sebdah/goldie \
           github.com/seccomp/libseccomp-golang:github.com/seccomp/libseccomp-golang \
           github.com/sergi/go-diff:github.com/sergi/go-diff \
           github.com/shengdoushi/base58:github.com/shengdoushi/base58 \
           github.com/shurcooL/sanitized_anchor_name:github.com/shurcooL/sanitized_anchor_name \
           github.com/sirupsen/logrus:github.com/sirupsen/logrus \
           github.com/smartystreets/assertions:github.com/smartystreets/assertions \
           github.com/smartystreets/goconvey:github.com/smartystreets/goconvey \
           github.com/soheilhy/cmux:github.com/soheilhy/cmux \
           github.com/songgao/water:github.com/songgao/water \
           github.com/spaolacci/murmur3:github.com/spaolacci/murmur3 \
           github.com/spf13/afero:github.com/spf13/afero \
           github.com/spf13/cast:github.com/spf13/cast \
           github.com/spf13/cobra:github.com/spf13/cobra \
           github.com/spf13/jwalterweatherman:github.com/spf13/jwalterweatherman \
           github.com/spf13/pflag:github.com/spf13/pflag \
           github.com/spf13/viper:github.com/spf13/viper \
           github.com/spyzhov/ajson:github.com/spyzhov/ajson \
           github.com/stefanberger/go-pkcs11uri:github.com/stefanberger/go-pkcs11uri \
           github.com/stoewer/go-strcase:github.com/stoewer/go-strcase \
           github.com/storageos/go-api:github.com/storageos/go-api \
           github.com/stretchr/objx:github.com/stretchr/objx \
           github.com/stretchr/testify:github.com/stretchr/testify \
           github.com/subosito/gotenv:github.com/subosito/gotenv \
           github.com/syndtr/gocapability:github.com/syndtr/gocapability \
           github.com/tchap/go-patricia:github.com/tchap/go-patricia \
           github.com/tencentcloud/tencentcloud-sdk-go:github.com/tencentcloud/tencentcloud-sdk-go \
           github.com/tidwall/pretty:github.com/tidwall/pretty \
           github.com/tmc/grpc-websocket-proxy:github.com/tmc/grpc-websocket-proxy \
           github.com/tmccombs/hcl2json:github.com/tmccombs/hcl2json \
           github.com/u-root/uio:github.com/u-root/uio \
           github.com/ugorji/go:github.com/ugorji/go \
           github.com/ulikunitz/xz:github.com/ulikunitz/xz \
           github.com/urfave/cli:github.com/urfave/cli \
           github.com/urfave/cli/v2:github.com/urfave/cli/v2 \
           github.com/urfave/negroni:github.com/urfave/negroni \
           github.com/vbatts/tar-split:github.com/vbatts/tar-split \
           github.com/vektah/gqlparser:github.com/vektah/gqlparser \
           github.com/vishvananda/netlink:github.com/vishvananda/netlink \
           github.com/vishvananda/netns:github.com/vishvananda/netns \
           github.com/vmihailenco/msgpack:github.com/vmihailenco/msgpack \
           github.com/vmihailenco/msgpack/v4:github.com/vmihailenco/msgpack/v4 \
           github.com/vmihailenco/tagparser:github.com/vmihailenco/tagparser \
           github.com/vmware/govmomi:github.com/vmware/govmomi \
           github.com/xiang90/probing:github.com/xiang90/probing \
           github.com/xlab/treeprint:github.com/xlab/treeprint \
           github.com/xordataexchange/crypt:github.com/xordataexchange/crypt \
           github.com/yl2chen/cidranger:github.com/yl2chen/cidranger \
           github.com/yuin/goldmark:github.com/yuin/goldmark \
           github.com/zclconf/go-cty:github.com/zclconf/go-cty \
           github.com/zclconf/go-cty-debug:github.com/zclconf/go-cty-debug \
           go.etcd.io/bbolt:go.etcd.io/bbolt \
           go.etcd.io/etcd/api/v3:github.com/k3s-io/etcd/api/v3//api \
           go.etcd.io/etcd/client/pkg/v3:github.com/k3s-io/etcd/api/v3//client/pkg \
           go.etcd.io/etcd/client/v3:github.com/k3s-io/etcd/api/v3//client/v3 \
           go.etcd.io/etcd/etcdutl/v3:github.com/k3s-io/etcd/api/v3//etcdutl \
           go.etcd.io/etcd/server/v3:github.com/k3s-io/etcd/api/v3//server \
           go.etcd.io/etcd/client/v2:github.com/etcd-io/etcd/client/v2//client/v2 \
           go.etcd.io/etcd/pkg/v3:github.com/etcd-io/etcd/client/v2//pkg \
           go.etcd.io/etcd/raft/v3:github.com/etcd-io/etcd/client/v2//raft \
           go.etcd.io/etcd/tests/v3:github.com/etcd-io/etcd/client/v2//tests \
           go.mozilla.org/pkcs7:go.mozilla.org/pkcs7 \
           go.opencensus.io:go.opencensus.io \
           go.opentelemetry.io/contrib:go.opentelemetry.io/contrib \
           go.opentelemetry.io/contrib/instrumentation/google.golang.org/grpc/otelgrpc:go.opentelemetry.io/contrib//instrumentation/google.golang.org/grpc/otelgrpc \
           go.opentelemetry.io/contrib/instrumentation/net/http/otelhttp:go.opentelemetry.io/contrib//instrumentation/net/http/otelhttp \
           go.opentelemetry.io/otel:go.opentelemetry.io/otel \
           go.opentelemetry.io/otel/exporters/otlp:go.opentelemetry.io/otel//exporters/otlp \
           go.opentelemetry.io/otel/metric:go.opentelemetry.io/otel//metric \
           go.opentelemetry.io/otel/oteltest:go.opentelemetry.io/otel//oteltest \
           go.opentelemetry.io/otel/sdk:go.opentelemetry.io/otel//sdk \
           go.opentelemetry.io/otel/sdk/export/metric:go.opentelemetry.io/otel//sdk/export/metric \
           go.opentelemetry.io/otel/sdk/metric:go.opentelemetry.io/otel//sdk/metric \
           go.opentelemetry.io/otel/trace:go.opentelemetry.io/otel//trace \
           go.opentelemetry.io/proto/otlp:github.com/open-telemetry/opentelemetry-proto-go/otlp//otlp \
           go.starlark.net:github.com/google/starlark-go \
           go.uber.org/atomic:go.uber.org/atomic \
           go.uber.org/goleak:go.uber.org/goleak \
           go.uber.org/multierr:go.uber.org/multierr \
           go.uber.org/zap:go.uber.org/zap \
           golang.org/x/crypto:golang.org/x/crypto \
           golang.org/x/exp:golang.org/x/exp \
           golang.org/x/image:golang.org/x/image \
           golang.org/x/lint:golang.org/x/lint \
           golang.org/x/mobile:golang.org/x/mobile \
           golang.org/x/mod:golang.org/x/mod \
           golang.org/x/net:golang.org/x/net \
           golang.org/x/oauth2:golang.org/x/oauth2 \
           golang.org/x/sync:golang.org/x/sync \
           golang.org/x/sys:golang.org/x/sys \
           golang.org/x/term:golang.org/x/term \
           golang.org/x/text:golang.org/x/text \
           golang.org/x/time:golang.org/x/time \
           golang.org/x/tools:golang.org/x/tools \
           golang.org/x/xerrors:golang.org/x/xerrors \
           golang.zx2c4.com/wintun:golang.zx2c4.com/wintun \
           golang.zx2c4.com/wireguard:golang.zx2c4.com/wireguard \
           golang.zx2c4.com/wireguard/wgctrl:golang.zx2c4.com/wireguard/wgctrl \
           gonum.org/v1/gonum:github.com/gonum/gonum \
           gonum.org/v1/netlib:github.com/gonum/netlib \
           gonum.org/v1/plot:github.com/gonum/plot \
           google.golang.org/api:google.golang.org/api \
           google.golang.org/appengine:google.golang.org/appengine \
           google.golang.org/genproto:google.golang.org/genproto \
           google.golang.org/grpc:github.com/grpc/grpc-go  \
           google.golang.org/grpc/cmd/protoc-gen-go-grpc:google.golang.org/grpc/cmd/protoc-gen-go-grpc \
           google.golang.org/protobuf:google.golang.org/protobuf \
           gopkg.in/airbrake/gobrake.v2:gopkg.in/airbrake/gobrake.v2 \
           gopkg.in/alecthomas/kingpin.v2:gopkg.in/alecthomas/kingpin.v2 \
           gopkg.in/check.v1:gopkg.in/check.v1 \
           gopkg.in/cheggaaa/pb.v1:gopkg.in/cheggaaa/pb.v1 \
           gopkg.in/errgo.v2:gopkg.in/errgo.v2 \
           gopkg.in/fsnotify.v1:gopkg.in/fsnotify.v1 \
           gopkg.in/gcfg.v1:gopkg.in/gcfg.v1 \
           gopkg.in/gemnasium/logrus-airbrake-hook.v2:gopkg.in/gemnasium/logrus-airbrake-hook.v2 \
           gopkg.in/inf.v0:gopkg.in/inf.v0 \
           gopkg.in/ini.v1:gopkg.in/ini.v1 \
           gopkg.in/natefinch/lumberjack.v2:gopkg.in/natefinch/lumberjack.v2 \
           gopkg.in/resty.v1:gopkg.in/resty.v1 \
           gopkg.in/square/go-jose.v2:gopkg.in/square/go-jose.v2 \
           gopkg.in/tomb.v1:gopkg.in/tomb.v1 \
           gopkg.in/warnings.v0:gopkg.in/warnings.v0 \
           gopkg.in/yaml.v2:gopkg.in/yaml.v2 \
           gopkg.in/yaml.v3:gopkg.in/yaml.v3 \
           gotest.tools:github.com/gotestyourself/gotest.tools \
           gotest.tools/v3:github.com/gotestyourself/gotest.tools568bc57cc5c19a2ef85e5749870b49a4cc2ab54d \
           honnef.co/go/tools:honnef.co/go/tools \
           inet.af/tcpproxy:inet.af/tcpproxy \
           k8s.io/api:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/api \
           k8s.io/apiextensions-apiserver:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/apiextensions-apiserver \
           k8s.io/apimachinery:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/apimachinery \
           k8s.io/apiserver:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/apiserver \
           k8s.io/cli-runtime:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/cli-runtime \
           k8s.io/client-go:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/client-go \
           k8s.io/cloud-provider:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/cloud-provider \
           k8s.io/cluster-bootstrap:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/cluster-bootstrap \
           k8s.io/code-generator:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/code-generator \
           k8s.io/component-base:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/component-base \
           k8s.io/component-helpers:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/component-helpers \
           k8s.io/controller-manager:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/controller-manager \
           k8s.io/cri-api:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/cri-api \
           k8s.io/csi-translation-lib:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/csi-translation-lib \
           k8s.io/kube-aggregator:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/kube-aggregator \
           k8s.io/kube-controller-manager:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/kube-controller-manager \
           k8s.io/kube-proxy:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/kube-proxy \
           k8s.io/kube-scheduler:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/kube-scheduler \
           k8s.io/kubectl:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/kubectl \
           k8s.io/kubelet:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/kubelet \
           k8s.io/kubernetes:github.com/k3s-io/kubernetes/staging/src/k8s.io/api \
           k8s.io/legacy-cloud-providers:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/legacy-cloud-providers \
           k8s.io/metrics:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/metrics \
           k8s.io/mount-utils:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/mount-utils \
           k8s.io/pod-security-admission:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/pod-security-admission \
           k8s.io/sample-apiserver:github.com/k3s-io/kubernetes/staging/src/k8s.io/api//staging/src/k8s.io/sample-apiserver \
           k8s.io/gengo:k8s.io/gengo \
           k8s.io/klog:k8s.io/klog \
           k8s.io/klog/v2:github.com/k3s-io/klog/v2 \
           k8s.io/kube-openapi:k8s.io/kube-openapi \
           k8s.io/system-validators:k8s.io/system-validators \
           k8s.io/utils:k8s.io/utils \
           modernc.org/cc:modernc.org/cc \
           modernc.org/golex:modernc.org/golex \
           modernc.org/mathutil:modernc.org/mathutil \
           modernc.org/strutil:modernc.org/strutil \
           modernc.org/xc:modernc.org/xc \
           rsc.io/binaryregexp:rsc.io/binaryregexp \
           rsc.io/pdf:rsc.io/pdf \
           rsc.io/quote/v3:github.com/rsc/quote/v3//v3 \
           rsc.io/sampler:rsc.io/sampler \
           sigs.k8s.io/apiserver-network-proxy/konnectivity-client:sigs.k8s.io/apiserver-network-proxy/konnectivity-client//konnectivity-client \
           sigs.k8s.io/cli-utils:sigs.k8s.io/cli-utils \
           sigs.k8s.io/controller-runtime:sigs.k8s.io/controller-runtime \
           sigs.k8s.io/json:sigs.k8s.io/json \
           sigs.k8s.io/kustomize/api:sigs.k8s.io/kustomize/api//api \
           sigs.k8s.io/kustomize/cmd/config:sigs.k8s.io/kustomize/cmd/config//cmd/config \
           sigs.k8s.io/kustomize/kustomize/v4:sigs.k8s.io/kustomize/kustomize/v4//kustomize \
           sigs.k8s.io/kustomize/kyaml:sigs.k8s.io/kustomize/kyaml//kyaml \
           sigs.k8s.io/structured-merge-diff/v4:sigs.k8s.io/structured-merge-diff/v4 \
           sigs.k8s.io/testing_frameworks:sigs.k8s.io/testing_frameworks \
           sigs.k8s.io/yaml:sigs.k8s.io/yaml"
	for s in $sites; do
            site_dest=$(echo $s | cut -d: -f1)
            site_source=$(echo $s | cut -d: -f2)
            mkdir -p vendor.copy/$site_dest
            [ -n "$(ls -A vendor.copy/$site_dest/*.go 2> /dev/null)" ] && { echo "[INFO] vendor.fetch/$site_source -> $site_dest: go copy skipped (files present)" ; true ; } || { echo "[INFO] $site_dest: copying .go files" ; rsync -a --exclude='vendor/' --exclude='.git/' vendor.fetch/$site_source/ vendor.copy/$site_dest ; }
	done

        ln -sf vendor.copy vendor

        # these are bad symlinks, go validates them and breaks the build if they are present
        rm -f vendor/go.etcd.io/etcd/client/v*/example_*
        rm -f vendor/go.etcd.io/etcd/client/v*/concurrency/example_*.go

	# Note: if no_brtfs is used in the tags, we'll violate build
	#       constraints, and the following files need to have them
	#       removed for the build to continue:
	#
	#         vendor/github.com/containerd/containerd/snapshots/btrfs/plugin/*.go

        cp ${WORKDIR}/modules.txt vendor/

        ${GO} build -trimpath -tags "$TAGS" -ldflags "${GO_BUILD_LDFLAGS} -w -s" -o ./dist/artifacts/k3s ./cmd/server/main.go

        # Use UPX if it is enabled (and thus exists) to compress binary
        if command -v upx > /dev/null 2>&1; then
                upx -9 ./dist/artifacts/k3s
        fi
}

do_install() {
        install -d "${D}${BIN_PREFIX}/bin"
        install -m 755 "${S}/src/import/dist/artifacts/k3s" "${D}${BIN_PREFIX}/bin"
        ln -sr "${D}/${BIN_PREFIX}/bin/k3s" "${D}${BIN_PREFIX}/bin/crictl"
        # We want to use the containerd provided ctr
        # ln -sr "${D}/${BIN_PREFIX}/bin/k3s" "${D}${BIN_PREFIX}/bin/ctr"
        ln -sr "${D}/${BIN_PREFIX}/bin/k3s" "${D}${BIN_PREFIX}/bin/kubectl"
        install -m 755 "${WORKDIR}/k3s-clean" "${D}${BIN_PREFIX}/bin"
        install -m 755 "${WORKDIR}/k3s-killall.sh" "${D}${BIN_PREFIX}/bin"

        if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
                install -D -m 0644 "${WORKDIR}/k3s.service" "${D}${systemd_system_unitdir}/k3s.service"
                install -D -m 0644 "${WORKDIR}/k3s-agent.service" "${D}${systemd_system_unitdir}/k3s-agent.service"
                sed -i "s#\(Exec\)\(.*\)=\(.*\)\(k3s\)#\1\2=${BIN_PREFIX}/bin/\4#g" "${D}${systemd_system_unitdir}/k3s.service" "${D}${systemd_system_unitdir}/k3s-agent.service"
                install -m 755 "${WORKDIR}/k3s-agent" "${D}${BIN_PREFIX}/bin"
        fi
}

PACKAGES =+ "${PN}-server ${PN}-agent"

SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}-server ${PN}-agent','',d)}"
SYSTEMD_SERVICE:${PN}-server = "${@bb.utils.contains('DISTRO_FEATURES','systemd','k3s.service','',d)}"
SYSTEMD_SERVICE:${PN}-agent = "${@bb.utils.contains('DISTRO_FEATURES','systemd','k3s-agent.service','',d)}"
SYSTEMD_AUTO_ENABLE:${PN}-agent = "disable"

FILES:${PN}-agent = "${BIN_PREFIX}/bin/k3s-agent"
FILES:${PN} += "${BIN_PREFIX}/bin/*"

RDEPENDS:${PN} = "k3s-cni conntrack-tools coreutils findutils iptables iproute2 ipset virtual-containerd"
RDEPENDS:${PN}-server = "${PN}"
RDEPENDS:${PN}-agent = "${PN}"

RRECOMMENDS:${PN} = "\
                     kernel-module-xt-addrtype \
                     kernel-module-xt-nat \
                     kernel-module-xt-multiport \
                     kernel-module-xt-conntrack \
                     kernel-module-xt-comment \
                     kernel-module-xt-mark \
                     kernel-module-xt-connmark \
                     kernel-module-vxlan \
                     kernel-module-xt-masquerade \
                     kernel-module-xt-statistic \
                     kernel-module-xt-physdev \
                     kernel-module-xt-nflog \
                     kernel-module-xt-limit \
                     kernel-module-nfnetlink-log \
                     kernel-module-ip-vs \
                     kernel-module-ip-vs-rr \
                     kernel-module-ip-vs-sh \
                     kernel-module-ip-vs-wrr \
                     "

RCONFLICTS:${PN} = "kubectl"

INHIBIT_PACKAGE_STRIP = "1"
INSANE_SKIP:${PN} += "ldflags already-stripped textrel"
