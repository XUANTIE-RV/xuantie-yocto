#!/usr/bin/env python3
#
# Copyright (C) 2014 Wind River Systems, Inc. 
# 
# Description: Calls other scripts in order, so that there can be multiple
# scripts for a particular hook tied to libvirt.
#
# For example: If this script is called "qemu" and is in the
# "/etc/libvirt/hooks/" directory.  This script will be called by libvirt
# when certain actions are performed on a qemu guest.  This script then
# will in turn call any executable file in the same directory matching
# "qemu-" followed by at least one alpha-numeric character.  The scripts
# are called in order (based on the python sorted function), and once any
# sub-script returns a non-zero exit code no futher scripts are called.
# This script passes any arguments it retrieves on the command line and a
# copy of stdin to the sub-scripts it calls.

import os
import re
import subprocess
import sys

def main():
	return_value = 0
	hook_name = os.path.basename( __file__ )
	try:
		hook_dir = os.path.dirname( __file__ )
		hook_args = sys.argv
		del hook_args[ 0 ] # Remove executable from argument list

		# Save stdin, so we can pass it to each sub-script.
		if sys.stdin.isatty():
			stdin_save = [ "" ]
		else: 
			stdin_save = sys.stdin.readlines()
		# Match the name name of the hook + a dash + atleast
		# one alpha-numeric character.
		matcher = re.compile( "%s-\w+" % hook_name )
		for file_name in sorted( os.listdir( hook_dir ) ):
			file_path = os.path.join( hook_dir, file_name )
			if matcher.match( file_name ) \
			   and os.access( file_path, os.X_OK ) \
			   and os.path.isfile( file_path ) \
			   and return_value == 0:
				cmd = [ file_path ] + hook_args
				p = subprocess.Popen( cmd, stdin=subprocess.PIPE )
				p.communicate( input = ''.join( stdin_save ) )[0]
				return_value = p.wait()
	except Exception as e:
		sys.stderr.write( "%s hook error: %s\n" % ( hook_name, str( e ) ) )
		return_value = 1
	return return_value

if __name__ == '__main__':
	sys.exit( main() )
