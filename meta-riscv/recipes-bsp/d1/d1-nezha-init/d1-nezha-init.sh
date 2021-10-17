#!/bin/sh

# headphone playback
amixer -D hw:audiocodec cset name='Headphone Switch' 1
amixer -D hw:audiocodec cset name='Headphone Volume' 3

# headset capture
amixer -D hw:audiocodec cset name='ADC1 Input LINEINL Switch' 1
amixer -D hw:audiocodec cset name='ADC2 Input LINEINR Switch' 1
amixer -D hw:audiocodec cset name='ADC3 Input MIC3 Boost Switch' 1
amixer -D hw:audiocodec cset name='MIC3 Input Select' 0
amixer -D hw:audiocodec cset name='MIC1 gain volume' 0
amixer -D hw:audiocodec cset name='MIC2 gain volume' 0
amixer -D hw:audiocodec cset name='MIC3 gain volume' 19

exit 0
