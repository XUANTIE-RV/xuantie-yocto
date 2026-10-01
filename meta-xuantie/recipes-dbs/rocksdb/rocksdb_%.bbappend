# LOCAL BUILD FIX (not for ceph commit): rocksdb 7.5.3 predates gcc 13 header
# hygiene (uint8_t/uint32_t used in table/block_based/data_block_hash_index.h
# without <cstdint>). XuanTie gcc 14.3 no longer includes it transitively.
# Force-include cstdint as a minimal compiler-compat flag.
CXXFLAGS:append = " -include cstdint"
