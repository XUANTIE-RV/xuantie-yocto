cmake_minimum_required(VERSION 3.0)
project(hellocmake C)

add_executable(hellocmake main.c)
include_directories(${PROJECT_SOURCE_DIR}/../B)
include_directories(${PROJECT_SOURCE_DIR}/../C)

message("PROJECT_SOURCE_DIR=${PROJECT_SOURCE_DIR}")
target_link_libraries(hellocmake  add)
target_link_libraries(hellocmake  sub)

find_library(AVCODEC_LIBRARY  avcodec)
find_library(AVFORMAT_LIBRARY avformat)
find_library(AVUTIL_LIBRARY   avutil)
find_library(SWSCALE_LIBRARY  swscale)
LINK_LIBRARIES(${AVCODEC_LIBRARY} ${AVFORMAT_LIBRARY} ${AVUTIL_LIBRARY} ${SWSCALE_LIBRARY})
#target_link_libraries(hellocmake)
target_link_libraries(hellocmake avcodec swscale avformat avutil)
