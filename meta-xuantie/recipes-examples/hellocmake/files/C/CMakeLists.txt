cmake_minimum_required(VERSION 3.0)
project(shared C)

include_directories(${PROJECT_SOURCE_DIR}/../B)

add_library(sub SHARED sub.c sub.h)
