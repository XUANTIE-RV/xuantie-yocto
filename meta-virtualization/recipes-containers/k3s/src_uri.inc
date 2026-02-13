# bazil.org/fuse v0.0.0-20160811212531-371fbbdaa898
# [1] git ls-remote https://github.com/bazil/fuse 371fbbdaa8987b715bdd21d6adc4c9b20155f748 
SRCREV_fuse="371fbbdaa8987b715bdd21d6adc4c9b20155f748"
SRC_URI += "git://github.com/bazil/fuse;name=fuse;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/bazil/fuse"

# bitbucket.org/bertimus9/systemstat v0.0.0-20180207000608-0eeff89b0690
# [1] git ls-remote https://bitbucket.org/bertimus9/systemstat 0eeff89b0690611fc32e21f0cd2e4434abf8fe53 
SRCREV_systemstat="0eeff89b0690611fc32e21f0cd2e4434abf8fe53"
SRC_URI += "git://bitbucket.org/bertimus9/systemstat;name=systemstat;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/bitbucket.org/bertimus9/systemstat"

# cloud.google.com/go v0.97.0
# [1] git ls-remote https://github.com/googleapis/google-cloud-go 26286e362470c1b4e60e33d07cb2e12b62843ed7 
SRCREV_go="26286e362470c1b4e60e33d07cb2e12b62843ed7"
SRC_URI += "git://github.com/googleapis/google-cloud-go;name=go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/googleapis/google-cloud-go"

# cloud.google.com/go/bigquery v1.8.0
# [1] git ls-remote https://github.com/googleapis/google-cloud-go 0aa265f094062dbc111a6906d279fbb88a6cc761 
SRCREV_bigquery="0aa265f094062dbc111a6906d279fbb88a6cc761"
SRC_URI += "git://github.com/googleapis/google-cloud-go;name=bigquery;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/googleapis/google-cloud-go/bigquery"

# cloud.google.com/go/datastore v1.1.0
# [1] git ls-remote https://github.com/googleapis/google-cloud-go b033a7846dd130b5e5824faf72e33253d08ec84f 
SRCREV_datastore="b033a7846dd130b5e5824faf72e33253d08ec84f"
SRC_URI += "git://github.com/googleapis/google-cloud-go;name=datastore;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/googleapis/google-cloud-go/datastore"

# cloud.google.com/go/pubsub v1.3.1
# [1] git ls-remote https://github.com/googleapis/google-cloud-go e6e706ae0e17ee021f241dc31d2b7158a366851f 
SRCREV_pubsub="e6e706ae0e17ee021f241dc31d2b7158a366851f"
SRC_URI += "git://github.com/googleapis/google-cloud-go;name=pubsub;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/googleapis/google-cloud-go/pubsub"

# cloud.google.com/go/storage v1.10.0
# [1] git ls-remote https://github.com/googleapis/google-cloud-go 38af8ae88ae851ca21cf385795250ed8e8d16aab 
SRCREV_storage="38af8ae88ae851ca21cf385795250ed8e8d16aab"
SRC_URI += "git://github.com/googleapis/google-cloud-go;name=storage;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/googleapis/google-cloud-go/storage"

# dmitri.shuralyov.com/gpu/mtl v0.0.0-20201218220906-28db891af037
# [1] git ls-remote https://dmitri.shuralyov.com/gpu/mtl 28db891af037715d8c1deec7652485a173c60e25 
SRCREV_mtl="28db891af037715d8c1deec7652485a173c60e25"
SRC_URI += "git://dmitri.shuralyov.com/gpu/mtl;name=mtl;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/dmitri.shuralyov.com/gpu/mtl"

# github.com/360EntSecGroup-Skylar/excelize v1.4.1
# [1] git ls-remote https://github.com/360EntSecGroup-Skylar/excelize dea7ba0ec43a4c29a6642d02b6edc73b8b0369f0 
SRCREV_excelize="dea7ba0ec43a4c29a6642d02b6edc73b8b0369f0"
SRC_URI += "git://github.com/360EntSecGroup-Skylar/excelize;name=excelize;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/360EntSecGroup-Skylar/excelize"

# github.com/Azure/azure-sdk-for-go v55.0.0+incompatible
# [1] git ls-remote https://github.com/Azure/azure-sdk-for-go 3640559afddbad452d265b54fb1c20b30be0b062 
SRCREV_azure-sdk-for-go="3640559afddbad452d265b54fb1c20b30be0b062"
SRC_URI += "git://github.com/Azure/azure-sdk-for-go;name=azure-sdk-for-go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/azure-sdk-for-go"

# github.com/Azure/go-ansiterm v0.0.0-20210617225240-d185dfc1b5a1
# [1] git ls-remote https://github.com/Azure/go-ansiterm d185dfc1b5a126116ea5a19e148e29d16b4574c9 
SRCREV_go-ansiterm="d185dfc1b5a126116ea5a19e148e29d16b4574c9"
SRC_URI += "git://github.com/Azure/go-ansiterm;name=go-ansiterm;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/go-ansiterm"

# github.com/Azure/go-autorest v14.2.0+incompatible
# [1] git ls-remote https://github.com/Azure/go-autorest e7b391b759b050d6719cc6fd8bb87b6dc038bca6 
SRCREV_go-autorest="e7b391b759b050d6719cc6fd8bb87b6dc038bca6"
SRC_URI += "git://github.com/Azure/go-autorest;name=go-autorest;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/go-autorest"

# github.com/Azure/go-autorest/autorest v0.11.20
# [1] git ls-remote https://github.com/Azure/go-autorest 3ddafdd73fe99af09e2226b8b3e9472ca456e355 
SRCREV_autorest="3ddafdd73fe99af09e2226b8b3e9472ca456e355"
SRC_URI += "git://github.com/Azure/go-autorest;name=autorest;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/go-autorest/autorest"

# github.com/Azure/go-autorest/autorest/adal v0.9.13
# [1] git ls-remote https://github.com/Azure/go-autorest 79a63e70c4d8b2840383d2a8791e41b6830ca9ad 
SRCREV_adal="79a63e70c4d8b2840383d2a8791e41b6830ca9ad"
SRC_URI += "git://github.com/Azure/go-autorest;name=adal;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/go-autorest/autorest/adal"

# github.com/Azure/go-autorest/autorest/azure/auth v0.5.8
# [1] git ls-remote https://github.com/Azure/go-autorest 76c7860e9b602854c8598676b7550c715c48a607 
SRCREV_auth="76c7860e9b602854c8598676b7550c715c48a607"
SRC_URI += "git://github.com/Azure/go-autorest;name=auth;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/go-autorest/autorest/azure/auth"

# github.com/Azure/go-autorest/autorest/azure/cli v0.4.2
# [1] git ls-remote https://github.com/Azure/go-autorest b965d219bd9d10676384ef8f05da2e2441df9829 
SRCREV_cli="b965d219bd9d10676384ef8f05da2e2441df9829"
SRC_URI += "git://github.com/Azure/go-autorest;name=cli;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/go-autorest/autorest/azure/cli"

# github.com/Azure/go-autorest/autorest/date v0.3.0
# [1] git ls-remote https://github.com/Azure/go-autorest 3ccb81ef55c23b14a37c08b83a6beba95a971dc3 
SRCREV_date="3ccb81ef55c23b14a37c08b83a6beba95a971dc3"
SRC_URI += "git://github.com/Azure/go-autorest;name=date;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/go-autorest/autorest/date"

# github.com/Azure/go-autorest/autorest/mocks v0.4.1
# [1] git ls-remote https://github.com/Azure/go-autorest c468b8dd87c08bc65a3aa8b084321bfc2a39e9bf 
SRCREV_mocks="c468b8dd87c08bc65a3aa8b084321bfc2a39e9bf"
SRC_URI += "git://github.com/Azure/go-autorest;name=mocks;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/go-autorest/autorest/mocks"

# github.com/Azure/go-autorest/autorest/to v0.4.0
# [1] git ls-remote https://github.com/Azure/go-autorest 5de0fcb73ebd13a61af8153e3d8efbea3c9d3941 
SRCREV_to="5de0fcb73ebd13a61af8153e3d8efbea3c9d3941"
SRC_URI += "git://github.com/Azure/go-autorest;name=to;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/go-autorest/autorest/to"

# github.com/Azure/go-autorest/autorest/validation v0.3.1
# [1] git ls-remote https://github.com/Azure/go-autorest cc407fb4f94c0e65dd4c5b9acd7b9fa341f018e8 
SRCREV_validation="cc407fb4f94c0e65dd4c5b9acd7b9fa341f018e8"
SRC_URI += "git://github.com/Azure/go-autorest;name=validation;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/go-autorest/autorest/validation"

# github.com/Azure/go-autorest/logger v0.2.1
# [1] git ls-remote https://github.com/Azure/go-autorest 820a319d2214dc91d083022ae5ec50070175314c 
SRCREV_logger="820a319d2214dc91d083022ae5ec50070175314c"
SRC_URI += "git://github.com/Azure/go-autorest;name=logger;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/go-autorest/logger"

# github.com/Azure/go-autorest/tracing v0.6.0
# [1] git ls-remote https://github.com/Azure/go-autorest 243e2a3d5e7529eaf8e0851f471f618295182156 
SRCREV_tracing="243e2a3d5e7529eaf8e0851f471f618295182156"
SRC_URI += "git://github.com/Azure/go-autorest;name=tracing;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Azure/go-autorest/tracing"

# github.com/BurntSushi/toml v1.1.0
# [1] git ls-remote https://github.com/BurntSushi/toml 891d2617ddbdfa265c4095b53103c010c98e6591 
SRCREV_toml="891d2617ddbdfa265c4095b53103c010c98e6591"
SRC_URI += "git://github.com/BurntSushi/toml;name=toml;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/BurntSushi/toml"

# github.com/BurntSushi/xgb v0.0.0-20160522181843-27f122750802
# [1] git ls-remote https://github.com/BurntSushi/xgb 27f122750802c950b2c869a5b63dafcf590ced95 
SRCREV_xgb="27f122750802c950b2c869a5b63dafcf590ced95"
SRC_URI += "git://github.com/BurntSushi/xgb;name=xgb;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/BurntSushi/xgb"

# github.com/GoogleCloudPlatform/k8s-cloud-provider v1.16.1-0.20210702024009-ea6160c1d0e3
# [1] git ls-remote https://github.com/GoogleCloudPlatform/k8s-cloud-provider ea6160c1d0e3f67d1d8658c70b0ab40df81a4f2b 
SRCREV_k8s-cloud-provider="ea6160c1d0e3f67d1d8658c70b0ab40df81a4f2b"
SRC_URI += "git://github.com/GoogleCloudPlatform/k8s-cloud-provider;name=k8s-cloud-provider;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/GoogleCloudPlatform/k8s-cloud-provider"

# github.com/JeffAshton/win_pdh v0.0.0-20161109143554-76bb4ee9f0ab
# [1] git ls-remote https://github.com/JeffAshton/win_pdh 76bb4ee9f0ab50f77826f2a2ee7fb9d3880d6ec2 
SRCREV_win_pdh="76bb4ee9f0ab50f77826f2a2ee7fb9d3880d6ec2"
SRC_URI += "git://github.com/JeffAshton/win_pdh;name=win_pdh;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/JeffAshton/win_pdh"

# github.com/MakeNowJust/heredoc v0.0.0-20170808103936-bb23615498cd
# [1] git ls-remote https://github.com/MakeNowJust/heredoc bb23615498cded5e105af4ce27de75b089cbe851 
SRCREV_heredoc="bb23615498cded5e105af4ce27de75b089cbe851"
SRC_URI += "git://github.com/MakeNowJust/heredoc;name=heredoc;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/MakeNowJust/heredoc"

# github.com/Microsoft/go-winio v0.5.2
# [1] git ls-remote https://github.com/Microsoft/go-winio dfd7da8f92a382999d77b5d9cfe8cc6bec1894c6 
SRCREV_go-winio="dfd7da8f92a382999d77b5d9cfe8cc6bec1894c6"
SRC_URI += "git://github.com/Microsoft/go-winio;name=go-winio;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Microsoft/go-winio"

# github.com/Microsoft/hcsshim v0.8.22
# [1] git ls-remote https://github.com/Microsoft/hcsshim 717ae58b49c5e73b746938f94ae3c696ebd5d932 
SRCREV_hcsshim="717ae58b49c5e73b746938f94ae3c696ebd5d932"
SRC_URI += "git://github.com/Microsoft/hcsshim;name=hcsshim;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Microsoft/hcsshim"

# github.com/NYTimes/gziphandler v1.1.1
# [1] git ls-remote https://github.com/NYTimes/gziphandler dd0439581c7657cb652dfe5c71d7d48baf39541d 
SRCREV_gziphandler="dd0439581c7657cb652dfe5c71d7d48baf39541d"
SRC_URI += "git://github.com/NYTimes/gziphandler;name=gziphandler;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/NYTimes/gziphandler"

# github.com/OneOfOne/xxhash v1.2.2
# [1] git ls-remote https://github.com/OneOfOne/xxhash 6def279d2ce6c81a79dd1c1be580f03bb216fb8a 
SRCREV_xxhash="6def279d2ce6c81a79dd1c1be580f03bb216fb8a"
SRC_URI += "git://github.com/OneOfOne/xxhash;name=xxhash;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/OneOfOne/xxhash"

# github.com/PuerkitoBio/goquery v1.5.0
# [1] git ls-remote https://github.com/PuerkitoBio/goquery 2d2796f41742ece03e8086188fa4db16a3a0b458 
SRCREV_goquery="2d2796f41742ece03e8086188fa4db16a3a0b458"
SRC_URI += "git://github.com/PuerkitoBio/goquery;name=goquery;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/PuerkitoBio/goquery"

# github.com/PuerkitoBio/purell v1.1.1
# [1] git ls-remote https://github.com/PuerkitoBio/purell 44968752391892e1b0d0b821ee79e9a85fa13049 
SRCREV_purell="44968752391892e1b0d0b821ee79e9a85fa13049"
SRC_URI += "git://github.com/PuerkitoBio/purell;name=purell;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/PuerkitoBio/purell"

# github.com/PuerkitoBio/urlesc v0.0.0-20170810143723-de5bf2ad4578
# [1] git ls-remote https://github.com/PuerkitoBio/urlesc de5bf2ad457846296e2031421a34e2568e304e35 
SRCREV_urlesc="de5bf2ad457846296e2031421a34e2568e304e35"
SRC_URI += "git://github.com/PuerkitoBio/urlesc;name=urlesc;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/PuerkitoBio/urlesc"

# github.com/Rican7/retry v0.1.0
# [1] git ls-remote https://github.com/Rican7/retry 28d17f812805614e76f170aa3c8698155ea8e019 
SRCREV_retry="28d17f812805614e76f170aa3c8698155ea8e019"
SRC_URI += "git://github.com/Rican7/retry;name=retry;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/Rican7/retry"

# github.com/agext/levenshtein v1.2.3
# [1] git ls-remote https://github.com/agext/levenshtein 52c14c47d03211d8ac1834e94601635e07c5a6ef 
SRCREV_levenshtein="52c14c47d03211d8ac1834e94601635e07c5a6ef"
SRC_URI += "git://github.com/agext/levenshtein;name=levenshtein;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/agext/levenshtein"

# github.com/agnivade/levenshtein v1.0.1
# [1] git ls-remote https://github.com/agnivade/levenshtein 834b435c3df04fba060c8d74a40d849ab2c4a090 
SRCREV_levenshtein1="834b435c3df04fba060c8d74a40d849ab2c4a090"
SRC_URI += "git://github.com/agnivade/levenshtein;name=levenshtein1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/agnivade/levenshtein"

# github.com/ajstarks/svgo v0.0.0-20180226025133-644b8db467af
# [1] git ls-remote https://github.com/ajstarks/svgo 644b8db467afccf19a0692a3e31a1868e4287ab8 
SRCREV_svgo="644b8db467afccf19a0692a3e31a1868e4287ab8"
SRC_URI += "git://github.com/ajstarks/svgo;name=svgo;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/ajstarks/svgo"

# github.com/alecthomas/template v0.0.0-20190718012654-fb15b899a751
# [1] git ls-remote https://github.com/alecthomas/template fb15b899a75114aa79cc930e33c46b577cc664b1 
SRCREV_template="fb15b899a75114aa79cc930e33c46b577cc664b1"
SRC_URI += "git://github.com/alecthomas/template;name=template;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/alecthomas/template"

# github.com/alecthomas/units v0.0.0-20190924025748-f65c72e2690d
# [1] git ls-remote https://github.com/alecthomas/units f65c72e2690dc4b403c8bd637baf4611cd4c069b 
SRCREV_units="f65c72e2690dc4b403c8bd637baf4611cd4c069b"
SRC_URI += "git://github.com/alecthomas/units;name=units;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/alecthomas/units"

# github.com/alexflint/go-filemutex v1.1.0
# [1] git ls-remote https://github.com/alexflint/go-filemutex 392c3ab8a40254815ad821ac02ee1354e98a56fe 
SRCREV_go-filemutex="392c3ab8a40254815ad821ac02ee1354e98a56fe"
SRC_URI += "git://github.com/alexflint/go-filemutex;name=go-filemutex;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/alexflint/go-filemutex"

# github.com/andreyvit/diff v0.0.0-20170406064948-c7f18ee00883
# [1] git ls-remote https://github.com/andreyvit/diff c7f18ee00883bfd3b00e0a2bf7607827e0148ad4 
SRCREV_diff="c7f18ee00883bfd3b00e0a2bf7607827e0148ad4"
SRC_URI += "git://github.com/andreyvit/diff;name=diff;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/andreyvit/diff"

# github.com/andybalholm/cascadia v1.0.0
# [1] git ls-remote https://github.com/andybalholm/cascadia 901648c87902174f774fac311d7f176f8647bdaa 
SRCREV_cascadia="901648c87902174f774fac311d7f176f8647bdaa"
SRC_URI += "git://github.com/andybalholm/cascadia;name=cascadia;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/andybalholm/cascadia"

# github.com/antihax/optional v1.0.0
# [1] git ls-remote https://github.com/antihax/optional c3f0ba9c1a592b971d66b2787679af55b5c58f21 
SRCREV_optional="c3f0ba9c1a592b971d66b2787679af55b5c58f21"
SRC_URI += "git://github.com/antihax/optional;name=optional;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/antihax/optional"

# github.com/antlr/antlr4/runtime/Go/antlr v0.0.0-20210826220005-b48c857c3a0e
# [1] git ls-remote https://github.com/antlr/antlr4 b48c857c3a0ea4329686abe99276dddd991a7724 
SRCREV_antlr="b48c857c3a0ea4329686abe99276dddd991a7724"
SRC_URI += "git://github.com/antlr/antlr4;name=antlr;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/antlr/antlr4/runtime/Go/antlr"

# github.com/apparentlymart/go-dump v0.0.0-20180507223929-23540a00eaa3
# [1] git ls-remote https://github.com/apparentlymart/go-dump 23540a00eaa318ee47d517dd7653fc81156a3e7e 
SRCREV_go-dump="23540a00eaa318ee47d517dd7653fc81156a3e7e"
SRC_URI += "git://github.com/apparentlymart/go-dump;name=go-dump;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/apparentlymart/go-dump"

# github.com/apparentlymart/go-textseg v1.0.0
# [1] git ls-remote https://github.com/apparentlymart/go-textseg 29d1648e97c6de68a8e9ec67de1f62fae5425001 
SRCREV_go-textseg="29d1648e97c6de68a8e9ec67de1f62fae5425001"
SRC_URI += "git://github.com/apparentlymart/go-textseg;name=go-textseg;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/apparentlymart/go-textseg"

# github.com/apparentlymart/go-textseg/v13 v13.0.0
# [1] git ls-remote https://github.com/apparentlymart/go-textseg 5b41aa275ccaac4ccaa91729a8ee92e7eac9c728 
SRCREV_v13="5b41aa275ccaac4ccaa91729a8ee92e7eac9c728"
SRC_URI += "git://github.com/apparentlymart/go-textseg;name=v13;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/apparentlymart/go-textseg/v13"

# github.com/armon/circbuf v0.0.0-20150827004946-bbbad097214e
# [1] git ls-remote https://github.com/armon/circbuf bbbad097214e2918d8543d5201d12bfd7bca254d 
SRCREV_circbuf="bbbad097214e2918d8543d5201d12bfd7bca254d"
SRC_URI += "git://github.com/armon/circbuf;name=circbuf;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/armon/circbuf"

# github.com/armon/consul-api v0.0.0-20180202201655-eb2c6b5be1b6
# [1] git ls-remote https://github.com/armon/consul-api eb2c6b5be1b66bab83016e0b05f01b8d5496ffbd 
SRCREV_consul-api="eb2c6b5be1b66bab83016e0b05f01b8d5496ffbd"
SRC_URI += "git://github.com/armon/consul-api;name=consul-api;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/armon/consul-api"

# github.com/armon/go-metrics v0.0.0-20180917152333-f0300d1749da
# [1] git ls-remote https://github.com/armon/go-metrics f0300d1749da6fa982027e449ec0c7a145510c3c 
SRCREV_go-metrics="f0300d1749da6fa982027e449ec0c7a145510c3c"
SRC_URI += "git://github.com/armon/go-metrics;name=go-metrics;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/armon/go-metrics"

# github.com/armon/go-radix v0.0.0-20180808171621-7fddfc383310
# [1] git ls-remote https://github.com/armon/go-radix 7fddfc383310abc091d79a27f116d30cf0424032 
SRCREV_go-radix="7fddfc383310abc091d79a27f116d30cf0424032"
SRC_URI += "git://github.com/armon/go-radix;name=go-radix;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/armon/go-radix"

# github.com/armon/go-socks5 v0.0.0-20160902184237-e75332964ef5
# [1] git ls-remote https://github.com/armon/go-socks5 e75332964ef517daa070d7c38a9466a0d687e0a5 
SRCREV_go-socks5="e75332964ef517daa070d7c38a9466a0d687e0a5"
SRC_URI += "git://github.com/armon/go-socks5;name=go-socks5;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/armon/go-socks5"

# github.com/asaskevich/govalidator v0.0.0-20190424111038-f61b66f89f4a
# [1] git ls-remote https://github.com/asaskevich/govalidator f61b66f89f4a311bef65f13e575bcf1a2ffadda6 
SRCREV_govalidator="f61b66f89f4a311bef65f13e575bcf1a2ffadda6"
SRC_URI += "git://github.com/asaskevich/govalidator;name=govalidator;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/asaskevich/govalidator"

# github.com/auth0/go-jwt-middleware v1.0.1
# [1] git ls-remote https://github.com/auth0/go-jwt-middleware dec8d9f66d0b99d1085a060812cd13d7219a7ebd 
SRCREV_go-jwt-middleware="dec8d9f66d0b99d1085a060812cd13d7219a7ebd"
SRC_URI += "git://github.com/auth0/go-jwt-middleware;name=go-jwt-middleware;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/auth0/go-jwt-middleware"

# github.com/aws/aws-lambda-go v1.13.3
# [1] git ls-remote https://github.com/aws/aws-lambda-go b5b7267d297de263cc5b61f8c37543daa9c95ffd 
SRCREV_aws-lambda-go="b5b7267d297de263cc5b61f8c37543daa9c95ffd"
SRC_URI += "git://github.com/aws/aws-lambda-go;name=aws-lambda-go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/aws/aws-lambda-go"

# github.com/aws/aws-sdk-go v1.44.24
# [1] git ls-remote https://github.com/aws/aws-sdk-go 2ec70f79013894dc50949a0cbe5b2f2a2d268fce 
SRCREV_aws-sdk-go="2ec70f79013894dc50949a0cbe5b2f2a2d268fce"
SRC_URI += "git://github.com/aws/aws-sdk-go;name=aws-sdk-go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/aws/aws-sdk-go"

# github.com/benbjohnson/clock v1.1.0
# [1] git ls-remote https://github.com/benbjohnson/clock 307483a2173c76d3ded778bd68214865fcf05ec8 
SRCREV_clock="307483a2173c76d3ded778bd68214865fcf05ec8"
SRC_URI += "git://github.com/benbjohnson/clock;name=clock;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/benbjohnson/clock"

# github.com/beorn7/perks v1.0.1
# [1] git ls-remote https://github.com/beorn7/perks 37c8de3658fcb183f997c4e13e8337516ab753e6 
SRCREV_perks="37c8de3658fcb183f997c4e13e8337516ab753e6"
SRC_URI += "git://github.com/beorn7/perks;name=perks;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/beorn7/perks"

# github.com/bgentry/go-netrc v0.0.0-20140422174119-9fd32a8b3d3d
# [1] git ls-remote https://github.com/bgentry/go-netrc 9fd32a8b3d3d3f9d43c341bfe098430e07609480 
SRCREV_go-netrc="9fd32a8b3d3d3f9d43c341bfe098430e07609480"
SRC_URI += "git://github.com/bgentry/go-netrc;name=go-netrc;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/bgentry/go-netrc"

# github.com/bgentry/speakeasy v0.1.0
# [1] git ls-remote https://github.com/bgentry/speakeasy 4aabc24848ce5fd31929f7d1e4ea74d3709c14cd 
SRCREV_speakeasy="4aabc24848ce5fd31929f7d1e4ea74d3709c14cd"
SRC_URI += "git://github.com/bgentry/speakeasy;name=speakeasy;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/bgentry/speakeasy"

# github.com/bketelsen/crypt v0.0.4
# [1] git ls-remote https://github.com/bketelsen/crypt 3f0829aaee54a3e9eabd45afbf68257a5cf754f7 
SRCREV_crypt="3f0829aaee54a3e9eabd45afbf68257a5cf754f7"
SRC_URI += "git://github.com/bketelsen/crypt;name=crypt;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/bketelsen/crypt"

# github.com/blang/semver v3.5.1+incompatible
# [1] git ls-remote https://github.com/blang/semver 2ee87856327ba09384cabd113bc6b5d174e9ec0f 
SRCREV_semver="2ee87856327ba09384cabd113bc6b5d174e9ec0f"
SRC_URI += "git://github.com/blang/semver;name=semver;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/blang/semver"

# github.com/blang/semver/v4 v4.0.0
# [1] git ls-remote https://github.com/blang/semver af3461a9cbcf1f3f5889d21b83f5ef63880c33a8 
SRCREV_v4="af3461a9cbcf1f3f5889d21b83f5ef63880c33a8"
SRC_URI += "git://github.com/blang/semver;name=v4;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/blang/semver/v4"

# github.com/boltdb/bolt v1.3.1
# [1] git ls-remote https://github.com/boltdb/bolt 2f1ce7a837dcb8da3ec595b1dac9d0632f0f99e8 
SRCREV_bolt="2f1ce7a837dcb8da3ec595b1dac9d0632f0f99e8"
SRC_URI += "git://github.com/boltdb/bolt;name=bolt;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/boltdb/bolt"

# github.com/boombuler/barcode v1.0.1-0.20190219062509-6c824513bacc
# [1] git ls-remote https://github.com/boombuler/barcode 6c824513baccd76c674ce72112c29ef550187b08 
SRCREV_barcode="6c824513baccd76c674ce72112c29ef550187b08"
SRC_URI += "git://github.com/boombuler/barcode;name=barcode;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/boombuler/barcode"

# github.com/bronze1man/goStrongswanVici v0.0.0-20201105010758-936f38b697fd
# [1] git ls-remote https://github.com/bronze1man/goStrongswanVici 936f38b697fd9a0054eee1469fee9d51199d4e66 
SRCREV_goStrongswanVici="936f38b697fd9a0054eee1469fee9d51199d4e66"
SRC_URI += "git://github.com/bronze1man/goStrongswanVici;name=goStrongswanVici;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/bronze1man/goStrongswanVici"

# github.com/buger/jsonparser v1.1.1
# [1] git ls-remote https://github.com/buger/jsonparser df3ea76ece10095374fd1c9a22a4fb85a44efc42 
SRCREV_jsonparser="df3ea76ece10095374fd1c9a22a4fb85a44efc42"
SRC_URI += "git://github.com/buger/jsonparser;name=jsonparser;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/buger/jsonparser"

# github.com/canonical/go-dqlite v1.5.1
# [1] git ls-remote https://github.com/canonical/go-dqlite 830c1a017995b73322e86c2a6f2ca799c1caca07 
SRCREV_go-dqlite="830c1a017995b73322e86c2a6f2ca799c1caca07"
SRC_URI += "git://github.com/canonical/go-dqlite;name=go-dqlite;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/canonical/go-dqlite"

# github.com/census-instrumentation/opencensus-proto v0.2.1
# [1] git ls-remote https://github.com/census-instrumentation/opencensus-proto d89fa54de508111353cb0b06403c00569be780d8 
SRCREV_opencensus-proto="d89fa54de508111353cb0b06403c00569be780d8"
SRC_URI += "git://github.com/census-instrumentation/opencensus-proto;name=opencensus-proto;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/census-instrumentation/opencensus-proto"

# github.com/certifi/gocertifi v0.0.0-20200922220541-2c3bb06c6054
# [1] git ls-remote https://github.com/certifi/gocertifi 2c3bb06c6054e133430498817d26ac003d08f020 
SRCREV_gocertifi="2c3bb06c6054e133430498817d26ac003d08f020"
SRC_URI += "git://github.com/certifi/gocertifi;name=gocertifi;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/certifi/gocertifi"

# github.com/cespare/xxhash v1.1.0
# [1] git ls-remote https://github.com/cespare/xxhash 569f7c8abf1f58d9043ab804d364483cb1c853b6 
SRCREV_xxhash1="569f7c8abf1f58d9043ab804d364483cb1c853b6"
SRC_URI += "git://github.com/cespare/xxhash;name=xxhash1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/cespare/xxhash"

# github.com/cespare/xxhash/v2 v2.1.2
# [1] git ls-remote https://github.com/cespare/xxhash e7a6b52374f7e2abfb8abb27249d53a1997b09a7 
SRCREV_v2="e7a6b52374f7e2abfb8abb27249d53a1997b09a7"
SRC_URI += "git://github.com/cespare/xxhash;name=v2;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/cespare/xxhash/v2"

# github.com/chai2010/gettext-go v0.0.0-20160711120539-c6fed771bfd5
# [1] git ls-remote https://github.com/chai2010/gettext-go c6fed771bfd517099caf0f7a961671fa8ed08723 
SRCREV_gettext-go="c6fed771bfd517099caf0f7a961671fa8ed08723"
SRC_URI += "git://github.com/chai2010/gettext-go;name=gettext-go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/chai2010/gettext-go"

# github.com/checkpoint-restore/go-criu/v5 v5.3.0
# [1] git ls-remote https://github.com/checkpoint-restore/go-criu e3059103a5cf6adca2183e08c0315e8d8d5135a5 
SRCREV_v5="e3059103a5cf6adca2183e08c0315e8d8d5135a5"
SRC_URI += "git://github.com/checkpoint-restore/go-criu;name=v5;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/checkpoint-restore/go-criu/v5"

# github.com/cheggaaa/pb v1.0.29
# [1] git ls-remote https://github.com/cheggaaa/pb 5784bcc95a1741c2ca2b46b19f1f3d398557300c 
SRCREV_pb="5784bcc95a1741c2ca2b46b19f1f3d398557300c"
SRC_URI += "git://github.com/cheggaaa/pb;name=pb;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/cheggaaa/pb"

# github.com/chzyer/logex v1.1.10
# [1] git ls-remote https://github.com/chzyer/logex cd112f618178aaaf4ea8592c8839f5276145d9cf 
SRCREV_logex="cd112f618178aaaf4ea8592c8839f5276145d9cf"
SRC_URI += "git://github.com/chzyer/logex;name=logex;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/chzyer/logex"

# github.com/chzyer/readline v0.0.0-20180603132655-2972be24d48e
# [1] git ls-remote https://github.com/chzyer/readline 2972be24d48e78746da79ba8e24e8b488c9880de 
SRCREV_readline="2972be24d48e78746da79ba8e24e8b488c9880de"
SRC_URI += "git://github.com/chzyer/readline;name=readline;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/chzyer/readline"

# github.com/chzyer/test v0.0.0-20180213035817-a1ea475d72b1
# [1] git ls-remote https://github.com/chzyer/test a1ea475d72b168a29f44221e0ad031a842642302 
SRCREV_test="a1ea475d72b168a29f44221e0ad031a842642302"
SRC_URI += "git://github.com/chzyer/test;name=test;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/chzyer/test"

# github.com/cilium/ebpf v0.7.0
# [1] git ls-remote https://github.com/cilium/ebpf 13667bdb8f164c32ae1b85e7130552dd93e86dfd 
SRCREV_ebpf="13667bdb8f164c32ae1b85e7130552dd93e86dfd"
SRC_URI += "git://github.com/cilium/ebpf;name=ebpf;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/cilium/ebpf"

# github.com/k3s-io/kube-router v1.5.1-0.20220630214451-a43bcd8511d2
# [1] git ls-remote https://github.com/k3s-io/kube-router a43bcd8511d263855338a2dcc4d7f409c918077b 
SRCREV_kube-router="a43bcd8511d263855338a2dcc4d7f409c918077b"
SRC_URI += "git://github.com/k3s-io/kube-router;name=kube-router;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/cloudnativelabs/kube-router"

# github.com/clusterhq/flocker-go v0.0.0-20160920122132-2b8b7259d313
# [1] git ls-remote https://github.com/clusterhq/flocker-go 2b8b7259d3139c96c4a6871031355808ab3fd3b3 
SRCREV_flocker-go="2b8b7259d3139c96c4a6871031355808ab3fd3b3"
SRC_URI += "git://github.com/clusterhq/flocker-go;name=flocker-go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/clusterhq/flocker-go"

# github.com/cncf/udpa/go v0.0.0-20201120205902-5459f2c99403
# [1] git ls-remote https://github.com/cncf/udpa 5459f2c994033b0afed7e4a70ac7e90c90c1ffee 
SRCREV_go1="5459f2c994033b0afed7e4a70ac7e90c90c1ffee"
SRC_URI += "git://github.com/cncf/udpa;name=go1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/cncf/udpa/go"

# github.com/cncf/xds/go v0.0.0-20210312221358-fbca930ec8ed
# [1] git ls-remote https://github.com/cncf/xds fbca930ec8edc42e1a2b083dc59d0f6ec4946eb1 
SRCREV_go12="fbca930ec8edc42e1a2b083dc59d0f6ec4946eb1"
SRC_URI += "git://github.com/cncf/xds;name=go12;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/cncf/xds/go"

# github.com/cockroachdb/datadriven v0.0.0-20200714090401-bf6692d28da5
# [1] git ls-remote https://github.com/cockroachdb/datadriven bf6692d28da5d6c6dade56f0c2679ca87705e1ad 
SRCREV_datadriven="bf6692d28da5d6c6dade56f0c2679ca87705e1ad"
SRC_URI += "git://github.com/cockroachdb/datadriven;name=datadriven;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/cockroachdb/datadriven"

# github.com/cockroachdb/errors v1.2.4
# [1] git ls-remote https://github.com/cockroachdb/errors 9e21257b06ad938e53c24c52b393076a51b61540 
SRCREV_errors="9e21257b06ad938e53c24c52b393076a51b61540"
SRC_URI += "git://github.com/cockroachdb/errors;name=errors;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/cockroachdb/errors"

# github.com/cockroachdb/logtags v0.0.0-20190617123548-eb05cc24525f
# [1] git ls-remote https://github.com/cockroachdb/logtags eb05cc24525fa45bcdbaaeec3e431a82099f9ad4 
SRCREV_logtags="eb05cc24525fa45bcdbaaeec3e431a82099f9ad4"
SRC_URI += "git://github.com/cockroachdb/logtags;name=logtags;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/cockroachdb/logtags"

# github.com/container-storage-interface/spec v1.5.0
# [1] git ls-remote https://github.com/container-storage-interface/spec f1ae02418ccc363f089ee952d438f30ffdb4b4dc 
SRCREV_spec="f1ae02418ccc363f089ee952d438f30ffdb4b4dc"
SRC_URI += "git://github.com/container-storage-interface/spec;name=spec;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/container-storage-interface/spec"

# github.com/containerd/aufs v1.0.0
# [1] git ls-remote https://github.com/containerd/aufs fb0192dcb2c0bbfce3bd8756fc88026870c0354d 
SRCREV_aufs="fb0192dcb2c0bbfce3bd8756fc88026870c0354d"
SRC_URI += "git://github.com/containerd/aufs;name=aufs;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/aufs"

# github.com/containerd/btrfs v1.0.0
# [1] git ls-remote https://github.com/containerd/btrfs 918d888fb676f1f2d80ad174f724a70dbb403130 
SRCREV_btrfs="918d888fb676f1f2d80ad174f724a70dbb403130"
SRC_URI += "git://github.com/containerd/btrfs;name=btrfs;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/btrfs"

# github.com/containerd/cgroups v1.0.1
# [1] git ls-remote https://github.com/containerd/cgroups b9de8a2212026c07cec67baf3323f1fc0121e048 
SRCREV_cgroups="b9de8a2212026c07cec67baf3323f1fc0121e048"
SRC_URI += "git://github.com/containerd/cgroups;name=cgroups;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/cgroups"

# github.com/containerd/console v1.0.3
# [1] git ls-remote https://github.com/containerd/console b5cb846c9186d67bcae3ce3c324e47cd317d9527 
SRCREV_console="b5cb846c9186d67bcae3ce3c324e47cd317d9527"
SRC_URI += "git://github.com/containerd/console;name=console;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/console"

# github.com/k3s-io/containerd v1.5.13-k3s1
# [1] git ls-remote https://github.com/k3s-io/containerd a999e83d47e77fddaf1dad7128fb92cd4331d122 
SRCREV_containerd="a999e83d47e77fddaf1dad7128fb92cd4331d122"
SRC_URI += "git://github.com/k3s-io/containerd;name=containerd;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/containerd"

# github.com/containerd/continuity v0.3.0
# [1] git ls-remote https://github.com/containerd/continuity 5ad51c7aca47b8e742f5e6e7dc841d50f5f6affd 
SRCREV_continuity="5ad51c7aca47b8e742f5e6e7dc841d50f5f6affd"
SRC_URI += "git://github.com/containerd/continuity;name=continuity;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/continuity"

# github.com/containerd/fifo v1.0.0
# [1] git ls-remote https://github.com/containerd/fifo 650e8a8a179d040123db61f016cb133143e7a581 
SRCREV_fifo="650e8a8a179d040123db61f016cb133143e7a581"
SRC_URI += "git://github.com/containerd/fifo;name=fifo;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/fifo"

# github.com/containerd/fuse-overlayfs-snapshotter v1.0.4
# [1] git ls-remote https://github.com/containerd/fuse-overlayfs-snapshotter db90194f0cf2f42ee8cdc3c542f6ed2c92ef8ffc 
SRCREV_fuse-overlayfs-snapshotter="db90194f0cf2f42ee8cdc3c542f6ed2c92ef8ffc"
SRC_URI += "git://github.com/containerd/fuse-overlayfs-snapshotter;name=fuse-overlayfs-snapshotter;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/fuse-overlayfs-snapshotter"

# github.com/containerd/go-cni v1.1.3
# [1] git ls-remote https://github.com/containerd/go-cni e559bd8b26eeb6b23c92686b808c057225ec5cfb 
SRCREV_go-cni="e559bd8b26eeb6b23c92686b808c057225ec5cfb"
SRC_URI += "git://github.com/containerd/go-cni;name=go-cni;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/go-cni"

# github.com/containerd/go-runc v1.0.0
# [1] git ls-remote https://github.com/containerd/go-runc 16b287bc67d069a60fa48db15f330b790b74365b 
SRCREV_go-runc="16b287bc67d069a60fa48db15f330b790b74365b"
SRC_URI += "git://github.com/containerd/go-runc;name=go-runc;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/go-runc"

# github.com/containerd/imgcrypt v1.1.3
# [1] git ls-remote https://github.com/containerd/imgcrypt 65d749cef36547473d1c1b0b67ed8a4b9e3d13d4 
SRCREV_imgcrypt="65d749cef36547473d1c1b0b67ed8a4b9e3d13d4"
SRC_URI += "git://github.com/containerd/imgcrypt;name=imgcrypt;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/imgcrypt"

# github.com/containerd/nri v0.1.0
# [1] git ls-remote https://github.com/containerd/nri bcfcdd354487690f7439a5d1cf3825e94489987e 
SRCREV_nri="bcfcdd354487690f7439a5d1cf3825e94489987e"
SRC_URI += "git://github.com/containerd/nri;name=nri;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/nri"

# github.com/containerd/stargz-snapshotter v0.11.4
# [1] git ls-remote https://github.com/containerd/stargz-snapshotter cf1c6d90461f1ae6f01a5b8f5e1dfc816223ad09 
SRCREV_stargz-snapshotter="cf1c6d90461f1ae6f01a5b8f5e1dfc816223ad09"
SRC_URI += "git://github.com/containerd/stargz-snapshotter;name=stargz-snapshotter;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/stargz-snapshotter"

# github.com/containerd/ttrpc v1.1.0
# [1] git ls-remote https://github.com/containerd/ttrpc 0247db16a1f98bb76731a12ad72b8d49705b38b3 
SRCREV_ttrpc="0247db16a1f98bb76731a12ad72b8d49705b38b3"
SRC_URI += "git://github.com/containerd/ttrpc;name=ttrpc;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/ttrpc"

# github.com/containerd/typeurl v1.0.2
# [1] git ls-remote https://github.com/containerd/typeurl 5e43fb8b75ed2f2305fc04e6918c8d10636771bc 
SRCREV_typeurl="5e43fb8b75ed2f2305fc04e6918c8d10636771bc"
SRC_URI += "git://github.com/containerd/typeurl;name=typeurl;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/typeurl"

# github.com/containerd/zfs v1.0.0
# [1] git ls-remote https://github.com/containerd/zfs 4140c9077d87eb8bea25bd935f56bc732b18c25c 
SRCREV_zfs="4140c9077d87eb8bea25bd935f56bc732b18c25c"
SRC_URI += "git://github.com/containerd/zfs;name=zfs;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containerd/zfs"

# github.com/containernetworking/cni v1.1.0
# [1] git ls-remote https://github.com/containernetworking/cni 08f8596ff3bef74c7999322deb62f4a8bf9e8174 
SRCREV_cni="08f8596ff3bef74c7999322deb62f4a8bf9e8174"
SRC_URI += "git://github.com/containernetworking/cni;name=cni;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containernetworking/cni"

# github.com/containernetworking/plugins v1.1.1
# [1] git ls-remote https://github.com/containernetworking/plugins 4744ec27b89c083194e7df498de50f03a8a1d3ec 
SRCREV_plugins="4744ec27b89c083194e7df498de50f03a8a1d3ec"
SRC_URI += "git://github.com/containernetworking/plugins;name=plugins;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containernetworking/plugins"

# github.com/containers/ocicrypt v1.1.2
# [1] git ls-remote https://github.com/containers/ocicrypt 1272152ccae67a2c3a4ed543d72b7e42928c99bb 
SRCREV_ocicrypt="1272152ccae67a2c3a4ed543d72b7e42928c99bb"
SRC_URI += "git://github.com/containers/ocicrypt;name=ocicrypt;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/containers/ocicrypt"

# github.com/coredns/caddy v1.1.0
# [1] git ls-remote https://github.com/coredns/caddy 115eb7dee367f3ee1907bc3d84d6aa7a7cd193d8 
SRCREV_caddy="115eb7dee367f3ee1907bc3d84d6aa7a7cd193d8"
SRC_URI += "git://github.com/coredns/caddy;name=caddy;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/coredns/caddy"

# github.com/coredns/corefile-migration v1.0.14
# [1] git ls-remote https://github.com/coredns/corefile-migration 3218e7abb9a15cb42e237f2304b489f5dab1402b 
SRCREV_corefile-migration="3218e7abb9a15cb42e237f2304b489f5dab1402b"
SRC_URI += "git://github.com/coredns/corefile-migration;name=corefile-migration;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/coredns/corefile-migration"

# github.com/coreos/bbolt v1.3.2
# [1] git ls-remote https://github.com/coreos/bbolt 63597a96ec0ad9e6d43c3fc81e809909e0237461 
SRCREV_bbolt="63597a96ec0ad9e6d43c3fc81e809909e0237461"
SRC_URI += "git://github.com/coreos/bbolt;name=bbolt;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/coreos/bbolt"

# github.com/coreos/etcd v3.3.13+incompatible
# [1] git ls-remote https://github.com/coreos/etcd 98d308426819d892e149fe45f6fd542464cb1f9d 
SRCREV_etcd="98d308426819d892e149fe45f6fd542464cb1f9d"
SRC_URI += "git://github.com/coreos/etcd;name=etcd;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/coreos/etcd"

# github.com/coreos/go-iptables v0.6.0
# [1] git ls-remote https://github.com/coreos/go-iptables 14d56d57c892f27a717aa6026fd2d3293221395b 
SRCREV_go-iptables="14d56d57c892f27a717aa6026fd2d3293221395b"
SRC_URI += "git://github.com/coreos/go-iptables;name=go-iptables;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/coreos/go-iptables"

# github.com/coreos/go-oidc v2.1.0+incompatible
# [1] git ls-remote https://github.com/coreos/go-oidc 2be1c5b8a260760503f66dc0996e102b683b3ac3 
SRCREV_go-oidc="2be1c5b8a260760503f66dc0996e102b683b3ac3"
SRC_URI += "git://github.com/coreos/go-oidc;name=go-oidc;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/coreos/go-oidc"

# github.com/coreos/go-semver v0.3.0
# [1] git ls-remote https://github.com/coreos/go-semver e214231b295a8ea9479f11b70b35d5acf3556d9b 
SRCREV_go-semver="e214231b295a8ea9479f11b70b35d5acf3556d9b"
SRC_URI += "git://github.com/coreos/go-semver;name=go-semver;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/coreos/go-semver"

# github.com/coreos/go-systemd v0.0.0-20190321100706-95778dfbb74e
# [1] git ls-remote https://github.com/coreos/go-systemd 95778dfbb74eb7e4dbaf43bf7d71809650ef8076 
SRCREV_go-systemd="95778dfbb74eb7e4dbaf43bf7d71809650ef8076"
SRC_URI += "git://github.com/coreos/go-systemd;name=go-systemd;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/coreos/go-systemd"

# github.com/coreos/go-systemd/v22 v22.3.2
# [1] git ls-remote https://github.com/coreos/go-systemd 777e73a89cef78631ccaa97f53a9bae67e166186 
SRCREV_v22="777e73a89cef78631ccaa97f53a9bae67e166186"
SRC_URI += "git://github.com/coreos/go-systemd;name=v22;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/coreos/go-systemd/v22"

# github.com/coreos/pkg v0.0.0-20180928190104-399ea9e2e55f
# [1] git ls-remote https://github.com/coreos/pkg 399ea9e2e55f791b6e3d920860dbecb99c3692f0 
SRCREV_pkg="399ea9e2e55f791b6e3d920860dbecb99c3692f0"
SRC_URI += "git://github.com/coreos/pkg;name=pkg;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/coreos/pkg"

# github.com/cpuguy83/go-md2man/v2 v2.0.1
# [1] git ls-remote https://github.com/cpuguy83/go-md2man b1ec32e02fe539480dc03e3bf381c20066e7c6cc 
SRCREV_v21="b1ec32e02fe539480dc03e3bf381c20066e7c6cc"
SRC_URI += "git://github.com/cpuguy83/go-md2man;name=v21;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/cpuguy83/go-md2man/v2"

# github.com/creack/pty v1.1.11
# [1] git ls-remote https://github.com/creack/pty 2a38352e8b4d7ab6c336eef107e42a55e72e7fbc 
SRCREV_pty="2a38352e8b4d7ab6c336eef107e42a55e72e7fbc"
SRC_URI += "git://github.com/creack/pty;name=pty;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/creack/pty"

# github.com/cyphar/filepath-securejoin v0.2.3
# [1] git ls-remote https://github.com/cyphar/filepath-securejoin 8f267f5ea675a20a2cb5e91011d063721f53bf79 
SRCREV_filepath-securejoin="8f267f5ea675a20a2cb5e91011d063721f53bf79"
SRC_URI += "git://github.com/cyphar/filepath-securejoin;name=filepath-securejoin;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/cyphar/filepath-securejoin"

# github.com/d2g/dhcp4 v0.0.0-20170904100407-a1d1b6c41b1c
# [1] git ls-remote https://github.com/d2g/dhcp4 a1d1b6c41b1ce8a71a5121a9cee31809c4707d9c 
SRCREV_dhcp4="a1d1b6c41b1ce8a71a5121a9cee31809c4707d9c"
SRC_URI += "git://github.com/d2g/dhcp4;name=dhcp4;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/d2g/dhcp4"

# github.com/d2g/dhcp4client v1.0.0
# [1] git ls-remote https://github.com/d2g/dhcp4client b7a004ff1a09ab6723625e179a44e476306977a6 
SRCREV_dhcp4client="b7a004ff1a09ab6723625e179a44e476306977a6"
SRC_URI += "git://github.com/d2g/dhcp4client;name=dhcp4client;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/d2g/dhcp4client"

# github.com/d2g/dhcp4server v0.0.0-20181031114812-7d4a0a7f59a5
# [1] git ls-remote https://github.com/d2g/dhcp4server 7d4a0a7f59a572d629ba5f49634b35c7fac7967e 
SRCREV_dhcp4server="7d4a0a7f59a572d629ba5f49634b35c7fac7967e"
SRC_URI += "git://github.com/d2g/dhcp4server;name=dhcp4server;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/d2g/dhcp4server"

# github.com/d2g/hardwareaddr v0.0.0-20190221164911-e7d9fbe030e4
# [1] git ls-remote https://github.com/d2g/hardwareaddr e7d9fbe030e4b7b08e3bdd128e2e81809e4389fd 
SRCREV_hardwareaddr="e7d9fbe030e4b7b08e3bdd128e2e81809e4389fd"
SRC_URI += "git://github.com/d2g/hardwareaddr;name=hardwareaddr;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/d2g/hardwareaddr"

# github.com/danieljoos/wincred v1.1.0
# [1] git ls-remote https://github.com/danieljoos/wincred 78f93c1f8b99b0c2f6e7f3d2bdc4993cf87bddff 
SRCREV_wincred="78f93c1f8b99b0c2f6e7f3d2bdc4993cf87bddff"
SRC_URI += "git://github.com/danieljoos/wincred;name=wincred;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/danieljoos/wincred"

# github.com/davecgh/go-spew v1.1.1
# [1] git ls-remote https://github.com/davecgh/go-spew 8991bc29aa16c548c550c7ff78260e27b9ab7c73 
SRCREV_go-spew="8991bc29aa16c548c550c7ff78260e27b9ab7c73"
SRC_URI += "git://github.com/davecgh/go-spew;name=go-spew;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/davecgh/go-spew"

# github.com/daviddengcn/go-colortext v0.0.0-20160507010035-511bcaf42ccd
# [1] git ls-remote https://github.com/daviddengcn/go-colortext 511bcaf42ccd42c38aba7427b6673277bf19e2a1 
SRCREV_go-colortext="511bcaf42ccd42c38aba7427b6673277bf19e2a1"
SRC_URI += "git://github.com/daviddengcn/go-colortext;name=go-colortext;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/daviddengcn/go-colortext"

# github.com/denverdino/aliyungo v0.0.0-20190125010748-a747050bb1ba
# [1] git ls-remote https://github.com/denverdino/aliyungo a747050bb1baf06cdd65de7cddc281a2b1c2fde5 
SRCREV_aliyungo="a747050bb1baf06cdd65de7cddc281a2b1c2fde5"
SRC_URI += "git://github.com/denverdino/aliyungo;name=aliyungo;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/denverdino/aliyungo"

# github.com/dgrijalva/jwt-go v3.2.0+incompatible
# [1] git ls-remote https://github.com/dgrijalva/jwt-go 06ea1031745cb8b3dab3f6a236daf2b0aa468b7e 
SRCREV_jwt-go="06ea1031745cb8b3dab3f6a236daf2b0aa468b7e"
SRC_URI += "git://github.com/dgrijalva/jwt-go;name=jwt-go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/dgrijalva/jwt-go"

# github.com/dgryski/go-farm v0.0.0-20200201041132-a6ae2369ad13
# [1] git ls-remote https://github.com/dgryski/go-farm a6ae2369ad13dc757768086f0cb902728c7e03e5 
SRCREV_go-farm="a6ae2369ad13dc757768086f0cb902728c7e03e5"
SRC_URI += "git://github.com/dgryski/go-farm;name=go-farm;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/dgryski/go-farm"

# github.com/dgryski/go-sip13 v0.0.0-20181026042036-e10d5fee7954
# [1] git ls-remote https://github.com/dgryski/go-sip13 e10d5fee79544bd84105c9329adcc7b745fd588c 
SRCREV_go-sip13="e10d5fee79544bd84105c9329adcc7b745fd588c"
SRC_URI += "git://github.com/dgryski/go-sip13;name=go-sip13;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/dgryski/go-sip13"

# github.com/dimchansky/utfbom v1.1.1
# [1] git ls-remote https://github.com/dimchansky/utfbom 6ae8f945ca96f30defc7e8ab12ec5d10cf86ded4 
SRCREV_utfbom="6ae8f945ca96f30defc7e8ab12ec5d10cf86ded4"
SRC_URI += "git://github.com/dimchansky/utfbom;name=utfbom;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/dimchansky/utfbom"

# github.com/dnaeon/go-vcr v1.0.1
# [1] git ls-remote https://github.com/dnaeon/go-vcr b3f5a17c396f1f45e232e36c6eed2577da52d22a 
SRCREV_go-vcr="b3f5a17c396f1f45e232e36c6eed2577da52d22a"
SRC_URI += "git://github.com/dnaeon/go-vcr;name=go-vcr;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/dnaeon/go-vcr"

# github.com/docker/cli v20.10.14+incompatible
# [1] git ls-remote https://github.com/docker/cli a224086349269551becacce16e5842ceeb2a98d6 
SRCREV_cli1="a224086349269551becacce16e5842ceeb2a98d6"
SRC_URI += "git://github.com/docker/cli;name=cli1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/docker/cli"

# github.com/docker/distribution v2.8.1+incompatible
# [1] git ls-remote https://github.com/docker/distribution b5ca020cfbe998e5af3457fda087444cf5116496 
SRCREV_distribution="b5ca020cfbe998e5af3457fda087444cf5116496"
SRC_URI += "git://github.com/docker/distribution;name=distribution;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/docker/distribution"

# github.com/docker/docker v20.10.12+incompatible
# [1] git ls-remote https://github.com/docker/docker 459d0dfbbb51fb2423a43655e6c62368ec0f36c9 
SRCREV_docker="459d0dfbbb51fb2423a43655e6c62368ec0f36c9"
SRC_URI += "git://github.com/docker/docker;name=docker;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/docker/docker"

# github.com/docker/docker-credential-helpers v0.6.4
# [1] git ls-remote https://github.com/docker/docker-credential-helpers fc9290adbcf1594e78910e2f0334090eaee0e1ee 
SRCREV_docker-credential-helpers="fc9290adbcf1594e78910e2f0334090eaee0e1ee"
SRC_URI += "git://github.com/docker/docker-credential-helpers;name=docker-credential-helpers;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/docker/docker-credential-helpers"

# github.com/docker/go-connections v0.4.0
# [1] git ls-remote https://github.com/docker/go-connections 7395e3f8aa162843a74ed6d48e79627d9792ac55 
SRCREV_go-connections="7395e3f8aa162843a74ed6d48e79627d9792ac55"
SRC_URI += "git://github.com/docker/go-connections;name=go-connections;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/docker/go-connections"

# github.com/docker/go-events v0.0.0-20190806004212-e31b211e4f1c
# [1] git ls-remote https://github.com/docker/go-events e31b211e4f1cd09aa76fe4ac244571fab96ae47f 
SRCREV_go-events="e31b211e4f1cd09aa76fe4ac244571fab96ae47f"
SRC_URI += "git://github.com/docker/go-events;name=go-events;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/docker/go-events"

# github.com/docker/go-metrics v0.0.1
# [1] git ls-remote https://github.com/docker/go-metrics b619b3592b65de4f087d9f16863a7e6ff905973c 
SRCREV_go-metrics1="b619b3592b65de4f087d9f16863a7e6ff905973c"
SRC_URI += "git://github.com/docker/go-metrics;name=go-metrics1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/docker/go-metrics"

# github.com/docker/go-units v0.4.0
# [1] git ls-remote https://github.com/docker/go-units 519db1ee28dcc9fd2474ae59fca29a810482bfb1 
SRCREV_go-units="519db1ee28dcc9fd2474ae59fca29a810482bfb1"
SRC_URI += "git://github.com/docker/go-units;name=go-units;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/docker/go-units"

# github.com/docker/spdystream v0.0.0-20181023171402-6480d4af844c
# [1] git ls-remote https://github.com/docker/spdystream 6480d4af844c189cf5dd913db24ddd339d3a4f85 
SRCREV_spdystream="6480d4af844c189cf5dd913db24ddd339d3a4f85"
SRC_URI += "git://github.com/docker/spdystream;name=spdystream;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/docker/spdystream"

# github.com/docopt/docopt-go v0.0.0-20180111231733-ee0de3bc6815
# [1] git ls-remote https://github.com/docopt/docopt-go ee0de3bc6815ee19d4a46c7eb90f829db0e014b1 
SRCREV_docopt-go="ee0de3bc6815ee19d4a46c7eb90f829db0e014b1"
SRC_URI += "git://github.com/docopt/docopt-go;name=docopt-go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/docopt/docopt-go"

# github.com/dustin/go-humanize v1.0.0
# [1] git ls-remote https://github.com/dustin/go-humanize 9f541cc9db5d55bce703bd99987c9d5cb8eea45e 
SRCREV_go-humanize="9f541cc9db5d55bce703bd99987c9d5cb8eea45e"
SRC_URI += "git://github.com/dustin/go-humanize;name=go-humanize;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/dustin/go-humanize"

# github.com/dustmop/soup v1.1.2-0.20190516214245-38228baa104e
# [1] git ls-remote https://github.com/dustmop/soup 38228baa104ea9c9b73420407c1c4ba813deeaae 
SRCREV_soup="38228baa104ea9c9b73420407c1c4ba813deeaae"
SRC_URI += "git://github.com/dustmop/soup;name=soup;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/dustmop/soup"

# github.com/eapache/channels v1.1.0
# [1] git ls-remote https://github.com/eapache/channels 47238d5aae8c0fefd518ef2bee46290909cf8263 
SRCREV_channels="47238d5aae8c0fefd518ef2bee46290909cf8263"
SRC_URI += "git://github.com/eapache/channels;name=channels;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/eapache/channels"

# github.com/eapache/queue v1.1.0
# [1] git ls-remote https://github.com/eapache/queue 44cc805cf13205b55f69e14bcb69867d1ae92f98 
SRCREV_queue="44cc805cf13205b55f69e14bcb69867d1ae92f98"
SRC_URI += "git://github.com/eapache/queue;name=queue;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/eapache/queue"

# github.com/elazarl/goproxy v0.0.0-20190911111923-ecfe977594f1
# [1] git ls-remote https://github.com/elazarl/goproxy ecfe977594f1c77c24997af02cbfffd88b296514 
SRCREV_goproxy="ecfe977594f1c77c24997af02cbfffd88b296514"
SRC_URI += "git://github.com/elazarl/goproxy;name=goproxy;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/elazarl/goproxy"

# github.com/elazarl/goproxy/ext v0.0.0-20190711103511-473e67f1d7d2
# [1] git ls-remote https://github.com/elazarl/goproxy 473e67f1d7d297e5be759edf6e981768c89529f2 
SRCREV_ext="473e67f1d7d297e5be759edf6e981768c89529f2"
SRC_URI += "git://github.com/elazarl/goproxy;name=ext;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/elazarl/goproxy/ext"

# github.com/emicklei/go-restful v2.9.5+incompatible
# [1] git ls-remote https://github.com/emicklei/go-restful b993709ae1a4f6dd19cfa475232614441b11c9d5 
SRCREV_go-restful="b993709ae1a4f6dd19cfa475232614441b11c9d5"
SRC_URI += "git://github.com/emicklei/go-restful;name=go-restful;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/emicklei/go-restful"

# github.com/envoyproxy/go-control-plane v0.9.9-0.20210512163311-63b5d3c536b0
# [1] git ls-remote https://github.com/envoyproxy/go-control-plane 63b5d3c536b0a97ec2bbb2e74de7adae1ba1dfee 
SRCREV_go-control-plane="63b5d3c536b0a97ec2bbb2e74de7adae1ba1dfee"
SRC_URI += "git://github.com/envoyproxy/go-control-plane;name=go-control-plane;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/envoyproxy/go-control-plane"

# github.com/envoyproxy/protoc-gen-validate v0.1.0
# [1] git ls-remote https://github.com/envoyproxy/protoc-gen-validate 9eff07ddfcb4001aa1aab280648153f46e1a8ddc 
SRCREV_protoc-gen-validate="9eff07ddfcb4001aa1aab280648153f46e1a8ddc"
SRC_URI += "git://github.com/envoyproxy/protoc-gen-validate;name=protoc-gen-validate;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/envoyproxy/protoc-gen-validate"

# github.com/erikdubbelboer/gspt v0.0.0-20190125194910-e68493906b83
# [1] git ls-remote https://github.com/erikdubbelboer/gspt e68493906b8382891943ddc9960cb9c6ecd1a1f0 
SRCREV_gspt="e68493906b8382891943ddc9960cb9c6ecd1a1f0"
SRC_URI += "git://github.com/erikdubbelboer/gspt;name=gspt;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/erikdubbelboer/gspt"

# github.com/etcd-io/gofail v0.0.0-20190801230047-ad7f989257ca
# [1] git ls-remote https://github.com/etcd-io/gofail ad7f989257ca7de90de65657c9748a3108b1460f 
SRCREV_gofail="ad7f989257ca7de90de65657c9748a3108b1460f"
SRC_URI += "git://github.com/etcd-io/gofail;name=gofail;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/etcd-io/gofail"

# github.com/euank/go-kmsg-parser v2.0.0+incompatible
# [1] git ls-remote https://github.com/euank/go-kmsg-parser 5ba4d492e455a77d25dcf0d2c4acc9f2afebef4e 
SRCREV_go-kmsg-parser="5ba4d492e455a77d25dcf0d2c4acc9f2afebef4e"
SRC_URI += "git://github.com/euank/go-kmsg-parser;name=go-kmsg-parser;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/euank/go-kmsg-parser"

# github.com/evanphx/json-patch v4.12.0+incompatible
# [1] git ls-remote https://github.com/evanphx/json-patch 50fdc0b4c9ab36933e6a0c9288fef3ac5df2b907 
SRCREV_json-patch="50fdc0b4c9ab36933e6a0c9288fef3ac5df2b907"
SRC_URI += "git://github.com/evanphx/json-patch;name=json-patch;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/evanphx/json-patch"

# github.com/exponent-io/jsonpath v0.0.0-20151013193312-d6023ce2651d
# [1] git ls-remote https://github.com/exponent-io/jsonpath d6023ce2651d8eafb5c75bb0c7167536102ec9f5 
SRCREV_jsonpath="d6023ce2651d8eafb5c75bb0c7167536102ec9f5"
SRC_URI += "git://github.com/exponent-io/jsonpath;name=jsonpath;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/exponent-io/jsonpath"

# github.com/fanliao/go-promise v0.0.0-20141029170127-1890db352a72
# [1] git ls-remote https://github.com/fanliao/go-promise 1890db352a72f9e6c6219c20111355cddc795297 
SRCREV_go-promise="1890db352a72f9e6c6219c20111355cddc795297"
SRC_URI += "git://github.com/fanliao/go-promise;name=go-promise;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/fanliao/go-promise"

# github.com/fatih/camelcase v1.0.0
# [1] git ls-remote https://github.com/fatih/camelcase 44e46d280b43ec1531bb25252440e34f1b800b65 
SRCREV_camelcase="44e46d280b43ec1531bb25252440e34f1b800b65"
SRC_URI += "git://github.com/fatih/camelcase;name=camelcase;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/fatih/camelcase"

# github.com/fatih/color v1.9.0
# [1] git ls-remote https://github.com/fatih/color daf2830f2741ebb735b21709a520c5f37d642d85 
SRCREV_color="daf2830f2741ebb735b21709a520c5f37d642d85"
SRC_URI += "git://github.com/fatih/color;name=color;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/fatih/color"

# github.com/felixge/httpsnoop v1.0.1
# [1] git ls-remote https://github.com/felixge/httpsnoop 33ec42cfe005395fb4cc4b296781f65d7ffef2c3 
SRCREV_httpsnoop="33ec42cfe005395fb4cc4b296781f65d7ffef2c3"
SRC_URI += "git://github.com/felixge/httpsnoop;name=httpsnoop;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/felixge/httpsnoop"

# github.com/flannel-io/flannel v0.18.1
# [1] git ls-remote https://github.com/flannel-io/flannel b31565ba01869ad22cdc4eb0601548ecb5a104cf 
SRCREV_flannel="b31565ba01869ad22cdc4eb0601548ecb5a104cf"
SRC_URI += "git://github.com/flannel-io/flannel;name=flannel;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/flannel-io/flannel"

# github.com/flynn/go-shlex v0.0.0-20150515145356-3f9db97f8568
# [1] git ls-remote https://github.com/flynn/go-shlex 3f9db97f856818214da2e1057f8ad84803971cff 
SRCREV_go-shlex="3f9db97f856818214da2e1057f8ad84803971cff"
SRC_URI += "git://github.com/flynn/go-shlex;name=go-shlex;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/flynn/go-shlex"

# github.com/fogleman/gg v1.2.1-0.20190220221249-0403632d5b90
# [1] git ls-remote https://github.com/fogleman/gg 0403632d5b905943a1c2a5b2763aaecd568467ec 
SRCREV_gg="0403632d5b905943a1c2a5b2763aaecd568467ec"
SRC_URI += "git://github.com/fogleman/gg;name=gg;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/fogleman/gg"

# github.com/form3tech-oss/jwt-go v3.2.3+incompatible
# [1] git ls-remote https://github.com/form3tech-oss/jwt-go 5b2d2b5f6c34ccb3b6b65f77f4706558067690ef 
SRCREV_jwt-go1="5b2d2b5f6c34ccb3b6b65f77f4706558067690ef"
SRC_URI += "git://github.com/form3tech-oss/jwt-go;name=jwt-go1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/form3tech-oss/jwt-go"

# github.com/frankban/quicktest v1.12.1
# [1] git ls-remote https://github.com/frankban/quicktest 46a4d984dc347eaa97966c8cc7b5630c2c1d3105 
SRCREV_quicktest="46a4d984dc347eaa97966c8cc7b5630c2c1d3105"
SRC_URI += "git://github.com/frankban/quicktest;name=quicktest;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/frankban/quicktest"

# github.com/fsnotify/fsnotify v1.4.9
# [1] git ls-remote https://github.com/fsnotify/fsnotify 45d7d09e39ef4ac08d493309fa031790c15bfe8a 
SRCREV_fsnotify="45d7d09e39ef4ac08d493309fa031790c15bfe8a"
SRC_URI += "git://github.com/fsnotify/fsnotify;name=fsnotify;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/fsnotify/fsnotify"

# github.com/fvbommel/sortorder v1.0.1
# [1] git ls-remote https://github.com/fvbommel/sortorder a1ddee917217bbd0691affdca9c88d24d3f5c27d 
SRCREV_sortorder="a1ddee917217bbd0691affdca9c88d24d3f5c27d"
SRC_URI += "git://github.com/fvbommel/sortorder;name=sortorder;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/fvbommel/sortorder"

# github.com/getkin/kin-openapi v0.76.0
# [1] git ls-remote https://github.com/getkin/kin-openapi f58924543e3f7a5e41383a2513933fa27477a02e 
SRCREV_kin-openapi="f58924543e3f7a5e41383a2513933fa27477a02e"
SRC_URI += "git://github.com/getkin/kin-openapi;name=kin-openapi;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/getkin/kin-openapi"

# github.com/getsentry/raven-go v0.2.0
# [1] git ls-remote https://github.com/getsentry/raven-go f04e7487e9a6b9d9837d52743fb5f40576c56411 
SRCREV_raven-go="f04e7487e9a6b9d9837d52743fb5f40576c56411"
SRC_URI += "git://github.com/getsentry/raven-go;name=raven-go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/getsentry/raven-go"

# github.com/ghodss/yaml v1.0.0
# [1] git ls-remote https://github.com/ghodss/yaml 0ca9ea5df5451ffdf184b4428c902747c2c11cd7 
SRCREV_yaml="0ca9ea5df5451ffdf184b4428c902747c2c11cd7"
SRC_URI += "git://github.com/ghodss/yaml;name=yaml;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/ghodss/yaml"

# github.com/globalsign/mgo v0.0.0-20181015135952-eeefdecb41b8
# [1] git ls-remote https://github.com/globalsign/mgo eeefdecb41b842af6dc652aaea4026e8403e62df 
SRCREV_mgo="eeefdecb41b842af6dc652aaea4026e8403e62df"
SRC_URI += "git://github.com/globalsign/mgo;name=mgo;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/globalsign/mgo"

# github.com/go-bindata/go-bindata v3.1.2+incompatible
# [1] git ls-remote https://github.com/go-bindata/go-bindata ff7109080027ec152dabb369e157c4f359b76f31 
SRCREV_go-bindata="ff7109080027ec152dabb369e157c4f359b76f31"
SRC_URI += "git://github.com/go-bindata/go-bindata;name=go-bindata;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-bindata/go-bindata"

# github.com/go-errors/errors v1.0.2-0.20180813162953-d98b870cc4e0
# [1] git ls-remote https://github.com/go-errors/errors d98b870cc4e05f1545532a80e9909be8216095b6 
SRCREV_errors1="d98b870cc4e05f1545532a80e9909be8216095b6"
SRC_URI += "git://github.com/go-errors/errors;name=errors1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-errors/errors"

# github.com/go-gl/glfw v0.0.0-20190409004039-e6da0acd62b1
# [1] git ls-remote https://github.com/go-gl/glfw e6da0acd62b1b57ee2799d4d0a76a7d4514dc5bc 
SRCREV_glfw="e6da0acd62b1b57ee2799d4d0a76a7d4514dc5bc"
SRC_URI += "git://github.com/go-gl/glfw;name=glfw;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-gl/glfw"

# github.com/go-gl/glfw/v3.3/glfw v0.0.0-20200222043503-6f7a984d4dc4
# [1] git ls-remote https://github.com/go-gl/glfw 6f7a984d4dc470c3f197229ad1991ae9e211bba2 
SRCREV_glfw1="6f7a984d4dc470c3f197229ad1991ae9e211bba2"
SRC_URI += "git://github.com/go-gl/glfw;name=glfw1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-gl/glfw/v3.3/glfw"

# github.com/go-ini/ini v1.28.1
# [1] git ls-remote https://github.com/go-ini/ini 3d73f4b845efdf9989fffd4b4e562727744a34ba 
SRCREV_ini="3d73f4b845efdf9989fffd4b4e562727744a34ba"
SRC_URI += "git://github.com/go-ini/ini;name=ini;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-ini/ini"

# github.com/go-kit/kit v0.9.0
# [1] git ls-remote https://github.com/go-kit/kit 150a65a7ec6156b4b640c1fd55f26fd3d475d656 
SRCREV_kit="150a65a7ec6156b4b640c1fd55f26fd3d475d656"
SRC_URI += "git://github.com/go-kit/kit;name=kit;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-kit/kit"

# github.com/go-kit/log v0.1.0
# [1] git ls-remote https://github.com/go-kit/log 63376a29a92260f7b256eedf2fae39e28161114d 
SRCREV_log="63376a29a92260f7b256eedf2fae39e28161114d"
SRC_URI += "git://github.com/go-kit/log;name=log;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-kit/log"

# github.com/go-logfmt/logfmt v0.5.0
# [1] git ls-remote https://github.com/go-logfmt/logfmt 3be5f6aae7db841d31dc5e1b3bb7b4cff19200b3 
SRCREV_logfmt="3be5f6aae7db841d31dc5e1b3bb7b4cff19200b3"
SRC_URI += "git://github.com/go-logfmt/logfmt;name=logfmt;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-logfmt/logfmt"

# github.com/go-logr/logr v1.2.2
# [1] git ls-remote https://github.com/go-logr/logr 99e02a994f50f904b6b201df394776cf687238a9 
SRCREV_logr="99e02a994f50f904b6b201df394776cf687238a9"
SRC_URI += "git://github.com/go-logr/logr;name=logr;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-logr/logr"

# github.com/go-logr/zapr v1.2.0
# [1] git ls-remote https://github.com/go-logr/zapr a01e9a220589e9122aebdf3dee4b9a5abd344554 
SRCREV_zapr="a01e9a220589e9122aebdf3dee4b9a5abd344554"
SRC_URI += "git://github.com/go-logr/zapr;name=zapr;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-logr/zapr"

# github.com/go-openapi/analysis v0.19.5
# [1] git ls-remote https://github.com/go-openapi/analysis 9864a87593df5ab83eac00921906932d1d77fae2 
SRCREV_analysis="9864a87593df5ab83eac00921906932d1d77fae2"
SRC_URI += "git://github.com/go-openapi/analysis;name=analysis;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-openapi/analysis"

# github.com/go-openapi/errors v0.19.2
# [1] git ls-remote https://github.com/go-openapi/errors 0b2a0a1f89aa2eec2d13e03cd80ab0fdaf01f8ce 
SRCREV_errors12="0b2a0a1f89aa2eec2d13e03cd80ab0fdaf01f8ce"
SRC_URI += "git://github.com/go-openapi/errors;name=errors12;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-openapi/errors"

# github.com/go-openapi/jsonpointer v0.19.5
# [1] git ls-remote https://github.com/go-openapi/jsonpointer 2446e21cad36eee826e1c2380f0ff747074a2faa 
SRCREV_jsonpointer="2446e21cad36eee826e1c2380f0ff747074a2faa"
SRC_URI += "git://github.com/go-openapi/jsonpointer;name=jsonpointer;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-openapi/jsonpointer"

# github.com/go-openapi/jsonreference v0.19.5
# [1] git ls-remote https://github.com/go-openapi/jsonreference 28a8ff900147568dc266d4cd84e062750e44f161 
SRCREV_jsonreference="28a8ff900147568dc266d4cd84e062750e44f161"
SRC_URI += "git://github.com/go-openapi/jsonreference;name=jsonreference;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-openapi/jsonreference"

# github.com/go-openapi/loads v0.19.4
# [1] git ls-remote https://github.com/go-openapi/loads bda4742c39071f7804171d847fb6e9bca84f9f57 
SRCREV_loads="bda4742c39071f7804171d847fb6e9bca84f9f57"
SRC_URI += "git://github.com/go-openapi/loads;name=loads;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-openapi/loads"

# github.com/go-openapi/runtime v0.19.4
# [1] git ls-remote https://github.com/go-openapi/runtime 882fa18ad52bee24eb77d2312d1929580e894013 
SRCREV_runtime="882fa18ad52bee24eb77d2312d1929580e894013"
SRC_URI += "git://github.com/go-openapi/runtime;name=runtime;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-openapi/runtime"

# github.com/go-openapi/spec v0.19.5
# [1] git ls-remote https://github.com/go-openapi/spec 772572fd19ebcc983369e53bfaed4bde2077fe0c 
SRCREV_spec1="772572fd19ebcc983369e53bfaed4bde2077fe0c"
SRC_URI += "git://github.com/go-openapi/spec;name=spec1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-openapi/spec"

# github.com/go-openapi/strfmt v0.19.5
# [1] git ls-remote https://github.com/go-openapi/strfmt 2887e74ed0fe2aea628affe322b19fc3112f98cd 
SRCREV_strfmt="2887e74ed0fe2aea628affe322b19fc3112f98cd"
SRC_URI += "git://github.com/go-openapi/strfmt;name=strfmt;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-openapi/strfmt"

# github.com/go-openapi/swag v0.19.14
# [1] git ls-remote https://github.com/go-openapi/swag c78fa6719bc3d4b977d19a09064feda1c07571af 
SRCREV_swag="c78fa6719bc3d4b977d19a09064feda1c07571af"
SRC_URI += "git://github.com/go-openapi/swag;name=swag;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-openapi/swag"

# github.com/go-openapi/validate v0.19.8
# [1] git ls-remote https://github.com/go-openapi/validate f2f9aec8857b8132ef7ac0940f52572195db1fdc 
SRCREV_validate="f2f9aec8857b8132ef7ac0940f52572195db1fdc"
SRC_URI += "git://github.com/go-openapi/validate;name=validate;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-openapi/validate"

# github.com/go-ozzo/ozzo-validation v3.5.0+incompatible
# [1] git ls-remote https://github.com/go-ozzo/ozzo-validation 106681dbb37bfa3e7683c4c8129cb7f5925ea3e9 
SRCREV_ozzo-validation="106681dbb37bfa3e7683c4c8129cb7f5925ea3e9"
SRC_URI += "git://github.com/go-ozzo/ozzo-validation;name=ozzo-validation;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-ozzo/ozzo-validation"

# github.com/go-sql-driver/mysql v1.6.0
# [1] git ls-remote https://github.com/go-sql-driver/mysql bcc459a906419e2890a50fc2c99ea6dd927a88f2 
SRCREV_mysql="bcc459a906419e2890a50fc2c99ea6dd927a88f2"
SRC_URI += "git://github.com/go-sql-driver/mysql;name=mysql;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-sql-driver/mysql"

# github.com/go-stack/stack v1.8.0
# [1] git ls-remote https://github.com/go-stack/stack 2fee6af1a9795aafbe0253a0cfbdf668e1fb8a9a 
SRCREV_stack="2fee6af1a9795aafbe0253a0cfbdf668e1fb8a9a"
SRC_URI += "git://github.com/go-stack/stack;name=stack;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-stack/stack"

# github.com/go-task/slim-sprig v0.0.0-20210107165309-348f09dbbbc0
# [1] git ls-remote https://github.com/go-task/slim-sprig 348f09dbbbc0ff8b922a311da7a90cd014b69aa3 
SRCREV_slim-sprig="348f09dbbbc0ff8b922a311da7a90cd014b69aa3"
SRC_URI += "git://github.com/go-task/slim-sprig;name=slim-sprig;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-task/slim-sprig"

# github.com/go-test/deep v1.0.7
# [1] git ls-remote https://github.com/go-test/deep c733f5ed5136b7437e29ac91bb97c653af3694b5 
SRCREV_deep="c733f5ed5136b7437e29ac91bb97c653af3694b5"
SRC_URI += "git://github.com/go-test/deep;name=deep;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/go-test/deep"

# github.com/godbus/dbus v0.0.0-20180201030542-885f9cc04c9c
# [1] git ls-remote https://github.com/godbus/dbus 885f9cc04c9c1a6a61a2008e211d36c5737be3f5 
SRCREV_dbus="885f9cc04c9c1a6a61a2008e211d36c5737be3f5"
SRC_URI += "git://github.com/godbus/dbus;name=dbus;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/godbus/dbus"

# github.com/godbus/dbus/v5 v5.0.6
# [1] git ls-remote https://github.com/godbus/dbus daa017464e266380cdeb4e6f9613eba9182b59a3 
SRCREV_v51="daa017464e266380cdeb4e6f9613eba9182b59a3"
SRC_URI += "git://github.com/godbus/dbus;name=v51;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/godbus/dbus/v5"

# github.com/gofrs/flock v0.8.1
# [1] git ls-remote https://github.com/gofrs/flock 6f010d1acea74a32f2f2066bfe324c08bbee30e3 
SRCREV_flock="6f010d1acea74a32f2f2066bfe324c08bbee30e3"
SRC_URI += "git://github.com/gofrs/flock;name=flock;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gofrs/flock"

# github.com/gofrs/uuid v4.0.0+incompatible
# [1] git ls-remote https://github.com/gofrs/uuid 4b36aa0ea796d49630bbae624f8f05ab7013af77 
SRCREV_uuid="4b36aa0ea796d49630bbae624f8f05ab7013af77"
SRC_URI += "git://github.com/gofrs/uuid;name=uuid;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gofrs/uuid"

# github.com/gogo/googleapis v1.4.0
# [1] git ls-remote https://github.com/gogo/googleapis 3a822044bc0ae0a330a7449d52de760218351d49 
SRCREV_googleapis="3a822044bc0ae0a330a7449d52de760218351d49"
SRC_URI += "git://github.com/gogo/googleapis;name=googleapis;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gogo/googleapis"

# github.com/gogo/protobuf v1.3.2
# [1] git ls-remote https://github.com/gogo/protobuf b03c65ea87cdc3521ede29f62fe3ce239267c1bc 
SRCREV_protobuf="b03c65ea87cdc3521ede29f62fe3ce239267c1bc"
SRC_URI += "git://github.com/gogo/protobuf;name=protobuf;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gogo/protobuf"

# github.com/golang/freetype v0.0.0-20170609003504-e2365dfdc4a0
# [1] git ls-remote https://github.com/golang/freetype e2365dfdc4a05e4b8299a783240d4a7d5a65d4e4 
SRCREV_freetype="e2365dfdc4a05e4b8299a783240d4a7d5a65d4e4"
SRC_URI += "git://github.com/golang/freetype;name=freetype;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/golang/freetype"

# github.com/golang/glog v1.0.0
# [1] git ls-remote https://github.com/golang/glog 9ef845f417d839250ceabbc25c1b26101e772dd7 
SRCREV_glog="9ef845f417d839250ceabbc25c1b26101e772dd7"
SRC_URI += "git://github.com/golang/glog;name=glog;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/golang/glog"

# github.com/golang/groupcache v0.0.0-20210331224755-41bb18bfe9da
# [1] git ls-remote https://github.com/golang/groupcache 41bb18bfe9da5321badc438f91158cd790a33aa3 
SRCREV_groupcache="41bb18bfe9da5321badc438f91158cd790a33aa3"
SRC_URI += "git://github.com/golang/groupcache;name=groupcache;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/golang/groupcache"

# github.com/golang/mock v1.6.0
# [1] git ls-remote https://github.com/golang/mock aba2ff9a6844d5e3289e8472d3217d5b3090f083 
SRCREV_mock="aba2ff9a6844d5e3289e8472d3217d5b3090f083"
SRC_URI += "git://github.com/golang/mock;name=mock;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/golang/mock"

# github.com/golang/protobuf v1.5.2
# [1] git ls-remote https://github.com/golang/protobuf ae97035608a719c7a1c1c41bed0ae0744bdb0c6f 
SRCREV_protobuf1="ae97035608a719c7a1c1c41bed0ae0744bdb0c6f"
SRC_URI += "git://github.com/golang/protobuf;name=protobuf1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/golang/protobuf"

# github.com/golang/snappy v0.0.3
# [1] git ls-remote https://github.com/golang/snappy 674baa8c7fc30da5df3074a459494a7e6b427dff 
SRCREV_snappy="674baa8c7fc30da5df3074a459494a7e6b427dff"
SRC_URI += "git://github.com/golang/snappy;name=snappy;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/golang/snappy"

# github.com/golangplus/bytes v0.0.0-20160111154220-45c989fe5450
# [1] git ls-remote https://github.com/golangplus/bytes 45c989fe545070ef7c9003cf1998bb195c61731a 
SRCREV_bytes="45c989fe545070ef7c9003cf1998bb195c61731a"
SRC_URI += "git://github.com/golangplus/bytes;name=bytes;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/golangplus/bytes"

# github.com/golangplus/fmt v1.0.0
# [1] git ls-remote https://github.com/golangplus/fmt 7a904709ad79c030bc913f5d1d1823dcf3953fa4 
SRCREV_fmt="7a904709ad79c030bc913f5d1d1823dcf3953fa4"
SRC_URI += "git://github.com/golangplus/fmt;name=fmt;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/golangplus/fmt"

# github.com/golangplus/testing v1.0.0
# [1] git ls-remote https://github.com/golangplus/testing d51138b619a0cd6d297e1a9985accd95ca75be46 
SRCREV_testing="d51138b619a0cd6d297e1a9985accd95ca75be46"
SRC_URI += "git://github.com/golangplus/testing;name=testing;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/golangplus/testing"

# github.com/google/btree v1.0.1
# [1] git ls-remote https://github.com/google/btree 479b5e81b0a93ec038d201b0b33d17db599531d3 
SRCREV_btree="479b5e81b0a93ec038d201b0b33d17db599531d3"
SRC_URI += "git://github.com/google/btree;name=btree;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/btree"

# github.com/google/cadvisor v0.44.1
# [1] git ls-remote https://github.com/google/cadvisor f95787b77e7d7430cfbc669123ddf0fb39ef825d 
SRCREV_cadvisor="f95787b77e7d7430cfbc669123ddf0fb39ef825d"
SRC_URI += "git://github.com/google/cadvisor;name=cadvisor;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/cadvisor"

# github.com/google/cel-go v0.10.1
# [1] git ls-remote https://github.com/google/cel-go 6efb4a8056e1b606a0a939972b593b0f69283c75 
SRCREV_cel-go="6efb4a8056e1b606a0a939972b593b0f69283c75"
SRC_URI += "git://github.com/google/cel-go;name=cel-go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/cel-go"

# github.com/google/cel-spec v0.6.0
# [1] git ls-remote https://github.com/google/cel-spec ad5c42c7f0a66f7ea43bd7299c0397ceef23beb5 
SRCREV_cel-spec="ad5c42c7f0a66f7ea43bd7299c0397ceef23beb5"
SRC_URI += "git://github.com/google/cel-spec;name=cel-spec;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/cel-spec"

# github.com/google/gnostic v0.5.7-v3refs
# [1] git ls-remote https://github.com/google/gnostic b49832d97d25b6a41cbe34ea12257924a934eeee 
SRCREV_gnostic="b49832d97d25b6a41cbe34ea12257924a934eeee"
SRC_URI += "git://github.com/google/gnostic;name=gnostic;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/gnostic"

# github.com/google/go-cmp v0.5.7
# [1] git ls-remote https://github.com/google/go-cmp 039e37cba1f3e52c48404633d6960421b369a19a 
SRCREV_go-cmp="039e37cba1f3e52c48404633d6960421b369a19a"
SRC_URI += "git://github.com/google/go-cmp;name=go-cmp;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/go-cmp"

# github.com/google/go-containerregistry v0.6.1-0.20211111182346-7a6ee45528a9
# [1] git ls-remote https://github.com/google/go-containerregistry 7a6ee45528a9faed11cf55b539478bd81d439868 
SRCREV_go-containerregistry="7a6ee45528a9faed11cf55b539478bd81d439868"
SRC_URI += "git://github.com/google/go-containerregistry;name=go-containerregistry;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/go-containerregistry"

# github.com/google/gofuzz v1.2.0
# [1] git ls-remote https://github.com/google/gofuzz 379e164120fbc98885a8f494b5aa41ba94f64c56 
SRCREV_gofuzz="379e164120fbc98885a8f494b5aa41ba94f64c56"
SRC_URI += "git://github.com/google/gofuzz;name=gofuzz;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/gofuzz"

# github.com/google/martian v2.1.0+incompatible
# [1] git ls-remote https://github.com/google/martian 195b986b4b6d4c513582cf4d2b8c4fd7e2494f7e 
SRCREV_martian="195b986b4b6d4c513582cf4d2b8c4fd7e2494f7e"
SRC_URI += "git://github.com/google/martian;name=martian;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/martian"

# github.com/google/martian/v3 v3.2.1
# [1] git ls-remote https://github.com/google/martian 7e75073889cd2324f33b959c4fb4545440da046c 
SRCREV_v3="7e75073889cd2324f33b959c4fb4545440da046c"
SRC_URI += "git://github.com/google/martian;name=v3;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/martian/v3"

# github.com/google/pprof v0.0.0-20210720184732-4bb14d4b1be1
# [1] git ls-remote https://github.com/google/pprof 4bb14d4b1be14417e47d0bbaf2bd4e188eda647f 
SRCREV_pprof="4bb14d4b1be14417e47d0bbaf2bd4e188eda647f"
SRC_URI += "git://github.com/google/pprof;name=pprof;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/pprof"

# github.com/google/renameio v0.1.0
# [1] git ls-remote https://github.com/google/renameio f0e32980c006571efd537032e5f9cd8c1a92819e 
SRCREV_renameio="f0e32980c006571efd537032e5f9cd8c1a92819e"
SRC_URI += "git://github.com/google/renameio;name=renameio;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/renameio"

# github.com/google/shlex v0.0.0-20191202100458-e7afc7fbc510
# [1] git ls-remote https://github.com/google/shlex e7afc7fbc51079733e9468cdfd1efcd7d196cd1d 
SRCREV_shlex="e7afc7fbc51079733e9468cdfd1efcd7d196cd1d"
SRC_URI += "git://github.com/google/shlex;name=shlex;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/shlex"

# github.com/google/uuid v1.3.0
# [1] git ls-remote https://github.com/google/uuid 44b5fee7c49cf3bcdf723f106b36d56ef13ccc88 
SRCREV_uuid1="44b5fee7c49cf3bcdf723f106b36d56ef13ccc88"
SRC_URI += "git://github.com/google/uuid;name=uuid1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/uuid"

# github.com/googleapis/gax-go/v2 v2.0.5
# [1] git ls-remote https://github.com/googleapis/gax-go bd5b16380fd03dc758d11cef74ba2e3bc8b0e8c2 
SRCREV_v212="bd5b16380fd03dc758d11cef74ba2e3bc8b0e8c2"
SRC_URI += "git://github.com/googleapis/gax-go;name=v212;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/googleapis/gax-go/v2"

# github.com/googleapis/gnostic v0.5.5
# [1] git ls-remote https://github.com/googleapis/gnostic 1550ae29653d42db69c8a98d368648be7f2c488d 
SRCREV_gnostic1="1550ae29653d42db69c8a98d368648be7f2c488d"
SRC_URI += "git://github.com/googleapis/gnostic;name=gnostic1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/googleapis/gnostic"

# github.com/gophercloud/gophercloud v0.1.0
# [1] git ls-remote https://github.com/gophercloud/gophercloud c2d73b246b48e239d3f03c455905e06fe26e33c3 
SRCREV_gophercloud="c2d73b246b48e239d3f03c455905e06fe26e33c3"
SRC_URI += "git://github.com/gophercloud/gophercloud;name=gophercloud;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gophercloud/gophercloud"

# github.com/gopherjs/gopherjs v0.0.0-20200217142428-fce0ec30dd00
# [1] git ls-remote https://github.com/gopherjs/gopherjs fce0ec30dd00773d3fa974351d04ce2737b5c4d9 
SRCREV_gopherjs="fce0ec30dd00773d3fa974351d04ce2737b5c4d9"
SRC_URI += "git://github.com/gopherjs/gopherjs;name=gopherjs;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gopherjs/gopherjs"

# github.com/gorilla/mux v1.8.0
# [1] git ls-remote https://github.com/gorilla/mux 98cb6bf42e086f6af920b965c38cacc07402d51b 
SRCREV_mux="98cb6bf42e086f6af920b965c38cacc07402d51b"
SRC_URI += "git://github.com/gorilla/mux;name=mux;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gorilla/mux"

# github.com/gorilla/websocket v1.4.2
# [1] git ls-remote https://github.com/gorilla/websocket b65e62901fc1c0d968042419e74789f6af455eb9 
SRCREV_websocket="b65e62901fc1c0d968042419e74789f6af455eb9"
SRC_URI += "git://github.com/gorilla/websocket;name=websocket;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gorilla/websocket"

# github.com/gregjones/httpcache v0.0.0-20180305231024-9cad4c3443a7
# [1] git ls-remote https://github.com/gregjones/httpcache 9cad4c3443a7200dd6400aef47183728de563a38 
SRCREV_httpcache="9cad4c3443a7200dd6400aef47183728de563a38"
SRC_URI += "git://github.com/gregjones/httpcache;name=httpcache;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gregjones/httpcache"

# github.com/grpc-ecosystem/go-grpc-middleware v1.3.0
# [1] git ls-remote https://github.com/grpc-ecosystem/go-grpc-middleware df0f91b29bbbdfc3a686a7a8edbe2b9de2072fdd 
SRCREV_go-grpc-middleware="df0f91b29bbbdfc3a686a7a8edbe2b9de2072fdd"
SRC_URI += "git://github.com/grpc-ecosystem/go-grpc-middleware;name=go-grpc-middleware;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/grpc-ecosystem/go-grpc-middleware"

# github.com/grpc-ecosystem/go-grpc-prometheus v1.2.0
# [1] git ls-remote https://github.com/grpc-ecosystem/go-grpc-prometheus c225b8c3b01faf2899099b768856a9e916e5087b 
SRCREV_go-grpc-prometheus="c225b8c3b01faf2899099b768856a9e916e5087b"
SRC_URI += "git://github.com/grpc-ecosystem/go-grpc-prometheus;name=go-grpc-prometheus;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/grpc-ecosystem/go-grpc-prometheus"

# github.com/grpc-ecosystem/grpc-gateway v1.16.0
# [1] git ls-remote https://github.com/grpc-ecosystem/grpc-gateway 094a6fe78b3ca888297d090185cdf30f0e42e157 
SRCREV_grpc-gateway="094a6fe78b3ca888297d090185cdf30f0e42e157"
SRC_URI += "git://github.com/grpc-ecosystem/grpc-gateway;name=grpc-gateway;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/grpc-ecosystem/grpc-gateway"

# github.com/gruntwork-io/go-commons v0.8.0
# [1] git ls-remote https://github.com/gruntwork-io/go-commons 96ff82e3db5f60e31be51ca89a678ec546285591 
SRCREV_go-commons="96ff82e3db5f60e31be51ca89a678ec546285591"
SRC_URI += "git://github.com/gruntwork-io/go-commons;name=go-commons;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gruntwork-io/go-commons"

# github.com/gruntwork-io/terratest v0.40.6
# [1] git ls-remote https://github.com/gruntwork-io/terratest 1439a3f3132f402a287a70379561c0d646b258fe 
SRCREV_terratest="1439a3f3132f402a287a70379561c0d646b258fe"
SRC_URI += "git://github.com/gruntwork-io/terratest;name=terratest;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gruntwork-io/terratest"

# github.com/hanwen/go-fuse/v2 v2.1.1-0.20220112183258-f57e95bda82d
# [1] git ls-remote https://github.com/hanwen/go-fuse f57e95bda82d40dd4b2ceb1065fbd98c834ba23b 
SRCREV_v2123="f57e95bda82d40dd4b2ceb1065fbd98c834ba23b"
SRC_URI += "git://github.com/hanwen/go-fuse;name=v2123;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hanwen/go-fuse/v2"

# github.com/hashicorp/consul/api v1.1.0
# [1] git ls-remote https://github.com/hashicorp/consul b2478036d88a7e8eb9d6a0daf1a1c9ad0c8885ca 
SRCREV_api="b2478036d88a7e8eb9d6a0daf1a1c9ad0c8885ca"
SRC_URI += "git://github.com/hashicorp/consul;name=api;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/consul/api"

# github.com/hashicorp/consul/sdk v0.1.1
# [1] git ls-remote https://github.com/hashicorp/consul 6abe00cb2c8a3298794a7a9a67815937bd56ed63 
SRCREV_sdk="6abe00cb2c8a3298794a7a9a67815937bd56ed63"
SRC_URI += "git://github.com/hashicorp/consul;name=sdk;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/consul/sdk"

# github.com/hashicorp/errwrap v1.1.0
# [1] git ls-remote https://github.com/hashicorp/errwrap 7b00e5db719c64d14dd0caaacbd13e76254d02c0 
SRCREV_errwrap="7b00e5db719c64d14dd0caaacbd13e76254d02c0"
SRC_URI += "git://github.com/hashicorp/errwrap;name=errwrap;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/errwrap"

# github.com/hashicorp/go-cleanhttp v0.5.2
# [1] git ls-remote https://github.com/hashicorp/go-cleanhttp 6d9e2ac5d828e5f8594b97f88c4bde14a67bb6d2 
SRCREV_go-cleanhttp="6d9e2ac5d828e5f8594b97f88c4bde14a67bb6d2"
SRC_URI += "git://github.com/hashicorp/go-cleanhttp;name=go-cleanhttp;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-cleanhttp"

# github.com/hashicorp/go-getter v1.5.9
# [1] git ls-remote https://github.com/hashicorp/go-getter 0821303379b8710364f15cae37bbbc8f0dc19cc7 
SRCREV_go-getter="0821303379b8710364f15cae37bbbc8f0dc19cc7"
SRC_URI += "git://github.com/hashicorp/go-getter;name=go-getter;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-getter"

# github.com/hashicorp/go-hclog v0.9.2
# [1] git ls-remote https://github.com/hashicorp/go-hclog 5ccdce08c75b6c7b37af61159f13f6a4f5e2e928 
SRCREV_go-hclog="5ccdce08c75b6c7b37af61159f13f6a4f5e2e928"
SRC_URI += "git://github.com/hashicorp/go-hclog;name=go-hclog;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-hclog"

# github.com/hashicorp/go-immutable-radix v1.0.0
# [1] git ls-remote https://github.com/hashicorp/go-immutable-radix 27df80928bb34bb1b0d6d0e01b9e679902e7a6b5 
SRCREV_go-immutable-radix="27df80928bb34bb1b0d6d0e01b9e679902e7a6b5"
SRC_URI += "git://github.com/hashicorp/go-immutable-radix;name=go-immutable-radix;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-immutable-radix"

# github.com/hashicorp/go-msgpack v0.5.3
# [1] git ls-remote https://github.com/hashicorp/go-msgpack be3a5be7ee2202386d02936a19ae4fbde1c77800 
SRCREV_go-msgpack="be3a5be7ee2202386d02936a19ae4fbde1c77800"
SRC_URI += "git://github.com/hashicorp/go-msgpack;name=go-msgpack;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-msgpack"

# github.com/hashicorp/go-multierror v1.1.1
# [1] git ls-remote https://github.com/hashicorp/go-multierror 9974e9ec57696378079ecc3accd3d6f29401b3a0 
SRCREV_go-multierror="9974e9ec57696378079ecc3accd3d6f29401b3a0"
SRC_URI += "git://github.com/hashicorp/go-multierror;name=go-multierror;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-multierror"

# github.com/hashicorp/go-retryablehttp v0.7.0
# [1] git ls-remote https://github.com/hashicorp/go-retryablehttp 02c1586c8f14be23e7eeb522f1094afbabf45e93 
SRCREV_go-retryablehttp="02c1586c8f14be23e7eeb522f1094afbabf45e93"
SRC_URI += "git://github.com/hashicorp/go-retryablehttp;name=go-retryablehttp;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-retryablehttp"

# github.com/hashicorp/go-rootcerts v1.0.0
# [1] git ls-remote https://github.com/hashicorp/go-rootcerts 63503fb4e1eca22f9ae0f90b49c5d5538a0e87eb 
SRCREV_go-rootcerts="63503fb4e1eca22f9ae0f90b49c5d5538a0e87eb"
SRC_URI += "git://github.com/hashicorp/go-rootcerts;name=go-rootcerts;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-rootcerts"

# github.com/hashicorp/go-safetemp v1.0.0
# [1] git ls-remote https://github.com/hashicorp/go-safetemp c9a55de4fe06c920a71964b53cfe3dd293a3c743 
SRCREV_go-safetemp="c9a55de4fe06c920a71964b53cfe3dd293a3c743"
SRC_URI += "git://github.com/hashicorp/go-safetemp;name=go-safetemp;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-safetemp"

# github.com/hashicorp/go-sockaddr v1.0.0
# [1] git ls-remote https://github.com/hashicorp/go-sockaddr a6a0d2df398f7e0e9f6e43f589c8b51cec0eb6b0 
SRCREV_go-sockaddr="a6a0d2df398f7e0e9f6e43f589c8b51cec0eb6b0"
SRC_URI += "git://github.com/hashicorp/go-sockaddr;name=go-sockaddr;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-sockaddr"

# github.com/hashicorp/go-syslog v1.0.0
# [1] git ls-remote https://github.com/hashicorp/go-syslog 8d1874e3e8d1862b74e0536851e218c4571066a5 
SRCREV_go-syslog="8d1874e3e8d1862b74e0536851e218c4571066a5"
SRC_URI += "git://github.com/hashicorp/go-syslog;name=go-syslog;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-syslog"

# github.com/hashicorp/go-uuid v1.0.1
# [1] git ls-remote https://github.com/hashicorp/go-uuid 4f571afc59f3043a65f8fe6bf46d887b10a01d43 
SRCREV_go-uuid="4f571afc59f3043a65f8fe6bf46d887b10a01d43"
SRC_URI += "git://github.com/hashicorp/go-uuid;name=go-uuid;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-uuid"

# github.com/hashicorp/go-version v1.3.0
# [1] git ls-remote https://github.com/hashicorp/go-version c2de2312d363381e4254abec260c6df2b1226483 
SRCREV_go-version="c2de2312d363381e4254abec260c6df2b1226483"
SRC_URI += "git://github.com/hashicorp/go-version;name=go-version;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go-version"

# github.com/hashicorp/go.net v0.0.1
# [1] git ls-remote https://github.com/hashicorp/go.net afc3cb3a421746fc66dd55b09a270c750cf536ce 
SRCREV_go.net="afc3cb3a421746fc66dd55b09a270c750cf536ce"
SRC_URI += "git://github.com/hashicorp/go.net;name=go.net;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/go.net"

# github.com/hashicorp/golang-lru v0.5.3
# [1] git ls-remote https://github.com/hashicorp/golang-lru 7f827b33c0f158ec5dfbba01bb0b14a4541fd81d 
SRCREV_golang-lru="7f827b33c0f158ec5dfbba01bb0b14a4541fd81d"
SRC_URI += "git://github.com/hashicorp/golang-lru;name=golang-lru;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/golang-lru"

# github.com/hashicorp/hcl v1.0.0
# [1] git ls-remote https://github.com/hashicorp/hcl 8cb6e5b959231cc1119e43259c4a608f9c51a241 
SRCREV_hcl="8cb6e5b959231cc1119e43259c4a608f9c51a241"
SRC_URI += "git://github.com/hashicorp/hcl;name=hcl;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/hcl"

# github.com/hashicorp/hcl/v2 v2.9.1
# [1] git ls-remote https://github.com/hashicorp/hcl 805c25b19a39c0b0153b0d7adf1d16e877a3c2a1 
SRCREV_v21234="805c25b19a39c0b0153b0d7adf1d16e877a3c2a1"
SRC_URI += "git://github.com/hashicorp/hcl;name=v21234;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/hcl/v2"

# github.com/hashicorp/logutils v1.0.0
# [1] git ls-remote https://github.com/hashicorp/logutils a335183dfd075f638afcc820c90591ca3c97eba6 
SRCREV_logutils="a335183dfd075f638afcc820c90591ca3c97eba6"
SRC_URI += "git://github.com/hashicorp/logutils;name=logutils;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/logutils"

# github.com/hashicorp/mdns v1.0.0
# [1] git ls-remote https://github.com/hashicorp/mdns c31d3f8e4a0a5b46b118c2fd26d9da36467002c9 
SRCREV_mdns="c31d3f8e4a0a5b46b118c2fd26d9da36467002c9"
SRC_URI += "git://github.com/hashicorp/mdns;name=mdns;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/mdns"

# github.com/hashicorp/memberlist v0.1.3
# [1] git ls-remote https://github.com/hashicorp/memberlist a9da52f0668fc4321ec18e9e28dd8141621a808f 
SRCREV_memberlist="a9da52f0668fc4321ec18e9e28dd8141621a808f"
SRC_URI += "git://github.com/hashicorp/memberlist;name=memberlist;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/memberlist"

# github.com/hashicorp/serf v0.8.2
# [1] git ls-remote https://github.com/hashicorp/serf b89a09ebd4b1b570e0076d5097272e67c10ac4f6 
SRCREV_serf="b89a09ebd4b1b570e0076d5097272e67c10ac4f6"
SRC_URI += "git://github.com/hashicorp/serf;name=serf;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/serf"

# github.com/hashicorp/terraform-json v0.13.0
# [1] git ls-remote https://github.com/hashicorp/terraform-json d1018bf93fd9c097133b0159ab8b3c0517a846c9 
SRCREV_terraform-json="d1018bf93fd9c097133b0159ab8b3c0517a846c9"
SRC_URI += "git://github.com/hashicorp/terraform-json;name=terraform-json;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hashicorp/terraform-json"

# github.com/heketi/heketi v10.3.0+incompatible
# [1] git ls-remote https://github.com/heketi/heketi 376fae199da0265b375f249b7b6ffca14b955cad 
SRCREV_heketi="376fae199da0265b375f249b7b6ffca14b955cad"
SRC_URI += "git://github.com/heketi/heketi;name=heketi;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/heketi/heketi"

# github.com/heketi/tests v0.0.0-20151005000721-f3775cbcefd6
# [1] git ls-remote https://github.com/heketi/tests f3775cbcefd6822086c729e3ce4b70ca85a5bd21 
SRCREV_tests="f3775cbcefd6822086c729e3ce4b70ca85a5bd21"
SRC_URI += "git://github.com/heketi/tests;name=tests;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/heketi/tests"

# github.com/hpcloud/tail v1.0.0
# [1] git ls-remote https://github.com/hpcloud/tail a30252cb686a21eb2d0b98132633053ec2f7f1e5 
SRCREV_tail="a30252cb686a21eb2d0b98132633053ec2f7f1e5"
SRC_URI += "git://github.com/hpcloud/tail;name=tail;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hpcloud/tail"

# github.com/hugelgupf/socketpair v0.0.0-20190730060125-05d35a94e714
# [1] git ls-remote https://github.com/hugelgupf/socketpair 05d35a94e71444d53460c2b34dfb3dc87c99d0bd 
SRCREV_socketpair="05d35a94e71444d53460c2b34dfb3dc87c99d0bd"
SRC_URI += "git://github.com/hugelgupf/socketpair;name=socketpair;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/hugelgupf/socketpair"

# github.com/ianlancetaylor/demangle v0.0.0-20200824232613-28f6c0f3b639
# [1] git ls-remote https://github.com/ianlancetaylor/demangle 28f6c0f3b63983aaa99575ca3b693afff7996387 
SRCREV_demangle="28f6c0f3b63983aaa99575ca3b693afff7996387"
SRC_URI += "git://github.com/ianlancetaylor/demangle;name=demangle;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/ianlancetaylor/demangle"

# github.com/imdario/mergo v0.3.12
# [1] git ls-remote https://github.com/imdario/mergo 29fb3d3bdc5512887f1dc9aedde6a0fed407fa8f 
SRCREV_mergo="29fb3d3bdc5512887f1dc9aedde6a0fed407fa8f"
SRC_URI += "git://github.com/imdario/mergo;name=mergo;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/imdario/mergo"

# github.com/inconshreveable/mousetrap v1.0.0
# [1] git ls-remote https://github.com/inconshreveable/mousetrap 76626ae9c91c4f2a10f34cad8ce83ea42c93bb75 
SRCREV_mousetrap="76626ae9c91c4f2a10f34cad8ce83ea42c93bb75"
SRC_URI += "git://github.com/inconshreveable/mousetrap;name=mousetrap;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/inconshreveable/mousetrap"

# github.com/insomniacslk/dhcp v0.0.0-20220119180841-3c283ff8b7dd
# [1] git ls-remote https://github.com/insomniacslk/dhcp 3c283ff8b7dd3a8ea2dbc37d13a35bba7aab00e5 
SRCREV_dhcp="3c283ff8b7dd3a8ea2dbc37d13a35bba7aab00e5"
SRC_URI += "git://github.com/insomniacslk/dhcp;name=dhcp;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/insomniacslk/dhcp"

# github.com/ishidawataru/sctp v0.0.0-20190723014705-7c296d48a2b5
# [1] git ls-remote https://github.com/ishidawataru/sctp 7c296d48a2b553e41cc06904a1e6317a20694dc0 
SRCREV_sctp="7c296d48a2b553e41cc06904a1e6317a20694dc0"
SRC_URI += "git://github.com/ishidawataru/sctp;name=sctp;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/ishidawataru/sctp"

# github.com/j-keck/arping v1.0.2
# [1] git ls-remote https://github.com/j-keck/arping ece45f66ff64f803af58baa61fb9c1c45b5365c0 
SRCREV_arping="ece45f66ff64f803af58baa61fb9c1c45b5365c0"
SRC_URI += "git://github.com/j-keck/arping;name=arping;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/j-keck/arping"

# github.com/jessevdk/go-flags v1.5.0
# [1] git ls-remote https://github.com/jessevdk/go-flags 1878de27329cba29066dc088d84b3ce743885f82 
SRCREV_go-flags="1878de27329cba29066dc088d84b3ce743885f82"
SRC_URI += "git://github.com/jessevdk/go-flags;name=go-flags;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/jessevdk/go-flags"

# github.com/jinzhu/copier v0.0.0-20190924061706-b57f9002281a
# [1] git ls-remote https://github.com/jinzhu/copier b57f9002281ac48ed8cbc489b1e91121e1a0824c 
SRCREV_copier="b57f9002281ac48ed8cbc489b1e91121e1a0824c"
SRC_URI += "git://github.com/jinzhu/copier;name=copier;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/jinzhu/copier"

# github.com/jmespath/go-jmespath v0.4.0
# [1] git ls-remote https://github.com/jmespath/go-jmespath 3d4fd11601ddca248480565884e34e393313cd62 
SRCREV_go-jmespath="3d4fd11601ddca248480565884e34e393313cd62"
SRC_URI += "git://github.com/jmespath/go-jmespath;name=go-jmespath;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/jmespath/go-jmespath"

# github.com/jmespath/go-jmespath/internal/testify v1.5.1
# [1] git ls-remote https://github.com/jmespath/go-jmespath ff168ca20786696708c0f3eb2cc29d7912467599 
SRCREV_testify="ff168ca20786696708c0f3eb2cc29d7912467599"
SRC_URI += "git://github.com/jmespath/go-jmespath;name=testify;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/jmespath/go-jmespath/internal/testify"

# github.com/joho/godotenv v0.0.0-20161216230537-726cc8b906e3
# [1] git ls-remote https://github.com/joho/godotenv 726cc8b906e3d31c70a9671c90a13716a8d3f50d 
SRCREV_godotenv="726cc8b906e3d31c70a9671c90a13716a8d3f50d"
SRC_URI += "git://github.com/joho/godotenv;name=godotenv;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/joho/godotenv"

# github.com/jonboulle/clockwork v0.2.2
# [1] git ls-remote https://github.com/jonboulle/clockwork 75ce4728e887d0503c30133699687076bb6beb43 
SRCREV_clockwork="75ce4728e887d0503c30133699687076bb6beb43"
SRC_URI += "git://github.com/jonboulle/clockwork;name=clockwork;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/jonboulle/clockwork"

# github.com/josharian/intern v1.0.0
# [1] git ls-remote https://github.com/josharian/intern 8e6ff32b3e7c0b018c43953085fe2ac330fe9acd 
SRCREV_intern="8e6ff32b3e7c0b018c43953085fe2ac330fe9acd"
SRC_URI += "git://github.com/josharian/intern;name=intern;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/josharian/intern"

# github.com/josharian/native v0.0.0-20200817173448-b6b71def0850
# [1] git ls-remote https://github.com/josharian/native b6b71def0850a2fbd7e6875f8e28217a48c5bcb4 
SRCREV_native="b6b71def0850a2fbd7e6875f8e28217a48c5bcb4"
SRC_URI += "git://github.com/josharian/native;name=native;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/josharian/native"

# github.com/jpillora/backoff v1.0.0
# [1] git ls-remote https://github.com/jpillora/backoff d80867952dff4e2fbfb4280ded4ff94d67790457 
SRCREV_backoff="d80867952dff4e2fbfb4280ded4ff94d67790457"
SRC_URI += "git://github.com/jpillora/backoff;name=backoff;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/jpillora/backoff"

# github.com/jsimonetti/rtnetlink v0.0.0-20211022192332-93da33804786
# [1] git ls-remote https://github.com/jsimonetti/rtnetlink 93da33804786e0d138aea412b38d80dd0099b941 
SRCREV_rtnetlink="93da33804786e0d138aea412b38d80dd0099b941"
SRC_URI += "git://github.com/jsimonetti/rtnetlink;name=rtnetlink;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/jsimonetti/rtnetlink"

# github.com/json-iterator/go v1.1.12
# [1] git ls-remote https://github.com/json-iterator/go 024077e996b048517130b21ea6bf12aa23055d3d 
SRCREV_go123="024077e996b048517130b21ea6bf12aa23055d3d"
SRC_URI += "git://github.com/json-iterator/go;name=go123;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/json-iterator/go"

# github.com/jstemmer/go-junit-report v0.9.1
# [1] git ls-remote https://github.com/jstemmer/go-junit-report cc1f095d5cc5eca2844f5c5ea7bb37f6b9bf6cac 
SRCREV_go-junit-report="cc1f095d5cc5eca2844f5c5ea7bb37f6b9bf6cac"
SRC_URI += "git://github.com/jstemmer/go-junit-report;name=go-junit-report;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/jstemmer/go-junit-report"

# github.com/jtolds/gls v4.20.0+incompatible
# [1] git ls-remote https://github.com/jtolds/gls b4936e06046bbecbb94cae9c18127ebe510a2cb9 
SRCREV_gls="b4936e06046bbecbb94cae9c18127ebe510a2cb9"
SRC_URI += "git://github.com/jtolds/gls;name=gls;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/jtolds/gls"

# github.com/julienschmidt/httprouter v1.3.0
# [1] git ls-remote https://github.com/julienschmidt/httprouter 4eec211fa4e8df74ed978dc5681612131854144f 
SRCREV_httprouter="4eec211fa4e8df74ed978dc5681612131854144f"
SRC_URI += "git://github.com/julienschmidt/httprouter;name=httprouter;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/julienschmidt/httprouter"

# github.com/jung-kurt/gofpdf v1.0.3-0.20190309125859-24315acbbda5
# [1] git ls-remote https://github.com/jung-kurt/gofpdf 24315acbbda57c4f6b80c8441fd108087dd7e305 
SRCREV_gofpdf="24315acbbda57c4f6b80c8441fd108087dd7e305"
SRC_URI += "git://github.com/jung-kurt/gofpdf;name=gofpdf;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/jung-kurt/gofpdf"

# github.com/k-sone/critbitgo v1.4.0
# [1] git ls-remote https://github.com/k-sone/critbitgo 4536a49cdb164af1fc76e276ebeaf004338d2044 
SRCREV_critbitgo="4536a49cdb164af1fc76e276ebeaf004338d2044"
SRC_URI += "git://github.com/k-sone/critbitgo;name=critbitgo;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/k-sone/critbitgo"

# github.com/k3s-io/helm-controller v0.12.3
# [1] git ls-remote https://github.com/k3s-io/helm-controller 8271cabfa84099e7edc1032482dec807fa16a73c 
SRCREV_helm-controller="8271cabfa84099e7edc1032482dec807fa16a73c"
SRC_URI += "git://github.com/k3s-io/helm-controller;name=helm-controller;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/k3s-io/helm-controller"

# github.com/k3s-io/kine v0.9.3
# [1] git ls-remote https://github.com/k3s-io/kine 1c94e77d00cae3fb6a095daf83c0f59e4a8f8941 
SRCREV_kine="1c94e77d00cae3fb6a095daf83c0f59e4a8f8941"
SRC_URI += "git://github.com/k3s-io/kine;name=kine;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/k3s-io/kine"

# github.com/karrick/godirwalk v1.16.1
# [1] git ls-remote https://github.com/karrick/godirwalk f64d91544257ab0cc1cca44f22600925a1b9ddae 
SRCREV_godirwalk="f64d91544257ab0cc1cca44f22600925a1b9ddae"
SRC_URI += "git://github.com/karrick/godirwalk;name=godirwalk;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/karrick/godirwalk"

# github.com/kisielk/errcheck v1.5.0
# [1] git ls-remote https://github.com/kisielk/errcheck ee08a456fc430219ad80ce5af98415bcc027a219 
SRCREV_errcheck="ee08a456fc430219ad80ce5af98415bcc027a219"
SRC_URI += "git://github.com/kisielk/errcheck;name=errcheck;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/kisielk/errcheck"

# github.com/kisielk/gotool v1.0.0
# [1] git ls-remote https://github.com/kisielk/gotool 80517062f582ea3340cd4baf70e86d539ae7d84d 
SRCREV_gotool="80517062f582ea3340cd4baf70e86d539ae7d84d"
SRC_URI += "git://github.com/kisielk/gotool;name=gotool;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/kisielk/gotool"

# github.com/klauspost/compress v1.15.1
# [1] git ls-remote https://github.com/klauspost/compress 7c7079ec1ed2c07fd15e6f3f369dbae5288051d5 
SRCREV_compress="7c7079ec1ed2c07fd15e6f3f369dbae5288051d5"
SRC_URI += "git://github.com/klauspost/compress;name=compress;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/klauspost/compress"

# github.com/klauspost/cpuid v1.3.1
# [1] git ls-remote https://github.com/klauspost/cpuid 750c0591dbbd50ef88371c665ad49e426a4b830b 
SRCREV_cpuid="750c0591dbbd50ef88371c665ad49e426a4b830b"
SRC_URI += "git://github.com/klauspost/cpuid;name=cpuid;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/klauspost/cpuid"

# github.com/konsorten/go-windows-terminal-sequences v1.0.3
# [1] git ls-remote https://github.com/konsorten/go-windows-terminal-sequences edb144dfd453055e1e49a3d8b410a660b5a87613 
SRCREV_go-windows-terminal-sequences="edb144dfd453055e1e49a3d8b410a660b5a87613"
SRC_URI += "git://github.com/konsorten/go-windows-terminal-sequences;name=go-windows-terminal-sequences;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/konsorten/go-windows-terminal-sequences"

# github.com/kr/fs v0.1.0
# [1] git ls-remote https://github.com/kr/fs 1455def202f6e05b95cc7bfc7e8ae67ae5141eba 
SRCREV_fs="1455def202f6e05b95cc7bfc7e8ae67ae5141eba"
SRC_URI += "git://github.com/kr/fs;name=fs;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/kr/fs"

# github.com/kr/logfmt v0.0.0-20140226030751-b84e30acd515
# [1] git ls-remote https://github.com/kr/logfmt b84e30acd515aadc4b783ad4ff83aff3299bdfe0 
SRCREV_logfmt1="b84e30acd515aadc4b783ad4ff83aff3299bdfe0"
SRC_URI += "git://github.com/kr/logfmt;name=logfmt1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/kr/logfmt"

# github.com/kr/pretty v0.3.0
# [1] git ls-remote https://github.com/kr/pretty a883a8422cd235c67c6c4fdcb7bbb022143e10b1 
SRCREV_pretty="a883a8422cd235c67c6c4fdcb7bbb022143e10b1"
SRC_URI += "git://github.com/kr/pretty;name=pretty;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/kr/pretty"

# github.com/kr/pty v1.1.5
# [1] git ls-remote https://github.com/kr/pty b6e1bdd4a4f88614e0c6e5e8089c7abed98aae17 
SRCREV_pty1="b6e1bdd4a4f88614e0c6e5e8089c7abed98aae17"
SRC_URI += "git://github.com/kr/pty;name=pty1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/kr/pty"

# github.com/kr/text v0.2.0
# [1] git ls-remote https://github.com/kr/text 702c74938df48b97370179f33ce2107bd7ff3b3e 
SRCREV_text="702c74938df48b97370179f33ce2107bd7ff3b3e"
SRC_URI += "git://github.com/kr/text;name=text;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/kr/text"

# github.com/k3s-io/cri-tools v1.24.0-k3s1
# [1] git ls-remote https://github.com/k3s-io/cri-tools e3bb4af4f33db194d5be5c4df7090d26463f5f15 
SRCREV_cri-tools="e3bb4af4f33db194d5be5c4df7090d26463f5f15"
SRC_URI += "git://github.com/k3s-io/cri-tools;name=cri-tools;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/kubernetes-sigs/cri-tools"

# github.com/kylelemons/godebug v1.1.0
# [1] git ls-remote https://github.com/kylelemons/godebug 9ff306d4fbead574800b66369df5b6144732d58e 
SRCREV_godebug="9ff306d4fbead574800b66369df5b6144732d58e"
SRC_URI += "git://github.com/kylelemons/godebug;name=godebug;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/kylelemons/godebug"

# github.com/lib/pq v1.10.2
# [1] git ls-remote https://github.com/lib/pq 2da6713d67f03911a05b1b6559adc85927fe076e 
SRCREV_pq="2da6713d67f03911a05b1b6559adc85927fe076e"
SRC_URI += "git://github.com/lib/pq;name=pq;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/lib/pq"

# github.com/libopenstorage/openstorage v1.0.0
# [1] git ls-remote https://github.com/libopenstorage/openstorage 093a0c3888753c2056e7373183693d670c6bba01 
SRCREV_openstorage="093a0c3888753c2056e7373183693d670c6bba01"
SRC_URI += "git://github.com/libopenstorage/openstorage;name=openstorage;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/libopenstorage/openstorage"

# github.com/liggitt/tabwriter v0.0.0-20181228230101-89fcab3d43de
# [1] git ls-remote https://github.com/liggitt/tabwriter 89fcab3d43de07060e4fd4c1547430ed57e87f24 
SRCREV_tabwriter="89fcab3d43de07060e4fd4c1547430ed57e87f24"
SRC_URI += "git://github.com/liggitt/tabwriter;name=tabwriter;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/liggitt/tabwriter"

# github.com/lithammer/dedent v1.1.0
# [1] git ls-remote https://github.com/lithammer/dedent 8478954c3bc893cf36c5ee7c822266b993a3b3ee 
SRCREV_dedent="8478954c3bc893cf36c5ee7c822266b993a3b3ee"
SRC_URI += "git://github.com/lithammer/dedent;name=dedent;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/lithammer/dedent"

# github.com/lpabon/godbc v0.1.1
# [1] git ls-remote https://github.com/lpabon/godbc d6a6d74016a4f32dd9a9bd32c1ac6c28ae68fa6a 
SRCREV_godbc="d6a6d74016a4f32dd9a9bd32c1ac6c28ae68fa6a"
SRC_URI += "git://github.com/lpabon/godbc;name=godbc;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/lpabon/godbc"

# github.com/magiconair/properties v1.8.5
# [1] git ls-remote https://github.com/magiconair/properties 291b8266af1000c77a12ae6d4dde1145a028012f 
SRCREV_properties="291b8266af1000c77a12ae6d4dde1145a028012f"
SRC_URI += "git://github.com/magiconair/properties;name=properties;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/magiconair/properties"

# github.com/mailru/easyjson v0.7.6
# [1] git ls-remote https://github.com/mailru/easyjson 8ab5ff9cd8e4e432e8b79f6c47d324a31dd803cf 
SRCREV_easyjson="8ab5ff9cd8e4e432e8b79f6c47d324a31dd803cf"
SRC_URI += "git://github.com/mailru/easyjson;name=easyjson;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mailru/easyjson"

# github.com/mattn/go-colorable v0.1.4
# [1] git ls-remote https://github.com/mattn/go-colorable 98ec13f34aabf44cc914c65a1cfb7b9bc815aef1 
SRCREV_go-colorable="98ec13f34aabf44cc914c65a1cfb7b9bc815aef1"
SRC_URI += "git://github.com/mattn/go-colorable;name=go-colorable;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mattn/go-colorable"

# github.com/mattn/go-isatty v0.0.11
# [1] git ls-remote https://github.com/mattn/go-isatty 31745d66dd679ac0ac4f8d3ecff168fce6170c6a 
SRCREV_go-isatty="31745d66dd679ac0ac4f8d3ecff168fce6170c6a"
SRC_URI += "git://github.com/mattn/go-isatty;name=go-isatty;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mattn/go-isatty"

# github.com/mattn/go-runewidth v0.0.9
# [1] git ls-remote https://github.com/mattn/go-runewidth 14e809f6d78fcf9f48ff9b70981472b64c05f754 
SRCREV_go-runewidth="14e809f6d78fcf9f48ff9b70981472b64c05f754"
SRC_URI += "git://github.com/mattn/go-runewidth;name=go-runewidth;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mattn/go-runewidth"

# github.com/mattn/go-shellwords v1.0.12
# [1] git ls-remote https://github.com/mattn/go-shellwords 973b9d5391598d4ee601db46fa32f6e186a356ac 
SRCREV_go-shellwords="973b9d5391598d4ee601db46fa32f6e186a356ac"
SRC_URI += "git://github.com/mattn/go-shellwords;name=go-shellwords;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mattn/go-shellwords"

# github.com/mattn/go-sqlite3 v1.14.8
# [1] git ls-remote https://github.com/mattn/go-sqlite3 cba6eaee486f22a1b0f3208b145673ed951cb840 
SRCREV_go-sqlite3="cba6eaee486f22a1b0f3208b145673ed951cb840"
SRC_URI += "git://github.com/mattn/go-sqlite3;name=go-sqlite3;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mattn/go-sqlite3"

# github.com/mattn/go-zglob v0.0.2-0.20190814121620-e3c945676326
# [1] git ls-remote https://github.com/mattn/go-zglob e3c9456763260506039bcfab455034abeee06244 
SRCREV_go-zglob="e3c9456763260506039bcfab455034abeee06244"
SRC_URI += "git://github.com/mattn/go-zglob;name=go-zglob;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mattn/go-zglob"

# github.com/matttproud/golang_protobuf_extensions v1.0.2-0.20181231171920-c182affec369
# [1] git ls-remote https://github.com/matttproud/golang_protobuf_extensions c182affec369e30f25d3eb8cd8a478dee585ae7d 
SRCREV_golang_protobuf_extensions="c182affec369e30f25d3eb8cd8a478dee585ae7d"
SRC_URI += "git://github.com/matttproud/golang_protobuf_extensions;name=golang_protobuf_extensions;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/matttproud/golang_protobuf_extensions"

# github.com/mdlayher/ethernet v0.0.0-20190606142754-0394541c37b7
# [1] git ls-remote https://github.com/mdlayher/ethernet 0394541c37b7f86a10e0b49492f6d4f605c34163 
SRCREV_ethernet="0394541c37b7f86a10e0b49492f6d4f605c34163"
SRC_URI += "git://github.com/mdlayher/ethernet;name=ethernet;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mdlayher/ethernet"

# github.com/mdlayher/ethtool v0.0.0-20211028163843-288d040e9d60
# [1] git ls-remote https://github.com/mdlayher/ethtool 288d040e9d60a1b55b53566a1624be427cfeef97 
SRCREV_ethtool="288d040e9d60a1b55b53566a1624be427cfeef97"
SRC_URI += "git://github.com/mdlayher/ethtool;name=ethtool;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mdlayher/ethtool"

# github.com/mdlayher/genetlink v1.1.0
# [1] git ls-remote https://github.com/mdlayher/genetlink 030878314e387340f2b49b161519600024f94bb9 
SRCREV_genetlink="030878314e387340f2b49b161519600024f94bb9"
SRC_URI += "git://github.com/mdlayher/genetlink;name=genetlink;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mdlayher/genetlink"

# github.com/mdlayher/netlink v1.4.2
# [1] git ls-remote https://github.com/mdlayher/netlink 6ce358ad548a9405a9580b723bfe2ba187b92cd5 
SRCREV_netlink="6ce358ad548a9405a9580b723bfe2ba187b92cd5"
SRC_URI += "git://github.com/mdlayher/netlink;name=netlink;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mdlayher/netlink"

# github.com/mdlayher/raw v0.0.0-20191009151244-50f2db8cc065
# [1] git ls-remote https://github.com/mdlayher/raw 50f2db8cc0658568575938a39dbaa46172921d98 
SRCREV_raw="50f2db8cc0658568575938a39dbaa46172921d98"
SRC_URI += "git://github.com/mdlayher/raw;name=raw;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mdlayher/raw"

# github.com/mdlayher/socket v0.0.0-20211102153432-57e3fa563ecb
# [1] git ls-remote https://github.com/mdlayher/socket 57e3fa563ecb8fbbbf28b56c674ce382ae48f7db 
SRCREV_socket="57e3fa563ecb8fbbbf28b56c674ce382ae48f7db"
SRC_URI += "git://github.com/mdlayher/socket;name=socket;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mdlayher/socket"

# github.com/miekg/dns v1.1.31
# [1] git ls-remote https://github.com/miekg/dns 34cecfe1b489442934480cec58277b4b39b1a1cd 
SRCREV_dns="34cecfe1b489442934480cec58277b4b39b1a1cd"
SRC_URI += "git://github.com/miekg/dns;name=dns;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/miekg/dns"

# github.com/miekg/pkcs11 v1.0.3
# [1] git ls-remote https://github.com/miekg/pkcs11 210dc1e16747c5ba98a03bcbcf728c38086ea357 
SRCREV_pkcs11="210dc1e16747c5ba98a03bcbcf728c38086ea357"
SRC_URI += "git://github.com/miekg/pkcs11;name=pkcs11;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/miekg/pkcs11"

# github.com/mikioh/ipaddr v0.0.0-20190404000644-d465c8ab6721
# [1] git ls-remote https://github.com/mikioh/ipaddr d465c8ab672111787b24b8f03326449059a4aa33 
SRCREV_ipaddr="d465c8ab672111787b24b8f03326449059a4aa33"
SRC_URI += "git://github.com/mikioh/ipaddr;name=ipaddr;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mikioh/ipaddr"

# github.com/mindprince/gonvml v0.0.0-20190828220739-9ebdce4bb989
# [1] git ls-remote https://github.com/mindprince/gonvml 9ebdce4bb98934573700a3bfc2f8e791934a578d 
SRCREV_gonvml="9ebdce4bb98934573700a3bfc2f8e791934a578d"
SRC_URI += "git://github.com/mindprince/gonvml;name=gonvml;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mindprince/gonvml"

# github.com/minio/highwayhash v1.0.2
# [1] git ls-remote https://github.com/minio/highwayhash 08ce0b4fa7932a018438133f1b632e1c674d4107 
SRCREV_highwayhash="08ce0b4fa7932a018438133f1b632e1c674d4107"
SRC_URI += "git://github.com/minio/highwayhash;name=highwayhash;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/minio/highwayhash"

# github.com/minio/md5-simd v1.1.0
# [1] git ls-remote https://github.com/minio/md5-simd d302c3dbe3e6573d7010992e3927355eb0e1ab91 
SRCREV_md5-simd="d302c3dbe3e6573d7010992e3927355eb0e1ab91"
SRC_URI += "git://github.com/minio/md5-simd;name=md5-simd;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/minio/md5-simd"

# github.com/minio/minio-go/v7 v7.0.7
# [1] git ls-remote https://github.com/minio/minio-go 40d41266f363962b9243529b3a042bb453c1f4c6 
SRCREV_v7="40d41266f363962b9243529b3a042bb453c1f4c6"
SRC_URI += "git://github.com/minio/minio-go;name=v7;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/minio/minio-go/v7"

# github.com/minio/sha256-simd v0.1.1
# [1] git ls-remote https://github.com/minio/sha256-simd 6de4475307716de15b286880ff321c9547086fdd 
SRCREV_sha256-simd="6de4475307716de15b286880ff321c9547086fdd"
SRC_URI += "git://github.com/minio/sha256-simd;name=sha256-simd;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/minio/sha256-simd"

# github.com/minio/sio v0.2.1
# [1] git ls-remote https://github.com/minio/sio ae0213457e8a919f927bb03909b2135b6ca02ef1 
SRCREV_sio="ae0213457e8a919f927bb03909b2135b6ca02ef1"
SRC_URI += "git://github.com/minio/sio;name=sio;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/minio/sio"

# github.com/mistifyio/go-zfs v2.1.2-0.20190413222219-f784269be439+incompatible
# [1] git ls-remote https://github.com/mistifyio/go-zfs f784269be439d704d3dfa1906f45dd848fed2beb 
SRCREV_go-zfs="f784269be439d704d3dfa1906f45dd848fed2beb"
SRC_URI += "git://github.com/mistifyio/go-zfs;name=go-zfs;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mistifyio/go-zfs"

# github.com/mitchellh/cli v1.0.0
# [1] git ls-remote https://github.com/mitchellh/cli 3d22a244be8aa6fb16ac24af0e195c08b7d973aa 
SRCREV_cli12="3d22a244be8aa6fb16ac24af0e195c08b7d973aa"
SRC_URI += "git://github.com/mitchellh/cli;name=cli12;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mitchellh/cli"

# github.com/mitchellh/copystructure v1.2.0
# [1] git ls-remote https://github.com/mitchellh/copystructure d4ce1f938f7a7ea2a40bff4544b56be9c00b5e84 
SRCREV_copystructure="d4ce1f938f7a7ea2a40bff4544b56be9c00b5e84"
SRC_URI += "git://github.com/mitchellh/copystructure;name=copystructure;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mitchellh/copystructure"

# github.com/mitchellh/go-homedir v1.1.0
# [1] git ls-remote https://github.com/mitchellh/go-homedir af06845cf3004701891bf4fdb884bfe4920b3727 
SRCREV_go-homedir="af06845cf3004701891bf4fdb884bfe4920b3727"
SRC_URI += "git://github.com/mitchellh/go-homedir;name=go-homedir;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mitchellh/go-homedir"

# github.com/mitchellh/go-testing-interface v1.0.0
# [1] git ls-remote https://github.com/mitchellh/go-testing-interface 6d0b8010fcc857872e42fc6c931227569016843c 
SRCREV_go-testing-interface="6d0b8010fcc857872e42fc6c931227569016843c"
SRC_URI += "git://github.com/mitchellh/go-testing-interface;name=go-testing-interface;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mitchellh/go-testing-interface"

# github.com/mitchellh/go-wordwrap v1.0.1
# [1] git ls-remote https://github.com/mitchellh/go-wordwrap ecf0936a077a4bd73a1cc2ac5c370f2b55618d62 
SRCREV_go-wordwrap="ecf0936a077a4bd73a1cc2ac5c370f2b55618d62"
SRC_URI += "git://github.com/mitchellh/go-wordwrap;name=go-wordwrap;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mitchellh/go-wordwrap"

# github.com/mitchellh/gox v0.4.0
# [1] git ls-remote https://github.com/mitchellh/gox c9740af9c6574448fd48eb30a71f964014c7a837 
SRCREV_gox="c9740af9c6574448fd48eb30a71f964014c7a837"
SRC_URI += "git://github.com/mitchellh/gox;name=gox;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mitchellh/gox"

# github.com/mitchellh/iochan v1.0.0
# [1] git ls-remote https://github.com/mitchellh/iochan b6e3aadd28864e14dae41ff5c7261455b1242d24 
SRCREV_iochan="b6e3aadd28864e14dae41ff5c7261455b1242d24"
SRC_URI += "git://github.com/mitchellh/iochan;name=iochan;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mitchellh/iochan"

# github.com/mitchellh/mapstructure v1.4.1
# [1] git ls-remote https://github.com/mitchellh/mapstructure 8ebf2d61a8b4adcce25fc9fc9b76e8452a00672f 
SRCREV_mapstructure="8ebf2d61a8b4adcce25fc9fc9b76e8452a00672f"
SRC_URI += "git://github.com/mitchellh/mapstructure;name=mapstructure;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mitchellh/mapstructure"

# github.com/mitchellh/reflectwalk v1.0.2
# [1] git ls-remote https://github.com/mitchellh/reflectwalk e0c24fdb021963cd2c4013097a0b993a7c4e344f 
SRCREV_reflectwalk="e0c24fdb021963cd2c4013097a0b993a7c4e344f"
SRC_URI += "git://github.com/mitchellh/reflectwalk;name=reflectwalk;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mitchellh/reflectwalk"

# github.com/moby/ipvs v1.0.1
# [1] git ls-remote https://github.com/moby/ipvs 4566ccea0e08d68e9614c3e7a64a23b850c4bb35 
SRCREV_ipvs="4566ccea0e08d68e9614c3e7a64a23b850c4bb35"
SRC_URI += "git://github.com/moby/ipvs;name=ipvs;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/moby/ipvs"

# github.com/moby/locker v1.0.1
# [1] git ls-remote https://github.com/moby/locker 281af2d563954745bea9d1487c965f24d30742fe 
SRCREV_locker="281af2d563954745bea9d1487c965f24d30742fe"
SRC_URI += "git://github.com/moby/locker;name=locker;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/moby/locker"

# github.com/moby/spdystream v0.2.0
# [1] git ls-remote https://github.com/moby/spdystream dbc715126c0e3fa07721879c6d265b2b82c71e5b 
SRCREV_spdystream1="dbc715126c0e3fa07721879c6d265b2b82c71e5b"
SRC_URI += "git://github.com/moby/spdystream;name=spdystream1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/moby/spdystream"

# github.com/moby/sys/mountinfo v0.6.1
# [1] git ls-remote https://github.com/moby/sys 523ebb9c6cc361d558aaf903946709d78efa0f6a 
SRCREV_mountinfo="523ebb9c6cc361d558aaf903946709d78efa0f6a"
SRC_URI += "git://github.com/moby/sys;name=mountinfo;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/moby/sys/mountinfo"

# github.com/moby/sys/symlink v0.2.0
# [1] git ls-remote https://github.com/moby/sys bbb236c90521a7724219600603cd4b9415807a5a 
SRCREV_symlink="03b9f8d59a07f5206a2264105f4903a222aea964"
SRC_URI += "git://github.com/moby/sys;name=symlink;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/moby/sys/symlink"

# github.com/moby/term v0.0.0-20210619224110-3f7ff695adc6
# [1] git ls-remote https://github.com/moby/term 3f7ff695adc6a35abc925370dd0a4dafb48ec64d 
SRCREV_term="3f7ff695adc6a35abc925370dd0a4dafb48ec64d"
SRC_URI += "git://github.com/moby/term;name=term;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/moby/term"

# github.com/moby/vpnkit v0.5.0
# [1] git ls-remote https://github.com/moby/vpnkit 7f0eff0dd99b576c5474de53b4454a157c642834 
SRCREV_vpnkit="7f0eff0dd99b576c5474de53b4454a157c642834"
SRC_URI += "git://github.com/moby/vpnkit;name=vpnkit;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/moby/vpnkit"

# github.com/modern-go/concurrent v0.0.0-20180306012644-bacd9c7ef1dd
# [1] git ls-remote https://github.com/modern-go/concurrent bacd9c7ef1dd9b15be4a9909b8ac7a4e313eec94 
SRCREV_concurrent="bacd9c7ef1dd9b15be4a9909b8ac7a4e313eec94"
SRC_URI += "git://github.com/modern-go/concurrent;name=concurrent;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/modern-go/concurrent"

# github.com/modern-go/reflect2 v1.0.2
# [1] git ls-remote https://github.com/modern-go/reflect2 2b33151c9bbc5231aea69b8861c540102b087070 
SRCREV_reflect2="2b33151c9bbc5231aea69b8861c540102b087070"
SRC_URI += "git://github.com/modern-go/reflect2;name=reflect2;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/modern-go/reflect2"

# github.com/mohae/deepcopy v0.0.0-20170929034955-c48cc78d4826
# [1] git ls-remote https://github.com/mohae/deepcopy c48cc78d482608239f6c4c92a4abd87eb8761c90 
SRCREV_deepcopy="c48cc78d482608239f6c4c92a4abd87eb8761c90"
SRC_URI += "git://github.com/mohae/deepcopy;name=deepcopy;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mohae/deepcopy"

# github.com/monochromegane/go-gitignore v0.0.0-20200626010858-205db1a8cc00
# [1] git ls-remote https://github.com/monochromegane/go-gitignore 205db1a8cc001de79230472da52edde4974df734 
SRCREV_go-gitignore="205db1a8cc001de79230472da52edde4974df734"
SRC_URI += "git://github.com/monochromegane/go-gitignore;name=go-gitignore;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/monochromegane/go-gitignore"

# github.com/morikuni/aec v1.0.0
# [1] git ls-remote https://github.com/morikuni/aec 39771216ff4c63d11f5e604076f9c45e8be1067b 
SRCREV_aec="39771216ff4c63d11f5e604076f9c45e8be1067b"
SRC_URI += "git://github.com/morikuni/aec;name=aec;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/morikuni/aec"

# github.com/mrunalp/fileutils v0.5.0
# [1] git ls-remote https://github.com/mrunalp/fileutils d7fdd64cc1cabe10bc154ee7d2318b07b7f296ef 
SRCREV_fileutils="d7fdd64cc1cabe10bc154ee7d2318b07b7f296ef"
SRC_URI += "git://github.com/mrunalp/fileutils;name=fileutils;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mrunalp/fileutils"

# github.com/munnerz/goautoneg v0.0.0-20191010083416-a7dc8b61c822
# [1] git ls-remote https://github.com/munnerz/goautoneg a7dc8b61c822528f973a5e4e7b272055c6fdb43e 
SRCREV_goautoneg="a7dc8b61c822528f973a5e4e7b272055c6fdb43e"
SRC_URI += "git://github.com/munnerz/goautoneg;name=goautoneg;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/munnerz/goautoneg"

# github.com/mvdan/xurls v1.1.0
# [1] git ls-remote https://github.com/mvdan/xurls d315b61cf6727664f310fa87b3197e9faf2a8513 
SRCREV_xurls="d315b61cf6727664f310fa87b3197e9faf2a8513"
SRC_URI += "git://github.com/mvdan/xurls;name=xurls;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mvdan/xurls"

# github.com/mwitkow/go-conntrack v0.0.0-20190716064945-2f068394615f
# [1] git ls-remote https://github.com/mwitkow/go-conntrack 2f068394615f73e460c2f3d2c158b0ad9321cadb 
SRCREV_go-conntrack="2f068394615f73e460c2f3d2c158b0ad9321cadb"
SRC_URI += "git://github.com/mwitkow/go-conntrack;name=go-conntrack;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mwitkow/go-conntrack"

# github.com/mxk/go-flowrate v0.0.0-20140419014527-cca7078d478f
# [1] git ls-remote https://github.com/mxk/go-flowrate cca7078d478f8520f85629ad7c68962d31ed7682 
SRCREV_go-flowrate="cca7078d478f8520f85629ad7c68962d31ed7682"
SRC_URI += "git://github.com/mxk/go-flowrate;name=go-flowrate;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/mxk/go-flowrate"

# github.com/natefinch/lumberjack v2.0.0+incompatible
# [1] git ls-remote https://github.com/natefinch/lumberjack 7d6a1875575e09256dc552b4c0e450dcd02bd10e 
SRCREV_lumberjack="7d6a1875575e09256dc552b4c0e450dcd02bd10e"
SRC_URI += "git://github.com/natefinch/lumberjack;name=lumberjack;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/natefinch/lumberjack"

# github.com/nats-io/jsm.go v0.0.31-0.20220317133147-fe318f464eee
# [1] git ls-remote https://github.com/nats-io/jsm.go fe318f464eee9ceef6120ea851e5cf94a0497ac6 
SRCREV_jsm.go="fe318f464eee9ceef6120ea851e5cf94a0497ac6"
SRC_URI += "git://github.com/nats-io/jsm.go;name=jsm.go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/nats-io/jsm.go"

# github.com/nats-io/jwt/v2 v2.2.1-0.20220113022732-58e87895b296
# [1] git ls-remote https://github.com/nats-io/jwt 58e87895b29641314818a5171038b73cff3bd25a 
SRCREV_v212345="58e87895b29641314818a5171038b73cff3bd25a"
SRC_URI += "git://github.com/nats-io/jwt;name=v212345;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/nats-io/jwt/v2"

# github.com/nats-io/nats-server/v2 v2.7.5-0.20220309212130-5c0d1999ff72
# [1] git ls-remote https://github.com/nats-io/nats-server 5c0d1999ff721e8fc130399c9779c6169118277b 
SRCREV_v2123456="5c0d1999ff721e8fc130399c9779c6169118277b"
SRC_URI += "git://github.com/nats-io/nats-server;name=v2123456;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/nats-io/nats-server/v2"

# github.com/nats-io/nats.go v1.13.1-0.20220318132711-e0e03e374228
# [1] git ls-remote https://github.com/nats-io/nats.go e0e03e37422898b7ef8ce59cce34a192f304d63c 
SRCREV_nats.go="e0e03e37422898b7ef8ce59cce34a192f304d63c"
SRC_URI += "git://github.com/nats-io/nats.go;name=nats.go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/nats-io/nats.go"

# github.com/nats-io/nkeys v0.3.0
# [1] git ls-remote https://github.com/nats-io/nkeys 44384414044304395985410ff55ed78cbf0b09a8 
SRCREV_nkeys="44384414044304395985410ff55ed78cbf0b09a8"
SRC_URI += "git://github.com/nats-io/nkeys;name=nkeys;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/nats-io/nkeys"

# github.com/nats-io/nuid v1.0.1
# [1] git ls-remote https://github.com/nats-io/nuid 4b96681fa6d28dd0ab5fe79bac63b3a493d9ee94 
SRCREV_nuid="4b96681fa6d28dd0ab5fe79bac63b3a493d9ee94"
SRC_URI += "git://github.com/nats-io/nuid;name=nuid;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/nats-io/nuid"

# github.com/networkplumbing/go-nft v0.2.0
# [1] git ls-remote https://github.com/networkplumbing/go-nft ac7b6d063e1d39cb4e1c2c24a3b3ce019a89d24d 
SRCREV_go-nft="ac7b6d063e1d39cb4e1c2c24a3b3ce019a89d24d"
SRC_URI += "git://github.com/networkplumbing/go-nft;name=go-nft;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/networkplumbing/go-nft"

# github.com/niemeyer/pretty v0.0.0-20200227124842-a10e7caefd8e
# [1] git ls-remote https://github.com/niemeyer/pretty a10e7caefd8e0d600cea437f5c3613aeb1553d56 
SRCREV_pretty1="a10e7caefd8e0d600cea437f5c3613aeb1553d56"
SRC_URI += "git://github.com/niemeyer/pretty;name=pretty1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/niemeyer/pretty"

# github.com/nxadm/tail v1.4.8
# [1] git ls-remote https://github.com/nxadm/tail abad231d8d07ef91e09cd4c4c457cac35ed3bbb9 
SRCREV_tail1="abad231d8d07ef91e09cd4c4c457cac35ed3bbb9"
SRC_URI += "git://github.com/nxadm/tail;name=tail1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/nxadm/tail"

# github.com/oklog/ulid v1.3.1
# [1] git ls-remote https://github.com/oklog/ulid 02a8604050d8466dd915307496174adb9be4593a 
SRCREV_ulid="02a8604050d8466dd915307496174adb9be4593a"
SRC_URI += "git://github.com/oklog/ulid;name=ulid;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/oklog/ulid"

# github.com/olekukonko/tablewriter v0.0.5
# [1] git ls-remote https://github.com/olekukonko/tablewriter c7d2a8a09b076b70918308a3cd95464b2ae3b5d8 
SRCREV_tablewriter="c7d2a8a09b076b70918308a3cd95464b2ae3b5d8"
SRC_URI += "git://github.com/olekukonko/tablewriter;name=tablewriter;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/olekukonko/tablewriter"

# github.com/onsi/ginkgo v1.16.5
# [1] git ls-remote https://github.com/onsi/ginkgo d38b9d946d52cd175495d30143fbecc5aff98f13 
SRCREV_ginkgo="d38b9d946d52cd175495d30143fbecc5aff98f13"
SRC_URI += "git://github.com/onsi/ginkgo;name=ginkgo;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/onsi/ginkgo"

# github.com/onsi/ginkgo/v2 v2.1.3
# [1] git ls-remote https://github.com/onsi/ginkgo 1ed6486197ba2a007858c59bdc4d1522631025d5 
SRCREV_v21234567="1ed6486197ba2a007858c59bdc4d1522631025d5"
SRC_URI += "git://github.com/onsi/ginkgo;name=v21234567;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/onsi/ginkgo/v2"

# github.com/onsi/gomega v1.19.0
# [1] git ls-remote https://github.com/onsi/gomega 3143ff4a92ce52253b4b256cc4c1e8f026c260d5 
SRCREV_gomega="3143ff4a92ce52253b4b256cc4c1e8f026c260d5"
SRC_URI += "git://github.com/onsi/gomega;name=gomega;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/onsi/gomega"

# github.com/opencontainers/go-digest v1.0.0
# [1] git ls-remote https://github.com/opencontainers/go-digest ea51bea511f75cfa3ef6098cc253c5c3609b037a 
SRCREV_go-digest="ea51bea511f75cfa3ef6098cc253c5c3609b037a"
SRC_URI += "git://github.com/opencontainers/go-digest;name=go-digest;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/opencontainers/go-digest"

# github.com/opencontainers/image-spec v1.0.2
# [1] git ls-remote https://github.com/opencontainers/image-spec 67d2d5658fe0476ab9bf414cec164077ebff3920 
SRCREV_image-spec="67d2d5658fe0476ab9bf414cec164077ebff3920"
SRC_URI += "git://github.com/opencontainers/image-spec;name=image-spec;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/opencontainers/image-spec"

# github.com/opencontainers/runc v1.1.3
# [1] git ls-remote https://github.com/opencontainers/runc 6724737f999df9ee0d8ca5c6d7b81f97adc34374 
SRCREV_runc="6724737f999df9ee0d8ca5c6d7b81f97adc34374"
SRC_URI += "git://github.com/opencontainers/runc;name=runc;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/opencontainers/runc"

# github.com/opencontainers/runtime-spec v1.0.3-0.20210326190908-1c3f411f0417
# [1] git ls-remote https://github.com/opencontainers/runtime-spec 1c3f411f041711bbeecf35ff7e93461ea6789220 
SRCREV_runtime-spec="1c3f411f041711bbeecf35ff7e93461ea6789220"
SRC_URI += "git://github.com/opencontainers/runtime-spec;name=runtime-spec;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/opencontainers/runtime-spec"

# github.com/opencontainers/selinux v1.10.1
# [1] git ls-remote https://github.com/opencontainers/selinux 84e248a28e776216cd33a51fdea90a20fe308136 
SRCREV_selinux="84e248a28e776216cd33a51fdea90a20fe308136"
SRC_URI += "git://github.com/opencontainers/selinux;name=selinux;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/opencontainers/selinux"

# github.com/opentracing/opentracing-go v1.1.0
# [1] git ls-remote https://github.com/opentracing/opentracing-go 659c90643e714681897ec2521c60567dd21da733 
SRCREV_opentracing-go="659c90643e714681897ec2521c60567dd21da733"
SRC_URI += "git://github.com/opentracing/opentracing-go;name=opentracing-go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/opentracing/opentracing-go"

# github.com/oracle/oci-go-sdk v7.1.0+incompatible
# [1] git ls-remote https://github.com/oracle/oci-go-sdk 836866c730718173acd1b37df7215c1dd88122e2 
SRCREV_oci-go-sdk="836866c730718173acd1b37df7215c1dd88122e2"
SRC_URI += "git://github.com/oracle/oci-go-sdk;name=oci-go-sdk;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/oracle/oci-go-sdk"

# github.com/osrg/gobgp v0.0.0-20211201041502-6248c576b118
# [1] git ls-remote https://github.com/osrg/gobgp 6248c576b118979d4dfc904c34bab7fb7ddd0b1a 
SRCREV_gobgp="6248c576b118979d4dfc904c34bab7fb7ddd0b1a"
SRC_URI += "git://github.com/osrg/gobgp;name=gobgp;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/osrg/gobgp"

# github.com/otiai10/copy v1.7.0
# [1] git ls-remote https://github.com/otiai10/copy 323db161ae97db91e6e13dbcda403fdb29c3fff8 
SRCREV_copy="323db161ae97db91e6e13dbcda403fdb29c3fff8"
SRC_URI += "git://github.com/otiai10/copy;name=copy;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/otiai10/copy"

# github.com/otiai10/curr v1.0.0
# [1] git ls-remote https://github.com/otiai10/curr 88e2f2cffa0b5f13e90ee963a321bd1b2f715561 
SRCREV_curr="88e2f2cffa0b5f13e90ee963a321bd1b2f715561"
SRC_URI += "git://github.com/otiai10/curr;name=curr;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/otiai10/curr"

# github.com/otiai10/mint v1.3.3
# [1] git ls-remote https://github.com/otiai10/mint f8e74d5076fdd79c3db0aa8b1dda35e603c328e5 
SRCREV_mint="f8e74d5076fdd79c3db0aa8b1dda35e603c328e5"
SRC_URI += "git://github.com/otiai10/mint;name=mint;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/otiai10/mint"

# github.com/pascaldekloe/goe v0.0.0-20180627143212-57f6aae5913c
# [1] git ls-remote https://github.com/pascaldekloe/goe 57f6aae5913c64c9bcae5dbdffd33365b5a7f138 
SRCREV_goe="57f6aae5913c64c9bcae5dbdffd33365b5a7f138"
SRC_URI += "git://github.com/pascaldekloe/goe;name=goe;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/pascaldekloe/goe"

# github.com/paulmach/orb v0.1.3
# [1] git ls-remote https://github.com/paulmach/orb c6e407b203494d3b1edb9fd5cae4cf34a4ae0f29 
SRCREV_orb="c6e407b203494d3b1edb9fd5cae4cf34a4ae0f29"
SRC_URI += "git://github.com/paulmach/orb;name=orb;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/paulmach/orb"

# github.com/pborman/uuid v1.2.1
# [1] git ls-remote https://github.com/pborman/uuid 5b6091a6a160ee5ce12917b21ab96acec2a4fdc0 
SRCREV_uuid12="5b6091a6a160ee5ce12917b21ab96acec2a4fdc0"
SRC_URI += "git://github.com/pborman/uuid;name=uuid12;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/pborman/uuid"

# github.com/pelletier/go-toml v1.9.4
# [1] git ls-remote https://github.com/pelletier/go-toml b8ba995eaaba4be30c8a8090bdfb4aa61af52054 
SRCREV_go-toml="b8ba995eaaba4be30c8a8090bdfb4aa61af52054"
SRC_URI += "git://github.com/pelletier/go-toml;name=go-toml;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/pelletier/go-toml"

# github.com/peterbourgon/diskv v2.0.1+incompatible
# [1] git ls-remote https://github.com/peterbourgon/diskv 5f041e8faa004a95c88a202771f4cc3e991971e6 
SRCREV_diskv="5f041e8faa004a95c88a202771f4cc3e991971e6"
SRC_URI += "git://github.com/peterbourgon/diskv;name=diskv;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/peterbourgon/diskv"

# github.com/pierrec/lz4 v2.6.0+incompatible
# [1] git ls-remote https://github.com/pierrec/lz4 0e583d326e0ec6b9c1ad223188dc709af385408e 
SRCREV_lz4="0e583d326e0ec6b9c1ad223188dc709af385408e"
SRC_URI += "git://github.com/pierrec/lz4;name=lz4;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/pierrec/lz4"

# github.com/pkg/errors v0.9.1
# [1] git ls-remote https://github.com/pkg/errors 614d223910a179a466c1767a985424175c39b465 
SRCREV_errors123="614d223910a179a466c1767a985424175c39b465"
SRC_URI += "git://github.com/pkg/errors;name=errors123;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/pkg/errors"

# github.com/pkg/sftp v1.10.1
# [1] git ls-remote https://github.com/pkg/sftp 3edd153f213d8d4191a0ee4577c61cca19436632 
SRCREV_sftp="3edd153f213d8d4191a0ee4577c61cca19436632"
SRC_URI += "git://github.com/pkg/sftp;name=sftp;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/pkg/sftp"

# github.com/pmezard/go-difflib v1.0.0
# [1] git ls-remote https://github.com/pmezard/go-difflib 792786c7400a136282c1664665ae0a8db921c6c2 
SRCREV_go-difflib="792786c7400a136282c1664665ae0a8db921c6c2"
SRC_URI += "git://github.com/pmezard/go-difflib;name=go-difflib;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/pmezard/go-difflib"

# github.com/posener/complete v1.1.1
# [1] git ls-remote https://github.com/posener/complete 98eb9847f27ba2008d380a32c98be474dea55bdf 
SRCREV_complete="98eb9847f27ba2008d380a32c98be474dea55bdf"
SRC_URI += "git://github.com/posener/complete;name=complete;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/posener/complete"

# github.com/pquerna/cachecontrol v0.0.0-20171018203845-0dec1b30a021
# [1] git ls-remote https://github.com/pquerna/cachecontrol 0dec1b30a0215bb68605dfc568e8855066c9202d 
SRCREV_cachecontrol="0dec1b30a0215bb68605dfc568e8855066c9202d"
SRC_URI += "git://github.com/pquerna/cachecontrol;name=cachecontrol;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/pquerna/cachecontrol"

# github.com/pquerna/otp v1.2.0
# [1] git ls-remote https://github.com/pquerna/otp 43bebefda392017900e7a7b237b4c914c6a55b50 
SRCREV_otp="43bebefda392017900e7a7b237b4c914c6a55b50"
SRC_URI += "git://github.com/pquerna/otp;name=otp;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/pquerna/otp"

# github.com/prometheus/client_golang v1.12.2
# [1] git ls-remote https://github.com/prometheus/client_golang e203144f43306c1f344fbc548fd02c4b79962e30 
SRCREV_client_golang="e203144f43306c1f344fbc548fd02c4b79962e30"
SRC_URI += "git://github.com/prometheus/client_golang;name=client_golang;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/prometheus/client_golang"

# github.com/prometheus/client_model v0.2.0
# [1] git ls-remote https://github.com/prometheus/client_model 7bc5445566f0fe75b15de23e6b93886e982d7bf9 
SRCREV_client_model="7bc5445566f0fe75b15de23e6b93886e982d7bf9"
SRC_URI += "git://github.com/prometheus/client_model;name=client_model;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/prometheus/client_model"

# github.com/prometheus/common v0.32.1
# [1] git ls-remote https://github.com/prometheus/common 2af6d036253eee1a9a08c6ddf6be6d67537bcdff 
SRCREV_common="2af6d036253eee1a9a08c6ddf6be6d67537bcdff"
SRC_URI += "git://github.com/prometheus/common;name=common;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/prometheus/common"

# github.com/prometheus/procfs v0.7.3
# [1] git ls-remote https://github.com/prometheus/procfs f436cbb89ece38bf080d446b3ca27053b305eaac 
SRCREV_procfs="f436cbb89ece38bf080d446b3ca27053b305eaac"
SRC_URI += "git://github.com/prometheus/procfs;name=procfs;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/prometheus/procfs"

# github.com/prometheus/tsdb v0.7.1
# [1] git ls-remote https://github.com/prometheus/tsdb c20450564cc42983bf923c13f3fda42de709ac13 
SRCREV_tsdb="c20450564cc42983bf923c13f3fda42de709ac13"
SRC_URI += "git://github.com/prometheus/tsdb;name=tsdb;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/prometheus/tsdb"

# github.com/qri-io/starlib v0.4.2-0.20200213133954-ff2e8cd5ef8d
# [1] git ls-remote https://github.com/qri-io/starlib ff2e8cd5ef8d5cfec439238ec91bc1f03c84b306 
SRCREV_starlib="ff2e8cd5ef8d5cfec439238ec91bc1f03c84b306"
SRC_URI += "git://github.com/qri-io/starlib;name=starlib;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/qri-io/starlib"

# github.com/quobyte/api v0.1.8
# [1] git ls-remote https://github.com/quobyte/api 61b1aa26de01c522ca8a2c713443137c99760036 
SRCREV_api1="61b1aa26de01c522ca8a2c713443137c99760036"
SRC_URI += "git://github.com/quobyte/api;name=api1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/quobyte/api"

# github.com/rancher/dynamiclistener v0.3.4-0.20220721210816-8ebd77f8a45a
# [1] git ls-remote https://github.com/rancher/dynamiclistener 8ebd77f8a45a764e8c4f389134683d2fe906fb71 
SRCREV_dynamiclistener="8ebd77f8a45a764e8c4f389134683d2fe906fb71"
SRC_URI += "git://github.com/rancher/dynamiclistener;name=dynamiclistener;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/rancher/dynamiclistener"

# github.com/rancher/lasso v0.0.0-20210616224652-fc3ebd901c08
# [1] git ls-remote https://github.com/rancher/lasso fc3ebd901c086dc8e4bfe2c07edf03c118572924 
SRCREV_lasso="fc3ebd901c086dc8e4bfe2c07edf03c118572924"
SRC_URI += "git://github.com/rancher/lasso;name=lasso;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/rancher/lasso"

# github.com/rancher/remotedialer v0.2.6-0.20220624190122-ea57207bf2b8
# [1] git ls-remote https://github.com/rancher/remotedialer ea57207bf2b8ab346426aa81a9b04c9dee43f831 
SRCREV_remotedialer="ea57207bf2b8ab346426aa81a9b04c9dee43f831"
SRC_URI += "git://github.com/rancher/remotedialer;name=remotedialer;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/rancher/remotedialer"

# github.com/rancher/wharfie v0.5.1
# [1] git ls-remote https://github.com/rancher/wharfie 80cc0b0ce983af2d75be7846e006d69fa5d64570 
SRCREV_wharfie="80cc0b0ce983af2d75be7846e006d69fa5d64570"
SRC_URI += "git://github.com/rancher/wharfie;name=wharfie;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/rancher/wharfie"

# github.com/rancher/wrangler v1.0.0
# [1] git ls-remote https://github.com/rancher/wrangler 5167c04fcdd50e24d9710813572382eeb3064805 
SRCREV_wrangler="5167c04fcdd50e24d9710813572382eeb3064805"
SRC_URI += "git://github.com/rancher/wrangler;name=wrangler;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/rancher/wrangler"

# github.com/remyoudompheng/bigfft v0.0.0-20170806203942-52369c62f446
# [1] git ls-remote https://github.com/remyoudompheng/bigfft 52369c62f4463a21c8ff8531194c5526322b8521 
SRCREV_bigfft="52369c62f4463a21c8ff8531194c5526322b8521"
SRC_URI += "git://github.com/remyoudompheng/bigfft;name=bigfft;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/remyoudompheng/bigfft"

# github.com/robfig/cron/v3 v3.0.1
# [1] git ls-remote https://github.com/robfig/cron ccba498c397bb90a9c84945bbb0f7af2d72b6309 
SRCREV_v31="ccba498c397bb90a9c84945bbb0f7af2d72b6309"
SRC_URI += "git://github.com/robfig/cron;name=v31;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/robfig/cron/v3"

# github.com/rogpeppe/fastuuid v1.2.0
# [1] git ls-remote https://github.com/rogpeppe/fastuuid 10c3923834d38e951ae8f627bfec2dc632c5b6cb 
SRCREV_fastuuid="10c3923834d38e951ae8f627bfec2dc632c5b6cb"
SRC_URI += "git://github.com/rogpeppe/fastuuid;name=fastuuid;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/rogpeppe/fastuuid"

# github.com/rogpeppe/go-charset v0.0.0-20180617210344-2471d30d28b4
# [1] git ls-remote https://github.com/rogpeppe/go-charset 2471d30d28b404738b546df7aaa82c45826bc02e 
SRCREV_go-charset="2471d30d28b404738b546df7aaa82c45826bc02e"
SRC_URI += "git://github.com/rogpeppe/go-charset;name=go-charset;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/rogpeppe/go-charset"

# github.com/rogpeppe/go-internal v1.6.1
# [1] git ls-remote https://github.com/rogpeppe/go-internal 76dc4b3fc37c97df8520f01985a79bbac5d1585d 
SRCREV_go-internal="76dc4b3fc37c97df8520f01985a79bbac5d1585d"
SRC_URI += "git://github.com/rogpeppe/go-internal;name=go-internal;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/rogpeppe/go-internal"

# github.com/rootless-containers/rootlesskit v1.0.1
# [1] git ls-remote https://github.com/rootless-containers/rootlesskit 5d5f4c0c26e26a6b51d1838c23e793e5836442d0 
SRCREV_rootlesskit="5d5f4c0c26e26a6b51d1838c23e793e5836442d0"
SRC_URI += "git://github.com/rootless-containers/rootlesskit;name=rootlesskit;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/rootless-containers/rootlesskit"

# github.com/rs/xid v1.4.0
# [1] git ls-remote https://github.com/rs/xid 66f8c42da230c3323ed4e29805e73eefbad41fc5 
SRCREV_xid="66f8c42da230c3323ed4e29805e73eefbad41fc5"
SRC_URI += "git://github.com/rs/xid;name=xid;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/rs/xid"

# github.com/rubiojr/go-vhd v0.0.0-20200706105327-02e210299021
# [1] git ls-remote https://github.com/rubiojr/go-vhd 02e2102990218160c1cb608a3e9679312a3b8425 
SRCREV_go-vhd="02e2102990218160c1cb608a3e9679312a3b8425"
SRC_URI += "git://github.com/rubiojr/go-vhd;name=go-vhd;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/rubiojr/go-vhd"

# github.com/russross/blackfriday v1.5.2
# [1] git ls-remote https://github.com/russross/blackfriday 05f3235734ad95d0016f6a23902f06461fcf567a 
SRCREV_blackfriday="05f3235734ad95d0016f6a23902f06461fcf567a"
SRC_URI += "git://github.com/russross/blackfriday;name=blackfriday;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/russross/blackfriday"

# github.com/russross/blackfriday/v2 v2.1.0
# [1] git ls-remote https://github.com/russross/blackfriday 4c9bf9512682b995722660a4196c0013228e2049 
SRCREV_v212345678="4c9bf9512682b995722660a4196c0013228e2049"
SRC_URI += "git://github.com/russross/blackfriday;name=v212345678;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/russross/blackfriday/v2"

# github.com/ryanuber/columnize v0.0.0-20160712163229-9b3edd62028f
# [1] git ls-remote https://github.com/ryanuber/columnize 9b3edd62028f107d7cabb19353292afd29311a4e 
SRCREV_columnize="9b3edd62028f107d7cabb19353292afd29311a4e"
SRC_URI += "git://github.com/ryanuber/columnize;name=columnize;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/ryanuber/columnize"

# github.com/safchain/ethtool v0.0.0-20210803160452-9aa261dae9b1
# [1] git ls-remote https://github.com/safchain/ethtool 9aa261dae9b185c85c3c35529bacc9778e207ad8 
SRCREV_ethtool1="9aa261dae9b185c85c3c35529bacc9778e207ad8"
SRC_URI += "git://github.com/safchain/ethtool;name=ethtool1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/safchain/ethtool"

# github.com/satori/go.uuid v1.2.0
# [1] git ls-remote https://github.com/satori/go.uuid f58768cc1a7a7e77a3bd49e98cdd21419399b6a3 
SRCREV_go.uuid="f58768cc1a7a7e77a3bd49e98cdd21419399b6a3"
SRC_URI += "git://github.com/satori/go.uuid;name=go.uuid;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/satori/go.uuid"

# github.com/sclevine/agouti v3.0.0+incompatible
# [1] git ls-remote https://github.com/sclevine/agouti e3f6c97a4077ad821daab49db8172cf9f6690faf 
SRCREV_agouti="e3f6c97a4077ad821daab49db8172cf9f6690faf"
SRC_URI += "git://github.com/sclevine/agouti;name=agouti;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/sclevine/agouti"

# github.com/sean-/seed v0.0.0-20170313163322-e2103e2c3529
# [1] git ls-remote https://github.com/sean-/seed e2103e2c35297fb7e17febb81e49b312087a2372 
SRCREV_seed="e2103e2c35297fb7e17febb81e49b312087a2372"
SRC_URI += "git://github.com/sean-/seed;name=seed;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/sean-/seed"

# github.com/sebdah/goldie v1.0.0
# [1] git ls-remote https://github.com/sebdah/goldie d313ffb52c776394bb6a2c72a386f918b7e826e7 
SRCREV_goldie="d313ffb52c776394bb6a2c72a386f918b7e826e7"
SRC_URI += "git://github.com/sebdah/goldie;name=goldie;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/sebdah/goldie"

# github.com/seccomp/libseccomp-golang v0.9.2-0.20220502022130-f33da4d89646
# [1] git ls-remote https://github.com/seccomp/libseccomp-golang f33da4d89646b6c01cb104d81094208b83a9b8e4 
SRCREV_libseccomp-golang="f33da4d89646b6c01cb104d81094208b83a9b8e4"
SRC_URI += "git://github.com/seccomp/libseccomp-golang;name=libseccomp-golang;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/seccomp/libseccomp-golang"

# github.com/sergi/go-diff v1.1.0
# [1] git ls-remote https://github.com/sergi/go-diff 58c5cb1602ee9676b5d3590d782bedde80706fcc 
SRCREV_go-diff="58c5cb1602ee9676b5d3590d782bedde80706fcc"
SRC_URI += "git://github.com/sergi/go-diff;name=go-diff;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/sergi/go-diff"

# github.com/shengdoushi/base58 v1.0.0
# [1] git ls-remote https://github.com/shengdoushi/base58 c5f44ca1af76fb63925d5fba8fa7b85870199839 
SRCREV_base58="c5f44ca1af76fb63925d5fba8fa7b85870199839"
SRC_URI += "git://github.com/shengdoushi/base58;name=base58;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/shengdoushi/base58"

# github.com/shurcooL/sanitized_anchor_name v1.0.0
# [1] git ls-remote https://github.com/shurcooL/sanitized_anchor_name 7bfe4c7ecddb3666a94b053b422cdd8f5aaa3615 
SRCREV_sanitized_anchor_name="7bfe4c7ecddb3666a94b053b422cdd8f5aaa3615"
SRC_URI += "git://github.com/shurcooL/sanitized_anchor_name;name=sanitized_anchor_name;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/shurcooL/sanitized_anchor_name"

# github.com/sirupsen/logrus v1.8.1
# [1] git ls-remote https://github.com/sirupsen/logrus bdc0db8ead3853c56b7cd1ac2ba4e11b47d7da6b 
SRCREV_logrus="bdc0db8ead3853c56b7cd1ac2ba4e11b47d7da6b"
SRC_URI += "git://github.com/sirupsen/logrus;name=logrus;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/sirupsen/logrus"

# github.com/smartystreets/assertions v1.1.0
# [1] git ls-remote https://github.com/smartystreets/assertions b63c46aebc02f130a052a785f4f27bc938f2a4f3 
SRCREV_assertions="b63c46aebc02f130a052a785f4f27bc938f2a4f3"
SRC_URI += "git://github.com/smartystreets/assertions;name=assertions;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/smartystreets/assertions"

# github.com/smartystreets/goconvey v1.6.4
# [1] git ls-remote https://github.com/smartystreets/goconvey 505e419363375c0dc132d3ac02632a4ee32199ca 
SRCREV_goconvey="505e419363375c0dc132d3ac02632a4ee32199ca"
SRC_URI += "git://github.com/smartystreets/goconvey;name=goconvey;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/smartystreets/goconvey"

# github.com/soheilhy/cmux v0.1.5
# [1] git ls-remote https://github.com/soheilhy/cmux 5ec6847320e53b5fee0ab9a4757b56625a946c85 
SRCREV_cmux="5ec6847320e53b5fee0ab9a4757b56625a946c85"
SRC_URI += "git://github.com/soheilhy/cmux;name=cmux;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/soheilhy/cmux"

# github.com/songgao/water v0.0.0-20200317203138-2b4b6d7c09d8
# [1] git ls-remote https://github.com/songgao/water 2b4b6d7c09d80835e5f13f6b040d69f00a158b24 
SRCREV_water="2b4b6d7c09d80835e5f13f6b040d69f00a158b24"
SRC_URI += "git://github.com/songgao/water;name=water;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/songgao/water"

# github.com/spaolacci/murmur3 v0.0.0-20180118202830-f09979ecbc72
# [1] git ls-remote https://github.com/spaolacci/murmur3 f09979ecbc725b9e6d41a297405f65e7e8804acc 
SRCREV_murmur3="f09979ecbc725b9e6d41a297405f65e7e8804acc"
SRC_URI += "git://github.com/spaolacci/murmur3;name=murmur3;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/spaolacci/murmur3"

# github.com/spf13/afero v1.6.0
# [1] git ls-remote https://github.com/spf13/afero bc94f58beda5dbff6f74599fcb1f1717c5b1c111 
SRCREV_afero="bc94f58beda5dbff6f74599fcb1f1717c5b1c111"
SRC_URI += "git://github.com/spf13/afero;name=afero;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/spf13/afero"

# github.com/spf13/cast v1.3.1
# [1] git ls-remote https://github.com/spf13/cast 1ffadf551085444af981432dd0f6d1160c11ec64 
SRCREV_cast="1ffadf551085444af981432dd0f6d1160c11ec64"
SRC_URI += "git://github.com/spf13/cast;name=cast;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/spf13/cast"

# github.com/spf13/cobra v1.4.0
# [1] git ls-remote https://github.com/spf13/cobra 5b2b9e9f61d36ccb66167301f76a2292c3729855 
SRCREV_cobra="5b2b9e9f61d36ccb66167301f76a2292c3729855"
SRC_URI += "git://github.com/spf13/cobra;name=cobra;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/spf13/cobra"

# github.com/spf13/jwalterweatherman v1.1.0
# [1] git ls-remote https://github.com/spf13/jwalterweatherman 94f6ae3ed3bceceafa716478c5fbf8d29ca601a1 
SRCREV_jwalterweatherman="94f6ae3ed3bceceafa716478c5fbf8d29ca601a1"
SRC_URI += "git://github.com/spf13/jwalterweatherman;name=jwalterweatherman;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/spf13/jwalterweatherman"

# github.com/spf13/pflag v1.0.5
# [1] git ls-remote https://github.com/spf13/pflag 2e9d26c8c37aae03e3f9d4e90b7116f5accb7cab 
SRCREV_pflag="2e9d26c8c37aae03e3f9d4e90b7116f5accb7cab"
SRC_URI += "git://github.com/spf13/pflag;name=pflag;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/spf13/pflag"

# github.com/spf13/viper v1.8.1
# [1] git ls-remote https://github.com/spf13/viper bd038658992ae06c6ccf069a5b4bebe432602d0f 
SRCREV_viper="bd038658992ae06c6ccf069a5b4bebe432602d0f"
SRC_URI += "git://github.com/spf13/viper;name=viper;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/spf13/viper"

# github.com/spyzhov/ajson v0.4.2
# [1] git ls-remote https://github.com/spyzhov/ajson 891ea8c52c9299c43a45458e6311dc3f6c84ae2a 
SRCREV_ajson="891ea8c52c9299c43a45458e6311dc3f6c84ae2a"
SRC_URI += "git://github.com/spyzhov/ajson;name=ajson;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/spyzhov/ajson"

# github.com/stefanberger/go-pkcs11uri v0.0.0-20201008174630-78d3cae3a980
# [1] git ls-remote https://github.com/stefanberger/go-pkcs11uri 78d3cae3a9805d89aa4fa80a362ca944c89a1b99 
SRCREV_go-pkcs11uri="78d3cae3a9805d89aa4fa80a362ca944c89a1b99"
SRC_URI += "git://github.com/stefanberger/go-pkcs11uri;name=go-pkcs11uri;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/stefanberger/go-pkcs11uri"

# github.com/stoewer/go-strcase v1.2.0
# [1] git ls-remote https://github.com/stoewer/go-strcase 7962b205553802087345c0b4c74d57b65236f676 
SRCREV_go-strcase="7962b205553802087345c0b4c74d57b65236f676"
SRC_URI += "git://github.com/stoewer/go-strcase;name=go-strcase;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/stoewer/go-strcase"

# github.com/storageos/go-api v2.2.0+incompatible
# [1] git ls-remote https://github.com/storageos/go-api d6e6bab2519238b8a582cabd2f4a71cca81963aa 
SRCREV_go-api="d6e6bab2519238b8a582cabd2f4a71cca81963aa"
SRC_URI += "git://github.com/storageos/go-api;name=go-api;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/storageos/go-api"

# github.com/stretchr/objx v0.2.0
# [1] git ls-remote https://github.com/stretchr/objx ea4fe68685ee0d3cee7032121851b57e7494e8ea 
SRCREV_objx="ea4fe68685ee0d3cee7032121851b57e7494e8ea"
SRC_URI += "git://github.com/stretchr/objx;name=objx;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/stretchr/objx"

# github.com/stretchr/testify v1.7.1
# [1] git ls-remote https://github.com/stretchr/testify 083ff1c0449867d0d8d456483ee5fab8e0c0e1e6 
SRCREV_testify1="083ff1c0449867d0d8d456483ee5fab8e0c0e1e6"
SRC_URI += "git://github.com/stretchr/testify;name=testify1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/stretchr/testify"

# github.com/subosito/gotenv v1.2.0
# [1] git ls-remote https://github.com/subosito/gotenv 2ef7124db659d49edac6aa459693a15ae36c671a 
SRCREV_gotenv="2ef7124db659d49edac6aa459693a15ae36c671a"
SRC_URI += "git://github.com/subosito/gotenv;name=gotenv;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/subosito/gotenv"

# github.com/syndtr/gocapability v0.0.0-20200815063812-42c35b437635
# [1] git ls-remote https://github.com/syndtr/gocapability 42c35b4376354fd554efc7ad35e0b7f94e3a0ffb 
SRCREV_gocapability="42c35b4376354fd554efc7ad35e0b7f94e3a0ffb"
SRC_URI += "git://github.com/syndtr/gocapability;name=gocapability;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/syndtr/gocapability"

# github.com/tchap/go-patricia v2.3.0+incompatible
# [1] git ls-remote https://github.com/tchap/go-patricia a7f0089c6f496e8e70402f61733606daa326cac5 
SRCREV_go-patricia="a7f0089c6f496e8e70402f61733606daa326cac5"
SRC_URI += "git://github.com/tchap/go-patricia;name=go-patricia;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/tchap/go-patricia"

# github.com/tencentcloud/tencentcloud-sdk-go v1.0.67
# [1] git ls-remote https://github.com/tencentcloud/tencentcloud-sdk-go d1f36c65d42b2276260adba712fcd48fa20f2ad3 
SRCREV_tencentcloud-sdk-go="d1f36c65d42b2276260adba712fcd48fa20f2ad3"
SRC_URI += "git://github.com/tencentcloud/tencentcloud-sdk-go;name=tencentcloud-sdk-go;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/tencentcloud/tencentcloud-sdk-go"

# github.com/tidwall/pretty v1.0.0
# [1] git ls-remote https://github.com/tidwall/pretty 1166b9ac2b65e46a43d8618d30d1554f4652d49b 
SRCREV_pretty12="1166b9ac2b65e46a43d8618d30d1554f4652d49b"
SRC_URI += "git://github.com/tidwall/pretty;name=pretty12;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/tidwall/pretty"

# github.com/tmc/grpc-websocket-proxy v0.0.0-20201229170055-e5319fda7802
# [1] git ls-remote https://github.com/tmc/grpc-websocket-proxy e5319fda780223b9d4d06dba2059a26ebff67b8a 
SRCREV_grpc-websocket-proxy="e5319fda780223b9d4d06dba2059a26ebff67b8a"
SRC_URI += "git://github.com/tmc/grpc-websocket-proxy;name=grpc-websocket-proxy;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/tmc/grpc-websocket-proxy"

# github.com/tmccombs/hcl2json v0.3.3
# [1] git ls-remote https://github.com/tmccombs/hcl2json 5428d3459c3c62940e28e54643580f44055511a0 
SRCREV_hcl2json="5428d3459c3c62940e28e54643580f44055511a0"
SRC_URI += "git://github.com/tmccombs/hcl2json;name=hcl2json;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/tmccombs/hcl2json"

# github.com/u-root/uio v0.0.0-20210528114334-82958018845c
# [1] git ls-remote https://github.com/u-root/uio 82958018845cfb6b02c09e57c390e67c1d81ee95 
SRCREV_uio="82958018845cfb6b02c09e57c390e67c1d81ee95"
SRC_URI += "git://github.com/u-root/uio;name=uio;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/u-root/uio"

# github.com/ugorji/go v1.1.4
# [1] git ls-remote https://github.com/ugorji/go 2adff0894ba3bc2eeb9f9aea45fefd49802e1a13 
SRCREV_go1234="2adff0894ba3bc2eeb9f9aea45fefd49802e1a13"
SRC_URI += "git://github.com/ugorji/go;name=go1234;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/ugorji/go"

# github.com/ulikunitz/xz v0.5.8
# [1] git ls-remote https://github.com/ulikunitz/xz 492fb8161cff03dd43fd938e40ecede6c6ffd8eb 
SRCREV_xz="492fb8161cff03dd43fd938e40ecede6c6ffd8eb"
SRC_URI += "git://github.com/ulikunitz/xz;name=xz;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/ulikunitz/xz"

# github.com/urfave/cli v1.22.9
# [1] git ls-remote https://github.com/urfave/cli 575b8b484800dff928fe8755d61fea64571bc191 
SRCREV_cli123="575b8b484800dff928fe8755d61fea64571bc191"
SRC_URI += "git://github.com/urfave/cli;name=cli123;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/urfave/cli"

# github.com/urfave/cli/v2 v2.5.1
# [1] git ls-remote https://github.com/urfave/cli 76418f2642c40d5baf00734e9601a2c6781cc629 
SRCREV_v2123456789="76418f2642c40d5baf00734e9601a2c6781cc629"
SRC_URI += "git://github.com/urfave/cli;name=v2123456789;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/urfave/cli/v2"

# github.com/urfave/negroni v1.0.0
# [1] git ls-remote https://github.com/urfave/negroni c6a59be0ce122566695fbd5e48a77f8f10c8a63a 
SRCREV_negroni="c6a59be0ce122566695fbd5e48a77f8f10c8a63a"
SRC_URI += "git://github.com/urfave/negroni;name=negroni;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/urfave/negroni"

# github.com/vbatts/tar-split v0.11.2
# [1] git ls-remote https://github.com/vbatts/tar-split 80a436fd6164c557b131f7c59ed69bd81af69761 
SRCREV_tar-split="80a436fd6164c557b131f7c59ed69bd81af69761"
SRC_URI += "git://github.com/vbatts/tar-split;name=tar-split;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/vbatts/tar-split"

# github.com/vektah/gqlparser v1.1.2
# [1] git ls-remote https://github.com/vektah/gqlparser 05741cdb0871330d8bc980d4afd21ab34eceee83 
SRCREV_gqlparser="05741cdb0871330d8bc980d4afd21ab34eceee83"
SRC_URI += "git://github.com/vektah/gqlparser;name=gqlparser;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/vektah/gqlparser"

# github.com/vishvananda/netlink v1.2.1-beta.2
# [1] git ls-remote https://github.com/vishvananda/netlink 5e915e0149386ce3d02379ff93f4c0a5601779d5 
SRCREV_netlink1="5e915e0149386ce3d02379ff93f4c0a5601779d5"
SRC_URI += "git://github.com/vishvananda/netlink;name=netlink1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/vishvananda/netlink"

# github.com/vishvananda/netns v0.0.0-20211101163701-50045581ed74
# [1] git ls-remote https://github.com/vishvananda/netns 50045581ed74c15eded78746bae9fb3df07d6655 
SRCREV_netns="50045581ed74c15eded78746bae9fb3df07d6655"
SRC_URI += "git://github.com/vishvananda/netns;name=netns;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/vishvananda/netns"

# github.com/vmihailenco/msgpack v3.3.3+incompatible
# [1] git ls-remote https://github.com/vmihailenco/msgpack b5e691b1eb52a28c05e67ab9df303626c095c23b 
SRCREV_msgpack="b5e691b1eb52a28c05e67ab9df303626c095c23b"
SRC_URI += "git://github.com/vmihailenco/msgpack;name=msgpack;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/vmihailenco/msgpack"

# github.com/vmihailenco/msgpack/v4 v4.3.12
# [1] git ls-remote https://github.com/vmihailenco/msgpack 016c538aea4133aab9baae5e99dc964b8178e60f 
SRCREV_v41="016c538aea4133aab9baae5e99dc964b8178e60f"
SRC_URI += "git://github.com/vmihailenco/msgpack;name=v41;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/vmihailenco/msgpack/v4"

# github.com/vmihailenco/tagparser v0.1.1
# [1] git ls-remote https://github.com/vmihailenco/tagparser e4b6c8cf4f3d7bb572551d5a531a9f2068b2fd6e 
SRCREV_tagparser="e4b6c8cf4f3d7bb572551d5a531a9f2068b2fd6e"
SRC_URI += "git://github.com/vmihailenco/tagparser;name=tagparser;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/vmihailenco/tagparser"

# github.com/vmware/govmomi v0.20.3
# [1] git ls-remote https://github.com/vmware/govmomi fdd277866f1ef3959c564881e297bb7a6baa5daa 
SRCREV_govmomi="fdd277866f1ef3959c564881e297bb7a6baa5daa"
SRC_URI += "git://github.com/vmware/govmomi;name=govmomi;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/vmware/govmomi"

# github.com/xiang90/probing v0.0.0-20190116061207-43a291ad63a2
# [1] git ls-remote https://github.com/xiang90/probing 43a291ad63a214a207fefbf03c7d9d78b703162b 
SRCREV_probing="43a291ad63a214a207fefbf03c7d9d78b703162b"
SRC_URI += "git://github.com/xiang90/probing;name=probing;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/xiang90/probing"

# github.com/xlab/treeprint v0.0.0-20181112141820-a009c3971eca
# [1] git ls-remote https://github.com/xlab/treeprint a009c3971eca89777614839eb7f69abed3ea3959 
SRCREV_treeprint="a009c3971eca89777614839eb7f69abed3ea3959"
SRC_URI += "git://github.com/xlab/treeprint;name=treeprint;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/xlab/treeprint"

# github.com/xordataexchange/crypt v0.0.3-0.20170626215501-b2862e3d0a77
# [1] git ls-remote https://github.com/xordataexchange/crypt b2862e3d0a775f18c7cfe02273500ae307b61218 
SRCREV_crypt1="b2862e3d0a775f18c7cfe02273500ae307b61218"
SRC_URI += "git://github.com/xordataexchange/crypt;name=crypt1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/xordataexchange/crypt"

# github.com/yl2chen/cidranger v1.0.2
# [1] git ls-remote https://github.com/yl2chen/cidranger 7ff5a0e84593dad6fbd50551343618d7956b3c71 
SRCREV_cidranger="7ff5a0e84593dad6fbd50551343618d7956b3c71"
SRC_URI += "git://github.com/yl2chen/cidranger;name=cidranger;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/yl2chen/cidranger"

# github.com/yuin/goldmark v1.4.1
# [1] git ls-remote https://github.com/yuin/goldmark 829d874034a5048e1a5c6354dcd8b40e1e41c3a2 
SRCREV_goldmark="829d874034a5048e1a5c6354dcd8b40e1e41c3a2"
SRC_URI += "git://github.com/yuin/goldmark;name=goldmark;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/yuin/goldmark"

# github.com/zclconf/go-cty v1.9.1
# [1] git ls-remote https://github.com/zclconf/go-cty e5d3f1507f80d88b60e58c4644f462636f400c98 
SRCREV_go-cty="e5d3f1507f80d88b60e58c4644f462636f400c98"
SRC_URI += "git://github.com/zclconf/go-cty;name=go-cty;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/zclconf/go-cty"

# github.com/zclconf/go-cty-debug v0.0.0-20191215020915-b22d67c1ba0b
# [1] git ls-remote https://github.com/zclconf/go-cty-debug b22d67c1ba0bcee1000318b54887fcd6180fbb58 
SRCREV_go-cty-debug="b22d67c1ba0bcee1000318b54887fcd6180fbb58"
SRC_URI += "git://github.com/zclconf/go-cty-debug;name=go-cty-debug;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/zclconf/go-cty-debug"

# go.etcd.io/bbolt v1.3.6
# [1] git ls-remote https://github.com/etcd-io/bbolt 685b13a4ef0053a4a38623bcebda621db6f7eaf7 
SRCREV_bbolt1="685b13a4ef0053a4a38623bcebda621db6f7eaf7"
SRC_URI += "git://github.com/etcd-io/bbolt;name=bbolt1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/go.etcd.io/bbolt"

# github.com/k3s-io/etcd/api/v3 v3.5.3-k3s1
# [1] git ls-remote https://github.com/k3s-io/etcd 73bb1a04099d1788a62f15ad33838beb170566ae 
SRCREV_v312="73bb1a04099d1788a62f15ad33838beb170566ae"
SRC_URI += "git://github.com/k3s-io/etcd;name=v312;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/k3s-io/etcd/api/v3"

# go.etcd.io/etcd/client/v2 v2.305.4
# [1] git ls-remote https://github.com/etcd-io/etcd 08407ff7600eb16c4445d5f21c4fafaf19412e24 
SRCREV_v212345678910="08407ff7600eb16c4445d5f21c4fafaf19412e24"
SRC_URI += "git://github.com/etcd-io/etcd;name=v212345678910;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/etcd-io/etcd/client/v2"

# [2] failed: fatal: repository 'https://go.mongodb.org/' not found
#     site; go.mongodb.org/mongo-driver
# git ls-remote https://go.mongodb.org v1.1.2 
# SRCREV_mongo-driver="deadbeef"
# SRC_URI += "git://go.mongodb.org;name=mongo-driver;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/go.mongodb.org/mongo-driver"

# go.mozilla.org/pkcs7 v0.0.0-20200128120323-432b2356ecb1
# [1] git ls-remote https://github.com/mozilla-services/pkcs7 432b2356ecb18209c1cec25680b8a23632794f21 
SRCREV_pkcs7="432b2356ecb18209c1cec25680b8a23632794f21"
SRC_URI += "git://github.com/mozilla-services/pkcs7;name=pkcs7;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/go.mozilla.org/pkcs7"

# go.opencensus.io v0.23.0
# [1] git ls-remote https://github.com/census-instrumentation/opencensus-go 49838f207d61097fc0ebb8aeef306913388376ca 
SRCREV_go.opencensus.io="49838f207d61097fc0ebb8aeef306913388376ca"
SRC_URI += "git://github.com/census-instrumentation/opencensus-go;name=go.opencensus.io;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/go.opencensus.io"

# go.opentelemetry.io/contrib v0.20.0
# [1] git ls-remote https://github.com/open-telemetry/opentelemetry-go-contrib 0e5bef9af1c708e28aabcadd3fd8b35c36526e44 
SRCREV_contrib="0e5bef9af1c708e28aabcadd3fd8b35c36526e44"
SRC_URI += "git://github.com/open-telemetry/opentelemetry-go-contrib;name=contrib;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/go.opentelemetry.io/contrib"

# go.opentelemetry.io/otel v0.20.0
# [1] git ls-remote https://github.com/open-telemetry/opentelemetry-go 02d8bdd5d9163f32c48f4db23bf2e589f89f16c0 
SRCREV_otel="02d8bdd5d9163f32c48f4db23bf2e589f89f16c0"
SRC_URI += "git://github.com/open-telemetry/opentelemetry-go;name=otel;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/go.opentelemetry.io/otel"

# go.opentelemetry.io/proto/otlp v0.7.0
# [1] git ls-remote https://github.com/open-telemetry/opentelemetry-proto-go 4fc4e99f9e4387bc9890e74f757d3994ffa384ce 
SRCREV_otlp1="4fc4e99f9e4387bc9890e74f757d3994ffa384ce"
SRC_URI += "git://github.com/open-telemetry/opentelemetry-proto-go;name=otlp1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/open-telemetry/opentelemetry-proto-go/otlp"

# go.starlark.net v0.0.0-20200306205701-8dd3e2ee1dd5
# [1] git ls-remote https://github.com/google/starlark-go 8dd3e2ee1dd5d034baada4c7b4fcf231294a1013 
SRCREV_go.starlark.net="8dd3e2ee1dd5d034baada4c7b4fcf231294a1013"
SRC_URI += "git://github.com/google/starlark-go;name=go.starlark.net;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/google/starlark-go"

# go.uber.org/atomic v1.7.0
# [1] git ls-remote https://github.com/uber-go/atomic 12f27ba2637fa0e13772a4f05fa46a5d18d53182 
SRCREV_atomic="12f27ba2637fa0e13772a4f05fa46a5d18d53182"
SRC_URI += "git://github.com/uber-go/atomic;name=atomic;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/go.uber.org/atomic"

# go.uber.org/goleak v1.1.10
# [1] git ls-remote https://github.com/uber-go/goleak 8186b793a375782d05df2748849e26b541b19ace 
SRCREV_goleak="8186b793a375782d05df2748849e26b541b19ace"
SRC_URI += "git://github.com/uber-go/goleak;name=goleak;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/go.uber.org/goleak"

# go.uber.org/multierr v1.6.0
# [1] git ls-remote https://github.com/uber-go/multierr 3114a8b704d2d28dbacda34a872690aaef66aeed 
SRCREV_multierr="3114a8b704d2d28dbacda34a872690aaef66aeed"
SRC_URI += "git://github.com/uber-go/multierr;name=multierr;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/go.uber.org/multierr"

# go.uber.org/zap v1.19.0
# [1] git ls-remote https://github.com/uber-go/zap c8e813efff7b3d6f791ee71b3f76b9a175d2a9d8 
SRCREV_zap="c8e813efff7b3d6f791ee71b3f76b9a175d2a9d8"
SRC_URI += "git://github.com/uber-go/zap;name=zap;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/go.uber.org/zap"

# golang.org/x/crypto v0.0.0-20220315160706-3147a52a75dd
# [1] git ls-remote https://github.com/golang/crypto 3147a52a75dda54ac3a611ef8978640d85188a2a 
SRCREV_crypto="3147a52a75dda54ac3a611ef8978640d85188a2a"
SRC_URI += "git://github.com/golang/crypto;name=crypto;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/crypto"

# golang.org/x/exp v0.0.0-20210220032938-85be41e4509f
# [1] git ls-remote https://github.com/golang/exp 85be41e4509f417cbf99006a027aef75deb8afd1 
SRCREV_exp="85be41e4509f417cbf99006a027aef75deb8afd1"
SRC_URI += "git://github.com/golang/exp;name=exp;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/exp"

# golang.org/x/image v0.0.0-20190802002840-cff245a6509b
# [1] git ls-remote https://github.com/golang/image cff245a6509b8c4de022d0d5b9037c503c5989d6 
SRCREV_image="cff245a6509b8c4de022d0d5b9037c503c5989d6"
SRC_URI += "git://github.com/golang/image;name=image;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/image"

# golang.org/x/lint v0.0.0-20210508222113-6edffad5e616
# [1] git ls-remote https://github.com/golang/lint 6edffad5e6160f5949cdefc81710b2706fbcd4f6 
SRCREV_lint="6edffad5e6160f5949cdefc81710b2706fbcd4f6"
SRC_URI += "git://github.com/golang/lint;name=lint;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/lint"

# golang.org/x/mobile v0.0.0-20201217150744-e6ae53a27f4f
# [1] git ls-remote https://github.com/golang/mobile e6ae53a27f4fd7cfa2943f2ae47b96cba8eb01c9 
SRCREV_mobile="e6ae53a27f4fd7cfa2943f2ae47b96cba8eb01c9"
SRC_URI += "git://github.com/golang/mobile;name=mobile;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/mobile"

# golang.org/x/mod v0.6.0-dev.0.20220106191415-9b9b3d81d5e3
# [1] git ls-remote https://github.com/golang/mod 9b9b3d81d5e39b22d65814a8daf6723f3035d813 
SRCREV_mod="9b9b3d81d5e39b22d65814a8daf6723f3035d813"
SRC_URI += "git://github.com/golang/mod;name=mod;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/mod"

# golang.org/x/net v0.0.0-20220127200216-cd36cc0744dd
# [1] git ls-remote https://github.com/golang/net cd36cc0744dd695657988f15f08446dc81e16efc 
SRCREV_net="cd36cc0744dd695657988f15f08446dc81e16efc"
SRC_URI += "git://github.com/golang/net;name=net;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/net"

# golang.org/x/oauth2 v0.0.0-20211104180415-d3ed0bb246c8
# [1] git ls-remote https://github.com/golang/oauth2 d3ed0bb246c8d3c75b63937d9a5eecff9c74d7fe 
SRCREV_oauth2="d3ed0bb246c8d3c75b63937d9a5eecff9c74d7fe"
SRC_URI += "git://github.com/golang/oauth2;name=oauth2;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/oauth2"

# golang.org/x/sync v0.0.0-20210220032951-036812b2e83c
# [1] git ls-remote https://github.com/golang/sync 036812b2e83c0ddf193dd5a34e034151da389d09 
SRCREV_sync="036812b2e83c0ddf193dd5a34e034151da389d09"
SRC_URI += "git://github.com/golang/sync;name=sync;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/sync"

# golang.org/x/sys v0.0.0-20220209214540-3681064d5158
# [1] git ls-remote https://github.com/golang/sys 3681064d51587c1db0324b3d5c23c2ddbcff6e8f 
SRCREV_sys="3681064d51587c1db0324b3d5c23c2ddbcff6e8f"
SRC_URI += "git://github.com/golang/sys;name=sys;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/sys"

# golang.org/x/term v0.0.0-20210927222741-03fcf44c2211
# [1] git ls-remote https://github.com/golang/term 03fcf44c2211dcd5eb77510b5f7c1fb02d6ded50 
SRCREV_term1="03fcf44c2211dcd5eb77510b5f7c1fb02d6ded50"
SRC_URI += "git://github.com/golang/term;name=term1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/term"

# golang.org/x/text v0.3.7
# [1] git ls-remote https://github.com/golang/text 383b2e75a7a4198c42f8f87833eefb772868a56f 
SRCREV_text1="383b2e75a7a4198c42f8f87833eefb772868a56f"
SRC_URI += "git://github.com/golang/text;name=text1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/text"

# golang.org/x/time v0.0.0-20220210224613-90d013bbcef8
# [1] git ls-remote https://github.com/golang/time 90d013bbcef8e15b6f78023a0e3b996267153e7d 
SRCREV_time="90d013bbcef8e15b6f78023a0e3b996267153e7d"
SRC_URI += "git://github.com/golang/time;name=time;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/time"

# golang.org/x/tools v0.1.10-0.20220218145154-897bd77cd717
# [1] git ls-remote https://github.com/golang/tools 897bd77cd7177f6d679eafa9767ff6ef9211458a 
SRCREV_tools="897bd77cd7177f6d679eafa9767ff6ef9211458a"
SRC_URI += "git://github.com/golang/tools;name=tools;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/tools"

# golang.org/x/xerrors v0.0.0-20200804184101-5ec99f83aff1
# [1] git ls-remote https://github.com/golang/xerrors 5ec99f83aff198f5fbd629d6c8d8eb38a04218ca 
SRCREV_xerrors="5ec99f83aff198f5fbd629d6c8d8eb38a04218ca"
SRC_URI += "git://github.com/golang/xerrors;name=xerrors;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.org/x/xerrors"

# [2] failed: fatal: repository 'https://git.zx2c4.com/' not found
#     site; golang.zx2c4.com/go118/netip
# git ls-remote https://git.zx2c4.com a4a02eeacf9d 
# SRCREV_netip="deadbeef"
# SRC_URI += "git://git.zx2c4.com;name=netip;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.zx2c4.com/go118/netip"

# golang.zx2c4.com/wintun v0.0.0-20211104114900-415007cec224
# [1] git ls-remote https://git.zx2c4.com/wintun-go 415007cec224e00ca8633a5d70c258cf9ab0cddd 
SRCREV_wintun="415007cec224e00ca8633a5d70c258cf9ab0cddd"
SRC_URI += "git://git.zx2c4.com/wintun-go;name=wintun;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.zx2c4.com/wintun"

# golang.zx2c4.com/wireguard v0.0.0-20220117163742-e0b8f11489c5
# [1] git ls-remote https://git.zx2c4.com/wireguard-go e0b8f11489c57cbb38b12ae446cf41c4df6ac553 
SRCREV_wireguard="e0b8f11489c57cbb38b12ae446cf41c4df6ac553"
SRC_URI += "git://git.zx2c4.com/wireguard-go;name=wireguard;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.zx2c4.com/wireguard"

# golang.zx2c4.com/wireguard/wgctrl v0.0.0-20211230205640-daad0b7ba671
# [1] git ls-remote https://github.com/WireGuard/wgctrl-go daad0b7ba671c0e628de07c6d16d24dbd0d8a55e 
SRCREV_wgctrl="daad0b7ba671c0e628de07c6d16d24dbd0d8a55e"
SRC_URI += "git://github.com/WireGuard/wgctrl-go;name=wgctrl;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/golang.zx2c4.com/wireguard/wgctrl"

# [2] failed: fatal: repository 'https://gomodules.xyz/' not found
#     site; gomodules.xyz/jsonpatch/v2
# git ls-remote https://gomodules.xyz v2.2.0 
# SRCREV_v21234567891011="deadbeef"
# SRC_URI += "git://gomodules.xyz;name=v21234567891011;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gomodules.xyz/jsonpatch/v2"

# gonum.org/v1/gonum v0.6.2
# [1] git ls-remote https://github.com/gonum/gonum 402b1e2868774b0eee0ec7c85bb9a7b36cf650ae 
SRCREV_gonum="402b1e2868774b0eee0ec7c85bb9a7b36cf650ae"
SRC_URI += "git://github.com/gonum/gonum;name=gonum;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gonum/gonum"

# gonum.org/v1/netlib v0.0.0-20190331212654-76723241ea4e
# [1] git ls-remote https://github.com/gonum/netlib 76723241ea4ea9096177178f67ff221166249992 
SRCREV_netlib="76723241ea4ea9096177178f67ff221166249992"
SRC_URI += "git://github.com/gonum/netlib;name=netlib;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gonum/netlib"

# gonum.org/v1/plot v0.0.0-20190515093506-e2840ee46a6b
# [1] git ls-remote https://github.com/gonum/plot e2840ee46a6b612972d746f9fea9920d329a0605 
SRCREV_plot="e2840ee46a6b612972d746f9fea9920d329a0605"
SRC_URI += "git://github.com/gonum/plot;name=plot;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gonum/plot"

# google.golang.org/api v0.57.0
# [1] git ls-remote https://github.com/googleapis/google-api-go-client 5dffc2b13eedc703d5e81c47bc3d2029a48203cb 
SRCREV_api12="5dffc2b13eedc703d5e81c47bc3d2029a48203cb"
SRC_URI += "git://github.com/googleapis/google-api-go-client;name=api12;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/google.golang.org/api"

# google.golang.org/appengine v1.6.7
# [1] git ls-remote https://github.com/golang/appengine 5d1c1d03f8703c2e81478d9a30e9afa2d3e4bd8a 
SRCREV_appengine="5d1c1d03f8703c2e81478d9a30e9afa2d3e4bd8a"
SRC_URI += "git://github.com/golang/appengine;name=appengine;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/google.golang.org/appengine"

# google.golang.org/genproto v0.0.0-20220107163113-42d7afdf6368
# [1] git ls-remote https://github.com/googleapis/go-genproto 42d7afdf6368b172d1756705e51339afa412e4f9 
SRCREV_genproto="42d7afdf6368b172d1756705e51339afa412e4f9"
SRC_URI += "git://github.com/googleapis/go-genproto;name=genproto;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/google.golang.org/genproto"

# google.golang.org/grpc v1.40.0
# [1] git ls-remote https://github.com/grpc/grpc-go 41e044e1c82fcf6a5801d6cbd7ecf952505eecb1
SRCREV_grpc="41e044e1c82fcf6a5801d6cbd7ecf952505eecb1"
SRC_URI += "git://github.com/grpc/grpc-go;name=grpc;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/grpc/grpc-go"

# google.golang.org/grpc/cmd/protoc-gen-go-grpc v1.1.0
# [1] git ls-remote https://github.com/grpc/grpc-go 938f6e2f7550e542bd78f3b9e8812665db109e02 
SRCREV_protoc-gen-go-grpc="938f6e2f7550e542bd78f3b9e8812665db109e02"
SRC_URI += "git://github.com/grpc/grpc-go;name=protoc-gen-go-grpc;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/google.golang.org/grpc/cmd/protoc-gen-go-grpc"

# google.golang.org/protobuf v1.27.1
# [1] git ls-remote https://github.com/protocolbuffers/protobuf-go b92717ecb630d4a4824b372bf98c729d87311a4d 
SRCREV_protobuf12="b92717ecb630d4a4824b372bf98c729d87311a4d"
SRC_URI += "git://github.com/protocolbuffers/protobuf-go;name=protobuf12;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/google.golang.org/protobuf"

# gopkg.in/airbrake/gobrake.v2 v2.0.9
# [1] git ls-remote https://gopkg.in/airbrake/gobrake.v2 668876711219e8b0206e2994bf0a59d889c775aa 
SRCREV_gobrake.v2="668876711219e8b0206e2994bf0a59d889c775aa"
SRC_URI += "git://gopkg.in/airbrake/gobrake.v2;name=gobrake.v2;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/airbrake/gobrake.v2"

# gopkg.in/alecthomas/kingpin.v2 v2.2.6
# [1] git ls-remote https://gopkg.in/alecthomas/kingpin.v2 947dcec5ba9c011838740e680966fd7087a71d0d 
SRCREV_kingpin.v2="947dcec5ba9c011838740e680966fd7087a71d0d"
SRC_URI += "git://gopkg.in/alecthomas/kingpin.v2;name=kingpin.v2;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/alecthomas/kingpin.v2"

# gopkg.in/check.v1 v1.0.0-20201130134442-10cb98267c6c
# [1] git ls-remote https://gopkg.in/check.v1 10cb98267c6cb43ea9cd6793f29ff4089c306974 
SRCREV_check.v1="10cb98267c6cb43ea9cd6793f29ff4089c306974"
SRC_URI += "git://gopkg.in/check.v1;name=check.v1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/check.v1"

# gopkg.in/cheggaaa/pb.v1 v1.0.27
# [1] git ls-remote https://gopkg.in/cheggaaa/pb.v1 1cc5bbe20449079337944d56292c7383510c534c 
SRCREV_pb.v1="1cc5bbe20449079337944d56292c7383510c534c"
SRC_URI += "git://gopkg.in/cheggaaa/pb.v1;name=pb.v1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/cheggaaa/pb.v1"

# gopkg.in/errgo.v2 v2.1.0
# [1] git ls-remote https://gopkg.in/errgo.v2 f768c5ab0476c50e978b039312180859c10fe8c0 
SRCREV_errgo.v2="f768c5ab0476c50e978b039312180859c10fe8c0"
SRC_URI += "git://gopkg.in/errgo.v2;name=errgo.v2;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/errgo.v2"

# gopkg.in/fsnotify.v1 v1.4.7
# [1] git ls-remote https://gopkg.in/fsnotify.v1 c2828203cd70a50dcccfb2761f8b1f8ceef9a8e9 
SRCREV_fsnotify.v1="c2828203cd70a50dcccfb2761f8b1f8ceef9a8e9"
SRC_URI += "git://gopkg.in/fsnotify.v1;name=fsnotify.v1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/fsnotify.v1"

# gopkg.in/gcfg.v1 v1.2.0
# [1] git ls-remote https://gopkg.in/gcfg.v1 27e4946190b4a327b539185f2b5b1f7c84730728 
SRCREV_gcfg.v1="27e4946190b4a327b539185f2b5b1f7c84730728"
SRC_URI += "git://gopkg.in/gcfg.v1;name=gcfg.v1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/gcfg.v1"

# gopkg.in/gemnasium/logrus-airbrake-hook.v2 v2.1.2
# [1] git ls-remote https://gopkg.in/gemnasium/logrus-airbrake-hook.v2 e928b033a891c0175fb643d5aa0779e86325eb12 
SRCREV_logrus-airbrake-hook.v2="e928b033a891c0175fb643d5aa0779e86325eb12"
SRC_URI += "git://gopkg.in/gemnasium/logrus-airbrake-hook.v2;name=logrus-airbrake-hook.v2;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/gemnasium/logrus-airbrake-hook.v2"

# gopkg.in/inf.v0 v0.9.1
# [1] git ls-remote https://gopkg.in/inf.v0 d2d2541c53f18d2a059457998ce2876cc8e67cbf 
SRCREV_inf.v0="d2d2541c53f18d2a059457998ce2876cc8e67cbf"
SRC_URI += "git://gopkg.in/inf.v0;name=inf.v0;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/inf.v0"

# gopkg.in/ini.v1 v1.62.0
# [1] git ls-remote https://gopkg.in/ini.v1 5e97220809ffaa826f787728501264e9114cb834 
SRCREV_ini.v1="5e97220809ffaa826f787728501264e9114cb834"
SRC_URI += "git://gopkg.in/ini.v1;name=ini.v1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/ini.v1"

# gopkg.in/natefinch/lumberjack.v2 v2.0.0
# [1] git ls-remote https://gopkg.in/natefinch/lumberjack.v2 7d6a1875575e09256dc552b4c0e450dcd02bd10e 
SRCREV_lumberjack.v2="7d6a1875575e09256dc552b4c0e450dcd02bd10e"
SRC_URI += "git://gopkg.in/natefinch/lumberjack.v2;name=lumberjack.v2;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/natefinch/lumberjack.v2"

# gopkg.in/resty.v1 v1.12.0
# [1] git ls-remote https://gopkg.in/resty.v1 fa5875c0caa5c260ab78acec5a244215a730247f 
SRCREV_resty.v1="fa5875c0caa5c260ab78acec5a244215a730247f"
SRC_URI += "git://gopkg.in/resty.v1;name=resty.v1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/resty.v1"

# gopkg.in/square/go-jose.v2 v2.2.2
# [1] git ls-remote https://gopkg.in/square/go-jose.v2 e94fb177d3668d35ab39c61cbb2f311550557e83 
SRCREV_go-jose.v2="e94fb177d3668d35ab39c61cbb2f311550557e83"
SRC_URI += "git://gopkg.in/square/go-jose.v2;name=go-jose.v2;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/square/go-jose.v2"

# gopkg.in/tomb.v1 v1.0.0-20141024135613-dd632973f1e7
# [1] git ls-remote https://gopkg.in/tomb.v1 dd632973f1e7218eb1089048e0798ec9ae7dceb8 
SRCREV_tomb.v1="dd632973f1e7218eb1089048e0798ec9ae7dceb8"
SRC_URI += "git://gopkg.in/tomb.v1;name=tomb.v1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/tomb.v1"

# gopkg.in/warnings.v0 v0.1.1
# [1] git ls-remote https://gopkg.in/warnings.v0 8a331561fe74dadba6edfc59f3be66c22c3b065d 
SRCREV_warnings.v0="8a331561fe74dadba6edfc59f3be66c22c3b065d"
SRC_URI += "git://gopkg.in/warnings.v0;name=warnings.v0;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/warnings.v0"

# gopkg.in/yaml.v2 v2.4.0
# [1] git ls-remote https://gopkg.in/yaml.v2 7649d4548cb53a614db133b2a8ac1f31859dda8c 
SRCREV_yaml.v2="7649d4548cb53a614db133b2a8ac1f31859dda8c"
SRC_URI += "git://gopkg.in/yaml.v2;name=yaml.v2;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/yaml.v2"

# gopkg.in/yaml.v3 v3.0.0
# [1] git ls-remote https://gopkg.in/yaml.v3 00bbc0947ae889b9e480044dbc3bc3e3216a6a89 
SRCREV_yaml.v3="00bbc0947ae889b9e480044dbc3bc3e3216a6a89"
SRC_URI += "git://gopkg.in/yaml.v3;name=yaml.v3;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/gopkg.in/yaml.v3"

# gotest.tools v2.2.0+incompatible
# [1] git ls-remote https://github.com/gotestyourself/gotest.tools 7c797b5133e5460410dbb22ba779bf35e6975dea 
SRCREV_gotest.tools="7c797b5133e5460410dbb22ba779bf35e6975dea"
SRC_URI += "git://github.com/gotestyourself/gotest.tools;name=gotest.tools;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gotestyourself/gotest.tools"

# gotest.tools/v3 v3.0.3
# [1] git ls-remote https://github.com/gotestyourself/gotest.tools 568bc57cc5c19a2ef85e5749870b49a4cc2ab54d 
SRCREV_v312345678910="568bc57cc5c19a2ef85e5749870b49a4cc2ab54d"
SRC_URI += "git://github.com/gotestyourself/gotest.tools;name=v312345678910;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/gotestyourself/gotest.tools568bc57cc5c19a2ef85e5749870b49a4cc2ab54d"

# honnef.co/go/tools v0.2.2
# [1] git ls-remote https://github.com/dominikh/go-tools c8caa92bad8c27ae734c6725b8a04932d54a147b 
SRCREV_tools1="c8caa92bad8c27ae734c6725b8a04932d54a147b"
SRC_URI += "git://github.com/dominikh/go-tools;name=tools1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/honnef.co/go/tools"

# inet.af/tcpproxy v0.0.0-20200125044825-b6bb9b5b8252
# [1] git ls-remote https://github.com/inetaf/tcpproxy b6bb9b5b82524122bcf27291ede32d1517a14ab8 
SRCREV_tcpproxy="b6bb9b5b82524122bcf27291ede32d1517a14ab8"
SRC_URI += "git://github.com/inetaf/tcpproxy;name=tcpproxy;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/inet.af/tcpproxy"

# github.com/k3s-io/kubernetes/staging/src/k8s.io/api v1.24.3-k3s1
# [1] git ls-remote https://github.com/k3s-io/kubernetes dd448004d5f03fe0465d4512f48d5a51b2b200ff 
SRCREV_api123="dd448004d5f03fe0465d4512f48d5a51b2b200ff"
SRC_URI += "git://github.com/k3s-io/kubernetes;name=api123;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/k3s-io/kubernetes/staging/src/k8s.io/api"

# k8s.io/gengo v0.0.0-20211129171323-c02415ce4185
# [1] git ls-remote https://github.com/kubernetes/gengo c02415ce41852193a7b49b79639ec83b8f517515 
SRCREV_gengo="c02415ce41852193a7b49b79639ec83b8f517515"
SRC_URI += "git://github.com/kubernetes/gengo;name=gengo;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/k8s.io/gengo"

# github.com/k3s-io/klog v1.0.0-k3s2
# [1] git ls-remote https://github.com/k3s-io/klog e94f416e9301d90f5c8d6043c999ea179bd9b455 
SRCREV_klog="e94f416e9301d90f5c8d6043c999ea179bd9b455"
SRC_URI += "git://github.com/k3s-io/klog;name=klog;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/k8s.io/klog"

# github.com/k3s-io/klog/v2 v2.60.1-k3s1
# [1] git ls-remote https://github.com/k3s-io/klog eefb2ec153e5a36888a9878c8d442746e459efb4 
SRCREV_v2123456789101112="eefb2ec153e5a36888a9878c8d442746e459efb4"
SRC_URI += "git://github.com/k3s-io/klog;name=v2123456789101112;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/k3s-io/klog/v2"

# k8s.io/kube-openapi v0.0.0-20220328201542-3ee0da9b0b42
# [1] git ls-remote https://github.com/kubernetes/kube-openapi 3ee0da9b0b4211c407396d9c233b38b77ce19773 
SRCREV_kube-openapi="3ee0da9b0b4211c407396d9c233b38b77ce19773"
SRC_URI += "git://github.com/kubernetes/kube-openapi;name=kube-openapi;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/k8s.io/kube-openapi"

# k8s.io/system-validators v1.7.0
# [1] git ls-remote https://github.com/kubernetes/system-validators 583756f47982f13e04533ad1a5f2fa24c571ce51 
SRCREV_system-validators="583756f47982f13e04533ad1a5f2fa24c571ce51"
SRC_URI += "git://github.com/kubernetes/system-validators;name=system-validators;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/k8s.io/system-validators"

# k8s.io/utils v0.0.0-20220210201930-3a6ce19ff2f9
# [1] git ls-remote https://github.com/kubernetes/utils 3a6ce19ff2f91f3dc49243f90f7834a0f1e4aaf3 
SRCREV_utils="3a6ce19ff2f91f3dc49243f90f7834a0f1e4aaf3"
SRC_URI += "git://github.com/kubernetes/utils;name=utils;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/k8s.io/utils"

# modernc.org/cc v1.0.0
# [1] git ls-remote https://gitlab.com/cznic/cc 4882521bdc8ca069d90861ecb0bc7fc11f76497f 
SRCREV_cc="4882521bdc8ca069d90861ecb0bc7fc11f76497f"
SRC_URI += "git://gitlab.com/cznic/cc;name=cc;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/modernc.org/cc"

# modernc.org/golex v1.0.0
# [1] git ls-remote https://gitlab.com/cznic/golex bc767dde60ca2c733d60c36421e559c302d87cef 
SRCREV_golex="bc767dde60ca2c733d60c36421e559c302d87cef"
SRC_URI += "git://gitlab.com/cznic/golex;name=golex;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/modernc.org/golex"

# modernc.org/mathutil v1.0.0
# [1] git ls-remote https://gitlab.com/cznic/mathutil 5712a870eb24900386234a0d001f00d2120cc76a 
SRCREV_mathutil="5712a870eb24900386234a0d001f00d2120cc76a"
SRC_URI += "git://gitlab.com/cznic/mathutil;name=mathutil;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/modernc.org/mathutil"

# modernc.org/strutil v1.0.0
# [1] git ls-remote https://gitlab.com/cznic/strutil 5caa9dea4513d6534e4cb63c091c6088e25f4b64 
SRCREV_strutil="5caa9dea4513d6534e4cb63c091c6088e25f4b64"
SRC_URI += "git://gitlab.com/cznic/strutil;name=strutil;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/modernc.org/strutil"

# modernc.org/xc v1.0.0
# [1] git ls-remote https://gitlab.com/cznic/xc 672a6557c164df3474e16ca639a7739038d9cb12 
SRCREV_xc="672a6557c164df3474e16ca639a7739038d9cb12"
SRC_URI += "git://gitlab.com/cznic/xc;name=xc;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/modernc.org/xc"

# rsc.io/binaryregexp v0.2.0
# [1] git ls-remote https://github.com/rsc/binaryregexp 857b9542aaa89cfa42a1477b43fa1f24192fe921 
SRCREV_binaryregexp="857b9542aaa89cfa42a1477b43fa1f24192fe921"
SRC_URI += "git://github.com/rsc/binaryregexp;name=binaryregexp;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/rsc.io/binaryregexp"

# rsc.io/pdf v0.1.1
# [1] git ls-remote https://github.com/rsc/pdf 48d040297cebccc9e8dc43927e1b8036ecd33bfe 
SRCREV_pdf="48d040297cebccc9e8dc43927e1b8036ecd33bfe"
SRC_URI += "git://github.com/rsc/pdf;name=pdf;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/rsc.io/pdf"

# rsc.io/quote/v3 v3.1.0
# [1] git ls-remote https://github.com/rsc/quote 0406d7298882187f4c75f75fa85cc808a64bcef1 
SRCREV_v31234567891011="0406d7298882187f4c75f75fa85cc808a64bcef1"
SRC_URI += "git://github.com/rsc/quote;name=v31234567891011;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/github.com/rsc/quote/v3"

# rsc.io/sampler v1.3.0
# [1] git ls-remote https://github.com/rsc/sampler 0cc034b51e57ed7832d4c67d526f75a900996e5c 
SRCREV_sampler="0cc034b51e57ed7832d4c67d526f75a900996e5c"
SRC_URI += "git://github.com/rsc/sampler;name=sampler;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/rsc.io/sampler"


# sigs.k8s.io/apiserver-network-proxy/konnectivity-client v0.0.30
# [1] git ls-remote https://github.com/kubernetes-sigs/apiserver-network-proxy 2dae7c8f256ec866271caabdf813031368dccff7 
SRCREV_konnectivity-client="2dae7c8f256ec866271caabdf813031368dccff7"
SRC_URI += "git://github.com/kubernetes-sigs/apiserver-network-proxy;name=konnectivity-client;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/sigs.k8s.io/apiserver-network-proxy/konnectivity-client"

# sigs.k8s.io/cli-utils v0.27.0
# [1] git ls-remote https://github.com/kubernetes-sigs/cli-utils 0c9b214db3846e52e736d439eece0a4ed6ad657f 
SRCREV_cli-utils="0c9b214db3846e52e736d439eece0a4ed6ad657f"
SRC_URI += "git://github.com/kubernetes-sigs/cli-utils;name=cli-utils;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/sigs.k8s.io/cli-utils"

# sigs.k8s.io/controller-runtime v0.10.1
# [1] git ls-remote https://github.com/kubernetes-sigs/controller-runtime 9d7bcf7f784f556882e881c1611240a8a9fdaeb2 
SRCREV_controller-runtime="9d7bcf7f784f556882e881c1611240a8a9fdaeb2"
SRC_URI += "git://github.com/kubernetes-sigs/controller-runtime;name=controller-runtime;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/sigs.k8s.io/controller-runtime"

# sigs.k8s.io/json v0.0.0-20211208200746-9f7c6b3444d2
# [1] git ls-remote https://github.com/kubernetes-sigs/json 9f7c6b3444d2435b37c474910c55d371a1c769be 
SRCREV_json="9f7c6b3444d2435b37c474910c55d371a1c769be"
SRC_URI += "git://github.com/kubernetes-sigs/json;name=json;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/sigs.k8s.io/json"

# sigs.k8s.io/kustomize/api v0.11.4
# [1] git ls-remote https://github.com/kubernetes-sigs/kustomize 0aa379c2284ce3be547569c00439e2da01fde664 
SRCREV_api1234="0aa379c2284ce3be547569c00439e2da01fde664"
SRC_URI += "git://github.com/kubernetes-sigs/kustomize;name=api1234;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/sigs.k8s.io/kustomize/api"

# sigs.k8s.io/kustomize/cmd/config v0.10.6
# [1] git ls-remote https://github.com/kubernetes-sigs/kustomize 4b64f1e0e825cfe9f57f2e7047f666c5ae037e23 
SRCREV_config="4b64f1e0e825cfe9f57f2e7047f666c5ae037e23"
SRC_URI += "git://github.com/kubernetes-sigs/kustomize;name=config;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/sigs.k8s.io/kustomize/cmd/config"

# sigs.k8s.io/kustomize/kustomize/v4 v4.5.4
# [1] git ls-remote https://github.com/kubernetes-sigs/kustomize cf3a452ddd6f83945d39d582243b8592ec627ae3
SRCREV_v422="cf3a452ddd6f83945d39d582243b8592ec627ae3"
SRC_URI += "git://github.com/kubernetes-sigs/kustomize;name=v422;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/sigs.k8s.io/kustomize/kustomize/v4"

# sigs.k8s.io/kustomize/kyaml v0.13.6
# [1] git ls-remote https://github.com/kubernetes-sigs/kustomize 485f7d44d9bca5857cdfb94d84378f5ff26e4a84 
SRCREV_kyaml="485f7d44d9bca5857cdfb94d84378f5ff26e4a84"
SRC_URI += "git://github.com/kubernetes-sigs/kustomize;name=kyaml;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/sigs.k8s.io/kustomize/kyaml"

# sigs.k8s.io/structured-merge-diff/v4 v4.2.1
# [1] git ls-remote https://github.com/kubernetes-sigs/structured-merge-diff 65611e5f7fc60e909529889c29faa9cac17cb485
SRCREV_v4122="65611e5f7fc60e909529889c29faa9cac17cb485"
SRC_URI += "git://github.com/kubernetes-sigs/structured-merge-diff;name=v4122;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/sigs.k8s.io/structured-merge-diff/v4"

# sigs.k8s.io/testing_frameworks v0.1.2
# [1] git ls-remote https://github.com/kubernetes-retired/testing_frameworks c7087048c932a346b910a2240f3a053e544eb352 
SRCREV_testing_frameworks="c7087048c932a346b910a2240f3a053e544eb352"
SRC_URI += "git://github.com/kubernetes-retired/testing_frameworks;name=testing_frameworks;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/sigs.k8s.io/testing_frameworks"

# sigs.k8s.io/yaml v1.3.0
# [1] git ls-remote https://github.com/kubernetes-sigs/yaml 9535b3b1e2893fe44efb37c5c9f5665e245d786a 
SRCREV_yaml1="9535b3b1e2893fe44efb37c5c9f5665e245d786a"
SRC_URI += "git://github.com/kubernetes-sigs/yaml;name=yaml1;protocol=https;nobranch=1;destsuffix=${WORKDIR}/${BP}/src/import/vendor.fetch/sigs.k8s.io/yaml"

